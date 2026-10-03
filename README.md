### Basic Data Types:
[`Byte`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-byte/), 
[`ByteString`](https://kotlinlang.org/api/kotlinx-io/kotlinx-io-bytestring/kotlinx.io.bytestring/-byte-string/)

### Functions implemented in the language
[`ceil(x:Double): Double`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.math/ceil.html),
[`floor(x:Double): Double`]([https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.math/ceil.html](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.math/floor.html)),
[`log(x: Double): Double`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.math/log2.html)
[`x.pow(y: Double)`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.math/pow.html)
[`a shl b (shift left)`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/shl.html)
[`a shr b (shift right)`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/shr.html)
`Standard ops: +, -, *, ++, or, and, xor`

### Functions to implement
`trunc(ℓ: ByteArray Int, x: )`: *truncates the bit-string x to the first ℓ bits.*,

`base_w(X, w, out_len)`: *Let X be a len_X- byte string, and w is an element of the set {4,16,256}, then base_w(X,w,out_len)
outputs an array of out_len integers between 0 and w−1. The length out_len is
REQUIRED to be less than or equal to 8 ∗len_X/log(w).*

`hash function `: SHA-256 or SHAKE-128

`PRF(msg) : B^n × B^n × B^∗ → B^n`: pseudorandom function PRFmsg to generate randomness for
the message compression 

`PRF: B^n × B^32 → B^n`: pseudorandom key generation

`H(msg) : B^n × B^n × B^n × B^∗ → B^m`: Compression function for the message

`Tweakable Hash Function`: I have yet to understand what actually means, aren't all hash functions tweakable?

Performance oriented concatenation can be implemented with [`x.copyInto(y: ByteArray, offset: Int)`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/copy-into.html) instead of using +