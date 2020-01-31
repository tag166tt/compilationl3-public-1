import sa.*;
import sc.analysis.DepthFirstAdapter;
import sc.node.*;

public class Sc2sa extends DepthFirstAdapter {
    private SaNode returnValue;

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

    // 50: programme = {decvarldecfonc} optdecvar listedecfonc
    @Override
    public void caseADecvarldecfoncProgramme(ADecvarldecfoncProgramme node) {
        SaLDec vars = (SaLDec) apply(node.getOptdecvar());
        SaLDec funcs = (SaLDec) apply(node.getListedecfonc());
        this.returnValue = new SaProg(vars, funcs);
    }

    // 51: programme = {ldecfonc} listedecfonc
    @Override
    public void caseALdecfoncProgramme(ALdecfoncProgramme node) {
        SaLDec funcs = (SaLDec) apply(node.getListedecfonc());
        this.returnValue = new SaProg(null, funcs);
    }

    // -----------------------------------------------------------------------

    // 53: optdecvar = listedecvar point_virgule
    @Override
    public void caseAOptdecvar(AOptdecvar node) {
        apply(node.getListedecvar());
    }

    // -----------------------------------------------------------------------

    // 56: listedecvar = {decvarldecvar} decvar listedecvarbis
    @Override
    public void caseADecvarldecvarListedecvar(ADecvarldecvarListedecvar node) {
        SaDecVar tete = (SaDecVar) apply(node.getDecvar());
        SaLDec queue = (SaLDec) apply(node.getListedecvarbis());
        this.returnValue = new SaLDec(tete, queue);
    }

    // 57: listedecvar = {decvar} decvar
    @Override
    public void caseADecvarListedecvar(ADecvarListedecvar node) {
        SaDec tete = (SaDec) apply(node.getDecvar());
        this.returnValue = new SaLDec(tete, null);
    }

    // -----------------------------------------------------------------------

    // 60: listedecvarbis = {decvarldecvar} virgule decvar listedecvarbis
    @Override
    public void caseADecvarldecvarListedecvarbis(ADecvarldecvarListedecvarbis node) {
        SaDecVar decvar = (SaDecVar) apply(node.getDecvar());
        SaLDec listDecVarBis = (SaLDec) apply(node.getListedecvarbis());
        this.returnValue = new SaLDec(decvar, listDecVarBis);
    }

    // 61: listedecvarbis = {decvar} virgule decvar
    @Override
    public void caseADecvarListedecvarbis(ADecvarListedecvarbis node) {
        SaDecVar decvar = (SaDecVar) apply(node.getDecvar());
        this.returnValue = new SaLDec(decvar, null);
    }

    // -----------------------------------------------------------------------

    // 64: decvar = {decvarentier} entier identif
    @Override
    public void caseADecvarentierDecvar(ADecvarentierDecvar node) {
        String nom = node.getIdentif().getText();
        returnValue = new SaDecVar(nom);
    }

    // 65: decvar = {decvartableau} entier identif crochet_ouvrant nombre crochet_fermant ;
    @Override
    public void caseADecvartableauDecvar(ADecvartableauDecvar node) {
        String nom = node.getIdentif().getText();
        int cap = Integer.parseInt(node.getNombre().getText());
        returnValue = new SaDecTab(nom, cap);
    }

    // -----------------------------------------------------------------------

    // 68: listedecfonc = {ldecfoncrec} decfonc listedecfonc |
    @Override
    public void caseALdecfoncrecListedecfonc(ALdecfoncrecListedecfonc node) {
        SaDecFonc fonc = (SaDecFonc) apply(node.getDecfonc());
        SaLDec liste = (SaLDec) apply(node.getListedecfonc());
        this.returnValue = new SaLDec(fonc, liste);
    }

    // 69: listedecfonc = {ldecfoncfinal}
    @Override
    public void caseALdecfoncfinalListedecfonc(ALdecfoncfinalListedecfonc node) {
        this.returnValue = null;
    }

    // -----------------------------------------------------------------------

    // 72: decfonc = {decvarinstr} identif listeparam optdecvar instrbloc
    @Override
    public void caseADecvarinstrDecfonc(ADecvarinstrDecfonc node) {
        String nom = node.getIdentif().getText();
        SaLDec params = (SaLDec) apply(node.getListeparam());
        SaLDec vars = (SaLDec) apply(node.getOptdecvar());
        SaInstBloc contenu = (SaInstBloc) apply(node.getInstrbloc());
        this.returnValue = new SaDecFonc(nom, params, vars, contenu);
    }

