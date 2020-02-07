import sa.*;
import sc.analysis.DepthFirstAdapter;
import sc.node.*;

public class Sc2sa extends DepthFirstAdapter {
    private SaNode returnValue;

    public SaNode getRoot() {
        return returnValue;
    }

    private <T extends SaNode> T apply(Switchable sw) {
        sw.apply(this);
        //noinspection unchecked
        return (T) this.returnValue;
    }

    @Override
    public void caseStart(Start node) {
        apply(node.getPProgramme());
    }

    @Override
    public void caseADeclistvardeclistfoncProgramme(ADeclistvardeclistfoncProgramme node) {
        this.returnValue = new SaProg(apply(node.getOptdecvar()), apply(node.getDeclistfunc()));
    }

    @Override
    public void caseADeclistfoncProgramme(ADeclistfoncProgramme node) {
        this.returnValue = new SaProg(null, apply(node.getDeclistfunc()));
    }

    @Override
    public void caseAOptdecvar(AOptdecvar node) {
        apply(node.getDeclistvar());
    }

    @Override
    public void caseADeclistvarDeclistvar(ADeclistvarDeclistvar node) {
        this.returnValue = new SaLDec(apply(node.getDecvar()), apply(node.getDeclistvarvir()));
    }

    @Override
    public void caseADecvarDeclistvar(ADecvarDeclistvar node) {
        this.returnValue = new SaLDec(apply(node.getDecvar()), null);
    }

    @Override
    public void caseADeclistvarvirDeclistvarvir(ADeclistvarvirDeclistvarvir node) {
        this.returnValue = new SaLDec(apply(node.getDecvar()), apply(node.getDeclistvarvir()));
    }

    @Override
    public void caseADecvarDeclistvarvir(ADecvarDeclistvarvir node) {
        this.returnValue = new SaLDec(apply(node.getDecvar()), null);
    }

    @Override
    public void caseADecvarboolDecvar(ADecvarboolDecvar node) {
        String nom = node.getNom().getText();
        returnValue = new SaDecVar(nom);
    }

    @Override
    public void caseADecvarbooltabDecvar(ADecvarbooltabDecvar node) {
        String nom = node.getNom().getText();
        int cap = Integer.parseInt(node.getNombre().getText());
        returnValue = new SaDecTab(nom, cap);
    }

    @Override
    public void caseADecvarintDecvar(ADecvarintDecvar node) {
        String nom = node.getNom().getText();
        returnValue = new SaDecVar(nom);
    }

    @Override
    public void caseADecvarinttabDecvar(ADecvarinttabDecvar node) {
        String nom = node.getNom().getText();
        int cap = Integer.parseInt(node.getNombre().getText());
        returnValue = new SaDecTab(nom, cap);
    }

    @Override
    public void caseADeclistfuncDeclistfunc(ADeclistfuncDeclistfunc node) {
        this.returnValue = new SaLDec(apply(node.getDecfunc()), apply(node.getDeclistfunc()));
    }

    @Override
    public void caseAEmptyDeclistfunc(AEmptyDeclistfunc node) {
        this.returnValue = null;
    }

    @Override
    public void caseADecvarinstDecfunc(ADecvarinstDecfunc node) {
        String nom = node.getNom().getText();
        this.returnValue = new SaDecFonc(
                nom, apply(node.getArglist()), apply(node.getOptdecvar()), apply(node.getInstbloc()));
    }

    @Override
    public void caseAInstDecfunc(AInstDecfunc node) {
        String nom = node.getNom().getText();
        this.returnValue = new SaDecFonc(nom, apply(node.getArglist()), null, apply(node.getInstbloc()));
    }

    @Override
    public void caseAEmptyargArglist(AEmptyargArglist node) {
        this.returnValue = null;
    }

    @Override
    public void caseAArgsArglist(AArgsArglist node) {
        apply(node.getDeclistvar());
    }

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

    @Override
    public void caseAInstemptyInst(AInstemptyInst node) {
        this.returnValue = null;
    }

    @Override
    public void caseAInstaffect(AInstaffect node) {
        this.returnValue = new SaInstAffect(apply(node.getVar()), apply(node.getExpression()));
    }

    @Override
    public void caseAInstbloc(AInstbloc node) {
        this.returnValue = new SaInstBloc(apply(node.getInstlist()));
    }

    @Override
    public void caseAInstlistInstlist(AInstlistInstlist node) {
        this.returnValue = new SaLInst(apply(node.getInst()), apply(node.getInstlist()));
    }

    @Override
    public void caseAEmptyInstlist(AEmptyInstlist node) {
        this.returnValue = null;
    }

    @Override
    public void caseAIfelseInstif(AIfelseInstif node) {
        this.returnValue = new SaInstSi(
                apply(node.getExpression()), apply(node.getInstbloc()), apply(node.getInstelse()));
    }

    @Override
    public void caseAIfInstif(AIfInstif node) {
        this.returnValue = new SaInstSi(apply(node.getExpression()), apply(node.getInstbloc()), null);
    }

    @Override
    public void caseAInstelse(AInstelse node) {
        apply(node.getInstbloc());
    }

