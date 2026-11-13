package sha256r.spec_128s

import org.example.hash.Sha256r
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
    val height = 63
    val layers = 7
    val a = 12
    val k = 14

    val sha = Sha256r(n, height, layers, k, a)
    val wots = WOTS(n, w.toUInt(), sha)

    @Test
    fun `WOTS sign test - Sha256 robust`() {
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