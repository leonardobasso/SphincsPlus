package org.example.hypertree

import org.example.utils.classes.Address
import org.example.xmss.XMSS
import org.example.xmss.XMSSSignature

/**
 * The SPHINCS+ hypertree HT is a variant of XMSSMT. It is essentially a certification tree
 * of XMSS instances. A HT is a tree of several layers of XMSS trees. The trees on top and
 * intermediate layers are used to sign the public keys, i.e., the root nodes, of the XMSS trees on
 * the respective next layer below. Trees on the lowest layer are used to sign the actual messages,
 * which are FORS public keys in SPHINCS+. All XMSS trees in HT have equal height. *(Sphincs+ v3 pp 4.2*
 *
 * @param height the height of the Hypertree
 * @param layers the layers of the hypertree (the number of trees in the Hypertree)
 * @param w the Witnizer parameter
 * @param xmss an instance of an XMSS scheme
 *
 * @see org.example.xmss.XMSS
 */
@OptIn(ExperimentalUnsignedTypes::class)
class Hypertree(val height: UInt, val layers: UInt, w: UInt, val xmss: XMSS) {

    val singleHeight = height / layers

    init {
        require(w == 4u || w == 16u || w == 256u) { "base must be 4, 16 or 256, instead is $w" }
    }

    /**
     * Generates a public key for the Hypertree
     *
     * @param SKSeed the secret key's seed
     * @param PKSeed the public key's seed
     *
     * @return the Hypertree's Public Key
     */
    fun pkGen(SKSeed: UByteArray, PKSeed: UByteArray): UByteArray {
        val address = Address()
        address.setLayerAddress(layers - 1u)
        address.setTreeAddress(0u)
        return xmss.pkGen(SKSeed, PKSeed, address)
    }

    /**
     * An HT signature SIGHT is a byte string of length (h+ d∗len) ∗n. It consists of d XMSS
     * signatures (of (h/d+ len) ∗n bytes each). *(Sphincs v3 p 4.2.3)*
     *
     * @param message the message to sign
     * @param SKSeed the secret key's seed
     * @param PKSeed the public key's seed
     * @param indexTree the memory address of the hypertree
     * @param indexLeaf the memory address of the hypertree's leaf
     *
     * @return the HyperTree's signature, an array of XMSS signatures
     */
    fun sign(
        message: UByteArray, SKSeed: UByteArray, PKSeed: UByteArray, indexTree: UInt, indexLeaf: UInt
    ): MutableList<XMSSSignature> {

        val sign = mutableListOf<XMSSSignature>()

        val address = Address()
        address.setLayerAddress(0u)
        address.setTreeAddress(indexTree)

        var tempSign = xmss.sign(message, SKSeed, PKSeed, indexLeaf, address)
        sign.addLast(tempSign)

        var root = xmss.pkFromSign(indexLeaf, tempSign, message, PKSeed, address)

        var currentIndexLeaf: UInt
        var currentIndexTree: UInt

        for (i in 1u until layers) {
            //TODO finish this implementation, currentindex... are wrongly implemented, I have to understand what is ment by tge pseudocode text
            currentIndexLeaf = height / layers // idx_leaf = (h / d) least significant bits of idx_tree;
            currentIndexTree = height - (i + 1u) * height / layers // idx_tree = (h - (j + 1) * (h / d)) most significant bits of idx_tree;

            address.setLayerAddress(i)
            address.setTreeAddress(currentIndexTree)
            tempSign = xmss.sign(root, SKSeed, PKSeed, currentIndexLeaf, address)
            sign.addLast(tempSign)

            if (i < layers - 1u) {
                root = xmss.pkFromSign(currentIndexLeaf, tempSign, root, PKSeed, address)
            }
        }
        return sign
    }

    /**
     * verifies if a message was signed by a HyperTree signature recreating the public key
     *
     * @param message the signed message
     * @param sign the HyperTree's signature
     * @param PKSeed the public key's seed
     * @param indexTree the memory address of the Hypertree
     * @param indexLeaf the memory address of the leaf
     * @param PKHyperTree the HyperTree's public key
     *
     * @return true if the message was signed using the given signature, false otherwise
     */
    fun verify(
        message: UByteArray,
        sign: MutableList<XMSSSignature>,
        PKSeed: UByteArray,
        indexTree: UInt,
        indexLeaf: UInt,
        PKHyperTree: UByteArray
    ): Boolean {
        val address = Address()

        address.setLayerAddress(0u)
        address.setTreeAddress(indexTree)
        var node = xmss.pkFromSign(
            indexLeaf,
            sign[0], // sign[i] is SIG_tmp in the pseudocode on the doc
            message,
            PKSeed,
            address
        )

        for (i in 1u until layers) {
            val tempIndexLeaf = height / layers
            val tempIndexTree = height - (i + 1u) * (height / layers)
            address.setLayerAddress(i)
            address.setTreeAddress(tempIndexTree)
            node = xmss.pkFromSign(tempIndexLeaf, sign[i.toInt()], node, PKSeed, address)
        }


        return if (node.contentEquals(PKHyperTree)) {
            true
        } else {
            false
        }
    }

}