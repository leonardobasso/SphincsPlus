package sha256

import org.example.hash.Sha256
import org.example.hypertree.Hypertree
import org.example.sphincs.Sphincs
import org.example.sphincs.SphincsSignature
import org.example.utils.functions.keyGen
import org.example.wots.WOTS
import org.example.xmss.XMSS
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalUnsignedTypes::class)
class SphincsTest {

    //256s
    val n = 32
    val w = 16
    val height = 64
    val layers = 8
    val a = 14
    val k = 22

    val message = Random.nextUBytes(n)

    val sha = Sha256(n, height, layers, k, a)
    val wots = WOTS(n, w.toUInt(), sha)
    val xmss = XMSS(height / layers, n.toUInt(), w.toUInt(), wots, sha)
    val hyperTree = Hypertree(height.toUInt(), layers.toUInt(), w.toUInt(), xmss)

    val sphincs = Sphincs(n, w.toUInt(), height, layers, k, a, true, sha)

    @Test
    fun `verify Sphincs - Sha256`() {

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

}