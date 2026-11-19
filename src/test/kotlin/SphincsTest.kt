package sha2

import org.example.hash.HashInterface
import org.example.hash.Sha2r
import org.example.hash.Sha2s
import org.example.hash.Shake256r
import org.example.hash.Shake256s
import org.example.hypertree.Hypertree
import org.example.sphincs.Sphincs
import org.example.sphincs.SphincsSignature
import org.example.utils.functions.keyGen
import org.example.wots.WOTS
import org.example.xmss.XMSS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalUnsignedTypes::class)
class SphincsTest {




    @ParameterizedTest(name = "FORS sign test - {0} {1}")
    @MethodSource("provideSphincsConfigurations")
    fun `verify Sphincs`(hash: String, variant: String) {
        val n: Int
        val height: Int
        val layers: Int
        val a: Int
        val k: Int
        val w = 16u

        when (variant) {
            "128s" -> {
                n = 16; height = 63; layers = 7; a = 12; k = 14
            }

            "128f" -> {
                n = 16; height = 66; layers = 22; a = 6; k = 33
            }

            "192s" -> {
                n = 24; height = 63; layers = 7; a = 14; k = 17
            }

            "192f" -> {
                n = 24; height = 66; layers = 22; a = 8; k = 33
            }

            "256s" -> {
                n = 32; height = 64; layers = 8; a = 14; k = 22
            }

            "256f" -> {
                n = 32; height = 68; layers = 17; a = 9; k = 35
            }

            else -> throw IllegalArgumentException("$variant does not exist")
        }
        val hash: HashInterface = when (hash) {
            "Sha2 robust" -> Sha2r(n, height, layers, k, a)
            "Sha2 simple" -> Sha2s(n, height, layers, k, a)
            "Shake256 robust" -> Shake256r(n, height, layers, k, a)
            "Shake256 simple" -> Shake256s(n, height, layers, k, a)
            else -> throw IllegalArgumentException("$hash does not exist")
        }
        val message = Random.nextUBytes(n)

        val wots = WOTS(n, w, hash)
        val xmss = XMSS(height / layers, n.toUInt(), w, wots, hash)
        val hyperTree = Hypertree(height.toUInt(), layers.toUInt(), w, xmss)

        val sphincs = Sphincs(n, w, height, layers, k, a, true, hash)


        // The message signs correctly
        val (pk, sk) = keyGen(n, hyperTree)
        val sign = sphincs.sign(message, sk, pk)

        assertTrue(sphincs.verify(message, pk, sign), "verify is false")

        // A sign of a message cannot verify a different message
        val fakeMessage = Random.nextUBytes(n)
        val fakeSign = sphincs.sign(fakeMessage, sk, pk)

        assertFalse(sphincs.verify(message, pk, fakeSign), "verify should be false, false sign")
        assertFalse(sphincs.verify(fakeMessage, pk, sign), "verify should be false, false message")

        // A corrupted message cannot be verified with its sign
        val corruptedMessage = message.copyOf()
        corruptedMessage[corruptedMessage.lastIndex] = corruptedMessage.last() xor 1u
        assertFalse(sphincs.verify(corruptedMessage, pk, sign), "verify should be false, corrupted message")

        for (i in message.indices) {
            val fakeMessage = message.copyOf()
            fakeMessage[i] = fakeMessage[i] xor 1u
            assertFalse(sphincs.verify(fakeMessage, pk, sign), "verify should be false, it's true at bitflip $i")
        }

        // A corrupted sign cannot open a message
        val corruptR = sign.r.copyOf()
        corruptR[0] = corruptR[0] xor 1u
        val corruptedSign = SphincsSignature(corruptR, sign.forsSign, sign.htSign)
        assertFalse(sphincs.verify(message, pk, corruptedSign), "verify should be false, corrupted sign")

        for (i in sign.r.indices) {
            val corruptR = sign.r.copyOf()
            corruptR[i] = corruptR[i] xor 1u
            assertFalse(
                sphincs.verify(message, pk, SphincsSignature(corruptR, sign.forsSign, sign.htSign)),
                "verify should be false, corrupted sign at bitflip $i"
            )
        }


    }

    companion object {
        @JvmStatic
        fun provideSphincsConfigurations(): Stream<Arguments> {
            val hashes = listOf("Sha2 robust", "Sha2 simple", "Shake256 robust", "Shake256 simple")
            val variants = listOf("128s", "128f", "192s", "192f", "256s", "256f")

            val combinations = hashes.flatMap { hash ->
                variants.map { variant -> Arguments.of(hash, variant) }
            }

            return combinations.stream()
        }
    }

}