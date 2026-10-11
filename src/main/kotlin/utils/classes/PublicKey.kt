package org.example.utils.classes

class PublicKey @OptIn(ExperimentalUnsignedTypes::class) constructor(
    val seed: UByteArray,
    val root: UByteArray
) {
}