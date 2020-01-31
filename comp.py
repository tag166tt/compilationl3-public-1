import os
base = [os.path.splitext(os.path.basename(f))[0] for f in os.listdir('test/input') if f.endswith('.sa')]

files = [(f'test/input/{f}.sa', f'test/sa-ref/{f}.sa') for f in base]

err = 0

for gen, ref in files:
  try:
    with open(gen, 'r') as f:
      gen_f = f.read()

    with open(ref, 'r') as f:
      ref_f = f.read()

    if gen_f != ref_f:
      print(gen_f == ref_f, '\t', gen, ref)
      err += 1
  except Exception as e:
    print(e)

print(f'errored: {err}/{len(files)}')
