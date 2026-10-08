package org.example.utils.classes

import org.example.utils.functions.toUByteArray
import org.example.utils.functions.toUInt

/**
 * Implementation of AddressInterface
 *
 * Functions expressed in *paragraph 4.1, table 1 of the SLH-DSA documentation*
 *
 * UInt is used instead of ByteAerray, except for the TreeAsset, because of *Sphics+ v3, 2.7.3*:
 * The structure of an address complies with word borders, with a word being 32 bits long in
 * this context. Only the tree address (i.e. the index of a specific subtree in the main tree) is too
 * long to fit a single word: for this, we reserve three words.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class Address {

    var address = UByteArray(32)

    /**
     * Creates a new address which is a deep copy of the original address
     *
     * @return a clone of the address
     */
    fun copyOf(): Address {
        val clone = Address()
        clone.address = this.address.copyOf()
        return clone
    }
    /**
     * Sets the Layer Address in the Address Data Structure
     *
     * @param layer the layer address
     */
    fun setLayerAddress(layer: UInt) {
        val layerAddress =
            layer.toUByteArray(4) // layer.toByteArray(4) is the Layer Address, made of 4 Bytes, same for all the others toByte
        for (i in 0..3) address[i] = layerAddress[i]
    }

    /**
     * Sets the Tree Addresses in the Address Data Structure
     *
     * @param tree the tree address
     */
    fun setTreeAddress(tree: UInt) {
        val treeAddress = tree.toUByteArray(12)
        for (i in 4..15) address[i] = treeAddress[i - 4]

    }

    /**
     * Sets the Type in the Address Data Structure and clears all the following Bytes.
     *
     * Complies with SPHINCS+ v3 specification (Section 2.7.3, Figure 2).
     *
     * @param type the type of the address, it is:
     * - 0u -> WOTS_HASH
     * - 1u -> WOTS_PK
     * - 2u -> TREE
     * - 3u -> FORS_TREE
     * - 4u -> FORS_ROOTS
     */
    fun setTypeAndClear(type: UInt) {
        require(type <= 4u) { "type parameter should be in a range {0u, 4u} instead is $type" }

        val typeAddress = type.toUByteArray(4)
        for (i in 16..19) address[i] = typeAddress[i - 16]
        for (i in 20 until 32) address[i] = 0.toUByte()
    }

    /**
     * Sets the Key Pair Addresses in the Address Data Structure
     *
     * @param i the key pair address
     */
    fun setKeyPairAddress(i: UInt) {
        val keyPairAddress = i.toUByteArray(4)
        for (i in 20..23) address[i] = keyPairAddress[i - 20]
    }

    /**
     * Sets the Chain Addresses in the Address Data Structure
     *
     * @param chain the chain address
     */
    fun setChainAddress(chain: UInt) {
        val chainAddress = chain.toUByteArray(4)
        for (i in 24..27) address[i] = chainAddress[i - 24]
    }

    /**
     *
     * Sets the Tree's Height in the Address Data Structure
     *
     * @param treeHeight the tree's height
     */
    fun setTreeHeight(treeHeight: UInt) {
        setChainAddress(treeHeight)
    }

    /**
     *
     * Sets the Tree's Height in the Address Data Structure
     *
     * @param treeHeight the tree's height
     */
    fun setTreeHeight(treeHeight: Int) {
        setChainAddress(treeHeight.toUInt())
    }

    /**
     * Sets the Hash Addresses in the Address Data Structure
     *
     * @param hashAddress the hash address
     */
    fun setHashAddress(hashAddress: UInt) {
        val chainAddress = hashAddress.toUByteArray(4)
        for (i in 28..31) address[i] = chainAddress[i - 28]
    }

    /**
     * Sets the Tree's Index in the Address Data Structure
     *
     * @param treeIndex the tree Index
     */
    fun setTreeIndex(treeIndex: UInt) {
        setHashAddress(treeIndex)
    }

    /**
     * Getter function for the KeyPair Address
     * @return the KeyPair Address
     */
    fun getKeyPairAddress(): UInt {
        return address.copyOfRange(20, 24).toUInt()
    }

    /**
     * Getter function for the Merkle's HyperTree Index
     *
     * @return The Tree's index
     */
    fun getTreeIndex(): UInt {
        return address.copyOfRange(28, 32).toUInt()
    }
    /**
     * Getter function for the Merkle's HyperTree Height
     *
     * @return The Tree's height
     */
    fun getTreeHeight(): UInt {
        return address.copyOfRange(24, 28).toUInt()
    }
}