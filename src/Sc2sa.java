import sa.*;
import sc.analysis.DepthFirstAdapter;
import sc.node.*;

public class Sc2sa extends DepthFirstAdapter {
    private SaNode returnValue;

    public SaNode getRoot() {
        return returnValue;
    }

    private SaNode apply(Switchable sw) {
        sw.apply(this);
        return this.returnValue;
    }

    // =======================================================================
    // From depth first adapter

    @Override
    public void caseStart(Start node) {
        apply(node.getPProgramme());
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseADeclistvardeclistfoncProgramme(ADeclistvardeclistfoncProgramme node) {
        SaLDec vars = (SaLDec) apply(node.getOptdecvar());
        SaLDec funcs = (SaLDec) apply(node.getDeclistfunc());
        this.returnValue = new SaProg(vars, funcs);
    }

    @Override
    public void caseADeclistfoncProgramme(ADeclistfoncProgramme node) {
        SaLDec funcs = (SaLDec) apply(node.getDeclistfunc());
        this.returnValue = new SaProg(null, funcs);
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAOptdecvar(AOptdecvar node) {
        apply(node.getDeclistvar());
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseADeclistvarDeclistvar(ADeclistvarDeclistvar node) {
        SaDecVar tete = (SaDecVar) apply(node.getDecvar());
        SaLDec queue = (SaLDec) apply(node.getDeclistvarvir());
        this.returnValue = new SaLDec(tete, queue);
    }

    @Override
    public void caseADecvarDeclistvar(ADecvarDeclistvar node) {
        SaDec tete = (SaDec) apply(node.getDecvar());
        this.returnValue = new SaLDec(tete, null);
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseADeclistvarvirDeclistvarvir(ADeclistvarvirDeclistvarvir node) {
        SaDecVar decvar = (SaDecVar) apply(node.getDecvar());
        SaLDec listDecVarBis = (SaLDec) apply(node.getDeclistvarvir());
        this.returnValue = new SaLDec(decvar, listDecVarBis);
    }

    @Override
    public void caseADecvarDeclistvarvir(ADecvarDeclistvarvir node) {
        SaDecVar decvar = (SaDecVar) apply(node.getDecvar());
        this.returnValue = new SaLDec(decvar, null);
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseADecvarintDecvar(ADecvarintDecvar node) {
        String nom = node.getNom().getText();
        returnValue = new SaDecVar(nom);
    }

    @Override
    public void caseADecvartabDecvar(ADecvartabDecvar node) {
        String nom = node.getNom().getText();
        int cap = Integer.parseInt(node.getNombre().getText());
        returnValue = new SaDecTab(nom, cap);
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseADeclistfuncDeclistfunc(ADeclistfuncDeclistfunc node) {
        SaDecFonc fonc = (SaDecFonc) apply(node.getDecfunc());
        SaLDec liste = (SaLDec) apply(node.getDeclistfunc());
        this.returnValue = new SaLDec(fonc, liste);
    }

    @Override
    public void caseAEmptyDeclistfunc(AEmptyDeclistfunc node) {
        this.returnValue = null;
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseADecvarinstDecfunc(ADecvarinstDecfunc node) {
        String nom = node.getNom().getText();
        SaLDec params = (SaLDec) apply(node.getArglist());
        SaLDec vars = (SaLDec) apply(node.getOptdecvar());
        SaInstBloc contenu = (SaInstBloc) apply(node.getInstbloc());
        this.returnValue = new SaDecFonc(nom, params, vars, contenu);
    }

    @Override
    public void caseAInstDecfunc(AInstDecfunc node) {
        String nom = node.getNom().getText();
        SaLDec params = (SaLDec) apply(node.getArglist());
        SaInstBloc contenu = (SaInstBloc) apply(node.getInstbloc());
        this.returnValue = new SaDecFonc(nom, params, null, contenu);
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAEmptyargArglist(AEmptyargArglist node) {
        this.returnValue = null;
    }

    @Override
    public void caseAArgsArglist(AArgsArglist node) {
        apply(node.getDeclistvar());
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAInstaffectInst(AInstaffectInst node) {
        apply(node.getInstaffect());
    }

    @Override
    public void caseAInstblocInst(AInstblocInst node) {
        apply(node.getInstbloc());
    }

    @Override
    public void caseAInstifInst(AInstifInst node) {
        apply(node.getInstif());
    }

    @Override
    public void caseAInsttqInst(AInsttqInst node) {
        apply(node.getInsttq());
    }

    @Override
    public void caseAInstappelInst(AInstappelInst node) {
        apply(node.getInstappel());
    }

    @Override
    public void caseAInstreturnInst(AInstreturnInst node) {
        apply(node.getInstreturn());
    }

    @Override
    public void caseAInstecrireInst(AInstecrireInst node) {
        apply(node.getInstecrire());
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAInstemptyInst(AInstemptyInst node) {
        this.returnValue = null;
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAInstaffect(AInstaffect node) {
        SaVar var = (SaVar) apply(node.getVar());
        SaExp exp = (SaExp) apply(node.getExpression());
        this.returnValue = new SaInstAffect(var, exp);
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAInstbloc(AInstbloc node) {
        SaLInst blc = (SaLInst) apply(node.getInstlist());
        this.returnValue = new SaInstBloc(blc);
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAInstlistInstlist(AInstlistInstlist node) {
        SaInst tete = (SaInst) apply(node.getInst());
        SaLInst queue = (SaLInst) apply(node.getInstlist());
        this.returnValue = new SaLInst(tete, queue);
    }

    @Override
    public void caseAEmptyInstlist(AEmptyInstlist node) {
        this.returnValue = null;
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAIfelseInstif(AIfelseInstif node) {
        SaExp exp = (SaExp) apply(node.getExpression());
        SaInstBloc bloc = (SaInstBloc) apply(node.getInstbloc());
        SaInstBloc blocSinon = (SaInstBloc) apply(node.getInstelse());
        this.returnValue = new SaInstSi(exp, bloc, blocSinon);
    }

    @Override
    public void caseAIfInstif(AIfInstif node) {
        SaExp exp = (SaExp) apply(node.getExpression());
        SaInstBloc bloc = (SaInstBloc) apply(node.getInstbloc());
        this.returnValue = new SaInstSi(exp, bloc, null);
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAInstelse(AInstelse node) {
        apply(node.getInstbloc());
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAInsttq(AInsttq node) {
        SaExp exp = (SaExp) apply(node.getExpression());
        SaInstBloc bloc = (SaInstBloc) apply(node.getInstbloc());
        this.returnValue = new SaInstTantQue(exp, bloc);
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAInstappel(AInstappel node) {
        apply(node.getAppelfunc());
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAInstreturn(AInstreturn node) {
        SaExp exp = (SaExp) apply(node.getExpression());
        this.returnValue = new SaInstRetour(exp);
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAInstecrire(AInstecrire node) {
        SaExp exp = (SaExp) apply(node.getExpression());
        this.returnValue = new SaInstEcriture(exp);
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAInstempty(AInstempty node) {
        this.returnValue = null;
    }

    // -----------------------------------------------------------------------

    @Override
    public void caseAOrExpression(AOrExpression node) {
        SaExp op1, op2;
        node.getExpression().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getEt().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpOr(op1, op2);
    }

    @Override
    public void caseAEtExpression(AEtExpression node) {
        // TODO: correct?
        node.getEt().apply(this);
    }

    @Override
    public void caseAAndEt(AAndEt node) {
        SaExp op1, op2;
        node.getEt().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getEgInf().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpAnd(op1, op2);
    }

    @Override
    public void caseAEgInfEt(AEgInfEt node) {
        // TODO: correct?
        node.getEgInf().apply(this);
    }

    @Override
    public void caseAInfEgInf(AInfEgInf node) {
        SaExp op1, op2;
        node.getEgInf().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getPlusMinus().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpInf(op1, op2);
    }

    @Override
    public void caseAEgEgInf(AEgEgInf node) {
        SaExp op1, op2;
        node.getEgInf().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getPlusMinus().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpEqual(op1, op2);
    }

    @Override
    public void caseAPlusMinusEgInf(APlusMinusEgInf node) {
        // TODO: correct?
        node.getPlusMinus().apply(this);
    }

    @Override
    public void caseAPlusPlusMinus(APlusPlusMinus node) {
        SaExp op1, op2;
        node.getPlusMinus().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getMultDiv().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpAdd(op1, op2);
    }

    @Override
    public void caseAMinusPlusMinus(AMinusPlusMinus node) {
        SaExp op1, op2;
        node.getPlusMinus().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getMultDiv().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpSub(op1, op2);
    }

    @Override
    public void caseAMultDivPlusMinus(AMultDivPlusMinus node) {
        // TODO: correct?
        node.getMultDiv().apply(this);
    }

    @Override
    public void caseAMultMultDiv(AMultMultDiv node) {
        SaExp op1, op2;
        node.getMultDiv().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getNon().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpMult(op1, op2);
    }

    @Override
    public void caseADivMultDiv(ADivMultDiv node) {
        SaExp op1, op2;
        node.getMultDiv().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getNon().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpDiv(op1, op2);
    }

    @Override
    public void caseANonMultDiv(ANonMultDiv node) {
        // TODO: correct?
        node.getNon().apply(this);
    }

    @Override
    public void caseANotNon(ANotNon node) {
        SaExp op1;
        node.getNon().apply(this);
        op1 = (SaExp) this.returnValue;
        this.returnValue = new SaExpNot(op1);
    }

    @Override
    public void caseAFacteurNon(AFacteurNon node) {
        // TODO: correct?
        node.getFacteur().apply(this);
    }

    @Override
    public void caseANombreFacteur(ANombreFacteur node) {
        // TODO: correct?
        // On aurait pu utiliser:
        // node.getNombre().apply(this);
        // Mais comment récupérer la valeur après?


        int value = Integer.parseInt(node.getNombre().getText());
        this.returnValue = new SaExpInt(value);
    }

    @Override
    public void caseAAppelfuncFacteur(AAppelfuncFacteur node) {
        SaAppel op1;
        node.getAppelfunc().apply(this);
        op1 = (SaAppel) this.returnValue;
        this.returnValue = new SaExpAppel(op1);
    }

    @Override
    public void caseAVarFacteur(AVarFacteur node) {
        SaVar op1;
        node.getVar().apply(this);
        op1 = (SaVar) this.returnValue;
        this.returnValue = new SaExpVar(op1);
    }

    @Override
    public void caseAParenthesesFacteur(AParenthesesFacteur node) {
        // TODO: correct?
        node.getExpression().apply(this);
    }

    @Override
    public void caseAReadFacteur(AReadFacteur node) {
        // TODO: lire? il n'y a pas d'expr entre les parenthèses?
        this.returnValue = new SaExpLire();
    }

    @Override
    public void caseAVartabVar(AVartabVar node) {
        // TODO: correct?
        String nom = node.getNom().getText();
        node.getExpression().apply(this);
        SaExp op1 = (SaExp) this.returnValue;
        this.returnValue = new SaVarIndicee(nom, op1);
    }

    @Override
    public void caseAVarnameVar(AVarnameVar node) {
        // TODO: correct?
        String nom = node.getNom().getText();
        this.returnValue = new SaVarSimple(nom);
    }

    @Override
    public void caseAExplistExplist(AExplistExplist node) {
        node.getExpression().apply(this);
        SaExp op1 = (SaExp) this.returnValue;
        node.getExplistvir().apply(this);
        SaLExp op2 = (SaLExp) this.returnValue;
        this.returnValue = new SaLExp(op1, op2);
    }

    @Override
    public void caseALastexpressionExplist(ALastexpressionExplist node) {
        // TODO: correct?
        node.getExpression().apply(this);
        SaExp exp = (SaExp) this.returnValue;
        this.returnValue = new SaLExp(exp, null);
    }

    @Override
    public void caseAExplistvirExplistvir(AExplistvirExplistvir node) {
        // TODO: correct?
        node.getExpression().apply(this);
        SaExp op1 = (SaExp) this.returnValue;
        node.getExplistvir().apply(this);
        SaLExp op2 = (SaLExp) this.returnValue;
        this.returnValue = new SaLExp(op1, op2);
    }

    @Override
    public void caseALastexpvirExplistvir(ALastexpvirExplistvir node) {
        // TODO: correct?
        node.getExpression().apply(this);
        SaExp exp = (SaExp) this.returnValue;
        this.returnValue = new SaLExp(exp, null);
    }

    @Override
    public void caseAFuncargAppelfunc(AFuncargAppelfunc node) {
        // TODO: correct?
        String nom = node.getNom().getText();
        node.getExplist().apply(this);
        SaLExp args = (SaLExp) this.returnValue;
        this.returnValue = new SaAppel(nom, args);
    }

    @Override
    public void caseAFuncAppelfunc(AFuncAppelfunc node) {
        // TODO: correct?
        String nom = node.getNom().getText();
        this.returnValue = new SaAppel(nom, null);
    }
}
