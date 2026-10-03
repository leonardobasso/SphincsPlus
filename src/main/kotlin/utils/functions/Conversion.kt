package org.example.utils.functions

/**
 * From Sphincs+ Doc [2.4]: For x and y non-negative integers, we define Z= toByte(x,y) to be the y-byte string containing the binary representation of x in big-endian byte-order.
 *
 * @param num the number to convert
 * @param length the length of the ByteArray
 *
 * @return the ByteArray representing the number given in input
 */
@OptIn(ExperimentalUnsignedTypes::class)
fun toByteArray(num: UInt, length: Int): UByteArray {
    var total = num
    val out = UByteArray(length)

    for (i in 0 until length) {
        out[length - 1 - i] = (total % 256u).toUByte()
        total = total shr 8
    }
    return out
}



/**
 * From SLH-DSA [4.4]: a function that converts a byte string 𝑋 of length 𝑛 to an integer
 *
 * @param byteArray The array to convert into an integer
 *
 * @return the integer representing hte ByteArray number
 */
@OptIn(ExperimentalUnsignedTypes::class)
fun toInt(byteArray: UByteArray): UInt {
    var out = 0u
    for (i in byteArray.indices){
        out = (256u * out + byteArray[i])
    }
    return out
}


//-----------------------------------------------------------
@OptIn(ExperimentalUnsignedTypes::class)
fun main() {

    println(toByteArray(1u, 4).contentToString())
    println(toByteArray(555u, 4).contentToString())
    println(toByteArray(9999999u, 5).contentToString())
    println("*toInt:*")
    println(toInt(ubyteArrayOf(1u)))
    println(toInt(ubyteArrayOf(0u, 0u, 2u, 43u)))
    println(toInt(ubyteArrayOf(0u, 152u, 150u, 127u)))

}