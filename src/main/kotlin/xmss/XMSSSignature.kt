package org.example.xmss

/**
 * A class representing an XMSS Signature
 *
 * @param signature the signature as an array of bytestrings
 * @param auth the authentication path for the leaf associated with the used WOTS+ key pair taking h·n bytes. *(Sphincs+ v3 pp 4.1.5)*
 *
 * @see XMSS
 */
@OptIn(ExperimentalUnsignedTypes::class)
class XMSSSignature(val signature: Array<UByteArray>, val auth: Array<UByteArray>) {
}