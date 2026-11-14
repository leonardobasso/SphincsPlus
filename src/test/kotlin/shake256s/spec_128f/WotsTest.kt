package shake256s.spec_128f

import org.example.hash.Shake256s
import org.example.utils.classes.Address
import org.example.wots.WOTS
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.assertContentEquals

@OptIn(ExperimentalUnsignedTypes::class)
class WotsTest {

    val n = 16
    val w = 16
    val height = 66
    val layers = 22
    val a = 6
    val k = 33

    val shake = Shake256s(n, height, layers, k, a)
    val wots = WOTS(n, w.toUInt(), shake)

    @Test
    fun `WOTS sign test - Shake256s`() {
        val message = Random.nextUBytes(n)
        val skSeed = Random.nextUBytes(n)
        val pkSeed = Random.nextUBytes(n)
        val address = Address()
        address.address = Random.nextUBytes(32)

        val pk = wots.pkGen(skSeed, pkSeed, address)
        val sign = wots.sign(message, skSeed, pkSeed, address)
        val pk2 = wots.pkFromSign(sign, message, pkSeed, address)

        assertContentEquals(pk, pk2, "${pk.contentToString()} is different from ${pk2.contentToString()}")
    }
}