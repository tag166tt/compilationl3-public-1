import c3a.C3a;
import nasm.Nasm;
import sa.Sa2Xml;
import sa.SaNode;
import sc.lexer.Lexer;
import sc.node.Start;
import sc.parser.Parser;
import ts.Ts;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
//import fg.*;

public class Compiler {
    public static void main(String[] args) {
        List<String> fileNames = new ArrayList<>();

        File folder = Paths.get("test", "input").toFile();
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile() && listOfFile.getName().endsWith(".l") && listOfFile.getName().contains("")) {
                fileNames.add(listOfFile.getAbsolutePath());
            }
        }

        //fileNames = Collections.singletonList(fileNames.get(0));

        for (String fileName : fileNames) {
            PushbackReader br = null;
            String baseName;

            try {
                br = new PushbackReader(new FileReader(fileName), 1024);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }

            baseName = removeSuffix(fileName, ".l");

            System.out.println();
            System.out.println(String.format("File base name: %s", baseName));

            try {
                // Create a Parser instance.
                Parser p = new Parser(new Lexer(br));
                // Parse the input.
                Start tree = p.parse();

                System.out.println("[SC]");
                tree.apply(new Sc2Xml(baseName));

                System.out.println("[SA]");
                Sc2sa sc2sa = new Sc2sa();
                tree.apply(sc2sa);
                SaNode saRoot = sc2sa.getRoot();
                new Sa2Xml(saRoot, baseName);

                checkSA(baseName);

                System.out.println("[TABLE SYMBOLES]");
                Ts table = new Sa2ts(saRoot).getTableGlobale();
                table.afficheTout(baseName);

                checkTS(baseName);

                System.out.println("[C3A]");
                C3a c3a = new Sa2c3a(saRoot, table).getC3a();
                c3a.affiche(baseName);

                checkC3a(baseName);

                System.out.print("[BUILD PRE NASM] ");
                Nasm nasm = new C3a2nasm(c3a, table).getNasm();
                System.out.println("[PRINT PRE NASM] ");
                nasm.affichePre(baseName);

                checkPreNASM(baseName);

        /*
        System.out.print("[BUILD FG] ");
        Fg fg = new Fg(nasm);
        System.out.print("[PRINT FG] ");
        fg.affiche(baseName);
        System.out.println("[SOLVE FG]");
        FgSolution fgSolution = new FgSolution(nasm, fg);
        fgSolution.affiche(baseName);
        */
            } catch (Exception e) {
                System.out.println("e.getClass().getSimpleName() = " + e.getClass().getSimpleName());
                e.printStackTrace();
                System.out.println("Patate");
                System.out.println(e.getMessage());
            }
        }
    }

    public static String removeSuffix(final String s, final String suffix) {
        if (s != null && suffix != null && s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }

    private static void checkGenFiles(String baseName, String mode) {
        try {
            int indexOfLastSeparator = baseName.lastIndexOf(File.separator);
            String f = baseName.substring(indexOfLastSeparator + 1);
            Process process = Runtime.getRuntime().exec(String.format("python comp.py %s %s", mode, f));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = stdError.readLine()) != null) {
                System.err.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkSA(String baseName) {
        checkGenFiles(baseName, "sa");
    }

    private static void checkTS(String baseName) {
        checkGenFiles(baseName, "ts");
    }

    private static void checkC3a(String baseName) {
        checkGenFiles(baseName, "c3a");
    }

    private static void checkPreNASM(String baseName) {
        checkGenFiles(baseName, "prenasm");
    }

    private static void checkNASM(String baseName) {
        checkGenFiles(baseName, "nasm");
    }
}
