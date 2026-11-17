package sha256s.spec_192s

import org.example.hash.Sha2s
import org.example.utils.classes.Address
import org.example.wots.WOTS
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.assertContentEquals

@OptIn(ExperimentalUnsignedTypes::class)
class WotsTest {

    val n = 24
    val w = 16
    val height = 63
    val layers = 7
    val a = 14
    val k = 17

    val sha = Sha2s(n, height, layers, k, a)
    val wots = WOTS(n, w.toUInt(), sha)

    @Test
    fun `WOTS sign test - Sha256`() {
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