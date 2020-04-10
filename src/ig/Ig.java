package ig;

import fg.FgSolution;
import nasm.*;
import util.graph.ColorGraph;
import util.graph.Graph;
import util.graph.Node;
import util.graph.NodeList;
import util.intset.IntSet;
import util.intset.IntSetUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Stream;

public class Ig {

    private final static int REGISTERS_COUNT = 4;

    private final Graph graph;
    private final FgSolution fgs;
    private final int regNb;
    private final Nasm nasm;
    private final Node[] int2Node;


    public Ig(FgSolution fgs) {
        this.fgs = fgs;
        this.graph = new Graph();
        this.nasm = fgs.nasm;
        this.regNb = this.nasm.getTempCounter();
        this.int2Node = new Node[regNb];
        this.build();
    }

    /**
     * <code>
     * A <- ∅
     * S <- {1, ..., R}
     * for all i ∈ Sa do
     *  for all (r, r') ∈ in(s) x in(s), r != r' do
     *      A <- A U (r, r')
     *  end for
     *  for all (r, r') ∈ out(s) x out(s), r != r' do
     *      A <- A U (r, r')
     *  end for
     * end for
     * </code>
     */
    public void build() {
        for (int i = 0; i < regNb; i++)
            int2Node[i] = graph.newNode();

        for (NasmInst inst: nasm.listeInst) {
            createNOEdges(fgs.in.get(inst));
            createNOEdges(fgs.out.get(inst));
        }
    }

    /**
     * Creates non oriented edges for the given set from each value to another, without connecting
     * the same value to itself.
     */
    private void createNOEdges(IntSet set) {
        for (List<Integer> tuple: IntSetUtils.twoSetsCartesianProduct(set, set)) {
            if (tuple.get(0).equals(tuple.get(1))) continue;
            Node from = int2Node[tuple.get(0)];
            Node to = int2Node[tuple.get(1)];
            graph.addNOEdge(from, to);
        }
    }

    /**
     * Collects the pre-colored temporaries into an array, where 0 indicates that the
     * register is not pre-colored.
     */
    public int[] getPreColoredTemporaries() {
        int[] preColoredTemporaries = new int[nasm.getTempCounter()];

        // Call the setOperandColor method for both the source and the destination operands
        // of each instruction.
        nasm.listeInst.stream()
                .flatMap(instruction -> Stream.of(instruction.source, instruction.destination))
                .forEach(operand -> setOperandColor(preColoredTemporaries, operand));

        return preColoredTemporaries;
    }

    /**
     * Sets the color for all of the registers contained in the given operand in the given
     * array.
     */
    private void setOperandColor(int[] colors, NasmOperand operand) {
        if (operand == null) return;

        if (operand instanceof NasmAddress) {
            NasmAddress address = (NasmAddress) operand;
            setOperandColor(colors, address.base);
            setOperandColor(colors, address.offset);
        }

        if (!operand.isGeneralRegister())
            return;

        NasmRegister register = (NasmRegister) operand;
        colors[register.val] = register.color;
    }

    /**
     * Allocates a real register to every temporary register in every instruction.
     */
    public void allocateRegisters() {
        ColorGraph colorGraph = new ColorGraph(graph, REGISTERS_COUNT, getPreColoredTemporaries());
        int[] colors = colorGraph.color();

        for (NasmInst inst: nasm.listeInst) {
            allocateRegister(colors, inst.source);
            allocateRegister(colors, inst.destination);
        }
    }

    /**
     * Allocates the register of a given operand, using the given colors of the graph.
     */
    private void allocateRegister(int[] colors, NasmOperand operand) {
        if (operand == null) return;

        if (operand instanceof NasmAddress) {
            NasmAddress address = (NasmAddress) operand;
            allocateRegister(colors, address.base);
            allocateRegister(colors, address.offset);
        }

        if (!operand.isGeneralRegister())
            return;

        NasmRegister register = (NasmRegister) operand;

        if (register.color == Nasm.REG_UNK)
            register.colorRegister(colors[register.val]);
    }

    public void affiche(String baseFileName) {
        String fileName;
        PrintStream out = System.out;

        if (baseFileName != null) {
            try {
                baseFileName = baseFileName;
                fileName = baseFileName + ".ig";
                out = new PrintStream(fileName);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        for (int i = 0; i < regNb; i++) {
            Node n = this.int2Node[i];
            out.print(n + " : ( ");
            for (NodeList q = n.succ(); q != null; q = q.tail) {
                out.print(q.head.toString());
                out.print(" ");
            }
            out.println(")");
        }
    }
}
	    
    

    
    
