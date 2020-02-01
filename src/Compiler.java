import sa.Sa2Xml;
import sa.SaNode;
import sc.lexer.Lexer;
import sc.node.Start;
import sc.parser.Parser;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Compiler {

    public static void main(String[] args) {
        List<String> fileNames = new ArrayList<>();

        File folder = Paths.get("test", "input").toFile();
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile() && listOfFile.getName().endsWith(".l")) {
                fileNames.add(listOfFile.getAbsolutePath());
            }
        }

        for (String fileName : fileNames) {
            PushbackReader br = null;
            String baseName = null;

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

                checkGenSA(baseName);

                /*System.out.println("[TABLE SYMBOLES]");
                Ts table = new Sa2ts(saRoot).getTableGlobale();
                table.afficheTout(baseName);

                /*System.out.println("[C3A]");
                C3a c3a = new Sa2c3a(saRoot, table).getC3a();
                c3a.affiche(baseName);

                /*System.out.println("[NASM]");
                Nasm nasm = new C3a2nasm(c3a, table).getNasm();
                nasm.affiche(baseName);

                /*System.out.println("[FLOW GRAPH]");
                Fg fg = new Fg(nasm);
                fg.affiche(baseName);

                /*System.out.println("[FLOW GRAPH SOLVE]");
                FgSolution fgSolution = new FgSolution(nasm, fg);
                fgSolution.affiche(baseName);*/
            } catch (Exception e) {
                System.out.println("fileName = " + fileName);
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static String removeSuffix(final String s, final String suffix) {
        if (s != null && suffix != null && s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }

    private static void checkGenSA(String baseName) {
        try {
            int indexOfLastSeparator = baseName.lastIndexOf(File.separator);
            String f = baseName.substring(indexOfLastSeparator + 1);
            Process process = Runtime.getRuntime().exec(String.format("python comp.py %s", f));
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = stdInput.readLine()) != null) {
                System.err.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
