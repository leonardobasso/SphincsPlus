package org.example.hash

import org.bouncycastle.crypto.digests.SHAKEDigest
import org.example.utils.classes.Address
import org.example.utils.classes.PublicKey
import kotlin.experimental.xor

@OptIn(ExperimentalUnsignedTypes::class)
class Shake256r(
    val n: Int,
    val height: Int,
    val layers: Int,
    val k: Int,
    val a: Int,
) : HashInterface {

    private val md = SHAKEDigest(256)
    private val maskDigest = SHAKEDigest(256)
    private val m = ((k * a + 7) / 8) + ((height - height / layers + 7) / 8) + ((height / layers + 7) / 8)

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

        maskDigest.reset()
        maskDigest.update(PKSeed.asByteArray(), 0, PKSeed.size)
        maskDigest.update(address.address.asByteArray(), 0, address.size)

        maskDigest.doFinal(mask, 0, mask.size)
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
        require(message.size == mask.size) {"Message and Mask should have the same size"}
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

        maskDigest.reset()
        maskDigest.update(PKSeed.asByteArray(), 0, PKSeed.size)
        maskDigest.update(address.address.asByteArray(), 0, address.size)

        maskDigest.doFinal(mask, 0, mask.size)

        return ByteArray(message.size) { i -> message[i].toByte() xor mask[i] }
    }


    override fun hashF(
        PKSeed: UByteArray, address: Address, msg: UByteArray
    ): UByteArray {
        md.reset()

        md.update(PKSeed.asByteArray(), 0, PKSeed.size)
        md.update(address.address.asByteArray(), 0, address.size)
        md.update(maskMessage(msg, PKSeed, address), 0, msg.size)

        val out = ByteArray(n)
        md.doFinal(out, 0, n)
        return out.toUByteArray()
    }

    override fun hashPRF(SKSeed: UByteArray, PKSeed: UByteArray, address: Address): UByteArray {
        md.reset()

        md.update(PKSeed.asByteArray(), 0, PKSeed.size)
        md.update(address.address.asByteArray(), 0, address.size)
        md.update(SKSeed.asByteArray(), 0, SKSeed.size)

        val out = ByteArray(n)
        md.doFinal(out, 0, n)
        return out.toUByteArray()

    }
    override fun hashTl(
        PKSeed: UByteArray, address: Address, msg: Array<UByteArray>
    ): UByteArray {
        md.reset()

        md.update(PKSeed.asByteArray(), 0, PKSeed.size)
        md.update(address.address.asByteArray(), 0, address.size)

        val mask = generateMask(PKSeed, address, msg[0].size)
        msg.forEach { m ->
            md.update(maskMessage(m, mask), 0, m.size)
        }

        val out = ByteArray(n)
        md.doFinal(out, 0, n)
        return out.toUByteArray()

    }

    override fun hashH(
        PKSeed: UByteArray, address: Address, msg: UByteArray
    ): UByteArray {
        md.reset()

        md.update(PKSeed.asByteArray(), 0, PKSeed.size)
        md.update(address.address.asByteArray(), 0, address.size)
        md.update(maskMessage(msg, PKSeed, address), 0, msg.size)

        val out = ByteArray(n)
        md.doFinal(out, 0, n)
        return out.toUByteArray()

    }

    override fun hashPrfMsg(
        PkPrf: UByteArray, opt: UByteArray, msg: UByteArray
    ): UByteArray {
        md.reset()

        md.update(PkPrf.asByteArray(), 0, PkPrf.size)
        md.update(opt.asByteArray(), 0, opt.size)
        md.update(msg.asByteArray(), 0, msg.size)

        val out = ByteArray(n)
        md.doFinal(out, 0, n)
        return out.toUByteArray()
    }

    override fun hashHMsg(
        r: UByteArray, publicKey: PublicKey, message: UByteArray
    ): UByteArray {
        md.reset()

        md.update(r.asByteArray(), 0, r.size)
        md.update(publicKey.seed.asByteArray(), 0, publicKey.seed.size)
        md.update(publicKey.root.asByteArray(), 0, publicKey.root.size)
        md.update(message.asByteArray(), 0, message.size)

        val out = ByteArray(m)
        md.doFinal(out, 0, m)

        return out.toUByteArray()

    }


}
