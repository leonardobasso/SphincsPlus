#!/usr/bin/env fish

mkdir -p results

set BASE_FILES ref/benchmark.c ref/address.c ref/fors.c ref/merkle.c ref/randombytes.c ref/sign.c ref/utils.c ref/utilsx1.c ref/wots.c ref/wotsx1.c

set HASHES sha2 shake
set SIZES 128 192 256
set OPTS s f
set THASHES simple robust

for hash in $HASHES
    if test "$hash" = "sha2"
        set HASH_FILES ref/hash_sha2.c ref/sha2.c
    else
        set HASH_FILES ref/hash_shake.c ref/fips202.c
    end

    for size in $SIZES
        for opt in $OPTS
            for thash in $THASHES
                set param "sphincs-$hash-$size$opt"
                set thash_file "ref/thash_"$hash"_"$thash".c"
                set out_csv "results/"$param"_"$thash".csv"

                echo "Benchmarking: $param ($thash)"

                clang -O3 -march=native -I. -Iref \
                    -DPARAMS=$param \
                    $BASE_FILES $HASH_FILES $thash_file \
                    -o my_bench

                if test $status -eq 0
                    ./my_bench --output="$out_csv"
                else
                    echo "Error for: $param ($thash)"
                end
            end
        end
    end
end

rm -f my_bench
echo "Endede"
