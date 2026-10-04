package org.example.utils.functions

/**
 * From Sphincs+ Doc [2.4]: For x and y non-negative integers, we define Z= toByte(x,y) to be the y-byte string containing the binary representation of x in big-endian byte-order.
 *
 * @param length the length of the ByteArray we want to output
 *
 * @return the ByteArray representing the number given in input
 */
@OptIn(ExperimentalUnsignedTypes::class)
fun UInt.toByteArray(length: Int): UByteArray {
    var total = this
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
 * @return the integer representing hte ByteArray number
 */
@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.toInt(): UInt {
    var out = 0u
    for (i in this.indices){
        out = (256u * out + this[i])
    }
    return out
}


//-----------------------------------------------------------
@OptIn(ExperimentalUnsignedTypes::class)
fun main() {

    println(1u.toByteArray( 4).contentToString())
    println(555u.toByteArray(4).contentToString())
    println(9999999u.toByteArray(5).contentToString())
    println("*toInt:*")
    println(ubyteArrayOf(1u).toInt())
    println(ubyteArrayOf(0u, 0u, 2u, 43u).toInt())
    println(ubyteArrayOf(0u, 152u, 150u, 127u).toInt())

}