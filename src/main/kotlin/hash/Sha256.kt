package org.example.hash

import org.example.utils.classes.Address
import org.example.utils.classes.PublicKey
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.Mac.getInstance
import javax.crypto.spec.SecretKeySpec

@OptIn(ExperimentalUnsignedTypes::class)
class Sha256(val n: Int) : HashInterface {

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

        return md.digest().toUByteArray()

    }
}

@OptIn(ExperimentalUnsignedTypes::class)
inline fun MessageDigest.update(array: UByteArray) = this.update(array.asByteArray())

@OptIn(ExperimentalUnsignedTypes::class)
inline fun Mac.update(array: UByteArray) = this.update(array.asByteArray())
