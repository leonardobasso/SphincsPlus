package sha256s

import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Setup
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import kotlin.random.Random
import kotlin.random.nextUBytes
import org.example.hash.Sha2s
import org.example.hypertree.Hypertree
import org.example.sphincs.Sphincs
import org.example.sphincs.SphincsSignature
import org.example.utils.classes.PublicKey
import org.example.utils.classes.SecretKey
import org.example.utils.functions.keyGen
import org.example.wots.WOTS
import org.example.xmss.XMSS
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalUnsignedTypes::class)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput, Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
class ShaBenchmarks {


    @Param("128f", "128s", "192f", "192s", "256f", "256s")
    var variant: String = ""


    private lateinit var sphincs: Sphincs
    private lateinit var hyperTree: Hypertree
    private lateinit var pk: PublicKey
    private lateinit var sk: SecretKey
    private lateinit var signature: SphincsSignature
    private var n: Int = 0
    private var message = ubyteArrayOf()

    @Setup(Level.Trial)
    fun setup() {
        val w = 16u
        val height: Int
        val layers: Int
        val a: Int
        val k: Int

        when (variant) {
            "128s" -> {
                n = 16; height = 63; layers = 7; a = 12; k = 14
            }

            "128f" -> {
                n = 16; height = 66; layers = 22; a = 6; k = 33
            }

            "192s" -> {
                n = 24; height = 63; layers = 7; a = 14; k = 17
            }

            "192f" -> {
                n = 24; height = 66; layers = 22; a = 8; k = 33
            }

            "256s" -> {
                n = 32; height = 64; layers = 8; a = 14; k = 22
            }

            "256f" -> {
                n = 32; height = 68; layers = 17; a = 9; k = 35
            }

            else -> throw IllegalArgumentException("Variante non supportata: $variant")
        }

        val hash = Sha2s(n, height, layers, k, a)
        val wots = WOTS(n, w, hash)
        val xmss = XMSS(height / layers, n.toUInt(), w, wots, hash)
        hyperTree = Hypertree(height.toUInt(), layers.toUInt(), w, xmss)
        sphincs = Sphincs(n, w, height, layers, k, a, false, hash)

        message = Random(42).nextUBytes(n)
        val keys = keyGen(n, hyperTree)
        pk = keys.first
        sk = keys.second
        signature = sphincs.sign(message, sk, pk)

    }


    @Benchmark
    fun keygen() = keyGen(n, hyperTree)

    @Benchmark
    fun sign() = sphincs.sign(message, sk, pk)

    @Benchmark
    fun verify() = sphincs.verify(message, pk, signature)

}
