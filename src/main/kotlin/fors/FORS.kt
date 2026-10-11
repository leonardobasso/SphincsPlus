package org.example.fors

/**
 * FORS [..] is a few-time signature scheme
 *
 * the input to FORS is the output of a hash function
 *
 * @param n the length of a private key, public key, or signature element in bytes
 * @param k the number of private key sets, trees and indices computed from the input string.
 * @param a t = 2^a is the number of elements per private key set, number of leaves per hash tree and upper bound on the index values.
 *
 * @see org.example.hypertree.Hypertree
 */
class FORS(k: UInt, a: UInt, n: UInt) {

    val t = 1u shl a.toInt()
    val stringLenght = k * t



}