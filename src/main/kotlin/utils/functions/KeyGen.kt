package org.example.utils.functions

import org.example.hypertree.Hypertree
import org.example.utils.classes.PublicKey
import org.example.utils.classes.SecretKey
import java.security.SecureRandom

/**
 * Genetates a pair of Public-Private key
 *
 * @param bytes the length of the seed and prf of the keys
 * @param hypertree an instance of an Hypertree
 *
 * @return an initialized pair <PublicKey, SecretKey>
 *
 * @see PublicKey
 * @see SecretKey
 * @see Hypertree
 */
@OptIn(ExperimentalUnsignedTypes::class)
fun keyGen(bytes: Int, hypertree: Hypertree): Pair<PublicKey, SecretKey> {

    val sr = SecureRandom()

    val skSeed = ByteArray(bytes)
    val skPrf = ByteArray(bytes)

    val pkSeed = ByteArray(bytes)

    sr.nextBytes(skSeed)
    sr.nextBytes(skPrf)
    sr.nextBytes(pkSeed)

    val uSkSeed = skSeed.toUByteArray()
    val uSkPrf = skPrf.toUByteArray()
    val uPkSeed = pkSeed.toUByteArray()

    val uPkRoot = hypertree.pkGen(uSkSeed, uPkSeed)

    return Pair(PublicKey(uPkSeed, uPkRoot), SecretKey(uSkSeed, uSkPrf))

}