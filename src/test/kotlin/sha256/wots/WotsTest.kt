package sha256.wots

import org.example.hash.Sha256
import org.example.utils.classes.Address
import org.example.wots.WOTS
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.assertContentEquals

@OptIn(ExperimentalUnsignedTypes::class)
class WotsTest {
    //256s
    val n = 32
    val w = 16
    val height = 10
    val layers = 8
    val a = 14
    val k = 22

    val sha = Sha256(n, height, layers, k, a)
    val wots = WOTS(n, w.toUInt(), sha)

    @Test
    fun `WOTS sign test`() {
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