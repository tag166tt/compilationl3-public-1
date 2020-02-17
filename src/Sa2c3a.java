import c3a.*;
import sa.*;
import ts.Ts;
import ts.TsItemFct;
import ts.TsItemVar;

import java.util.ArrayList;
import java.util.List;

public class Sa2c3a extends SaDepthFirstVisitor<C3aOperand> {

    private C3a c3a = new C3a();
    private Ts tableGlobale;
    private Ts tableLocaleCourante = null;

    public Sa2c3a(SaNode saRoot, Ts table) {
        this.tableGlobale = table;
        saRoot.accept(this);
    }

    public C3a getC3a() {
        return c3a;
    }

    @Override
    public C3aOperand visit(SaProg node) {
        return super.visit(node);
    }

    @Override
    public C3aOperand visit(SaDecFonc node) {
        TsItemFct tsItemFct = tableGlobale.getFct(node.getNom());
        c3a.ajouteInst(new C3aInstFBegin(tsItemFct, "entree fonction"));
        tableLocaleCourante = tableGlobale.getTableLocale(node.getNom());
        super.visit(node);
        tableLocaleCourante = null;
        c3a.ajouteInst(new C3aInstFEnd(""));
        return null;
    }

    @Override
    public C3aOperand visit(SaInstEcriture node) {
        c3a.ajouteInst(new C3aInstWrite(
                node.getArg().accept(this),
                ""
        ));
        return null;
    }

    @Override
    public C3aOperand visit(SaExpAdd node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);

