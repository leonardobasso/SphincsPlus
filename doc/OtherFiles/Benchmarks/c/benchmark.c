#include "api.h"
#include "params.h"
#include "randombytes.h"
#include "ubench.h"

#define MLEN 32

struct state {
  unsigned char pk[SPX_PK_BYTES];
  unsigned char sk[SPX_SK_BYTES];
  unsigned char m[MLEN];
  unsigned char sm[SPX_BYTES + MLEN];
  unsigned char mout[SPX_BYTES + MLEN];
  unsigned long long smlen;
  unsigned long long mlen;
};

UBENCH_F_SETUP(state) {
  randombytes(ubench_fixture->m, MLEN);
  crypto_sign_keypair(ubench_fixture->pk, ubench_fixture->sk);
  crypto_sign(ubench_fixture->sm, &ubench_fixture->smlen, ubench_fixture->m, MLEN, ubench_fixture->sk);
}

UBENCH_F_TEARDOWN(state) {
  (void)ubench_fixture;
}

UBENCH_EX(sphincs, keygen) {
  unsigned char pk[SPX_PK_BYTES], sk[SPX_SK_BYTES];

  UBENCH_DO_BENCHMARK() {
    crypto_sign_keypair(pk, sk);
  }

  UBENCH_DO_NOTHING(pk);
  UBENCH_DO_NOTHING(sk);
}

UBENCH_EX_F(state, sign) {
  UBENCH_DO_BENCHMARK() {
    crypto_sign(ubench_fixture->sm, &ubench_fixture->smlen, ubench_fixture->m, MLEN, ubench_fixture->sk);
  }

  UBENCH_DO_NOTHING(ubench_fixture->sm);
}

UBENCH_EX_F(state, verify) {
  UBENCH_DO_BENCHMARK() {
    crypto_sign_open(ubench_fixture->mout, &ubench_fixture->mlen, ubench_fixture->sm, ubench_fixture->smlen, ubench_fixture->pk);
  }

  UBENCH_DO_NOTHING(ubench_fixture->mout);
}

UBENCH_MAIN();
