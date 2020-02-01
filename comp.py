import sys


def main():
    base_gen_directory = 'test/input/'
    base_sa_ref_directory = 'test/sa-ref/'

    if len(sys.argv) != 2:
        print('Usage: python comp.py baseFileName')
        return None

    base_file_name = sys.argv[1]

    gen_file = base_gen_directory + f'{base_file_name}.sa'
    ref_file = base_sa_ref_directory + f'{base_file_name}.sa'

    try:
        with open(gen_file, 'r') as f:
            gen_f = f.read()

        with open(ref_file, 'r') as f:
            ref_f = f.read()

        if gen_f != ref_f:
            print(gen_f == ref_f, '\t', gen_file, ref_file, file=sys.stderr)
    except Exception as e:
        print(e, file=sys.stderr)


if __name__ == '__main__':
    main()
