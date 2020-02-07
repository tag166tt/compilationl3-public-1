import os
import subprocess
import sys

classpath = ""


################################################################################
def compile_compiler():
    print("Compiling Compiler.java...", end="", file=sys.stderr)
    return_code = subprocess.Popen("cd ../src/ && javac Compiler.java", shell=True, stdout=subprocess.PIPE,
                                   stderr=subprocess.PIPE).wait()
    if return_code == 0:
        print("Done", file=sys.stderr)
    else:
        print("ERROR !", file=sys.stderr)
    print("", file=sys.stderr)


################################################################################

################################################################################
def delete_classes():
    for root, subdirs, files in os.walk(".."):
        if ".git" in root:
            continue
        for filename in files:
            if os.path.splitext(filename)[1] == ".class":
                os.remove(root + "/" + filename)

    return classpath


################################################################################

################################################################################
def find_classpath():
    global classpath

    if len(classpath) > 0:
        return classpath

    for root, subdirs, files in os.walk(".."):
        if ".git" in root:
            continue
        for filename in files:
            if os.path.splitext(filename)[1] == ".class":
                classpath += ("" if len(classpath) == 0 else ":") + root
                break

    return classpath


################################################################################

################################################################################
def compiler():
    return "java -classpath %s Compiler" % find_classpath()


################################################################################

################################################################################
def green(string):
    return "\033[92m%s\033[0m" % string


################################################################################

################################################################################
def purple(string):
    return "\033[95m%s\033[0m" % string


################################################################################

################################################################################
def red(string):
    return "\033[91m%s\033[0m" % string


################################################################################

################################################################################
def change_extension(filename, new_extension):
    return os.path.splitext(filename)[0] + new_extension


################################################################################

################################################################################
def find_input_files():
    input_files = []
    for filename in os.listdir('input'):
        if os.path.splitext(filename)[1] == ".l":
            input_files.append(filename)
    return input_files


################################################################################

################################################################################
def delete_compilation_outputs():
    output_extensions = [".sa", ".sc", ".ts", ".nasm", ".pre-nasm", ".c3a", ".fg", ".fgs", ".ig"]
    for filename in os.listdir('input'):
        if os.path.splitext(filename)[1] in output_extensions:
            os.remove("input/" + filename)


################################################################################

################################################################################
def compile_input_files(input_files):
    for inputFile in input_files:
        print("Compiling %s..." % inputFile, end="", file=sys.stderr)
        return_code = subprocess.Popen("{} input/{}".format(compiler(), inputFile), shell=True, stdout=subprocess.PIPE,
                                       stderr=subprocess.PIPE).wait()
        if return_code == 0:
            print("Done", file=sys.stderr)
        else:
            print("ERROR !", file=sys.stderr)
    print("", file=sys.stderr)


################################################################################

################################################################################
def get_new_evaluation_result(name):
    return [name, {"correct": [], "incorrect": [], "notfound": []}]


################################################################################

################################################################################
def evaluate_sa(input_files):
    evaluation = get_new_evaluation_result("Syntaxe Abstraite")
    compare_arbres = "compare_arbres/compare_arbres_xml"
    if not os.path.isfile(compare_arbres):
        print("Executable non trouvé : %s (il faut le compiler)" % compare_arbres, file=sys.stderr)
        exit(1)

    for filename in input_files:
        sa_filename = change_extension(filename, ".sa")
        if not os.path.isfile("input/" + sa_filename):
            evaluation[1]["notfound"].append(sa_filename)
            continue

        sa_ref = "sa-ref/" + sa_filename
        if not os.path.isfile(sa_ref):
            print("Fichier non trouvé : %s" % sa_ref, file=sys.stderr)
            exit(1)

        res = subprocess.Popen("{} {} input/{}".format(compare_arbres, sa_ref, sa_filename), shell=True,
                               stdout=open(os.devnull, "w"), stderr=subprocess.PIPE).stderr.read()
        if "egaux" in str(res):
            evaluation[1]["correct"].append(sa_filename)
        else:
            evaluation[1]["incorrect"].append(sa_filename)

    return evaluation


################################################################################

################################################################################
def evaluate_diff(input_files, extension, path, name):
    evaluation = get_new_evaluation_result(name)

    for filename in input_files:
        produced_file = change_extension(filename, extension)
        if not os.path.isfile("input/" + produced_file):
            evaluation[1]["notfound"].append(produced_file)
            continue

        ref = path + produced_file
        if not os.path.isfile(ref):
            print("Fichier non trouvé : %s" % ref, file=sys.stderr)
            exit(1)

        res = subprocess.Popen("diff {} input/{}".format(ref, produced_file), shell=True, stdout=subprocess.PIPE,
                               stderr=subprocess.PIPE).stdout.read()
        if len(res.strip()) == 0:
            evaluation[1]["correct"].append(produced_file)
        else:
            evaluation[1]["incorrect"].append(produced_file)

    return evaluation


################################################################################

################################################################################
def print_list_elements(destination, elements, color_function, use_color, result_str):
    if len(elements) == 0:
        return
    max_column_size = len(max(elements, key=len))
    for filename in elements:
        if use_color:
            print("\t{}".format(color_function(filename)), file=destination)
        else:
            print("\t{:{}} {}".format(filename, max_column_size + 2, result_str), file=destination)


################################################################################

################################################################################
def print_evaluation_result(destination, evaluation_result, use_color):
    name = evaluation_result[0]
    correct = evaluation_result[1]["correct"]
    incorrect = evaluation_result[1]["incorrect"]
    notfound = evaluation_result[1]["notfound"]

    nb_correct = len(correct)
    nb_total = len(correct) + len(incorrect) + len(notfound)

    print("Évaluation de %s :" % name, file=destination)
    print("{}/{} correct ({:6.2f}%)".format(nb_correct, nb_total, 100.0 * nb_correct / nb_total), file=destination)
    print_list_elements(destination, correct, green, use_color, "CORRECT")
    print_list_elements(destination, incorrect, purple, use_color, "INCORRECT")
    print_list_elements(destination, notfound, red, use_color, "NON-EXISTANT")


################################################################################

################################################################################
if __name__ == "__main__":

    inputFiles = find_input_files()
    delete_compilation_outputs()

    compile_compiler()
    compile_input_files(inputFiles)
    delete_classes()

    saEvaluation = evaluate_sa(inputFiles)
    tsEvaluation = evaluate_diff(inputFiles, ".ts", "ts-ref/", "Table des Symboles")

    useColor = True

    if useColor:
        print("Légende : {}  {}  {}".format(green("CORRECT"), purple("INCORRECT"), red("NON-EXISTANT")))

    print_evaluation_result(sys.stdout, saEvaluation, useColor)
    print_evaluation_result(sys.stdout, tsEvaluation, useColor)
################################################################################
