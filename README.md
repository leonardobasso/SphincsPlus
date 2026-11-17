# Sphincs+ Implementation in Kotlin

### Notes:

In the [Sphincs+ v3 doc](https://sphincs.org/resources.html) functions are "base" based while in the 
[SLH-DSA doc](https://nvlpubs.nist.gov/nistpubs/fips/nist.fips.205.pdf) they are "log_w" based. 
This implementation uses the "base" approach, thus some functions may have some differences from the pseudocode
of SLH-DSA

### To run tests
``` ./gradlew test```

### To run benchmarks
```./gradlew benchmark```

You can find the results in `SphincsPlus/build/reports/benchmarks/main/...`

_Please, do not use this in production._
