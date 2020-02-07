import sys


def run_checks(gen_file, ref_file):
    try:
        with open(gen_file, 'r') as f:
            gen_f = f.read()

        with open(ref_file, 'r') as f:
            ref_f = f.read()

        if gen_f != ref_f:
            print(gen_f == ref_f, '\t', gen_file, ref_file, file=sys.stderr)

    except Exception as e:
        print(e, file=sys.stderr)


base_gen_directory = 'test/input/'


def check_sa(base_file_name):
    base_sa_ref_directory = 'test/sa-ref/'

    gen_sa_file = base_gen_directory + f'{base_file_name}.sa.xml'
    ref_sa_file = base_sa_ref_directory + f'{base_file_name}.sa'

    run_checks(gen_sa_file, ref_sa_file)


def check_ts(base_file_name):
    base_ts_ref_directory = 'test/ts-ref/'

    gen_ts_file = base_gen_directory + f'{base_file_name}.ts'
    ref_ts_file = base_ts_ref_directory + f'{base_file_name}.ts'

    run_checks(gen_ts_file, ref_ts_file)


def main():
    if len(sys.argv) != 3:
        print(f'Usage: python {sys.argv[0]} type_to_compare baseFileName')
        return None

    if sys.argv[1] == 'sa':
        check_sa(sys.argv[2])
    elif sys.argv[1] == 'ts':
        check_ts(sys.argv[2])


if __name__ == '__main__':
    main()