        C3aTemp temp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstAdd(op1, op2, temp, ""));
        return temp;
    }

    @Override
    public C3aOperand visit(SaExpSub node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);

        C3aTemp temp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstSub(op1, op2, temp, ""));
        return temp;
    }

    @Override
    public C3aOperand visit(SaExpDiv node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);

        C3aTemp temp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstDiv(op1, op2, temp, ""));
        return temp;
    }

    @Override
    public C3aOperand visit(SaExpMult node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);

        C3aTemp temp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstMult(op1, op2, temp, ""));
        return temp;
    }

    @Override
    public C3aOperand visit(SaExpAnd node) {
        /*
        if op1 = 0 goto labelIfResultIsFalse
        if op2 = 0 goto labelIfResultIfFalse
        result = 1
        goto result
        $labelIfResultIfFalse: result = 0
        $labelResult: use result
         */

        C3aTemp result = c3a.newTemp();
        C3aLabel labelResult = c3a.newAutoLabel();
        C3aLabel labelIfResultIsFalse = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aConstant falseConstant = new C3aConstant(0);
        c3a.ajouteInst(new C3aInstJumpIfEqual(op1, falseConstant, labelIfResultIsFalse, ""));
        c3a.ajouteInst(new C3aInstJumpIfEqual(op2, falseConstant, labelIfResultIsFalse, ""));
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(1), result, ""));
        c3a.ajouteInst(new C3aInstJump(labelResult, ""));
        c3a.addLabelToNextInst(labelIfResultIsFalse);
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(0), result, ""));
        c3a.addLabelToNextInst(labelResult);
        return result;
    }

    @Override
    public C3aOperand visit(SaExpOr node) {
        /*
        if op1 != 0 goto labelIfResultIsTrue
        if op2 != 0 goto labelIfResultIsTrue
        result = 0
        goto labelResult
        $labelIfResultIsTrue: result = 1
        $labelResult: use result
         */

        C3aTemp result = c3a.newTemp();
        C3aLabel labelResult = c3a.newAutoLabel();
        C3aLabel labelIfResultIsTrue = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aConstant falseConstant = new C3aConstant(0);
        c3a.ajouteInst(new C3aInstJumpIfNotEqual(op1, falseConstant, labelIfResultIsTrue, ""));
        c3a.ajouteInst(new C3aInstJumpIfNotEqual(op2, falseConstant, labelIfResultIsTrue, ""));
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(0), result, ""));
        c3a.ajouteInst(new C3aInstJump(labelResult, ""));
        c3a.addLabelToNextInst(labelIfResultIsTrue);
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(1), result, ""));
        c3a.addLabelToNextInst(labelResult);
        return result;
    }

    @Override
    public C3aOperand visit(SaExpInf node) {
        /*
        result = 1
        if op1 < op2 goto labelResult
        result = 0
        $labelResult: use result
         */

        C3aTemp result = c3a.newTemp();
        C3aLabel labelResult = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(1), result, ""));
        c3a.ajouteInst(new C3aInstJumpIfLess(op1, op2, labelResult, ""));
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(0), result, ""));
        c3a.addLabelToNextInst(labelResult);
        return result;
    }

    @Override
    public C3aOperand visit(SaExpInt node) {
        return new C3aConstant(node.getVal());
    }

    @Override
    public C3aOperand visit(SaInstAffect node) {
        C3aOperand var = node.getLhs().accept(this);
        C3aOperand exp = node.getRhs().accept(this);
        c3a.ajouteInst(new C3aInstAffect(exp, var, ""));
        return null;
    }

    @Override
    public C3aOperand visit(SaExpVar node) {
        return node.getVar().accept(this);
    }

    @Override
    public C3aOperand visit(SaVarSimple node) {
        TsItemVar var = tableLocaleCourante.getVar(node.getNom());
        if (var == null) var = tableGlobale.getVar(node.getNom());
        return new C3aVar(var, null);
    }

    @Override
    public C3aOperand visit(SaVarIndicee node) {
        TsItemVar var = tableGlobale.getVar(node.getNom());
        C3aOperand indice = node.getIndice().accept(this);
        // TODO: instanceof Constant
        return new C3aVar(var, indice);
    }

    @Override
    public C3aOperand visit(SaInstRetour node) {
        C3aOperand op = node.getVal().accept(this);
        c3a.ajouteInst(new C3aInstReturn(op, ""));
        return null;
    }

    @Override
    public C3aOperand visit(SaExpAppel node) {
        return node.getVal().accept(this);
    }

    @Override
    public C3aOperand visit(SaAppel node) {
        if (node.getArguments() != null) {
            for (SaExp exp: toList(node.getArguments()))
                c3a.ajouteInst(new C3aInstParam(exp.accept(this), ""));
        }

        C3aFunction function = new C3aFunction(tableGlobale.getFct(node.getNom()));
        C3aTemp temp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstCall(function, temp, ""));
        return temp;
    }

    private List<SaExp> toList(SaLExp list) {
        List<SaExp> out = new ArrayList<>();
        out.add(list.getTete());
        while (list.getQueue() != null) {
            list = list.getQueue();
            out.add(list.getTete());
        }
        return out;
    }

    @Override
    public C3aOperand visit(SaInstSi node) {
        /*
        if test = 0 goto labelEnd
        $labelAlors
        $labelEnd
         */

        C3aLabel labelElse = c3a.newAutoLabel();
        C3aLabel labelEnd = c3a.newAutoLabel();

        C3aOperand opTest = node.getTest().accept(this);

        if (node.getSinon() == null) {
            c3a.ajouteInst(new C3aInstJumpIfEqual(opTest, new C3aConstant(0),  labelEnd, ""));
            node.getAlors().accept(this);
        } else {
            c3a.ajouteInst(new C3aInstJumpIfEqual(opTest, new C3aConstant(0),  labelElse, ""));
            node.getAlors().accept(this);
            c3a.ajouteInst(new C3aInstJump(labelEnd, ""));
            c3a.addLabelToNextInst(labelElse);
            node.getSinon().accept(this);
        }

        c3a.addLabelToNextInst(labelEnd);
        return null;
    }

    @Override
    public C3aOperand visit(SaInstTantQue node) {
        /*
        $labelTest: do test
        if $test = 0 goto $labelEnd
        do code
        goto $labelTest
        $labelEnd: next
         */

        C3aLabel labelTest = c3a.newAutoLabel();
        C3aLabel labelEnd = c3a.newAutoLabel();
        c3a.addLabelToNextInst(labelTest);
        C3aOperand testResult = node.getTest().accept(this);
        c3a.ajouteInst(new C3aInstJumpIfEqual(testResult, new C3aConstant(0), labelEnd, ""));
        node.getFaire().accept(this);
        c3a.ajouteInst(new C3aInstJump(labelTest, ""));
        c3a.addLabelToNextInst(labelEnd);
        return null;
    }

    @Override
    public C3aOperand visit(SaExpEqual node) {
        // WARNING: there are no matching reference file for this method

        /*
        temp = 1
        if op1 = op2 goto $labelEnd
        temp = 0
        $labelEnd: use temp
         */

        C3aTemp temp = c3a.newTemp();
        C3aLabel labelEnd = c3a.newAutoLabel();

        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);

        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(1), temp, ""));
        c3a.ajouteInst(new C3aInstJumpIfEqual(op1, op2, labelEnd, ""));
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(0), temp, ""));
        c3a.addLabelToNextInst(labelEnd);

        return temp;
    }

    @Override
    public C3aOperand visit(SaExpNot node) {
        /*
        temp = 0
        if op1 = 1 goto end
        temp = 1
        $end: use temp
         */

        // TODO: la grammaire n'a pas de '!'?

        C3aTemp temp = c3a.newTemp();
        C3aLabel labelEnd = c3a.newAutoLabel();

        C3aOperand op1 = node.getOp1().accept(this);

        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(0), temp, ""));
        c3a.ajouteInst(new C3aInstJumpIfEqual(op1, new C3aConstant(1), labelEnd, ""));
        c3a.ajouteInst(new C3aInstAffect(new C3aConstant(1), temp, ""));

        c3a.addLabelToNextInst(labelEnd);
        return temp;
    }

    @Override
    public C3aOperand visit(SaExpLire node) {
        // TODO: pas idéal
        C3aTemp temp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstRead(temp, ""));
        return temp;
    }
}
