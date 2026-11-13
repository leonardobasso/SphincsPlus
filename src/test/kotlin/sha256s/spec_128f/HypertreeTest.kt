package sha256s.spec_128f

import org.example.hash.Sha256s
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
    val height = 66
    val layers = 22
    val a = 6
    val k = 33

    val sha = Sha256s(n, height, layers, k, a)
    val wots = WOTS(n, w.toUInt(), sha)
    val xmss = XMSS(height / layers  , n.toUInt(), w.toUInt(), wots, sha)

    val hypertree = Hypertree(height.toUInt(), layers.toUInt(), w.toUInt(),xmss )

    @Test
    fun `Hypertree verify test - Sha256`() {
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