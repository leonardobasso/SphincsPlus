package sha256s.spec_192s

import org.example.fors.FORS
import org.example.hash.Sha2s
import org.example.utils.classes.Address
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.assertContentEquals

@OptIn(ExperimentalUnsignedTypes::class)
class ForsTest {

    val n = 24
    val w = 16
    val height = 63
    val layers = 7
    val a = 14
    val k = 17

    val mLen = (a * k + 7) / 8

    val sha = Sha2s(n, height, layers, k, a)
    val fors = FORS(k.toUInt(), a, n.toUInt(), sha)

    @Test
    fun `FORS sign test - Sha256`() {
        val message = Random.nextUBytes(mLen)
        val skSeed = Random.nextUBytes(n)
        val pkSeed = Random.nextUBytes(n)
        val address = Address()
        address.address = Random.nextUBytes(32)

        val pk = fors.pkGen(skSeed, pkSeed, address)
        val sign = fors.sign(message, pkSeed, skSeed, address)
        val pk2 = fors.pkFromSign(sign, message, pkSeed, address)

        assertContentEquals(pk, pk2, "${pk.contentToString()} is not equal to ${pk2.contentToString()}")

    }

}