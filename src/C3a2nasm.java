import c3a.*;
import nasm.*;
import ts.Ts;
import ts.TsItemFct;
import ts.TsItemVar;

public class C3a2nasm implements C3aVisitor<NasmOperand> {

    private final Nasm nasm;
    private TsItemFct currentFunction;

    public C3a2nasm(C3a c3a, Ts table) {
        this.nasm = new Nasm(table);
        nasm.setTempCounter(c3a.getTempCounter());
        addPrelude();
        acceptAllInstructions(c3a);
    }

    /**
     * Adds the following prelude:
     * call main
     * mov ebx, 0
     * mov eax, 1
     * int 0x80
     */
    private void addPrelude() {
        nasm.ajouteInst(new NasmCall(null, new NasmLabel("main"), ""));

        NasmRegister eax = newTempColoredRegister(Nasm.REG_EAX);
        NasmRegister ebx = newTempColoredRegister(Nasm.REG_EBX);

        nasm.ajouteInst(new NasmMov(null, ebx, new NasmConstant(0), " valeur de retour du programme"));
        nasm.ajouteInst(new NasmMov(null, eax, new NasmConstant(1), ""));
        nasm.ajouteInst(new NasmInt(null, ""));
    }

    /**
     * Calls {@link C3aInst#accept(C3aVisitor)} on all instructions stored in the C3a object.
     */
    private void acceptAllInstructions(C3a c3a) {
        for (C3aInst inst : c3a.listeInst)
            inst.accept(this);
    }

    /**
     * Creates a new colored register, incrementing the temp counter of nasm
     * in the process.
     * @implNote This is done to have the same register number as the reference files.
     * @param color The color of the register.
     * @return The colored register.
     */
    private NasmRegister newTempColoredRegister(int color) {
        NasmRegister temp = nasm.newRegister();
        temp.colorRegister(color);
        return temp;
    }

    /**
     * Returns the label for the specified instruction.
     * @param inst The instruction to extract the label from.
     * @return The label to use for the next NASM instruction.
     */
    private NasmLabel getLabel(C3aInst inst) {
        if (inst.label == null)
            return null;

        return (NasmLabel) inst.label.accept(this);
    }

    public Nasm getNasm() {
        return nasm;
    }

    /**
     * Adds two integers.
     *
     * args: op1 -> a, op2 -> b, result
     *
     * mov result, a
     * add result, b
     */
    @Override
    public NasmOperand visit(C3aInstAdd inst) {
        NasmLabel label = getLabel(inst);

        NasmOperand a = inst.op1.accept(this);
        NasmOperand b = inst.op2.accept(this);
        NasmOperand result = inst.result.accept(this);

        nasm.ajouteInst(new NasmMov(label, result, a, ""));
        nasm.ajouteInst(new NasmAdd(null, result, b, ""));

        return null;
    }

    /**
     * Soustraction entière.
     *
     * args: op1 -> a, op2 -> b, result
     *
     * mov result, a
     * sub result, b
     * sub
     */
    @Override
    public NasmOperand visit(C3aInstSub inst) {
        NasmOperand label = getLabel(inst);

        NasmOperand result = inst.result.accept(this);
        NasmOperand a = inst.op1.accept(this);
        NasmOperand b = inst.op2.accept(this);

        nasm.ajouteInst(new NasmMov(label, result, a, ""));
        nasm.ajouteInst(new NasmSub(null, result, b, ""));

        return null;
    }

    /**
     * Multiplication entière signée.
     *
     * args: op1 -> a, op2 -> b, result
     *
     * mov result, a
     * imul result, b
     */
    @Override
    public NasmOperand visit(C3aInstMult inst) {
        NasmOperand label = getLabel(inst);

        NasmOperand result = inst.result.accept(this);
        NasmOperand a = inst.op1.accept(this);
        NasmOperand b = inst.op2.accept(this);

        // TODO: warning: result should be a general purpose register, maybe assert?

        nasm.ajouteInst(new NasmMov(label, result, a, ""));
        nasm.ajouteInst(new NasmMul(label, result, b, ""));

        return null;
    }

