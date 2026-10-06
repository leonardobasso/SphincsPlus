package org.example.xmss

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
class XMSS(val height: UInt, val lenght: UInt, val w: UInt) {



}