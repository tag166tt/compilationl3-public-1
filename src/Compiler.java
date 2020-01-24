import sc.lexer.Lexer;
import sc.node.Start;
import sc.parser.Parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;

public class Compiler {

    public static void main(String[] args) {
        List<String> fileNames = new ArrayList<>();

        File folder = new File("test\\input");
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
                if (0 < args.length) {
                    br = new PushbackReader(new FileReader(fileName), 1024);
                    baseName = removeSuffix(fileName, ".l");
                } else {
                    System.out.println("il manque un argument");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                // Create a Parser instance.
                Parser p = new Parser(new Lexer(br));
                // Parse the input.
                Start tree = p.parse();

                System.out.println("[SC]");
                tree.apply(new Sc2Xml(baseName));

                /*System.out.println("[SA]");
                Sc2sa sc2sa = new Sc2sa();
                tree.apply(sc2sa);
                SaNode saRoot = sc2sa.getRoot();
                new Sa2Xml(saRoot, baseName);

                System.out.println("[TABLE SYMBOLES]");
                Ts table = new Sa2ts(saRoot).getTableGlobale();
                table.afficheTout(baseName);

                System.out.println("[C3A]");
                C3a c3a = new Sa2c3a(saRoot, table).getC3a();
                c3a.affiche(baseName);

                System.out.println("[NASM]");
                Nasm nasm = new C3a2nasm(c3a, table).getNasm();
                nasm.affiche(baseName);

                System.out.println("[FLOW GRAPH]");
                Fg fg = new Fg(nasm);
                fg.affiche(baseName);

                System.out.println("[FLOW GRAPH SOLVE]");
                FgSolution fgSolution = new FgSolution(nasm, fg);
                fgSolution.affiche(baseName);*/
            } catch (Exception e) {
                System.out.println("fileName = " + fileName);
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

}
