package sha256r.spec_256f

import org.example.fors.FORS
import org.example.hash.Sha2r
import org.example.utils.classes.Address
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.assertContentEquals

@OptIn(ExperimentalUnsignedTypes::class)
class ForsTest {

    val n = 32
    val w = 16
    val height = 68
    val layers = 17
    val a = 9
    val k = 35

    val mLen = (a * k + 7) / 8

    val sha = Sha2r(n, height, layers, k, a)
    val fors = FORS(k.toUInt(), a, n.toUInt(), sha)

    @Test
    fun `FORS sign test - Sha256 robust`() {
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