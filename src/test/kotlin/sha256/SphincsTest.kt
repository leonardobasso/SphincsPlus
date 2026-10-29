package sha256

import org.example.hash.Sha256
import org.example.sphincs.Sphincs
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.assertTrue

@OptIn(ExperimentalUnsignedTypes::class)
class SphincsTest {

    //256s
    val n = 32
    val w = 16
    val height = 64
    val layers = 8
    val a = 14
    val k = 22

    val message = Random.nextUBytes(n)

    val sha = Sha256(n, height, layers, k, a)
    val sphincs = Sphincs(n, w.toUInt(), height, layers, k, a, true, sha)

    @Test
    fun `verify Sphincs - SHA256`() {

        val (pk, sk) = sphincs.keyGen(n)
        val sign = sphincs.sign(message, sk, pk)
        val verify = sphincs.verify(message, pk, sign)

        assertTrue(verify, "verify is $verify")
    }

}