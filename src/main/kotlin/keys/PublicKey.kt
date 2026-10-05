package org.example.keys.interfaces

class PublicKey @OptIn(ExperimentalUnsignedTypes::class) constructor(
    val seed: UByteArray,
    val root: UByteArray
) {
}