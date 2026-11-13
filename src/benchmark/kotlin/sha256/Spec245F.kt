package sha256

import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import kotlin.random.Random
import kotlin.random.nextUBytes
import org.example.hash.Sha256
import org.example.hypertree.Hypertree
import org.example.sphincs.Sphincs
import org.example.utils.functions.keyGen
import org.example.wots.WOTS
import org.example.xmss.XMSS
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalUnsignedTypes::class)
@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
class Spec245F {

    val n = 32
    val w = 16u
    val height = 68
    val layers = 17
    val a = 9
    val k = 35

    val sha = Sha256(n, height, layers, k, a)
    val wots = WOTS(n, w, sha)
    val xmss = XMSS(height / layers, n.toUInt(), w, wots, sha)
    val hyperTree = Hypertree(height.toUInt(), layers.toUInt(), w, xmss)

    val sphincs = Sphincs(n, w, height, layers, k, a, false, sha)

    val message = Random(42).nextUBytes(n)

    private val keys = keyGen(n, hyperTree)
    val pk = keys.first
    val sk = keys.second

    val signature = sphincs.sign(message, sk, pk)

    @Benchmark
    fun sign() = sphincs.sign(message, sk, pk)

    @Benchmark
    fun verify() = sphincs.verify(message, pk, signature)
}
