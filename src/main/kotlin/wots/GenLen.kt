package org.example.utils.functions

import kotlin.math.pow

/**
 * Generates len1 parameter for WOTS+ schemes
 *
 * Code: Algorithm 1, section 3.1 from the SLA-DSA doc
 *
 * @param length the length of the message/hash or of its hash
 * @param base the base we want to represent the message or its hash
 *
 * @return len1, the number of base-w digits needed to represent the message or its hash
 */
fun genLen1(length: Int, base: Int): Int {
    val logW = log2W(base)
    return ((8 * length + logW - 1) / logW)
}

/**
 * Generates len2 parameter for WOTS+ schemes without using floating-point arithmetic
 *
 * Code: Algorithm 1, section 3.1 from the SLA-DSA doc
 *
 * @param length the length of the message/hash or of its hash
 * @param base the base we want to represent the message or its hash
 *
 * @return len2, the number of additional chains needed sign the checksum and thus to prevent a falsification of the sign
 */
fun genLen2(length: Int, base: Int): Int {
    val logW = log2W(base)
    val w = 2.0.pow(logW) // Floating point!!!
    val len1 = genLen1(length, logW)
    val maxCheckSum = len1 * (w - 1)

    var len2 = 1
    var capacity = w
    while (capacity <= maxCheckSum) {
        len2++
        capacity *= w
    }

    return len2
}

/**
 * Generates len parameter for WOTS+ schemes
 *
 * Code: Algorithm 1, section 3.1 from the SLA-DSA doc
 *
 * @param length the length of the message/hash or of its hash
 * @param base the base we want to represent the message or its hash

 * @return len, the number of hash chains needed to create a key
 */
fun genLen(length: Int, base: Int): Int {
    return genLen1(length, base) + genLen2(length, base)
}