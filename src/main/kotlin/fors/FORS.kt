package org.example.fors

import org.example.hash.hashF
import org.example.hash.hashH
import org.example.hash.hashPRF
import org.example.hash.hashTl
import org.example.utils.classes.Address
import org.example.utils.classes.Node
import org.example.utils.functions.baseW

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
class FORS(val k: UInt, val a: UInt, val n: UInt) {

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

    /**
     *
     * computes the FORS hypertree
     *
     * @param SKSeed the secret key's seed
     * @param PKSeed the public key's seed
     * @param startIndex the start index, the leaf at index startIndex is a leftmost leaf of a sub-tree of height treeHeight. *(Sphincs+ v3 pp 5.3)*
     * @param treeHeight the height of the Hypertree
     * @param address a FORS address
     *
     * @return the root node of a tree of height treeHeight with the leftmost leaf
     * being the hash of the private key element at index startIndex *(Sphincs+ v3 pp 5.3)
     */
    private fun treehash(
        SKSeed: UByteArray, PKSeed: UByteArray, startIndex: UInt, treeHeight: UInt, address: Address
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
            address.setTreeIndex(startIndex + i)
            while (stack.isNotEmpty() && stack.last().height == address.getTreeHeight()) {
                address.setTreeIndex(((address.getTreeIndex() - 1u) / 2u))
                node = hashH(PKSeed, address, stack.removeLast().value.plus(node))
                address.setTreeHeight(address.getTreeHeight() + 1u)
            }
            stack.add(Node(node, address.getTreeHeight()))
        }
        return stack.removeLast().value

    }

    /**
     * Converts a message to its indices
     *
     * @param message the message to convert
     *
     * @return the indices
     */
    private fun messageToIndices(message: UByteArray): UIntArray {

        var offset = 0
        val out = UIntArray(k.toInt())

        for (i in 0 until k.toInt()) {
            out[i] = 0u
            for (j in 0 until a.toInt()) {
                out[i] = out[i] xor (((message[offset shr 3].toUInt() shr (offset and 0x7)) and 1u) shl j)
                offset++
            }
        }

        return out
    }

    /**
     * Generates the FORS Public Key
     *
     * @param SKSeed the Secret Key's seed
     * @param PKSeed the Public Key's seed
     * @param address a FORS Address
     *
     * @return the Public Key
     */
    fun pkGen(SKSeed: UByteArray, PKSeed: UByteArray, address: Address): UByteArray {

        val pkAddress = address.copyOf()
        val root = Array(k.toInt()) { ubyteArrayOf() }

        for (i in 0u until k) {
            root[i.toInt()] = treehash(SKSeed, PKSeed, i * t, a, address)
        }

        pkAddress.setTypeAndClear(4u)
        pkAddress.setKeyPairAddress(address.getTreeHeight())
        return hashTl(PKSeed, pkAddress, root)
    }

    /**
     * sign function for FORS
     *
     * It contains k private key values, n-bytes each, and their associated authentication paths, log t n-byte values each. *(Sphincs+ v3 pp 5.5)*
     *
     * @param message the message to sign
     * @param PKSeed the private key's seed
     * @param SKSeed the secret key's seed
     * @param address a FORS address
     *
     * @return a mutable list of FORSElement serving as the sign
     * @see FORSElement
     */
    fun sign(
        message: UByteArray, PKSeed: UByteArray, SKSeed: UByteArray, address: Address
    ): MutableList<FORSElement> {

        val index = messageToIndices(message)

        val signature = mutableListOf<FORSElement>()
        val pkAddress = address.copyOf()
        val treeAddress = address.copyOf()

        treeAddress.setTypeAndClear(3u)
        pkAddress.setTypeAndClear(4u)



        for (i in 0 until k.toInt()) {
            val ui = i.toUInt()
            val auth = Array(a.toInt()) { ubyteArrayOf() }

            treeAddress.setTreeHeight(0u)
            treeAddress.setTreeIndex(ui * t + index[i])
            val leaf = hashPRF(SKSeed, treeAddress)

            for (j in 0 until a.toInt()) {
                val s = (index[i] / (1u shl j)) xor 1u
                auth[j] = treehash(
                    SKSeed, PKSeed, ui * t + s * (1u shl j), j.toUInt(), treeAddress
                )
            }
            signature.add(FORSElement(leaf, auth))
        }
        return signature
    }

    fun pkFromSign(
        signature: MutableList<FORSElement>, message: UByteArray, PKSeed: UByteArray, address: Address
    ): UByteArray {

        val index = messageToIndices(message)
        val node = Array(2) { ubyteArrayOf() } // probably not needed an array, see after testing
        val root =Array(k.toInt()) { ubyteArrayOf() }


        for (i in 0 until k.toInt()) {
            val ui = i.toUInt()

            val sk = signature[i].leaf
            address.setTreeHeight(0u)
            address.setTreeIndex(ui * t + index[i])
            node[0] = hashF(PKSeed, address, sk)

            val auth = signature[i].auth
            address.setTreeIndex(ui * t + index[i])

            for (j in 0 until a.toInt()) {
                address.setTreeHeight(j.toUInt() + 1u)
                if ((index[i] / (1u shl j) % 2u) == 0u) {
                    address.setTreeIndex(address.getTreeIndex() / 2u)
                    node[1] = hashH(PKSeed, address, node[0].plus(auth[j]))
                } else {
                    address.setTreeIndex((address.getTreeIndex() -1u ) / 2u)
                    node[1] = hashH(PKSeed, address, auth[j].plus(node[0]))
                }
                node[0] = node[1]
            }
            root[i] = node[1]
        }

        val forsAddress = address.copyOf()
        forsAddress.setTypeAndClear(4u)
        forsAddress.setKeyPairAddress(address.getKeyPairAddress())

        return hashTl(PKSeed, address, root)
    }
}