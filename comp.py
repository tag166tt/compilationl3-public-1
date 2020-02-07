import sys


def main():
    base_gen_directory = 'test/input/'
    base_sa_ref_directory = 'test/sa-ref/'
    base_ts_ref_directory = 'test/ts-ref/'

    if len(sys.argv) != 2:
        print('Usage: python comp.py baseFileName')
        return None

    base_file_name = sys.argv[1]

    gen_sa_file = base_gen_directory + f'{base_file_name}.sa.xml'
    ref_sa_file = base_sa_ref_directory + f'{base_file_name}.sa'
    gen_ts_file = base_gen_directory + f'{base_file_name}.ts'
    ref_ts_file = base_ts_ref_directory + f'{base_file_name}.ts'

    try:
        with open(gen_sa_file, 'r') as f:
            gen_sa_f = f.read()

        with open(ref_sa_file, 'r') as f:
            ref_sa_f = f.read()

        with open(gen_ts_file, 'r') as f:
            gen_ts_f = f.read()

        with open(ref_ts_file, 'r') as f:
            ref_ts_f = f.read()

        if gen_sa_f != ref_sa_f:
            print(gen_sa_f == ref_sa_f, '\t', gen_sa_file, ref_sa_file, file=sys.stderr)

        if gen_ts_f != ref_ts_f:
            print(gen_ts_f == ref_ts_f, '\t', gen_ts_file, ref_ts_file, file=sys.stderr)
    except Exception as e:
        print(e, file=sys.stderr)


if __name__ == '__main__':
    main()