    // 73: decfonc = {instr} identif listeparam instrbloc
    @Override
    public void caseAInstrDecfonc(AInstrDecfonc node) {
        String nom = node.getIdentif().getText();
        SaLDec params = (SaLDec) apply(node.getListeparam());
        SaInstBloc contenu = (SaInstBloc) apply(node.getInstrbloc());
        this.returnValue = new SaDecFonc(nom, params, null, contenu);
    }

    // -----------------------------------------------------------------------

    // 76: listeparam = {sansparam} parenthese_ouvrante parenthese_fermante
    @Override
    public void caseASansparamListeparam(ASansparamListeparam node) {
        this.returnValue = null;
    }

    // 77: listeparam = {avecparam} parenthese_ouvrante listedecvar parenthese_fermante
    @Override
    public void caseAAvecparamListeparam(AAvecparamListeparam node) {
        apply(node.getListedecvar());
    }

    // -----------------------------------------------------------------------

    // 80: instr = {instraffect} instraffect
    @Override
    public void caseAInstraffectInstr(AInstraffectInstr node) {
        apply(node.getInstraffect());
    }

    // 81: instr = {instrbloc } instrbloc
    @Override
    public void caseAInstrblocInstr(AInstrblocInstr node) {
        apply(node.getInstrbloc());
    }

    // 82: instr = instrsi } instrsi
    @Override
    public void caseAInstrsiInstr(AInstrsiInstr node) {
        apply(node.getInstrsi());
    }

    // 83: instr = {instrtantque } instrtantque
    @Override
    public void caseAInstrtantqueInstr(AInstrtantqueInstr node) {
        apply(node.getInstrtantque());
    }

    // 84: instr = {instrappel } instrappel
    @Override
    public void caseAInstrappelInstr(AInstrappelInstr node) {
        apply(node.getInstrappel());
    }

    // 85: instr = {instrretour } instrretour
    @Override
    public void caseAInstrretourInstr(AInstrretourInstr node) {
        apply(node.getInstrretour());
    }

    // 86: instr = {instrecriture } instrecriture
    @Override
    public void caseAInstrecritureInstr(AInstrecritureInstr node) {
        apply(node.getInstrecriture());
    }

    // -----------------------------------------------------------------------

    // 87: {instrvide} instrvide
    @Override
    public void caseAInstrvideInstr(AInstrvideInstr node) {
        this.returnValue = null;
    }

    // -----------------------------------------------------------------------

    // 89: instraffect = var egal exp point_virgule
    @Override
    public void caseAInstraffect(AInstraffect node) {
        SaVar var = (SaVar) apply(node.getVar());
        SaExp exp = (SaExp) apply(node.getExp());
        this.returnValue = new SaInstAffect(var, exp);
    }

    // -----------------------------------------------------------------------

    // 91: instrbloc = accolade_ouvrante listeinst accolade_fermante
    @Override
    public void caseAInstrbloc(AInstrbloc node) {
        SaLInst blc = (SaLInst) apply(node.getListeinst());
        this.returnValue = new SaInstBloc(blc);
    }

    // -----------------------------------------------------------------------

    // 96: listeinst = {linstrec} instr listeinst
    @Override
    public void caseALinstrecListeinst(ALinstrecListeinst node) {
        SaInst tete = (SaInst) apply(node.getInstr());
        SaLInst queue = (SaLInst) apply(node.getListeinst());
        this.returnValue = new SaLInst(tete, queue);
    }

    // 97: listeinst = {linstfinal}
    @Override
    public void caseALinstfinalListeinst(ALinstfinalListeinst node) {
        this.returnValue = null;
    }

    // -----------------------------------------------------------------------

    // 100: instrsi = {avecsinon} si exp alors instrbloc instrsinon
    @Override
    public void caseAAvecsinonInstrsi(AAvecsinonInstrsi node) {
        SaExp exp = (SaExp) apply(node.getExp());
        SaInstBloc bloc = (SaInstBloc) apply(node.getInstrbloc());
        SaInstBloc blocSinon = (SaInstBloc) apply(node.getInstrsinon());
        this.returnValue = new SaInstSi(exp, bloc, blocSinon);
    }

    // 101: instrsi = {sanssinon} si exp alors instrbloc
    @Override
    public void caseASanssinonInstrsi(ASanssinonInstrsi node) {
        SaExp exp = (SaExp) apply(node.getExp());
        SaInstBloc bloc = (SaInstBloc) apply(node.getInstrbloc());
        this.returnValue = new SaInstSi(exp, bloc, null);
    }

    // -----------------------------------------------------------------------

