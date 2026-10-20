package org.example.sphincs

import org.example.fors.FORSElement
import org.example.xmss.XMSSSignature

@OptIn(ExperimentalUnsignedTypes::class)
class SphincsSignature(
    val r: UByteArray, val forsSign: MutableList<FORSElement>, val htSign: MutableList<XMSSSignature>
)