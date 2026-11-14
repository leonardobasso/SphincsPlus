package org.example.hash

import org.bouncycastle.crypto.digests.SHAKEDigest
import org.example.utils.classes.Address
import org.example.utils.classes.PublicKey

@OptIn(ExperimentalUnsignedTypes::class)
class Shake256s(
    val n: Int,
    val height: Int,
    val layers: Int,
    val k: Int,
    val a: Int,
) : HashInterface {

    private val md = SHAKEDigest(256)
    private val m = ((k * a + 7) / 8) + ((height - height / layers + 7) / 8) + ((height / layers + 7) / 8)



    override fun hashF(
        PKSeed: UByteArray, address: Address, msg: UByteArray
    ): UByteArray {
        md.reset()

        md.update(PKSeed.asByteArray(), 0, PKSeed.size)
        md.update(address.address.asByteArray(), 0, address.size)
        md.update(msg.asByteArray(), 0, msg.size)

        val out = ByteArray(n)
        md.doFinal(out, 0, n)
        return out.toUByteArray()
    }

    override fun hashPRF(SKSeed: UByteArray, address: Address): UByteArray {
        md.reset()

        md.update(SKSeed.asByteArray(), 0, SKSeed.size)
        md.update(address.address.asByteArray(), 0, address.size)

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
        msg.forEach { m -> md.update(m.asByteArray(), 0, m.size) }

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
        md.update(msg.asByteArray(), 0, msg.size)

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