    /**
     * Division entière signée.
     *
     * Utilisation de idiv:
     * idiv source
     * la division faite est: edx:eax / source
     * le ':' signifie concaténé
     * le quotient est mis dans eax
     * le reste est mis dans edx
     * source should be a general-purpose register or a memory location
     *
     * args: op1 -> a, op2 -> b, result
     *
     * mov eax, $a
     * mov $temp, $b    ; only if b is a constant
     * div $temp
     * mov $result, eax
     *
     * TODO: division par zéro?
     */
    @Override
    public NasmOperand visit(C3aInstDiv inst) {
        NasmOperand label = getLabel(inst);

        NasmOperand result = inst.result.accept(this);
        NasmRegister eax = newTempColoredRegister(Nasm.REG_EAX);

        NasmOperand b = inst.op2.accept(this);
        NasmOperand a = inst.op1.accept(this);

        nasm.ajouteInst(new NasmMov(label, eax, a, ""));

        // TODO: should we do this every time ?
        if (b instanceof NasmConstant) {
            // We have to use a temporary value, move b into it
            NasmRegister temp = nasm.newRegister();
            b = inst.op2.accept(this);
            nasm.ajouteInst(new NasmMov(null, temp, b, ""));
            b = temp;
        }

        nasm.ajouteInst(new NasmDiv(null, b, ""));
        nasm.ajouteInst(new NasmMov(null, result, eax, ""));

        return null;
    }

    /**
     * Ajout du début de fonction:
     * nomFct : push ebp
     *          mov ebp, esp
     *          sub esp, $localVarsSize
     */
    @Override
    public NasmOperand visit(C3aInstFBegin inst) {
        NasmOperand label = new NasmLabel(inst.val.identif);
        NasmRegister ebp = NasmRegister.colored(Nasm.REG_EBP);
        NasmRegister esp = NasmRegister.colored(Nasm.REG_ESP);
        nasm.ajouteInst(new NasmPush(label, ebp, "sauvegarde la valeur de ebp"));
        nasm.ajouteInst(new NasmMov(null, ebp, esp, "nouvelle valeur de ebp"));
        currentFunction = inst.val;
        // TODO
        NasmConstant localVarsSize = new NasmConstant(4 * currentFunction.getTable().nbVar());
        nasm.ajouteInst(new NasmSub(null, esp, localVarsSize, "allocation des variables locales"));
        return null;
    }

    /**
     * Ajout de la fin d'une fonction.
     *
     * add esp, 4*argSize
     */
    @Override
    public NasmOperand visit(C3aInstFEnd inst) {
        NasmOperand label = getLabel(inst);
        NasmConstant localVarsSize = new NasmConstant(4 * currentFunction.getTable().nbVar());
        NasmRegister esp = NasmRegister.colored(Nasm.REG_ESP);
        NasmRegister ebp = NasmRegister.colored(Nasm.REG_EBP);
        nasm.ajouteInst(new NasmAdd(label, esp, localVarsSize, "désallocation des variables locales"));
        nasm.ajouteInst(new NasmPop(null, ebp, "restaure la valeur de ebp"));
        nasm.ajouteInst(new NasmRet(null, ""));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInst inst) {
        throw new RuntimeException();
    }

    /**
     * Appel d'une fonction.
     * NOTE: les paramètres ont déjà été pris en compte, avec {@link C3a2nasm#visit(C3aInstParam)}.
     *
     * args: op1.val -> function, result
     *
     * sub esp, 4                       ; allocate space for return value
     * call ${function.identifier}
     * pop $result                      ; get back result value
     * add esp, 4 * ${function.nbArgs}  ; free memory of arguments (added with push)
     */
    @Override
    public NasmOperand visit(C3aInstCall inst) {
        NasmRegister esp = NasmRegister.colored(Nasm.REG_ESP);

        TsItemFct function = inst.op1.val;
        NasmOperand result = inst.result.accept(this);

        NasmOperand label = getLabel(inst);

        nasm.ajouteInst(new NasmSub(label, esp, new NasmConstant(4), "allocation mémoire pour la valeur de retour"));
        nasm.ajouteInst(new NasmCall(null, new NasmLabel(function.identif), ""));
        nasm.ajouteInst(new NasmPop(null, result, "récupération de la valeur de retour"));

        if (function.nbArgs > 0)
            nasm.ajouteInst(new NasmAdd(null, esp, new NasmConstant(4 * function.nbArgs), "désallocation des arguments"));

        return null;
    }

