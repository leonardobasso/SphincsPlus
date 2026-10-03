### Notes:

In the [Sphincs+ v3 doc](https://sphincs.org/resources.html) functions are "base" based while in the 
[SLH-DSA doc](https://nvlpubs.nist.gov/nistpubs/fips/nist.fips.205.pdf) they are "log_w" based. 
This implementation uses the "base" approach, thus some functions may have some differences from the pseudocode
of SLH-DSA