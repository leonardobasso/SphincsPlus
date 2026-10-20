package org.example.sphincs

import org.example.fors.FORS
import org.example.hash.HashInterface
import org.example.hypertree.Hypertree
import org.example.utils.classes.Address
import org.example.utils.classes.PublicKey
import org.example.utils.classes.SecretKey
import org.example.utils.functions.toUInt
import org.example.wots.WOTS
import org.example.xmss.XMSS
import kotlin.random.Random
import kotlin.random.nextUBytes

@OptIn(ExperimentalUnsignedTypes::class)
class Sphincs(
    val n: Int,
    val w: UInt,
    val height: Int,
    val layers: Int,
    val k: Int,
    val a: Int,
    val randomize: Boolean,
    val hashInterface: HashInterface,

    ) {

    private val fors: FORS
    private val hypertree: Hypertree

    init {
        val wots = WOTS(n, w, hashInterface)
        val xmss = XMSS(
            (height / layers), n.toUInt(), w, wots, hashInterface
        )

        this.fors = FORS(k.toUInt(), a, n.toUInt(), hashInterface)
        this.hypertree = Hypertree(height.toUInt(), layers.toUInt(), w, xmss)
    }

    private val hashHMsg = hashInterface::hashHMsg
    private val hashPRFmsg = hashInterface::hashPrfMsg


    /**
     * Extract Digest parameters from the digest
     * @param digest the digest
     *
     * @return a truple <Message Digest, Index Tree, Index Leaf>
     */
    private fun extractDigestFields(digest: UByteArray): Triple<UByteArray, UInt, UInt> {
        val indexTreeBits = height - (height / layers)
        val indexLeafBits = height / layers

        val mdBytes = (k * a + 7) / 8
        val indexTreeEnd = mdBytes + (indexTreeBits + 7) / 8
        val indexLeafEnd = indexTreeEnd + (indexLeafBits + 7) / 8

        val md = digest.copyOfRange(0, mdBytes)
        val indexTree = digest.copyOfRange(mdBytes, indexTreeEnd).toUInt() and ((1u shl indexTreeBits) - 1u)
        val indexLeaf = digest.copyOfRange(indexTreeEnd, indexLeafEnd).toUInt() and ((1u shl indexLeafBits) - 1u)

        return Triple(md, indexTree, indexLeaf)
    }

    /**
     * Signs a Sphincs+ message
     *
     * @param message the message to sign
     * @param secretKey the secret key
     * @param publicKey the public key
     *
     * @return the signature
     * @see SphincsSignature
     */
    fun sign(message: UByteArray, secretKey: SecretKey, publicKey: PublicKey): SphincsSignature {
        val address = Address()

        val opt = if (randomize) {
            Random.nextUBytes(n)
        } else UByteArray(n)

        val r = hashPRFmsg(secretKey.prf, opt, message)

        val digest = hashHMsg(r, publicKey, message)
        val (md, indexTree, indexLeaf) = extractDigestFields(digest)

        address.setLayerAddress(0u)
        address.setTreeAddress(indexTree)
        address.setTypeAndClear(3u)
        address.setKeyPairAddress(indexLeaf)

        val forsSign = fors.sign(md, publicKey.seed, secretKey.seed, address)
        val forsPK = fors.pkFromSign(forsSign, md, publicKey.seed, address)

        address.setTypeAndClear(2u)
        val htSign = hypertree.sign(forsPK, secretKey.seed, publicKey.seed, indexTree, indexLeaf)

        return SphincsSignature(r, forsSign, htSign)
    }

    /**
     * Verifies the authenticity of a firm
     *
     * @param message the signed message
     * @param publicKey the public key
     * @param signature the message's signature
     *
     * @return true if the sign is authentic, false otherwise
     *
     * @see SphincsSignature
     * @see Hypertree
     */
    fun verify(message: UByteArray, publicKey: PublicKey, signature: SphincsSignature): Boolean {
        val address = Address()
        val forsSign = signature.forsSign
        val r = signature.r
        val htSign = signature.htSign

        val digest = hashHMsg(r, publicKey, message)
        val (md, indexTree, indexLeaf) = extractDigestFields(digest)

        address.setLayerAddress(0u)
        address.setTreeAddress(indexTree)
        address.setTypeAndClear(3u)
        address.setKeyPairAddress(indexLeaf)

        val forsPK = fors.pkFromSign(forsSign, md, publicKey.seed, address)

        address.setTypeAndClear(2u)

        return hypertree.verify(forsPK, htSign, publicKey.seed, indexTree, indexLeaf, publicKey.root)
    }
}