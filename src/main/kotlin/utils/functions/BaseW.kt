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


//----------------------------------------------------------

@OptIn(ExperimentalUnsignedTypes::class)
fun main() {
    val bs = ubyteArrayOf(49u, 68u, 22u, 6u, 52u, 85u)

    val out1 = baseW(bs, 16u, 12)
    val out2 = baseW(bs, 16u, 4)
    val out3 = baseW(bs, 4u, 4)
    val out4 = baseW(bs, 256u, 4)

    println("out1 (base 16, length 8) is: ${out1.contentToString()}")
    println("out2 (base 16, length 4) is: ${out2.contentToString()}")
    println("out3 (base 4, length 4) is: ${out3.contentToString()}")
    println("out4  (base 256, length 4) is: ${out4.contentToString()}")
}