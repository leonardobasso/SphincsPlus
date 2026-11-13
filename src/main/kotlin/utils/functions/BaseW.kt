package org.example.utils.functions

/**
 * From Sphincs+ Doc [2.5]: A byte string can be considered as a string of base "w" numbers
 *
 * Given a ByteArray, the function returns an array of base "w" integers (w=4 -> i=0,1,2,3)
 *
 * @param byteArray ByteArray to convert into an IntArray
 * @param base element of the set {2,4,8}
 * @param length output IntArray length, must be <= (8 * byteArray.size) / log2(base)
 *
 * @return an array of integers
 */
@OptIn(ExperimentalUnsignedTypes::class)
fun baseW(byteArray: UByteArray, base: UInt, length: Int): UIntArray {

    val log = log2W(base)

    require(length <= (8 * byteArray.size) / log) { "length must to be <= ${8 * byteArray.size / log}, instead is $length" }

    var input = 0
    var total = 0u
    var bits = 0
    val outputArray = UIntArray(length)

    for (out in 0 until length) {
        if (bits == 0) {
            total = byteArray[input].toUInt()
            input++
            bits += 8
        }
        bits -= log
        outputArray[out] = (total shr bits) and (base - 1u)
    }
    return outputArray
}