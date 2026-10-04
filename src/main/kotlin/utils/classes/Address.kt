package org.example.utils.classes

import org.example.utils.functions.toByteArray
import org.example.utils.functions.toInt
import org.example.utils.interfaces.AddressInterface

/**
 * Implementation of AddressInterface
 *
 * @see AddressInterface
 */
@OptIn(ExperimentalUnsignedTypes::class)
class Address : AddressInterface {

    var address = UByteArray(32)

    /**
     * Sets the Layer Address in the Address Data Structure
     *
     * @param layer the layer address
     */
    override fun setLayerAddress(layer: UInt) {
        val layerAddress =
            layer.toByteArray(4) // layer.toByteArray(4) is the Layer Address, made of 4 Bytes, same for all the others toByte
        for (i in 0..3) address[i] = layerAddress[i]
    }

    /**
     * Sets the Tree Addresses in the Address Data Structure
     *
     * @param tree the tree address
     */
    override fun setTreeAddress(tree: UInt) {
        val treeAddress = tree.toByteArray(12)
        for (i in 4..15) address[i] = treeAddress[i - 4]

    }

    /**
     * Sets the Tree Addresses in the Address Data Structure and clears all the following Bytes
     *
     * @param type the type of the address, it is *(SHL-SDA 4.2)*:
     * - 1u -> WOTS_PK
     * - 2u -> TREE
     * - 3u -> FORS_TREE
     * - 4u -> FORS_ROOTS
     * - 5u -> WOTS_PRF
     * - 6u -> FORS_PRF
     */
    override fun setTypeAndClear(type: UInt) {
        val treeAddress = type.toByteArray(4)
        for (i in 16..19) address[i] = treeAddress[i - 16]
        for (i in 20 until 32) address[i] = 0.toUByte()
    }
    /**
     * Sets the Key Pair Addresses in the Address Data Structure
     *
     * @param i the key pair address
     */
    override fun setKeyPairAddress(i: UInt) {
        val keyPairAddress = i.toByteArray(4)
        for (i in 20..23) address[i] = keyPairAddress[i - 20]
    }

    /**
     * Sets the Chain Addresses in the Address Data Structure
     *
     * @param chain the chain address
     */
    override fun setChainAddress(chain: UInt) {
        val chainAddress = chain.toByteArray(4)
        for (i in 24..27) address[i] = chainAddress[i-24]
    }
    /**
     *
     * Sets the Tree's Height in the Address Data Structure
     *
     * @param treeHeight the tree's height
     */
    override fun setTreeHeight(treeHeight: UInt) {
        setChainAddress(treeHeight)
    }

    /**
     * Sets the Hash Addresses in the Address Data Structure
     *
     * @param hashAddress the hash address
     */
    override fun setHashAddress(hashAddress: UInt) {
        val chainAddress = hashAddress.toByteArray(4)
        for (i in 28..31) address[i] = chainAddress[i-28]
    }

    /**
     * Sets the Tree's Index in the Address Data Structure
     *
     * @param treeIndex the tree Index
     */
    override fun setTreeIndex(treeIndex: UInt) {
        setHashAddress(treeIndex)
    }

    /**
     * Getter function for the KeyPair Address
     * @return the KeyPair Address
     */
    override fun getKeyPairAddress(): UInt {
        return  address.copyOfRange(20, 24).toInt()
    }

    /**
     * Getter function for the Merkle's HyperTree Index
     *
     * @return The Tree's index
     */
    override fun getTreeIndex(): UInt {
        return  address.copyOfRange(28, 32).toInt()
    }
}