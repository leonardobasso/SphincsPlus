package sha256.wots

import org.example.hash.Sha256
import org.example.utils.classes.Address
import org.example.wots.WOTS
import org.example.xmss.XMSS
import org.example.xmss.XMSSSignature
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.assertContentEquals

@OptIn(ExperimentalUnsignedTypes::class)
class XmssTest {

    //256s
    val n = 32
    val w = 16
    val height = 64
    val layers = 8
    val a = 14
    val k = 22
    val index = 1u

    val sha = Sha256(n, height, layers, k, a)
    val wots = WOTS(n, w.toUInt(), sha)

    val xmss = XMSS(height / layers  , n.toUInt(), w.toUInt(), wots, sha)

    @Test
    fun `XMSS sign test`() {
        val message = Random.nextUBytes(n)
        val skSeed = Random.nextUBytes(n)
        val pkSeed = Random.nextUBytes(n)
        val address = Address()
        address.address = Random.nextUBytes(32)

        val pk = xmss.pkGen(skSeed, pkSeed, address)
        val sign = xmss.sign(message, skSeed, pkSeed, index, address)
        val pk2 = xmss.pkFromSign(index,  sign, message, pkSeed, address)

       assertContentEquals(pk, pk2, "${pk.contentToString()} is different from ${pk2.contentToString()}")
    }

}