    // 103: instrsinon = sinon instrbloc
    @Override
    public void caseAInstrsinon(AInstrsinon node) {
        apply(node.getInstrbloc());
    }

    // -----------------------------------------------------------------------

    // 105: instrtantque = tantque exp faire instrbloc
    @Override
    public void caseAInstrtantque(AInstrtantque node) {
        SaExp exp = (SaExp) apply(node.getExp());
        SaInstBloc bloc = (SaInstBloc) apply(node.getInstrbloc());
        this.returnValue = new SaInstTantQue(exp, bloc);
    }

    // -----------------------------------------------------------------------

    // 107: instrappel = appelfct point_virgule
    @Override
    public void caseAInstrappel(AInstrappel node) {
        apply(node.getAppelfct());
    }

    // -----------------------------------------------------------------------

    // 109: instrretour = retour exp point_virgule
    @Override
    public void caseAInstrretour(AInstrretour node) {
        SaExp exp = (SaExp) apply(node.getExp());
        this.returnValue = new SaInstRetour(exp);
    }

    // -----------------------------------------------------------------------

    // 111: instrecriture = ecrire parenthese_ouvrante exp parenthese_fermante point_virgule
    @Override
    public void caseAInstrecriture(AInstrecriture node) {
        SaExp exp = (SaExp) apply(node.getExp());
        this.returnValue = new SaInstEcriture(exp);
    }

    // -----------------------------------------------------------------------

    // 113: instrvide = point_virgule
    @Override
    public void caseAInstrvide(AInstrvide node) {
        this.returnValue = null;
    }

    // -----------------------------------------------------------------------

    // 128: exp = {ou} exp ou exp1
    @Override
    public void caseAOuExp(AOuExp node) {
        SaExp op1, op2;
        node.getExp().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getExp1().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpOr(op1, op2);
    }

    // 129: exp = {exp1} exp1
    @Override
    public void caseAExp1Exp(AExp1Exp node) {
        // TODO: correct?
        node.getExp1().apply(this);
    }

    // 132: exp1 = {et} exp1 et exp2
    @Override
    public void caseAEtExp1(AEtExp1 node) {
        SaExp op1, op2;
        node.getExp1().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getExp2().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpAnd(op1, op2);
    }

    // 133: exp1 = {exp2} exp2
    @Override
    public void caseAExp2Exp1(AExp2Exp1 node) {
        // TODO: correct?
        node.getExp2().apply(this);
    }

    // 136: exp2 = {inf} exp2 inferieur exp3
    @Override
    public void caseAInfExp2(AInfExp2 node) {
        SaExp op1, op2;
        node.getExp2().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getExp3().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpInf(op1, op2);
    }

    // 137: exp2 = {egal} exp2 egal exp3
    @Override
    public void caseAEgalExp2(AEgalExp2 node) {
        SaExp op1, op2;
        node.getExp2().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getExp3().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpEqual(op1, op2);
    }

    // 138: exp2 = {exp3} exp3
    @Override
    public void caseAExp3Exp2(AExp3Exp2 node) {
        // TODO: correct?
        node.getExp3().apply(this);
    }

    // 141: exp3 = {plus} exp3 plus exp4
    @Override
    public void caseAPlusExp3(APlusExp3 node) {
        SaExp op1, op2;
        node.getExp3().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getExp4().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpAdd(op1, op2);
    }

    // 142: exp3 = {moins} exp3 moins exp4
    @Override
    public void caseAMoinsExp3(AMoinsExp3 node) {
        SaExp op1, op2;
        node.getExp3().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getExp4().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpSub(op1, op2);
    }

    // 143: exp3 = {exp4} exp4
    @Override
    public void caseAExp4Exp3(AExp4Exp3 node) {
        // TODO: correct?
        node.getExp4().apply(this);
    }

    // 146: exp4 = {fois} exp4 fois exp5
    @Override
    public void caseAFoisExp4(AFoisExp4 node) {
        SaExp op1, op2;
        node.getExp4().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getExp5().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpMult(op1, op2);
    }

    // 147: exp4 = {divise} exp4 divise exp5
    @Override
    public void caseADiviseExp4(ADiviseExp4 node) {
        SaExp op1, op2;
        node.getExp4().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getExp5().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpDiv(op1, op2);
    }

    // 148: exp4 = {exp5} exp5
    @Override
    public void caseAExp5Exp4(AExp5Exp4 node) {
        // TODO: correct?
        node.getExp5().apply(this);
    }

