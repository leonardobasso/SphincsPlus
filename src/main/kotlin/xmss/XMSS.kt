package org.example.xmss

import org.example.hash.hashH
import org.example.utils.classes.Address
import org.example.utils.classes.XMSSNode
import org.example.wots.WOTS
import kotlin.collections.plus

/**
 * Wrapper class for WOTS+ related code
 *
 * @param height the height (number of levels - 1) of the tree. There are 2h′ leaves in the tree.  *(Sphincs v3, p4.1.1)*
 * @param length the length in bytes of messages as well as of each node. *(Sphincs v3, p4.1.1)*
 * @param w the Winternitz parameter as defined for WOTS+ *(Sphincs v3, p4.1.1)*
 *
 * Code from Chapter 5 od SLH-DSA and Chapter 3 of Sphincs v3
 *
 * @see org.example.wots.WOTS
 * @see XMSSNode
 * @see XMSSSignature
 */
@OptIn(ExperimentalUnsignedTypes::class)
class XMSS(val height: Int, val length: UInt, val w: UInt, val wots: WOTS) {

    /**
     * The treehash algorithm returns the root node of a tree
     * of height z with the leftmost leaf being the WOTS+ pk at index s. *(Sphincs+ v3 p4.1.3)*
     *
     * @param SKSeed the secret key's seed
     * @param PKSeed the public key's seed
     * @param index the leaf's starting index
     * @param targetHeight the height of the tree
     * @param address the XMSS Address
     *
     * @return the value of the radix of the tree
     */
    fun treehash(SKSeed: UByteArray, PKSeed: UByteArray, index: UInt, targetHeight: Int, address: Address): UByteArray {

        val iter = 1u shl targetHeight
        require(index % iter == 0u) { "index % (1u shl targetHeight) is ${index % iter}, it should be 0" }

        val stack = mutableListOf<XMSSNode>()

        for (i in 0u until iter) {
            address.setTypeAndClear(0u)
            address.setKeyPairAddress(index + i)

            var node = wots.pkGen(SKSeed, PKSeed, address)

            address.setTypeAndClear(2u)
            address.setTreeHeight(1u)
            address.setTreeIndex(index + i)

            while (stack.isNotEmpty() && stack.last().height == address.getTreeHeight()) {
                address.setTreeIndex((address.getTreeIndex() - 1u) / 2u)
                node = hashH(PKSeed, address, (stack.removeLast().value.plus(node)))
                address.setTreeHeight(address.getTreeHeight() + 1u)
            }
            stack.add(XMSSNode(node, address.getTreeHeight()))
        }
        return stack.removeLast().value
    }

    /**
     * Function to generate the XMSS public key.
     * the XMSS public key PK is the root of the binary hash tree, The root is
     * computed using treehash(...) *(Sphincs+ v3 p4.1.3)*
     *
     * @param SKSeed the secret key's seed
     * @param PKSeed the public key's seed
     * @param address the XMSS Address
     *
     * @return the XMSS public key
     */
    fun pkGen(SKSeed: UByteArray, PKSeed: UByteArray, address: Address): UByteArray {
        return treehash(SKSeed, PKSeed, 0u, height, address)
    }

    /**
     * Function to sign a message using XMSS
     *
     * An XMSS signature is a ((len + h′) ∗n)-byte string consisting of
     *
     * • a WOTS+ signature sig taking len·n bytes,
     *
     * • the authentication path AUTH for the leaf associated with the used WOTS+ key pair taking h′·n bytes. *(Sphincs+ v3 pp 4.1.5)*
     *
     * @param message the message to sign
     * @param SKSeed the secret key's seed
     * @param PKSeed the public key's seed
     * @param index the leaf's starting index
     * @param address a XMSS address
     *
     * @return the XMSS signature
     */
    fun sign(
        message: UByteArray, SKSeed: UByteArray, PKSeed: UByteArray, index: UInt, address: Address
    ): XMSSSignature {
        val auth = Array(height) { ubyteArrayOf() }
        for (i in 0 until height) {
            val k = (index / (1u shl i)) xor 1u
            auth[i] = treehash(SKSeed, PKSeed, k * (1u shl i), i, address)
        }
        address.setTypeAndClear(0u)
        address.setKeyPairAddress(index)
        val wotsSign = wots.sign(message, SKSeed, PKSeed, address)

        return XMSSSignature(wotsSign, auth)
    }

    /**
     * Computing an XMSS public key from an XMSS signature. *(Sphincs 4.1.7)*
     *
     * @param index the leaf's starting index
     * @param xmssSign the SMSS signature used to derive the public key
     * @param message the message signed by the XMSS key
     * @param PKSeed the public key's seed
     * @param address a XMSS address
     *
     * @return the public key
     */
    fun pkFromSign(
        index: UInt, xmssSign: XMSSSignature, message: UByteArray, PKSeed: UByteArray, address: Address
    ): UByteArray {

        val node = Array(2) { ubyteArrayOf() }

        address.setTypeAndClear(0u)
        address.setKeyPairAddress(index)
        val sign = xmssSign.signature
        val auth = xmssSign.auth
        node[0] = wots.pkFromSign(sign, message, PKSeed, address)

        address.setTypeAndClear(2u)
        address.setTreeIndex(index)

        for (i in 0 until height) {
            address.setTreeHeight(i + 1)

            if ((index / (1u shl i) % 2u) == 0u) {
                address.setTreeHeight(address.getTreeHeight() / 2u)
                node[1] = hashH(PKSeed, address, node[0].plus(auth[i]))
            } else {
                address.setTreeHeight((address.getTreeHeight() - 1u) / 2u)
                node[1] = hashH(PKSeed, address, auth[i].plus(node[0]))
            }
            node[0] = node[1]
        }
        return node[0]
    }
}