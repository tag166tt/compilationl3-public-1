import org.junit.Test;
import sa.SaNode;
import sc.lexer.Lexer;
import sc.lexer.LexerException;
import sc.node.Start;
import sc.parser.Parser;
import sc.parser.ParserException;
import ts.Ts;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import static org.junit.Assert.*;

public class Sa2tsTest {

    private void buildTs(String code) throws ParserException, IOException, LexerException {
        var reader = new PushbackReader(new StringReader(code));
        var parser = new Parser(new Lexer(reader));
        var tree = parser.parse();
        var sc2sa = new Sc2sa();
        tree.apply(sc2sa);
        new Sa2ts(sc2sa.getRoot());
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testNoDuplicateVariableGlobalScope() throws ParserException, IOException, LexerException {
        buildTs("entier a, entier a; main() {}");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testNoDuplicateArrayGlobalScope() throws ParserException, IOException, LexerException {
        buildTs("entier a[5], entier a[5]; main() {}");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testNoDuplicateVariableArgumentScope() throws ParserException, IOException, LexerException {
        buildTs("main(entier a, entier a) {}");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testNoDuplicateVariableFunctionScope() throws ParserException, IOException, LexerException {
        buildTs("main() entier a, entier a; {}");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testNoFunctionVariableSameNameAsArgument() throws ParserException, IOException, LexerException {
        buildTs("main(entier a) entier a; {}");
    }

    // TODO : uniquement la portée la plus proche est accessible

    @Test(expected = Sa2ts.TsException.class)
    public void testArraysAreGlobals() throws ParserException, IOException, LexerException {
        buildTs("main() entier a[5]; {}");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testNoFunctionSameNameAsVariableLocalScope() throws ParserException, IOException, LexerException {
        buildTs("main() entier test; {main();} test() {test();}");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testNoFunctionSameNameAsVariableGlobalScope() throws ParserException, IOException, LexerException {
        buildTs("entier main; main() {main();}");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testNoFunctionSameNameAsArrayGlobalScope() throws ParserException, IOException, LexerException {
        buildTs("entier main[2]; main() {main();}");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testUsedVariableIsDeclared() throws ParserException, IOException, LexerException {
        buildTs("main() { a = 1; }");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testUsedArrayIsDeclared() throws ParserException, IOException, LexerException {
        buildTs("main() { a[0] = 1; }");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testArrayNotUsedAsInteger() throws ParserException, IOException, LexerException {
        buildTs("entier a[2]; main() { a = 0; }");
    }

    // TODO

    /*@Test(expected = Sa2ts.TsException.class)
    public void testIntegerNotUsedAsArray() throws ParserException, IOException, LexerException {
        buildTs("entier a; main() { a[0] = 0; }");
    }*/

    @Test(expected = Sa2ts.TsException.class)
    public void testNoCastsFromArrayToInteger() throws ParserException, IOException, LexerException {
        buildTs("entier a[2], entier b; main() { b = a; }");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testNoCastsFromIntegerToArray() throws ParserException, IOException, LexerException {
        try {
            buildTs("entier a[2], entier b; main() { a = b; }");
        } catch (Sa2ts.TsException e) {
            buildTs("entier a[2]; g() { retour 0; } main() { a = g(); }");
        }
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testAllFunctionsHaveUniqueNames() throws ParserException, IOException, LexerException {
        buildTs("main() { main(); } main() { main(); }");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testCalledFunctionIsDeclared() throws ParserException, IOException, LexerException {
        buildTs("main() { g(); }");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testCalledFunctionIsDeclaredBefore() throws ParserException, IOException, LexerException {
        buildTs("main() { g(); } g() { retour 0; }");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testFunctionCallHasCorrectNumberOfArguments() throws ParserException, IOException, LexerException {
        buildTs("g(entier a) { retour 0; } main() { g(); }");
    }

    @Test(expected = Sa2ts.TsException.class)
    public void testMainExists() throws ParserException, IOException, LexerException {
        buildTs("g() { retour 0; }");
    }

}
