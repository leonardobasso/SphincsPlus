package org.example.hash

import org.example.utils.classes.Address

/**
 * F(PK.seed, ADRS, 𝑀1) (𝔹𝑛 × 𝔹32 × 𝔹𝑛 → 𝔹𝑛) is a hash function that takes an 𝑛-byte
 * message as input and produces an 𝑛-byte output. *(SLH-DSA 4.1)*
 */
@OptIn(ExperimentalUnsignedTypes::class)
fun hashF(PKSeed: UByteArray, address: Address, msg: UByteArray): UByteArray {
    return ubyteArrayOf()
}

/**
 * SPHINCS+ makes use of a pseudorandom function PRF for pseudorandom key generation:
 * PRF : Bn ×B32 →Bn
 *  *Sphincs+ 2.7.2)*
 */
@OptIn(ExperimentalUnsignedTypes::class)
fun hashPRF(SKSeed: UByteArray, address: Address): UByteArray {
    return ubyteArrayOf()
}

/**
 * Tℓ(PK.seed, ADRS, 𝑀ℓ) (𝔹𝑛 × 𝔹32 × 𝔹ℓ𝑛 → 𝔹𝑛) is a hash function that maps an
 * ℓ𝑛-byte message to an 𝑛-byte message.
 *
 * Per n si intende  +n di Witnizer??
 */
@OptIn(ExperimentalUnsignedTypes::class)
fun hashTl(PKSeed: UByteArray, address: Address, msg: Array<UByteArray>): UByteArray {
    return ubyteArrayOf()

}

@OptIn(ExperimentalUnsignedTypes::class)
fun hashH(PKSeed: UByteArray, asddress: Address, msg: UByteArray): UByteArray {
    return ubyteArrayOf()
}