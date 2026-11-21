import json
import csv
import os
import math

with open("kotlin/benchmark.json", "r", encoding="utf-8") as f:
    kotlin_raw = json.load(f)

kotlin_data = {"keygen": {}, "sign": {}, "verify": {}}
for entry in kotlin_raw:
    op = entry["benchmark"].split(".")[-1]
    h_name = entry["params"]["hashName"]
    var = entry["params"]["variant"]
    score_ms = entry["primaryMetric"]["score"]
    kotlin_data[op].setdefault(h_name, {})[var] = score_ms

c_hash_map = {
    ("sha2", "simple"): "Sha2s",
    ("sha2", "robust"): "Sha2r",
    ("shake", "simple"): "Shake256s",
    ("shake", "robust"): "Shake256r",
}
variants = ["128f", "128s", "192f", "192s", "256f", "256s"]
c_data = {"keygen": {}, "sign": {}, "verify": {}}

for (c_hash, thash), k_hash in c_hash_map.items():
    for var in variants:
        csv_path = f"c/results/sphincs-{c_hash}-{var}_{thash}.csv"
        if not os.path.exists(csv_path):
            continue
        with open(csv_path, "r", encoding="utf-8") as f:
            reader = csv.DictReader(f)
            for row in reader:
                clean_row = {k.strip(): v.strip() for k, v in row.items() if k}

                bench_name = clean_row["name"].split(".")[-1]
                mean_ns = float(clean_row["mean (ns)"])
                mean_ms = mean_ns / 1_000_000.0
                c_data[bench_name].setdefault(k_hash, {})[var] = mean_ms
mean_ms

ops_titles = [("keygen", "Keygen"), ("sign", "Sign"), ("verify", "Verify")]
hash_titles = [
    ("Sha2s", "Sha2 Simple"),
    ("Sha2r", "Sha2 Robust"),
    ("Shake256s", "Shake256 Simple"),
    ("Shake256r", "Shake256 Robust"),
]

def fmt(val):
    return f"[{val:.3f} ms]" if val is not None else "[-]"

for op_key, op_title in ops_titles:
    print(f"*{op_title}*\n")
    for h_key, h_title in hash_titles:
        print(f"{h_title}")
        print("#table(")
        print("  columns: (1fr, 1fr, 1fr, 1fr, 1fr, 1fr, 1fr),")
        print("  table.header([], [*128f*], [*128s*], [*192f*], [*192s*], [*256f*], [*256s*]),")

        k_vals = [fmt(kotlin_data.get(op_key, {}).get(h_key, {}).get(v)) for v in variants]
        c_vals = [fmt(c_data.get(op_key, {}).get(h_key, {}).get(v)) for v in variants]

        print(f"  [*Kotlin*], {', '.join(k_vals)},")
        print(f"  [*C*], {', '.join(c_vals)},")
        print(")\n")
print(f"--------------------------------------------------------------------------------------------------------------------")
languages = {"C": c_data}
baseline = kotlin_data
print("=== Math analysis ===\n")
def geo_mean(ratios):
    return math.exp(sum(math.log(r) for r in ratios) / len(ratios)) if ratios else 0.0

for lang_name, lang_data in languages.items():
    print(f"--- {lang_name} vs Kotlin ---")
    for h_key, h_title in hash_titles:
        all_hash_ratios = []

        for op_key, op_title in ops_titles:
            op_ratios = []
            for v in variants:
                t_lang = lang_data.get(op_key, {}).get(h_key, {}).get(v)
                t_c = baseline.get(op_key, {}).get(h_key, {}).get(v)
                if t_lang and t_c:
                    op_ratios.append(t_lang / t_c)

            all_hash_ratios.extend(op_ratios)
            print(f"  {h_title} + {op_title}: {geo_mean(op_ratios):.2f}x")

        print(f"  >> TOTAL {h_title}: {geo_mean(all_hash_ratios):.2f}x\n")