import org.example.hash.HashInterface
import org.example.hash.Sha2r
import org.example.hash.Sha2s
import org.example.hash.Shake256r
import org.example.hash.Shake256s
import org.example.hypertree.Hypertree
import org.example.utils.classes.Address
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
class HypertreeTest {


    @ParameterizedTest(name = "Hypertree sign test - {0} {1}")
    @MethodSource("provideFORSConfigurations")
    fun `Hypertree verify test`(hash: String, variant: String) {

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

        val wots = WOTS(n, w, hash)
        val xmss = XMSS(height / layers, n.toUInt(), w, wots, hash)
        val hypertree = Hypertree(height.toUInt(), layers.toUInt(), w, xmss)

        val message = Random.nextUBytes(n)
        val skSeed = Random.nextUBytes(n)
        val pkSeed = Random.nextUBytes(n)
        val address = Address()
        address.address = Random.nextUBytes(32)

        val pk = hypertree.pkGen(skSeed, pkSeed)
        val sign = hypertree.sign(message, skSeed, pkSeed, 0u, 0u)

        assertTrue(hypertree.verify(message, sign, pkSeed, 0u, 0u, pk))

        val fakeMsg = Random.nextUBytes(n)
        val fakeSign = hypertree.sign(fakeMsg, skSeed, pkSeed, 0u, 0u)
        assertFalse(hypertree.verify(message, fakeSign, pkSeed, 0u, 0u, pk))
    }

    companion object {
        @JvmStatic
        fun provideFORSConfigurations(): Stream<Arguments> {
            val hashes = listOf("Sha2 robust", "Sha2 simple", "Shake256 robust", "Shake256 simple")
            val variants = listOf("128s", "128f", "192s", "192f", "256s", "256f")

            val combinations = hashes.flatMap { hash ->
                variants.map { variant -> Arguments.of(hash, variant) }
            }

            return combinations.stream()
        }
    }
}