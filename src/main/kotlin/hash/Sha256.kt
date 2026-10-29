package org.example.hash

import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.MGF1BytesGenerator
import org.bouncycastle.crypto.params.MGFParameters
import org.example.hash.update
import org.example.utils.classes.Address
import org.example.utils.classes.PublicKey
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.Mac.getInstance
import javax.crypto.spec.SecretKeySpec

/**
 * Implementation of SHA-256 using the parameters specified in Sphincs+ v3 pp 7.2.2.
 *
 * @param n the security parameter in bytes
 * @param height the height of the hypertree.
 * @param layers the number of layers in the hypertree.
 * @param k the number of trees in FORS
 * @param a t = 2^a is the number of elements per private key set, number of leaves per hash tree and upper bound on the index values.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class Sha256(
    val n: Int,
    val height: Int,
    val layers: Int,
    val k: Int,
    val a: Int,
) : HashInterface {

    val md = MessageDigest.getInstance("SHA-256")
    val mac = getInstance("HmacSHA256")

    private val padding = UByteArray(64 - n)

    override fun hashF(
        PKSeed: UByteArray, address: Address, msg: UByteArray
    ): UByteArray {
        md.reset()

        md.update(PKSeed)
        md.update(padding)
        md.update(address.address)
        md.update(msg)

        return md.digest().toUByteArray()
    }

    override fun hashPRF(SKSeed: UByteArray, address: Address): UByteArray {
        md.reset()

        md.update(SKSeed)
        md.update(address.address)

        return md.digest().toUByteArray()
    }

    override fun hashTl(
        PKSeed: UByteArray, address: Address, msg: Array<UByteArray>
    ): UByteArray {
        md.reset()

        md.update(PKSeed)
        md.update(padding)
        md.update(address.address)
        msg.forEach { m -> md.update(m) }

        return md.digest().toUByteArray()

    }

    override fun hashH(
        PKSeed: UByteArray, address: Address, msg: UByteArray
    ): UByteArray {
        md.reset()

        md.update(PKSeed)
        md.update(padding)
        md.update(address.address)
        md.update(msg)

        return md.digest().toUByteArray()

    }

    override fun hashPrfMsg(
        PkPrf: UByteArray, opt: UByteArray, msg: UByteArray
    ): UByteArray {
        mac.reset()
        val secretKey = SecretKeySpec(PkPrf.asByteArray(), "HmacSHA256")

        mac.init(secretKey)
        mac.update(opt)
        mac.update(msg)

        return mac.doFinal().toUByteArray()
    }

    override fun hashHMsg(
        r: UByteArray, publicKey: PublicKey, message: UByteArray
    ): UByteArray {
        md.reset()

        md.update(r)
        md.update(publicKey.seed)
        md.update(publicKey.root)
        md.update(message)

        val mgf = MGF1BytesGenerator(SHA256Digest())
        val len = ((k * a + 7) / 8) + ((height - height / layers + 7) / 8) + ((height / layers + 7) / 8)
        val out = ByteArray(len)

        mgf.init(MGFParameters(md.digest()))
        mgf.generateBytes(out, 0, len)

        return out.toUByteArray()

    }
}

@OptIn(ExperimentalUnsignedTypes::class)
inline fun MessageDigest.update(array: UByteArray) = this.update(array.asByteArray())

@OptIn(ExperimentalUnsignedTypes::class)
inline fun Mac.update(array: UByteArray) = this.update(array.asByteArray())
