package org.example.fors

import org.example.hash.hashF
import org.example.hash.hashH
import org.example.hash.hashPRF
import org.example.utils.classes.Address
import org.example.utils.classes.Node
import kotlin.require

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
@OptIn(ExperimentalUnsignedTypes::class)
class FORS(k: UInt, a: UInt, n: UInt) {

    val t = 1u shl a.toInt()
    val stringLenght = k * t

    /**
     * Generates the Secret Key for FORS
     *
     * a FORS private key is the single private seed SK.seed contained
     * in the SPHINCS+ private key *(Sphincs+ v3 pp 5.2)*
     *
     * @param SKSeed the secret key's seed
     * @param address a FORS Address
     * @param index the index of the Hypertree
     *
     * @return the secret key
     */
    fun skGen(SKSeed: UByteArray, address: Address, index: UInt): UByteArray {

        address.setTreeHeight(0u)
        address.setTreeIndex(index)
        return hashPRF(SKSeed, address)
    }

    private fun treehash(
        SKSeed: UByteArray,
        PKSeed: UByteArray,
        startIndex: UInt,
        treeHeight: UInt,
        address: Address
    ): UByteArray {

        val h = 1u shl treeHeight.toInt()

        require((startIndex % h) == 0u) { "startIndex % 2^treeHeight must be equal to 0" }

        val stack = ArrayDeque<Node>()

        for (i in 0u until h) {
            address.setTreeHeight(0u)
            address.setTreeIndex(startIndex + i)
            val sk = hashPRF(SKSeed, address)
            var node = hashF(PKSeed, address, sk)
            address.setTreeHeight(1u)
            address.setTreeIndex(startIndex + i) //repetition??
            while (stack.isNotEmpty() && stack.last().height == address.getTreeHeight()) {
                address.setTreeIndex(((address.getTreeIndex() - 1u) / 2u))
                node = hashH(PKSeed, address, stack.removeLast().value.plus(node))  // MODIFY
                address.setTreeHeight(address.getTreeHeight() + 1u)
            }
            stack.add(Node(node, address.getTreeHeight()))
        }
        return stack.removeLast().value

    }
}