package org.example.utils.classes

/**
 * Data class for the Nodes used in the stack of XMSS
 *
 * @see org.example.xmss.XMSS
 */
@OptIn(ExperimentalUnsignedTypes::class)
class XMSSNode(val value: UByteArray, val height: UInt)
