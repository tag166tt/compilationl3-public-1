import c3a.*;
import nasm.*;
import ts.Ts;
import ts.TsItemVar;

public class C3a2nasm implements C3aVisitor<NasmOperand> {
    private C3a c3a;
    private Ts table;
    private Nasm nasm;
    private int nbArgs = -1;

    public C3a2nasm(C3a c3a, Ts table) {
        this.c3a = c3a;
        this.table = table;
        this.nasm = new Nasm(table);
        nasm.ajouteInst(new NasmCall(null, new NasmLabel("main"), ""));
        NasmRegister ebx = new NasmRegister(Nasm.REG_EBX);
        ebx.colorRegister(Nasm.REG_EBX);
        nasm.ajouteInst(new NasmMov(null, ebx, new NasmConstant(0), " valeur de retour du programme"));
        NasmRegister eax = new NasmRegister(Nasm.REG_EAX);
        eax.colorRegister(Nasm.REG_EAX);
        nasm.ajouteInst(new NasmMov(null, eax, new NasmConstant(1), ""));
        nasm.ajouteInst(new NasmInt(null, ""));
        for (C3aInst inst : c3a.listeInst) {
            inst.accept(this);
        }
    }

    public Nasm getNasm() {
        return nasm;
    }

    @Override
    public NasmOperand visit(C3aInstAdd inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        nasm.ajouteInst(new NasmAdd(null, dest, oper2, ""));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstSub inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        nasm.ajouteInst(new NasmSub(null, dest, oper2, ""));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstMult inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        nasm.ajouteInst(new NasmMul(null, dest, oper2, ""));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstDiv inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        NasmOperand patate = nasm.newRegister();
        nasm.ajouteInst(new NasmMov(label, patate, oper2, ""));
        nasm.ajouteInst(new NasmDiv(null, patate, ""));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstFBegin inst) {
        NasmOperand label = new NasmLabel(inst.val.identif);
        NasmRegister ebp = new NasmRegister(Nasm.REG_EBP);
        ebp.colorRegister(Nasm.REG_EBP);
        String comment = inst.comment;
        nasm.ajouteInst(new NasmPush(label, ebp, "sauvegarde la valeur de ebp"));
        NasmRegister esp = new NasmRegister(Nasm.REG_ESP);
        esp.colorRegister(Nasm.REG_ESP);
        nasm.ajouteInst(new NasmMov(null, ebp, esp, "nouvelle valeur de ebp"));
        nbArgs = inst.val.nbArgs;
        nasm.ajouteInst(new NasmSub(null, esp, new NasmConstant(nbArgs), "allocation des variables locales"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstFEnd inst) {
        NasmRegister ebp = new NasmRegister(Nasm.REG_EBP);
        ebp.colorRegister(Nasm.REG_EBP);
        NasmRegister esp = new NasmRegister(Nasm.REG_ESP);
        esp.colorRegister(Nasm.REG_ESP);
        nasm.ajouteInst(new NasmAdd(null, esp, new NasmConstant(nbArgs), "désallocation des variables locales"));
        nasm.ajouteInst(new NasmPop(null, ebp, "restaure la valeur de ebp"));
        nasm.ajouteInst(new NasmRet(null, ""));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInst inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstCall inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand op1 = inst.op1.accept(this);
        String comment = inst.comment;
        nasm.ajouteInst(new NasmCall(label, op1, comment));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstAffect inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand op1 = inst.op1.accept(this);
        NasmOperand result = inst.result.accept(this);
        String comment = inst.comment;
        nasm.ajouteInst(new NasmMov(label, new NasmAddress(result), op1, comment));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstRead inst) {
        //TODO: WTF IS THIS SHIT???!!!!!!!!
        /*
        t4 = read
        mov eax, sinput
        call readline
        call atoi
         */
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstWrite inst) {
        NasmRegister eax = new NasmRegister(Nasm.REG_EAX);
        eax.colorRegister(Nasm.REG_EAX);
        NasmOperand operand = inst.op1.accept(this);
        nasm.ajouteInst(new NasmMov(null, eax, operand, "Write 1"));
        nasm.ajouteInst(new NasmCall(null, new NasmLabel("iprintLF"), "Write 2"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfLess inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand address = inst.result.accept(this);
        String comment = inst.comment;
        nasm.ajouteInst(new NasmJl(label, address, comment));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfEqual inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand address = inst.result.accept(this);
        String comment = inst.comment;
        nasm.ajouteInst(new NasmJe(label, address, comment));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfNotEqual inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand address = inst.result.accept(this);
        String comment = inst.comment;
        nasm.ajouteInst(new NasmJne(label, address, comment));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJump inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand address = inst.result.accept(this);
        String comment = inst.comment;
        nasm.ajouteInst(new NasmJmp(label, address, comment));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstParam inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand op1 = inst.op1.accept(this);
        String comment = inst.comment;
        nasm.ajouteInst(new NasmPush(label, op1, comment));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstReturn inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        String comment = inst.comment;
        nasm.ajouteInst(new NasmRet(label, comment));
        return null;
    }

    @Override
    public NasmOperand visit(C3aConstant oper) {
        int val = oper.val;
        return new NasmConstant(val);
    }

    @Override
    public NasmOperand visit(C3aLabel oper) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aTemp oper) {
        System.out.println("Patate chaude");
        return new NasmRegister(oper.num);
    }

    @Override
    public NasmOperand visit(C3aVar oper) {
        NasmRegister ebp = new NasmRegister(Nasm.REG_EBP);
        ebp.colorRegister(Nasm.REG_EBP);
        TsItemVar fct = oper.item;
        int address = 8 + 4 * fct.portee.nbArg() - 4 * fct.adresse;
        return new NasmAddress(ebp, '+', PROUT);
    }

    @Override
    public NasmOperand visit(C3aFunction oper) {
        return null;
    }
}
