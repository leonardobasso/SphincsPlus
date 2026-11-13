package sha256.spec_192f

import org.example.fors.FORS
import org.example.hash.Sha256
import org.example.utils.classes.Address
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.assertContentEquals

@OptIn(ExperimentalUnsignedTypes::class)
class ForsTest {

    val n = 24
    val w = 16
    val height = 66
    val layers = 22
    val a = 8
    val k = 33

    val mLen = (a * k + 7) / 8

    val sha = Sha256(n, height, layers, k, a)
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