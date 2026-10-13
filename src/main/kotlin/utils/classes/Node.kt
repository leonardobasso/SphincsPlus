package org.example.utils.classes

/**
 * Data class for the Nodes used in the stack of XMSS and FORS
 *
 * @see org.example.xmss.XMSS
 * @see org.example.fors.FORS
 */
@OptIn(ExperimentalUnsignedTypes::class)
class Node(val value: UByteArray, val height: UInt)
