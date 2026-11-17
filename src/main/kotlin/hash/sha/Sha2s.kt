package org.example.hash

import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.generators.MGF1BytesGenerator
import org.bouncycastle.crypto.params.MGFParameters
import org.example.utils.classes.Address
import org.example.utils.classes.PublicKey
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Implementation of SHA-256 simple using the parameters specified in Sphincs+ v3 pp 7.2.2.
 *
 * @param n the security parameter in bytes
 * @param height the height of the hypertree.
 * @param layers the number of layers in the hypertree.
 * @param k the number of trees in FORS
 * @param a t = 2^a is the number of elements per private key set, number of leaves per hash tree and upper bound on the index values.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class Sha2s(
    val n: Int,
    val height: Int,
    val layers: Int,
    val k: Int,
    val a: Int,
) : HashInterface {

    private val isSha512 = n != 16
    private val algo = if (isSha512) "512" else "256"

    private val md = MessageDigest.getInstance("SHA-256")
    private val mdX = if (isSha512) MessageDigest.getInstance("SHA-512")
    else md

    private val mac = Mac.getInstance("HmacSHA$algo")
    private val mgf = MGF1BytesGenerator(if (isSha512) SHA512Digest() else SHA256Digest())

    private val m = ((k * a + 7) / 8) + ((height - height / layers + 7) / 8) + ((height / layers + 7) / 8)

    /**
     * BlockPad(PK.seed) = PK.seed||toByte(0,bl−n).
     * where bl = 64 for SHA2-256 and bl = 128 for SHA2-512. Sphincs v3 pp 7.7.2
     */
    private val pad64 = ByteArray(64 - n)
    private val padX = if (isSha512) ByteArray(128 - n) else pad64

    override fun hashF(
        PKSeed: UByteArray, address: Address, msg: UByteArray
    ): UByteArray {
        md.reset()

        md.update(PKSeed)
        md.update(pad64)
        md.update(address.compress())
        md.update(msg)

        return md.digest().copyOf(n).toUByteArray()
    }

    override fun hashPRF(SKSeed: UByteArray, PKSeed: UByteArray, address: Address): UByteArray {
        md.reset()

        md.update(PKSeed)
        md.update(pad64)
        md.update(address.compress())
        md.update(SKSeed)

        return md.digest().copyOf(n).toUByteArray()
    }

    override fun hashTl(
        PKSeed: UByteArray, address: Address, msg: Array<UByteArray>
    ): UByteArray {
        mdX.reset()

        mdX.update(PKSeed)
        mdX.update(padX)
        mdX.update(address.compress())
        msg.forEach { m -> mdX.update(m) }

        return mdX.digest().copyOf(n).toUByteArray()

    }

    override fun hashH(
        PKSeed: UByteArray, address: Address, msg: UByteArray
    ): UByteArray {
        mdX.reset()

        mdX.update(PKSeed)
        mdX.update(padX)
        mdX.update(address.compress())
        mdX.update(msg)

        return mdX.digest().copyOf(n).toUByteArray()

    }

    override fun hashPrfMsg(
        PkPrf: UByteArray, opt: UByteArray, msg: UByteArray
    ): UByteArray {
        mac.reset()
        val secretKey = SecretKeySpec(PkPrf.asByteArray(), "HmacSHA$algo")

        mac.init(secretKey)
        mac.update(opt)
        mac.update(msg)

        return mac.doFinal().copyOf(n).toUByteArray()
    }

    override fun hashHMsg(
        r: UByteArray, publicKey: PublicKey, message: UByteArray
    ): UByteArray {
        mdX.reset()

        mdX.update(r)
        mdX.update(publicKey.seed)
        mdX.update(publicKey.root)
        mdX.update(message)

        val len = m
        val out = ByteArray(len)

        mgf.init(MGFParameters(r.asByteArray() + publicKey.seed.asByteArray() + mdX.digest()))
        mgf.generateBytes(out, 0, len)

        return out.toUByteArray()

    }
}

@OptIn(ExperimentalUnsignedTypes::class)
fun MessageDigest.update(array: UByteArray) = this.update(array.asByteArray())

@OptIn(ExperimentalUnsignedTypes::class)
fun Mac.update(array: UByteArray) = this.update(array.asByteArray())