    /**
     * Affects a value to a variable.
     *
     * args: op1 -> value, result
     *
     * mov result, value
     */
    @Override
    public NasmOperand visit(C3aInstAffect inst) {
        NasmOperand label = getLabel(inst);

        NasmOperand result = inst.result.accept(this);
        NasmOperand value = inst.op1.accept(this);

        nasm.ajouteInst(new NasmMov(label, result, value, "Affect"));

        return null;
    }

    /**
     * Reads user input.
     *
     * args: result
     *
     * mov eax, sinput
     * call readline
     * call atoi
     * mov result, eax
     */
    @Override
    public NasmOperand visit(C3aInstRead inst) {
        NasmOperand label = getLabel(inst);
        NasmOperand result = inst.result.accept(this);
        NasmRegister eax = newTempColoredRegister(Nasm.REG_EAX);

        nasm.ajouteInst(new NasmMov(label, eax, new NasmLabel("sinput"), ""));
        nasm.ajouteInst(new NasmCall(null, new NasmLabel("readline"), ""));
        nasm.ajouteInst(new NasmCall(null, new NasmLabel("atoi"), ""));
        nasm.ajouteInst(new NasmMov(null, result, eax, ""));

        return null;
    }

    /**
     * Writes the result to the screen using the iprintLF method.
     *
     * args: op1 -> printed, result is not used (TODO)
     *
     * mov eax, printed
     * call iprintLF
     */
    @Override
    public NasmOperand visit(C3aInstWrite inst) {
        NasmOperand label = getLabel(inst);

        NasmRegister eax = newTempColoredRegister(Nasm.REG_EAX);
        NasmOperand printed = inst.op1.accept(this);

        nasm.ajouteInst(new NasmMov(label, eax, printed, "Write 1"));
        nasm.ajouteInst(new NasmCall(null, new NasmLabel("iprintLF"), "Write 2"));

        return null;
    }

    /**
     * Jump if less
     *
     * args: op1 -> a, op2 -> b, result -> to
     *
     * cmp $a, $b
     * jl $to
     */
    @Override
    public NasmOperand visit(C3aInstJumpIfLess inst) {
        NasmLabel label = getLabel(inst);

        NasmOperand to = inst.result.accept(this);
        NasmOperand a = inst.op1.accept(this);
        NasmOperand b = inst.op2.accept(this);

        nasm.ajouteInst(new NasmCmp(label, a, b, "JumpIfLess 1"));
        nasm.ajouteInst(new NasmJl(null, to, "JumpIfLess 2"));

        return null;
    }

    /**
     * Jump if equal.
     *
     * args: op1 -> a, op2 -> b, result -> to
     *
     * cmp $a, $b
     * je $to
     */
    @Override
    public NasmOperand visit(C3aInstJumpIfEqual inst) {
        // TODO: check correctness

        NasmLabel label = getLabel(inst);

        NasmOperand to = inst.result.accept(this);
        NasmOperand a = inst.op1.accept(this);
        NasmOperand b = inst.op2.accept(this);

        nasm.ajouteInst(new NasmCmp(label, a, b, "JumpIfEqual 1"));
        nasm.ajouteInst(new NasmJe(null, to, "JumpIfEqual 2"));

        return null;
    }

    /**
     * Jump if not equal.
     *
     * args: op1 -> a, op2 -> b, result -> to
     *
     * cmp $a, $b
     * jne $to
     */
    @Override
    public NasmOperand visit(C3aInstJumpIfNotEqual inst) {
        // TODO: check correctness

        NasmLabel label = getLabel(inst);

        NasmOperand to = inst.result.accept(this);
        NasmOperand a = inst.op1.accept(this);
        NasmOperand b = inst.op2.accept(this);

        nasm.ajouteInst(new NasmCmp(label, a, b, "jumpIfNotEqual 1"));
        nasm.ajouteInst(new NasmJne(null, to, "jumpIfNotEqual 2"));

        return null;
    }

