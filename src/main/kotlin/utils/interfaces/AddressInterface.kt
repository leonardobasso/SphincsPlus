package org.example.utils.interfaces

@OptIn(ExperimentalUnsignedTypes::class)

/**
 * Interface for the Address class
 *
 * Functions expressed in *paragraph 4.1, table 1 of the SLH-DSA documentation*
 *
 * UInt is used instead of ByteAerray, except for the TreeAsset, because of *Sphics+ v3, 2.7.3*:
 * The structure of an address complies with word borders, with a word being 32 bits long in
 * this context. Only the tree address (i.e. the index of a specific subtree in the main tree) is too
 * long to fit a single word: for this, we reserve three words.
 *
 * @see org.example.utils.classes.Address
 */
interface AddressInterface {
    fun setLayerAddress(layer: UInt)
    fun setTreeAddress(tree: UInt)
    fun setTypeAndClear(type: UInt)
    fun setKeyPairAddress(keyPair: UInt)
    fun setChainAddress(chain: UInt)
    fun setTreeHeight(treeHeight: UInt)
    fun setHashAddress(hashAddress: UInt)
    fun setTreeIndex(treeIndex: UInt)
    fun getKeyPairAddress(): UInt
    fun getTreeIndex(): UInt
}