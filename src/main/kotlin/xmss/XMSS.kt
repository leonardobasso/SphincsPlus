package org.example.xmss

import org.example.hash.hashH
import org.example.utils.classes.Address
import org.example.utils.classes.XMSSNode
import org.example.wots.WOTS

/**
 * Wrapper class for WOTS+ related code
 *
 * @param height the height (number of levels - 1) of the tree. There are 2h′ leaves in the tree.  *(Sphincs v3, p4.1.1)*
 * @param lenght the length in bytes of messages as well as of each node. *(Sphincs v3, p4.1.1)*
 * @param w the Winternitz parameter as defined for WOTS+ *(Sphincs v3, p4.1.1)*
 *
 * Code from Chapter 5 od SLH-DSA and Chapter 3 of Sphincs v3
 *
 * @see org.example.wots.WOTS
 */
@OptIn(ExperimentalUnsignedTypes::class)
class XMSS(val height: UInt, val lenght: UInt, val w: UInt, val wots: WOTS) {

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
    fun treehash(SKSeed: UByteArray, PKSeed: UByteArray, index: UInt, targetHeight: UInt, address: Address): UByteArray {

        val iter = 1u shl targetHeight.toInt()
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
                address.setTreeIndex((address.getTreeIndex() -1u) / 2u)
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
     * computed using treehash(..) *(Sphincs+ v3 p4.1.3)*
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
}