    /**
     * Unconditional jump instruction.
     *
     * args: result -> to
     *
     * jmp $to
     */
    @Override
    public NasmOperand visit(C3aInstJump inst) {
        NasmOperand label = getLabel(inst);
        NasmOperand to = inst.result.accept(this);

        nasm.ajouteInst(new NasmJmp(label, to, "Jump"));

        return null;
    }

    /**
     * Ajout de paramètre lors de l'appel d'une fonction.
     *
     * args: op1 -> arg
     *
     * push $arg
     */
    @Override
    public NasmOperand visit(C3aInstParam inst) {
        NasmLabel label = getLabel(inst);
        NasmOperand arg = inst.op1.accept(this);

        nasm.ajouteInst(new NasmPush(label, arg, "Param"));

        return null;
    }

    /**
     * Instruction de retour.
     *
     * args: op1 -> source
     *
     * mov dword [ebp+4*2], $source
     */
    @Override
    public NasmOperand visit(C3aInstReturn inst) {
        NasmOperand source = inst.op1.accept(this);

        NasmRegister ebp = NasmRegister.colored(Nasm.REG_EBP);

        // Formula to get the address of the returned value:
        // ebp + 8
        NasmAddress destination = new NasmAddress(ebp, '+', new NasmConstant(2));

        NasmLabel label = getLabel(inst);
        nasm.ajouteInst(new NasmMov(label, destination, source, "ecriture de la valeur de retour"));

        return null;
    }

    /**
     * Creates a new constant, which is added as-is in the pre-asm code.
     */
    @Override
    public NasmOperand visit(C3aConstant oper) {
        return new NasmConstant(oper.val);
    }

    /**
     * Creates a label from the corresponding {@link C3aLabel}.
     */
    @Override
    public NasmOperand visit(C3aLabel oper) {
        return new NasmLabel(oper.toString());
    }

    /**
     * Creates a new temporary register.
     */
    @Override
    public NasmOperand visit(C3aTemp oper) {
        return new NasmRegister(oper.num);
    }

    /**
     * Évaluation d'une variable (qui peut être un tableau ou un entier) où d'un paramètre.
     */
    @Override
    public NasmOperand visit(C3aVar oper) {
        TsItemVar variable = oper.item;

        if (variable.isParam)
            return getParameterAddress(variable);

        return getVariableAddress(variable, oper.index);
    }

    private NasmAddress getVariableAddress(TsItemVar variable, C3aOperand index) {
        if (index != null) {
            // TODO: here too
            NasmOperand indexOperand = index.accept(this);

            // We have to use a temporary value to use a variable as an index
            // TODO: the next label should be put here instead, this (probably) introduces a new bug
            if (!(indexOperand instanceof NasmConstant)) {
                NasmRegister temp = nasm.newRegister();
                nasm.ajouteInst(new NasmMov(null, temp, indexOperand, ""));
                indexOperand = temp;
            }

            return new NasmAddress(new NasmLabel(variable.getIdentif()), '+', indexOperand);
        }

        boolean isLocal = currentFunction.getTable().variables.containsKey(variable.identif);

        if (isLocal) {
            NasmRegister ebp = NasmRegister.colored(Nasm.REG_EBP);
            return new NasmAddress(ebp, '-', new NasmConstant(1 + variable.adresse));
        }

        return new NasmAddress(new NasmLabel(variable.identif));
    }

    private NasmAddress getParameterAddress(TsItemVar variable) {
        // Formula to get the address of an argument:
        // ebp + 8 + 4 * args_count - a.address

        // Given that all variables are 4 bytes wide, we can rewrite this formula as:
        // ebp + 8 + 4 * args_count - 4 * a.index

        // Or:
        // ebp + 4 * (2 + args_count - a.index)

        int argsCount = variable.portee.nbArg();
        int varIndex = variable.adresse; // Note: TsItemVar#adresse is the index of the variable, not it's address

        NasmRegister ebp = NasmRegister.colored(Nasm.REG_EBP);

        return new NasmAddress(ebp, '+', new NasmConstant(2 + argsCount - varIndex));
    }

    @Override
    public NasmOperand visit(C3aFunction oper) {
        throw new RuntimeException();

    }

}
