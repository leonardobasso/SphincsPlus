package shake256r.spec_128s

import org.example.hash.Shake256r
import org.example.hypertree.Hypertree
import org.example.utils.classes.Address
import org.example.wots.WOTS
import org.example.xmss.XMSS
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalUnsignedTypes::class)
class HypertreeTest {

    val n = 16
    val w = 16
    val height = 63
    val layers = 7
    val a = 12
    val k = 14

    val shake = Shake256r(n, height, layers, k, a)
    val wots = WOTS(n, w.toUInt(), shake)
    val xmss = XMSS(height / layers  , n.toUInt(), w.toUInt(), wots, shake)

    val hypertree = Hypertree(height.toUInt(), layers.toUInt(), w.toUInt(),xmss )

    @Test
    fun `Hypertree verify test - Shake256r`() {
        val message = Random.nextUBytes(n)
        val skSeed = Random.nextUBytes(n)
        val pkSeed = Random.nextUBytes(n)
        val address = Address()
        address.address = Random.nextUBytes(32)

        val pk = hypertree.pkGen(skSeed, pkSeed)
        val sign = hypertree.sign(message, skSeed, pkSeed, 0u, 0u)

        assertTrue(hypertree.verify(message, sign,pkSeed, 0u, 0u, pk))

        val fakeMsg = Random.nextUBytes(n)
        val fakeSign = hypertree.sign(fakeMsg, skSeed, pkSeed, 0u, 0u)
        assertFalse(hypertree.verify(message, fakeSign, pkSeed, 0u, 0u, pk))
    }
}