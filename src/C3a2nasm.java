import c3a.*;
import nasm.*;
import ts.Ts;
import ts.TsItemVar;

import java.util.Map;

public class C3a2nasm implements C3aVisitor<NasmOperand> {
    private Nasm nasm;
    private NasmRegister eax;
    private NasmRegister ebp;
    private NasmRegister esp;

    private NasmOperand label;
    private NasmOperand oper1;
    private NasmOperand oper2;
    private NasmOperand dest;

    private boolean DEBUG = false;

    private int argSize = -1;
    private int nbArgs = 0;

    private int div_offset = 0;
    private int jmp_eq_offset = 0;
    private int jmp_l_offset = 0;
    private int var_offset = 1;

    private Map<String, TsItemVar> localVar;

    public C3a2nasm(C3a c3a, Ts table) {
        NasmRegister ebx = new NasmRegister(Nasm.REG_EBX);
        ebx.colorRegister(Nasm.REG_EBX);
        this.eax = new NasmRegister(Nasm.REG_EAX);
        eax.colorRegister(Nasm.REG_EAX);
        this.ebp = new NasmRegister(Nasm.REG_EBP);
        ebp.colorRegister(Nasm.REG_EBP);
        this.esp = new NasmRegister(Nasm.REG_ESP);
        esp.colorRegister(Nasm.REG_ESP);

        this.nasm = new Nasm(table);

        nasm.ajouteInst(new NasmCall(null, new NasmLabel("main"), ""));
        nasm.ajouteInst(new NasmMov(null, ebx, new NasmConstant(0), " valeur de retour du programme"));
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
        var_offset = 1;
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
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
        var_offset = 1;
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
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
        var_offset = 1;
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
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
        var_offset = 1;
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        if (DEBUG) System.out.println(String.format("OP1 : %s", inst.op1.getClass().getSimpleName()));
        if (DEBUG) System.out.println(String.format("OP2 : %s", inst.op2.getClass().getSimpleName()));
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        nasm.ajouteInst(new NasmMov(null, eax, oper1, ""));
        NasmOperand register = new NasmRegister(4 + div_offset); //TODO find proper register number (don't know how) (will fix div2 prio3/4)
        nasm.ajouteInst(new NasmMov(label, register, oper2, ""));
        nasm.ajouteInst(new NasmDiv(null, register, ""));
        NasmRegister r0 = new NasmRegister(div_offset / 2);
        nasm.ajouteInst(new NasmMov(null, r0, eax, ""));
        div_offset += 2;
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstFBegin inst) {
        var_offset = 1;
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        NasmOperand label = new NasmLabel(inst.val.identif);
        nasm.ajouteInst(new NasmPush(label, ebp, "sauvegarde la valeur de ebp"));
        nasm.ajouteInst(new NasmMov(null, ebp, esp, "nouvelle valeur de ebp"));
        argSize = inst.val.getTable().nbVar();
        nbArgs = inst.val.getNbArgs();
        localVar = inst.val.getTable().variables;
        nasm.ajouteInst(new NasmSub(null, esp, new NasmConstant(4 * argSize), "allocation des variables locales"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstFEnd inst) {
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        nasm.ajouteInst(new NasmAdd(label, esp, new NasmConstant(4 * argSize), "désallocation des variables locales"));
        nasm.ajouteInst(new NasmPop(null, ebp, "restaure la valeur de ebp"));
        nasm.ajouteInst(new NasmRet(null, ""));
        argSize = -1;
        return null;
    }

    @Override
    public NasmOperand visit(C3aInst inst) {
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstCall inst) {
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        nasm.ajouteInst(new NasmSub(null, esp, new NasmConstant(4), "allocation mémoire pour la valeur de retour"));
        nasm.ajouteInst(new NasmCall(null, new NasmLabel(inst.op1.val.identif), ""));
        nasm.ajouteInst(new NasmPop(null, new NasmRegister(1), "récupération de la valeur de retour"));
        if (inst.op1.val.nbArgs > 0)
            nasm.ajouteInst(new NasmAdd(null, esp, new NasmConstant(8), "désallocation des arguments"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstAffect inst) {
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand op1 = inst.op1.accept(this);
        NasmOperand result = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, result, op1, "Affect"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstRead inst) {
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
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
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand operand = inst.op1.accept(this);
        nasm.ajouteInst(new NasmMov(label, eax, operand, "Write 1"));
        nasm.ajouteInst(new NasmCall(null, new NasmLabel("iprintLF"), "Write 2"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfLess inst) {
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        if (!(inst.op1 instanceof C3aTemp)) {
            NasmRegister r3 = new NasmRegister(3);
            nasm.ajouteInst(new NasmMov(null, r3, inst.op1.accept(this), "JumpIfLess 1"));
            nasm.ajouteInst(new NasmCmp(null, r3, inst.op2.accept(this), "on passe par un registre temporaire"));
            NasmOperand address = inst.result.accept(this);
            nasm.ajouteInst(new NasmJl(null, address, "JumpIfLess 2"));
        } else {
            NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
            NasmRegister r2 = new NasmRegister(3 + jmp_l_offset); //TODO find proper register (2 or 3 but don't know which one at what time) (will fix prio5/6 tantque)
            jmp_l_offset += 2;
            nasm.ajouteInst(new NasmCmp(label, r2, inst.op2.accept(this), "JumpIfLess 1"));
            NasmOperand address = inst.result.accept(this);
            nasm.ajouteInst(new NasmJl(null, address, "JumpIfLess 2"));
        }
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfEqual inst) {
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        if (DEBUG) System.out.println(String.format("OP1 : %s", inst.op1.getClass().getSimpleName()));
        if (DEBUG) System.out.println(String.format("OP2 : %s", inst.op2.getClass().getSimpleName()));
        if (!(inst.op1 instanceof C3aTemp)) {
            NasmRegister r = new NasmRegister(2 + jmp_eq_offset); //TODO besoin de 3 pour les ET mais 2 pour les SI/SINON
            nasm.ajouteInst(new NasmMov(null, r, inst.op1.accept(this), "JumpIfEqual 1"));
            nasm.ajouteInst(new NasmCmp(null, r, inst.op2.accept(this), "on passe par un registre temporaire"));
            jmp_eq_offset++;
        } else {
            NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
            NasmRegister r = new NasmRegister(1 + jmp_eq_offset);
            nasm.ajouteInst(new NasmCmp(label, r, inst.op2.accept(this), "JumpIfEqual 1"));
            jmp_eq_offset += 3;
        }
        nasm.ajouteInst(new NasmJe(null, inst.result.accept(this), "JumpIfEqual 2"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfNotEqual inst) {
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        if (!(inst.op1 instanceof C3aTemp)) {
            NasmOperand address = inst.result.accept(this);
            NasmRegister r3 = new NasmRegister(3 + jmp_eq_offset);
            jmp_eq_offset++;
            nasm.ajouteInst(new NasmMov(null, r3, inst.op1.accept(this), "jumpIfNotEqual 1"));
            nasm.ajouteInst(new NasmCmp(null, r3, inst.op2.accept(this), "on passe par un registre temporaire"));
            nasm.ajouteInst(new NasmJne(null, address, "jumpIfNotEqual 2"));
        } else {
            NasmOperand address = inst.result.accept(this);
            NasmRegister r3 = new NasmRegister(3 + jmp_eq_offset);
            jmp_eq_offset++;
            NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
            nasm.ajouteInst(new NasmCmp(label, r3, inst.op1.accept(this), "jumpIfNotEqual 1"));
            nasm.ajouteInst(new NasmJne(null, address, "jumpIfNotEqual 2"));
        }
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJump inst) {
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand address = inst.result.accept(this);
        nasm.ajouteInst(new NasmJmp(label, address, "Jump"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstParam inst) {
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand op1 = inst.op1.accept(this);
        nasm.ajouteInst(new NasmPush(label, op1, "Param"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstReturn inst) {
        if (DEBUG) System.out.println(inst.getClass().getSimpleName());
        NasmRegister r = new NasmRegister(0);
        nasm.ajouteInst(new NasmMov(null, new NasmAddress(ebp, '+', new NasmConstant(localVar.size())), r, "ecriture de la valeur de retour"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aConstant oper) {
        if (DEBUG) System.out.println(oper.getClass().getSimpleName());
        int val = oper.val;
        return new NasmConstant(val);
    }

    @Override
    public NasmOperand visit(C3aLabel oper) {
        if (DEBUG) System.out.println(oper.getClass().getSimpleName());
        return new NasmLabel(oper.toString());
    }

    @Override
    public NasmOperand visit(C3aTemp oper) {
        if (DEBUG) System.out.println(oper.getClass().getSimpleName());
        return new NasmRegister(oper.num);
    }

    @Override
    public NasmOperand visit(C3aVar oper) {
        if (DEBUG) System.out.println(oper.getClass().getSimpleName());
        if (DEBUG) System.out.println(oper.item);
        TsItemVar varItem = oper.item;
        if (oper.index != null) {
            return new NasmAddress(new NasmLabel(varItem.getIdentif()), '+', oper.index.accept(this));
        } else if (varItem.isParam) {
            return new NasmAddress(ebp, '+', new NasmConstant(2 + varItem.portee.nbArg() - varItem.adresse));
        } else if (localVar.isEmpty()) {
            return new NasmAddress(new NasmLabel(varItem.getIdentif()));
        }
        if (DEBUG) System.out.println("loop out");
        int offset = var_offset;
        var_offset += 2;
        return new NasmAddress(ebp, '-', new NasmConstant(offset + varItem.portee.nbArg() - varItem.adresse));
    }

    @Override
    public NasmOperand visit(C3aFunction oper) {
        if (DEBUG) System.out.println(oper.getClass().getSimpleName());
        if (DEBUG) System.out.println("plopinou");
        return null;
    }
}
