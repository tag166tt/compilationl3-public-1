package fg;

import nasm.*;
import util.graph.GraphUtils;
import util.graph.Node;
import util.intset.IntSet;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class FgSolution {
    public Nasm nasm;
    public Map<NasmInst, IntSet> use;
    public Map<NasmInst, IntSet> def;
    public Map<NasmInst, IntSet> in;
    public Map<NasmInst, IntSet> out;
    int iterNum = 0;
    Fg fg;

    public FgSolution(Nasm nasm, Fg fg) {
        this.nasm = nasm;
        this.fg = fg;
        this.use = new HashMap<>();
        this.def = new HashMap<>();
        this.in = new HashMap<>();
        this.out = new HashMap<>();

        createDefUseSets();
        createInAndOutSets();
    }

    private void createDefUseSets() {
        for (NasmInst inst : nasm.listeInst) {
            createInstructionDefSet(inst);
            createInstructionUseSet(inst);
        }
    }

    private void createInstructionDefSet(NasmInst inst) {
        IntSet defSet = new IntSet(nasm.listeInst.size());
        if (inst.srcDef) addNasmOperand(defSet, inst.source);
        if (inst.destDef) addNasmOperand(defSet, inst.destination);
        def.put(inst, defSet);
    }

    private void createInstructionUseSet(NasmInst inst) {
        IntSet useSet = new IntSet(nasm.listeInst.size());
        if (inst.srcUse) addNasmOperand(useSet, inst.source);
        if (inst.destUse) addNasmOperand(useSet, inst.destination);
        use.put(inst, useSet);
    }

    private void addNasmOperand(IntSet to, NasmOperand operand) {
        if (operand == null) return;

        if (operand instanceof NasmAddress) {
            NasmAddress address = (NasmAddress) operand;
            addNasmOperand(to, address.base);
            addNasmOperand(to, address.offset);
        }

        if (operand.isGeneralRegister()) {
            NasmRegister register = (NasmRegister) operand;
            to.add(register.val);
        }
    }

    /**
     * Updates the in and out sets using an iterative approach.
     *
     * Algorithm:
     * <pre>
     * for all s do
     * 	in(s) = {}
     * 	out(s) = {}
     * end for
     *
     * repeat
     * 	for all s do
     * 		in'(s) = in(s)
     * 		out'(s) = out(s)
     * 		in(s) = use(s) U (out(s) - def(s))
     * 		out(n) = U s∈succ(s) in(s)
     * 	end for
     * until in'(s) = in(s) and out'(s) = out(s), ∀s
     * </pre>
     */
    private void createInAndOutSets() {
        createInAndOutEmptySets();

        boolean allEqual = false;

        while (!allEqual) {
            allEqual = true;
            iterNum++;

            for (NasmInst inst : nasm.listeInst) {
                IntSet oldIn = in.get(inst).copy();
                IntSet oldOut = out.get(inst).copy();

                IntSet newIn = computeNewIn(inst);
                in.put(inst, newIn);

                IntSet newOut = computeNewOut(inst);
                out.put(inst, newOut);

                if (!oldIn.equal(newIn) || !oldOut.equal(newOut))
                    allEqual = false;
            }
        }
    }

    private void createInAndOutEmptySets() {
        for (NasmInst inst : nasm.listeInst) {
            in.put(inst, new IntSet(nasm.listeInst.size()));
            out.put(inst, new IntSet(nasm.listeInst.size()));
        }
    }

    /**
     * Computes the new in(s) set, using the following formula:
     * in(s) = use(s) U (out(s) - def(s))
     *
     * @param inst The instruction to compute the new set for.
     * @implNote WARNING: the union operations are mutable, they don't create new objects.
     * @return The new in(s) set for the next iteration of the algorithm.
     */
    private IntSet computeNewIn(NasmInst inst) {
        IntSet out = this.out.get(inst),
                def = this.def.get(inst),
                use = this.use.get(inst);

        return use.copy().union(out.copy().minus(def));
    }

    /**
     * Computes the new out(s) set, using the following formula:
     * out(n) = U s∈succ(s) in(s)
     *
     * @param inst The instruction to compute the new set for.
     * @implNote WARNING: the union operations are mutable, they don't create new objects.
     * @return The new out(s) set for the next iteration of the algorithm.
     */
    private IntSet computeNewOut(NasmInst inst) {
        Node instructionNode = fg.inst2Node.get(inst);
        IntSet out = new IntSet(nasm.listeInst.size());

        GraphUtils.streamFromNodeList(instructionNode.succ()).forEach(node -> {
            NasmInst nodeInstruction = fg.node2Inst.get(node);
            out.union(in.get(nodeInstruction));
        });

        return out;
    }

    public void affiche(String baseFileName) {
        String fileName;
        PrintStream out = System.out;

        if (baseFileName != null) {
            try {
                fileName = baseFileName + ".fgs";
                out = new PrintStream(fileName);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        out.println("iter num = " + iterNum);
        for (NasmInst nasmInst : this.nasm.listeInst) {
            out.println("use = " + this.use.get(nasmInst) + " def = " + this.def.get(nasmInst) + "\tin = " + this.in.get(nasmInst) + "\t \tout = " + this.out.get(nasmInst) + "\t \t" + nasmInst);
        }
    }
}

    
