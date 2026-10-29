package sha256

import org.example.hash.Sha256
import org.example.hypertree.Hypertree
import org.example.sphincs.Sphincs
import org.example.utils.classes.PublicKey
import org.example.utils.classes.SecretKey
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
    val xmss = XMSS(height / layers  , n.toUInt(), w.toUInt(), wots, sha)
    val hyperTree = Hypertree(height.toUInt(), layers.toUInt(), w.toUInt(), xmss)

    val sphincs = Sphincs(n, w.toUInt(), height, layers, k, a, true, sha)

    @Test
    fun `verify Sphincs - Sha256`() {

        val (pk, sk) = keyGen(n, hyperTree)
        val sign = sphincs.sign(message, sk, pk)

        assertTrue(sphincs.verify(message, pk, sign), "verify is false")

        val fakeMessage = Random.nextUBytes(n)
        val fakeSign = sphincs.sign(fakeMessage, sk, pk)

        assertFalse(sphincs.verify(message, pk, fakeSign), "verify should be false, false sign")
        assertFalse(sphincs.verify(fakeMessage, pk, sign), "verify should be false, false message")

    }

}