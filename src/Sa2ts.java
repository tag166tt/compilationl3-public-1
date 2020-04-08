import sa.*;
import ts.Ts;
import ts.TsItemFct;
import ts.TsItemVar;

import java.util.Map;

public class Sa2ts extends SaDepthFirstVisitor<Void> {

    public static class TsException extends RuntimeException {
        public TsException(String message, Object ...args) {
            super(String.format(message, args));
        }
    }

    private Ts table = new Ts();
    private Ts localTable = null;
    private Integer localArgsLength = null;

    public Ts getTableGlobale() {
        return table;
    }

    public Sa2ts(SaNode saRoot) throws TsException {
        visit((SaProg) saRoot); // TODO: is there a better way to do this?
        checkNoVariableHasSameNameHasFunction();
        checkMainExists();
    }

    private void checkNoVariableHasSameNameHasFunction() {
        // global variables
        for (String functionName : table.fonctions.keySet()) {
            if (table.variables.containsKey(functionName)) {
                throw new TsException("The global variable '%s' has the same name as a function.", functionName);
            }
        }

        // local variables
        for (TsItemFct function : table.fonctions.values()) {
            for (String variableName : function.getTable().variables.keySet()) {
                if (table.fonctions.containsKey(variableName))
                    throw new TsException("The variable / argument '%s' has the same name as a function.", variableName);
            }
        }
    }

    private void checkMainExists() {
        if (!table.fonctions.containsKey("main"))
            throw new TsException("The main function does nit exist.");
    }

    @Override
    public Void visit(SaDecVar node) {
        // Il n’y a pas deux variables identiques déclarées dans une même portée

        if (localTable != null) {
            // Its an argument
            if (localTable.variables.size() < localArgsLength) {
                if (localTable.variables.containsKey(node.getNom()))
                    throw new TsException("The argument '%s' has already been declared in this scope.", node.getNom());

                localTable.addParam(node.getNom());
            }
            // Its a variable
            else {
                if (localTable.variables.containsKey(node.getNom()))
                    throw new TsException("The variable '%s' has already been declared in this scope.", node.getNom());

                localTable.addVar(node.getNom(), 1);
            }
        } else {
            if (table.variables.containsKey(node.getNom()))
                throw new TsException("The variable '%s' has already been declared in this scope.", node.getNom());

            table.addVar(node.getNom(), 1);
        }

        return super.visit(node);
    }

    @Override
    public Void visit(SaDecTab node) {
        if (localTable != null)
            throw new TsException("The array '%s' is declared in a function scope, but it should be in the global scope.", node.getNom());

        if (table.variables.containsKey(node.getNom()))
            throw new TsException("The array '%s' has already been declared.", node.getNom());

        table.addVar(node.getNom(), node.getTaille());
        return super.visit(node);
    }

    @Override
    public Void visit(SaDecFonc node) {
        if (table.fonctions.containsKey(node.getNom()))
            throw new TsException("The function '%s' is declared twice.", node.getNom());

        SaLDec params = node.getParametres();
        localArgsLength = (params == null) ? 0 : params.length();
        localTable = new Ts();
        table.addFct(node.getNom(), localArgsLength, localTable, node);
        return super.visit(node);
    }

    @Override
    public Void visit(SaVarSimple node) {
        TsItemVar variable = localTable.variables.get(node.nom);
        if (variable == null) variable = table.variables.get(node.nom);
        if (variable == null) throw new TsException("The variable '%s' is not declared.", node.nom);
        // TODO: pas de moyen autre de différencier les types...
        if (variable.taille > 1) throw new TsException("Trying to use the variable '%s' as an integer, but it is an array.", node.nom);
        node.tsItem = variable;
        return super.visit(node);
    }

    @Override
    public Void visit(SaVarIndicee node) {
        TsItemVar variable = localTable.variables.get(node.getNom());
        if (variable == null) variable = table.variables.get(node.getNom());
        if (variable == null) throw new TsException("The variable '%s' is not declared.", node.getNom());
        // TODO: pas de moyen de vérifier si la variable est bien un tableau, on a pas le type (taille = 1 pour variable
        //  normale)
        node.tsItem = variable;
        return super.visit(node);
    }

    @Override
    public Void visit(SaAppel node) {
        if (!table.fonctions.containsKey(node.getNom()))
            throw new TsException("Trying to call the function '%s' that has not been defined (yet).", node.getNom());

        TsItemFct fonction = table.fonctions.get(node.getNom());
        int nodeArgsLength = (node.getArguments() == null) ? 0 : node.getArguments().length();

        if (fonction.nbArgs != nodeArgsLength)
            throw new TsException("Trying to call the function '%s' with %d arguments, but that function takes %d arguments.", node.getNom(), nodeArgsLength, fonction.nbArgs);

        node.tsItem = fonction;
        return super.visit(node);
    }

}
