package org.example.utils.functions

/**
 * Given a base of 4, 16 or 256, it returns its log2 value
 *
 * @param base the base, it must be an element of the set {4, 16, 256} [Sphincs+ v3, paragraph 2.5]
 *
 * @return log2(base) in O(1)
 */
fun log2W(base: Int): Int {
    require(base == 4 || base == 16 || base == 256) { "base must be 4, 16 or 256, instead is $base" }

    return when (base) {
        4 -> 2
        16 -> 4
        256 -> 8
        else -> throw IllegalArgumentException("base must be 4, 16 or 256, instead is $base")
    }
}

/**
 * Given a base of 4, 16 or 256, it returns its log2 value
 *
 * @param base the base, it must be an element of the set {4u, 16u, 256u} [Sphincs+ v3, paragraph 2.5]
 *
 * @return log2(base) in O(1)
 */
fun log2W(base: UInt): Int {
    require(base == 4u || base == 16u || base == 256u) { "base must be 4, 16 or 256, instead is $base" }

    return when (base) {
        4u -> 2
        16u -> 4
        256u -> 8
        else -> throw IllegalArgumentException("base must be 4, 16 or 256, instead is $base")
    }
}