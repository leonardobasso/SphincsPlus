package org.example.fors

/**
 * A class representing a FORS element for the Signature
 *
 * A FORS signature is an array of FORS elements
 *
 * @param leaf the leaf representing the Private key value
 * @param auth the authentication path for the leaf
 * @see FORS
 */

@OptIn(ExperimentalUnsignedTypes::class)
class FORSElement(val leaf: UByteArray, val auth: Array<UByteArray>)