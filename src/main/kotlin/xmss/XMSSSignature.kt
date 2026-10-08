package org.example.xmss

@OptIn(ExperimentalUnsignedTypes::class)
class XMSSSignature(val signature: Array<UByteArray>, val auth: Array<UByteArray>) {
}