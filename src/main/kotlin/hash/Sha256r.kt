package org.example.hash

import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.MGF1BytesGenerator
import org.bouncycastle.crypto.params.MGFParameters
import org.example.utils.classes.Address
import org.example.utils.classes.PublicKey
import java.security.MessageDigest
import javax.crypto.Mac.getInstance
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor

/**
 * Implementation of SHA-256 robust using the parameters specified in Sphincs+ v3 pp 7.2.2.
 *
 * @param n the security parameter in bytes
 * @param height the height of the hypertree.
 * @param layers the number of layers in the hypertree.
 * @param k the number of trees in FORS
 * @param a t = 2^a is the number of elements per private key set, number of leaves per hash tree and upper bound on the index values.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class Sha256r(
    val n: Int,
    val height: Int,
    val layers: Int,
    val k: Int,
    val a: Int,
) : HashInterface {

    private val md = MessageDigest.getInstance("SHA-256")
    private val mac = getInstance("HmacSHA256")
    private val mgf = MGF1BytesGenerator(SHA256Digest())
    private val len = ((k * a + 7) / 8) + ((height - height / layers + 7) / 8) + ((height / layers + 7) / 8)


    private val padding = UByteArray(64 - n)


    /**
     * Generates a mask from the Public Key's seed and the address
     * @param PKSeed the public key's seed
     * @param address the address
     * @param size the mask size, it should be the same size of the message you want to mask
     *
     * @return the mask
     */
    private fun generateMask(PKSeed: UByteArray, address: Address, size: Int): ByteArray {
        val mask = ByteArray(size)

        mgf.init(MGFParameters((PKSeed + address.address).toByteArray()))
        mgf.generateBytes(mask, 0, size)
        return mask

    }

    /**
     * Given a message and a mask of the same size, the messahe het masked
     *
     * @param message the message to mask
     * @param mask the mask
     *
     * @return the masked message
     */
    private fun maskMessage(message: UByteArray, mask: ByteArray): ByteArray {
        require(message.size == mask.size) { "Message and Mask should have the same size" }
        return ByteArray(message.size) { i -> message[i].toByte() xor mask[i] }
    }

    /**
     * Generates a mask from a Public Key's seed and an Address, then masks the message
     *
     * @param message the message to mask
     * @param PKSeed the public key's seed
     * @param address the address
     *
     * @return the masked message
     */
    private fun maskMessage(message: UByteArray, PKSeed: UByteArray, address: Address): ByteArray {
        val mask = ByteArray(message.size)

        mgf.init(MGFParameters((PKSeed + address.address).toByteArray()))
        mgf.generateBytes(mask, 0, message.size)

        return ByteArray(message.size) { i -> message[i].toByte() xor mask[i] }

    }

    override fun hashF(
        PKSeed: UByteArray, address: Address, msg: UByteArray
    ): UByteArray {

        md.reset()

        md.update(PKSeed)
        md.update(padding)
        md.update(address.address)
        md.update(maskMessage(msg, PKSeed, address))

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

        val mask = generateMask(PKSeed, address, msg[0].size)
        msg.forEach { m -> md.update(maskMessage(m, mask)) }

        return md.digest().toUByteArray()

    }

    override fun hashH(
        PKSeed: UByteArray, address: Address, msg: UByteArray
    ): UByteArray {
        md.reset()

        md.update(PKSeed)
        md.update(padding)
        md.update(address.address)
        md.update(maskMessage(msg, PKSeed, address))

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

        val out = ByteArray(len)

        mgf.init(MGFParameters(md.digest()))
        mgf.generateBytes(out, 0, len)

        return out.toUByteArray()

    }
}
