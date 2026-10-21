package org.example.hash

import org.example.utils.classes.Address
import org.example.utils.classes.PublicKey

/**
 * Interface for the hash functions used in Sphincs+
 * @see Sha256
 */
interface HashInterface {
    /**
     * F : Bn ×B32 ×Bn →Bn
     * F = Tℓ with ℓ = 1
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    fun hashF(PKSeed: UByteArray, address: Address, msg: UByteArray): UByteArray

    /**
     * SPHINCS+ makes use of a pseudorandom function PRF for pseudorandom key generation:
     * PRF : Bn ×B32 →Bn
     *  *Sphincs+ 2.7.2)*
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    fun hashPRF(SKSeed: UByteArray, address: Address): UByteArray

    /**
     * Tℓ(PK.seed, ADRS, 𝑀ℓ) (𝔹𝑛 × 𝔹32 × 𝔹ℓ𝑛 → 𝔹𝑛) is a hash function that maps an
     * ℓ𝑛-byte message to an 𝑛-byte message.
     *
     * Per n si intende  +n di Witnizer??
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    fun hashTl(PKSeed: UByteArray, address: Address, msg: Array<UByteArray>): UByteArray

    /**
     * H : Bn ×B32 ×B2n →Bn *(Sphincs+ v3 pp 2.7.2)*
     * H = Tℓ with ℓ = 2
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    fun hashH(PKSeed: UByteArray, address: Address, msg: UByteArray): UByteArray

    /**
     * SPHINCS+ uses a pseudorandom function PRFmsg to generate randomness for
     * the message compression
     * PRFmsg : Bn ×Bn ×B∗→Bn *(Sphincs+ v3 pp 2.7.2)*
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    fun hashPrfMsg(PkPrf: UByteArray, opt: UByteArray, msg: UByteArray): UByteArray

    /**
     * To compress the message to be signed, SPHINCS+ uses an additional keyed hash function
     * Hmsg that can process arbitrary length messages:
     * Hmsg : Bn ×Bn ×Bn ×B∗→Bm *(Sphincs+ v3 pp 2.7.2)*
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    fun hashHMsg(r: UByteArray, publicKey: PublicKey, message: UByteArray): UByteArray
}