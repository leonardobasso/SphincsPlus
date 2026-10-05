package org.example.wots

import org.example.hash.hashF
import org.example.hash.hashPRF
import org.example.hash.hashTl
import org.example.utils.classes.Address
import org.example.utils.functions.baseW
import org.example.utils.functions.log2W
import kotlin.math.log

/**
 * Wrapper class for WOTS+ related code
 *
 * Code from Chapter 5 od SLH-DSA and Chapter 3 of Sphincs v3
 *
 * @param n the security parameter; it is the message length as well as the length of a private key, public key, or signature element in bytes. *(Sphincs v3, p3.1)*
 * @param w the Winternitz parameter; it is an element of the set {4,16,256}. *(Sphincs v3, p3.1)*
 */
@OptIn(ExperimentalUnsignedTypes::class)
class WOTS(val n: Int, val w: UInt) {

    init {
        require(w == 4u || w == 16u || w == 256u) { "base must be 4, 16 or 256, instead is $w" }
    }


    val len: Int = genLen(n, w.toInt())
    val len1: Int = genLen1(n, w.toInt())
    val len2: Int = genLen2(n, w.toInt())

    fun chain(msg: UByteArray, index: UInt, steps: UInt, PKSeed: UByteArray, address: Address): UByteArray {
        if (steps == 0u) return msg
        if ((index + steps) > (w - 1u)) throw IndexOutOfBoundsException("index + steps is > w - 1")

        var temp = msg
        for (j in index until index + steps - 1u) {
            address.setHashAddress(j)
            temp = hashF(PKSeed, address, temp)
        }
        return temp
    }

    /**
     * A WOTS+ key pair defines a virtual structure that consists of len hash chains of length w.
     * Each of the len stings of n-bytes in the private key defines the start node for one hash chain.
     * The public key is the tweakable hash of the end nodes of these hash chains. *(Sphincs v3 p 3.4)*
     *
     * @param SKSeed the secret key's seed
     * @param PKSeed the public key's seed
     * @param address a WOTS+ hash address
     *
     * @return the public key
     */
    fun pkGen(SKSeed: UByteArray, PKSeed: UByteArray, address: Address): UByteArray {
        val pkAddress = address.copyOf()
        var sk: UByteArray
        val temp = Array(len) { ubyteArrayOf() }

        for (i in 0 until len) {
            address.setChainAddress(i.toUInt())
            address.setHashAddress(0u)
            sk = hashPRF(SKSeed, address)
            temp[i] = chain(
                sk, 0u, w - 1u, PKSeed, address
            ) //pseudocode says tmp[i] = chain(sk[i], 0, w - 1, PK.seed, ADRS); I that think s[i] is an error
        }
        pkAddress.setTypeAndClear(1u)
        pkAddress.setKeyPairAddress(address.getKeyPairAddress())

        return hashTl(PKSeed, address, temp) // pk
    }

    fun sign(message: UByteArray, SKSeed: UByteArray, PKSeed: UByteArray, address: Address): UByteArray {

        var checksum = 0u
        var msg = baseW(message, w, len1)

        for (i in 0 until len1) {
            checksum = checksum + w - 1u - msg[i]
        }

        val lgW = log2W(w)
        if ((lgW % 8) != 0) {
            checksum = checksum shl (8 - ((len2 * lgW) % 8))
        }
        /*TODO*/
        return ubyteArrayOf()
    }

}