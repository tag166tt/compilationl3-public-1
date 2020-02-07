import sa.*;
import ts.Ts;

public class Sa2ts extends SaDepthFirstVisitor<SaNode> {

    private Ts table = new Ts();
    private Ts localTable = null;
    private Integer localArgsLength = null;

    public Ts getTableGlobale() {
        return table;
    }

    public Sa2ts(SaNode saRoot) {
        visit((SaProg) saRoot); // TODO
    }

    @Override
    public SaNode visit(SaDecVar node) {
        if (localTable != null) {
            // Its an argument
            if (localTable.variables.size() < localArgsLength) {
                localTable.addParam(node.getNom());
            }
            // Its a variable
            else {
                localTable.addVar(node.getNom(), 1);
            }
        } else {
            table.addVar(node.getNom(), 1);
        }

        return super.visit(node);
    }

    @Override
    public SaNode visit(SaDecTab node) {
        int taille = node.getTaille();

        if (localTable != null)
            localTable.addVar(node.getNom(), taille);
        else
            table.addVar(node.getNom(), taille);

        return super.visit(node);
    }

    @Override
    public SaNode visit(SaDecFonc node) {
        SaLDec params = node.getParametres();
        localArgsLength = (params == null) ? 0 : params.length();
        localTable = new Ts();
        table.addFct(node.getNom(), localArgsLength, localTable, node);
        return super.visit(node);
    }

    @Override
    public SaNode visit(SaVarSimple node) {
        return super.visit(node);
    }

    @Override
    public SaNode visit(SaVarIndicee node) {
        return super.visit(node);
    }

    @Override
    public SaNode visit(SaAppel node) {
        return super.visit(node);
    }

}
