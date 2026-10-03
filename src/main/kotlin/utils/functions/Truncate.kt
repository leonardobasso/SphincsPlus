package org.example.utils.functions

/**Probably a wrong implementation
 *
 *
 * From Sphincs+ Doc [2.5]: Truncℓ(x) : truncates the bit-string x to the first ℓ bits.
 * Works only with bits convertible to bytes
 *
 * @param byteArray The array of bytes to truncate
 * @param length The length in bits
 *
 * @return the truncated ByteArray
 */

@OptIn(ExperimentalUnsignedTypes::class)
fun truncate(byteArray: UByteArray, length: Int): UByteArray {

    require(length / 8 <= byteArray.size) { "lenght exceeds the byteArray size (lenght / 8 > byteArray.size)" }

    return byteArray.copyOfRange(0, length / 8)
}