    @Override
    public void caseAInsttq(AInsttq node) {
        this.returnValue = new SaInstTantQue(apply(node.getExpression()), apply(node.getInstbloc()));
    }

    @Override
    public void caseAInstappel(AInstappel node) {
        apply(node.getAppelfunc());
    }

    @Override
    public void caseAInstreturn(AInstreturn node) {
        this.returnValue = new SaInstRetour(apply(node.getExpression()));
    }

    @Override
    public void caseAInstecrire(AInstecrire node) {
        this.returnValue = new SaInstEcriture(apply(node.getExpression()));
    }

    @Override
    public void caseAInstempty(AInstempty node) {
        this.returnValue = null;
    }

    @Override
    public void caseAOrExpression(AOrExpression node) {
        this.returnValue = new SaExpOr(apply(node.getExpression()), apply(node.getEt()));
    }

    @Override
    public void caseAEtExpression(AEtExpression node) {
        apply(node.getEt());
    }

    @Override
    public void caseAAndEt(AAndEt node) {
        this.returnValue = new SaExpAnd(apply(node.getEt()), apply(node.getEgInf()));
    }

    @Override
    public void caseAEgInfEt(AEgInfEt node) {
        apply(node.getEgInf());
    }

    @Override
    public void caseAInfEgInf(AInfEgInf node) {
        this.returnValue = new SaExpInf(apply(node.getEgInf()), apply(node.getPlusMinus()));
    }

    @Override
    public void caseAEgEgInf(AEgEgInf node) {
        this.returnValue = new SaExpEqual(apply(node.getEgInf()), apply(node.getPlusMinus()));
    }

    @Override
    public void caseAPlusMinusEgInf(APlusMinusEgInf node) {
        apply(node.getPlusMinus());
    }

    @Override
    public void caseAPlusPlusMinus(APlusPlusMinus node) {
        this.returnValue = new SaExpAdd(apply(node.getPlusMinus()), apply(node.getMultDiv()));
    }

    @Override
    public void caseAMinusPlusMinus(AMinusPlusMinus node) {
        this.returnValue = new SaExpSub(apply(node.getPlusMinus()), apply(node.getMultDiv()));
    }

    @Override
    public void caseAMultDivPlusMinus(AMultDivPlusMinus node) {
        apply(node.getMultDiv());
    }

    @Override
    public void caseAMultMultDiv(AMultMultDiv node) {
        this.returnValue = new SaExpMult(apply(node.getMultDiv()), apply(node.getNon()));
    }

    @Override
    public void caseADivMultDiv(ADivMultDiv node) {
        this.returnValue = new SaExpDiv(apply(node.getMultDiv()), apply(node.getNon()));
    }

    @Override
    public void caseANonMultDiv(ANonMultDiv node) {
        apply(node.getNon());
    }

    @Override
    public void caseANotNon(ANotNon node) {
        this.returnValue = new SaExpNot(apply(node.getNon()));
    }

    @Override
    public void caseAFacteurNon(AFacteurNon node) {
        apply(node.getFacteur());
    }

    @Override
    public void caseANombreFacteur(ANombreFacteur node) {
        int value = Integer.parseInt(node.getNombre().getText());
        this.returnValue = new SaExpInt(value);
    }

    @Override
    public void caseAAppelfuncFacteur(AAppelfuncFacteur node) {
        this.returnValue = new SaExpAppel(apply(node.getAppelfunc()));
    }

    @Override
    public void caseAVarFacteur(AVarFacteur node) {
        this.returnValue = new SaExpVar(apply(node.getVar()));
    }

    @Override
    public void caseAParenthesesFacteur(AParenthesesFacteur node) {
        apply(node.getExpression());
    }

    @Override
    public void caseAReadFacteur(AReadFacteur node) {
        this.returnValue = new SaExpLire();
    }

    @Override
    public void caseAVartabVar(AVartabVar node) {
        String nom = node.getNom().getText();
        this.returnValue = new SaVarIndicee(nom, apply(node.getExpression()));
    }

    @Override
    public void caseAVarnameVar(AVarnameVar node) {
        String nom = node.getNom().getText();
        this.returnValue = new SaVarSimple(nom);
    }

    @Override
    public void caseAExplistExplist(AExplistExplist node) {
        this.returnValue = new SaLExp(apply(node.getExpression()), apply(node.getExplistvir()));
    }

    @Override
    public void caseALastexpressionExplist(ALastexpressionExplist node) {
        this.returnValue = new SaLExp(apply(node.getExpression()), null);
    }

    @Override
    public void caseAExplistvirExplistvir(AExplistvirExplistvir node) {
        this.returnValue = new SaLExp(apply(node.getExpression()), apply(node.getExplistvir()));
    }

    @Override
    public void caseALastexpvirExplistvir(ALastexpvirExplistvir node) {
        this.returnValue = new SaLExp(apply(node.getExpression()), null);
    }

    @Override
    public void caseAFuncargAppelfunc(AFuncargAppelfunc node) {
        String nom = node.getNom().getText();
        this.returnValue = new SaAppel(nom, apply(node.getExplist()));
    }

    @Override
    public void caseAFuncAppelfunc(AFuncAppelfunc node) {
        String nom = node.getNom().getText();
        this.returnValue = new SaAppel(nom, null);
    }
}
