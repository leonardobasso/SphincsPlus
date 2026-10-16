package org.example.utils.classes

/**
 * Secret Key class
 *
 * @param seed the Secret Key's seed
 * @param prf a pseudorandom secret used to generate the message digest
 *
 * @see org.example.utils.functions.keyGen
 */
@OptIn(ExperimentalUnsignedTypes::class)
class SecretKey(
    val seed: UByteArray, val prf: UByteArray
) {

}