    // 151: exp5 = {non} non exp5
    @Override
    public void caseANonExp5(ANonExp5 node) {
        SaExp op1;
        node.getExp5().apply(this);
        op1 = (SaExp) this.returnValue;
        this.returnValue = new SaExpNot(op1);
    }

    // 152: exp5 = {exp6} exp6
    @Override
    public void caseAExp6Exp5(AExp6Exp5 node) {
        // TODO: correct?
        node.getExp6().apply(this);
    }

    // 155: exp6 = {nombre} nombre
    @Override
    public void caseANombreExp6(ANombreExp6 node) {
        // TODO: correct?
        // On aurait pu utiliser:
        // node.getNombre().apply(this);
        // Mais comment récupérer la valeur après?


        int value = Integer.parseInt(node.getNombre().getText());
        this.returnValue = new SaExpInt(value);
    }

    // 156: exp6 = {appelfct} appelfct
    @Override
    public void caseAAppelfctExp6(AAppelfctExp6 node) {
        SaAppel op1;
        node.getAppelfct().apply(this);
        op1 = (SaAppel) this.returnValue;
        this.returnValue = new SaExpAppel(op1);
    }

    // 157: exp6 = {var} var
    @Override
    public void caseAVarExp6(AVarExp6 node) {
        SaVar op1;
        node.getVar().apply(this);
        op1 = (SaVar) this.returnValue;
        this.returnValue = new SaExpVar(op1);
    }

    // 158: exp6 = {parentheses} parenthese_ouvrante exp parenthese_fermante
    @Override
    public void caseAParenthesesExp6(AParenthesesExp6 node) {
        // TODO: correct?
        node.getExp().apply(this);
    }

    // 159: exp6 = {lire} lire parenthese_ouvrante parenthese_fermante
    @Override
    public void caseALireExp6(ALireExp6 node) {
        // TODO: lire? il n'y a pas d'expr entre les parenthèses?
        this.returnValue = new SaExpLire();
    }

    // 164: var = {vartab} identif crochet_ouvrant exp crochet_fermant
    @Override
    public void caseAVartabVar(AVartabVar node) {
        // TODO: correct?
        String nom = node.getIdentif().getText();
        node.getExp().apply(this);
        SaExp op1 = (SaExp) this.returnValue;
        this.returnValue = new SaVarIndicee(nom, op1);
    }

    // 165: var = {varsimple} identif
    @Override
    public void caseAVarsimpleVar(AVarsimpleVar node) {
        // TODO: correct?
        String nom = node.getIdentif().getText();
        this.returnValue = new SaVarSimple(nom);
    }

    // 168: listeexp = {recursif} exp listeexpbis
    @Override
    public void caseARecursifListeexp(ARecursifListeexp node) {
        node.getExp().apply(this);
        SaExp op1 = (SaExp) this.returnValue;
        node.getListeexpbis().apply(this);
        SaLExp op2 = (SaLExp) this.returnValue;
        this.returnValue = new SaLExp(op1, op2);
    }

    // 169: listeexp = {final} exp
    @Override
    public void caseAFinalListeexp(AFinalListeexp node) {
        // TODO: correct?
        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;
        this.returnValue = new SaLExp(exp, null);
    }

    // 172: listeexpbis = {final} virgule exp
    @Override
    public void caseAFinalListeexpbis(AFinalListeexpbis node) {
        // TODO: correct?
        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;
        this.returnValue = new SaLExp(exp, null);
    }

    // 173: listeexpbis = {recursif} virgule exp listeexpbis
    @Override
    public void caseARecursifListeexpbis(ARecursifListeexpbis node) {
        // TODO: correct?
        node.getExp().apply(this);
        SaExp op1 = (SaExp) this.returnValue;
        node.getListeexpbis().apply(this);
        SaLExp op2 = (SaLExp) this.returnValue;
        this.returnValue = new SaLExp(op1, op2);
    }

    // 176: appelfct = {avecparam} identif parenthese_ouvrante listeexp parenthese_fermante
    @Override
    public void caseAAvecparamAppelfct(AAvecparamAppelfct node) {
        // TODO: correct?
        String nom = node.getIdentif().getText();
        node.getListeexp().apply(this);
        SaLExp args = (SaLExp) this.returnValue;
        this.returnValue = new SaAppel(nom, args);
    }

    // 177: appelfct = {sansparam} identif parenthese_ouvrante parenthese_fermante
    @Override
    public void caseASansparamAppelfct(ASansparamAppelfct node) {
        // TODO: correct?
        String nom = node.getIdentif().getText();
        this.returnValue = new SaAppel(nom, new SaLExp(null, null));
    }

    public SaNode getRoot() {
        return returnValue;
    }
}
