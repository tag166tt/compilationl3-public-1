import sys
import itertools


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


def run_check_pre_nasm(gen_file, ref_file):
    '''Doesn't account for comments or register numbers.'''

    try:
        with open(gen_file, 'r') as f:
            gen_f = f.read()

        with open(ref_file, 'r') as f:
            ref_f = f.read()

        if gen_f == ref_f:
            return True

        lines_gen = gen_f.split('\n')
        lines_ref = ref_f.split('\n')

        if len(lines_gen) != len(lines_ref):
            return False

        # Remove comments
        lines_gen = [l.split(';', 1)[0].strip() for l in lines_gen]
        lines_ref = [l.split(';', 1)[0].strip() for l in lines_ref]

        gen_f = "\n".join(lines_gen)
        ref_f = "\n".join(lines_ref)

        # There are 8 general purpose registers

        # Iterate over all permutations of the registers we want to try
        for comb in itertools.permutations(range(8)):
            # Copy text
            temp = gen_f

            # First rewrite to temp variables (to avoid replacing multiple times without knowing it)
            for a in range(8):
                temp_name = f"$TEMP{a}$"
                temp = temp.replace(f"r{a}", temp_name)

            # Then replace those temp variables with the intended registers
            for a, b in zip(range(8), comb):
                temp_name = f"$TEMP{a}$"
                temp = temp.replace(temp_name, f"r{b}")

            if temp == ref_f:
                return True
    except Exception as e:
        print(e, file=sys.stderr)

    return False


base_gen_directory = 'test/input/'


def check_sa(base_file_name):
    base_sa_ref_directory = 'test/sa-ref/'

    gen_sa_file = f'{base_gen_directory}{base_file_name}.sa.xml'
    ref_sa_file = f'{base_sa_ref_directory}{base_file_name}.sa'

    run_checks(gen_sa_file, ref_sa_file)


def check_ts(base_file_name):
    base_ts_ref_directory = 'test/ts-ref/'

    gen_ts_file = f'{base_gen_directory}{base_file_name}.ts'
    ref_ts_file = f'{base_ts_ref_directory}{base_file_name}.ts'

    run_checks(gen_ts_file, ref_ts_file)


def check_c3a(base_file_name):
    base_c3a_ref_directory = 'test/c3a-ref/'

    gen_c3a_file = f'{base_gen_directory}{base_file_name}.c3a'
    ref_c3a_file = f'{base_c3a_ref_directory}{base_file_name}.c3a'

    run_checks(gen_c3a_file, ref_c3a_file)


def check_pre_nasm(base_file_name):
    base_pre_nasm_ref_directory = 'test/prenasm-ref/'

    gen_pre_nasm_file = f'{base_gen_directory}{base_file_name}.pre-nasm'
    ref_pre_nasm_file = f'{base_pre_nasm_ref_directory}{base_file_name}.pre-nasm'

    if not run_check_pre_nasm(gen_pre_nasm_file, ref_pre_nasm_file):
        print(False, '\t', gen_pre_nasm_file, ref_pre_nasm_file, file=sys.stderr)


def check_nasm(base_file_name):
    base_pre_nasm_ref_directory = 'test/nasm-ref/'

    gen_nasm_file = f'{base_gen_directory}{base_file_name}.nasm'
    ref_nasm_file = f'{base_pre_nasm_ref_directory}{base_file_name}.nasm'

    run_checks(gen_nasm_file, ref_nasm_file)


def main():
    if len(sys.argv) != 3:
        print(f'Usage: python {sys.argv[0]} type_to_compare baseFileName')
        return None

    if sys.argv[1] == 'sa':
        check_sa(sys.argv[2])
    elif sys.argv[1] == 'ts':
        check_ts(sys.argv[2])
    elif sys.argv[1] == 'c3a':
        check_c3a(sys.argv[2])
    elif sys.argv[1] == 'prenasm':
        check_pre_nasm(sys.argv[2])
    elif sys.argv[1] == 'nasm':
        check_nasm(sys.argv[2])
    else:
        print(f'Unsuported mode {sys.argv[1]}', file=sys.stderr)


if __name__ == '__main__':
    main()
