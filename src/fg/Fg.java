package fg;

import nasm.*;
import util.graph.*;

import java.util.*;
import java.io.*;

public class Fg implements NasmVisitor<Void> {
    public Nasm nasm;
    public Graph graph;
    Map<NasmInst, Node> inst2Node;
    Map<Node, NasmInst> node2Inst;
    Map<String, NasmInst> label2Inst;
    private Node[] nodeArray;

    public Fg(Nasm nasm) {
        this.nasm = nasm;
        this.inst2Node = new HashMap<>();
        this.node2Inst = new HashMap<>();
        this.label2Inst = new HashMap<>();
        this.graph = new Graph();

        nasm.listeInst.forEach(this::createInstructionVertex);
        nodeArray = graph.nodeArray();
        nasm.listeInst.forEach(i -> i.accept(this));
    }

    public void affiche(String baseFileName) {
        String fileName;
        PrintStream out = System.out;

        if (baseFileName != null) {
            try {
                fileName = baseFileName + ".fg";
                out = new PrintStream(fileName);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        for (NasmInst nasmInst : nasm.listeInst) {
            Node n = this.inst2Node.get(nasmInst);
            out.print(n + " : ( ");
            for (NodeList q = n.succ(); q != null; q = q.tail) {
                out.print(q.head.toString());
                out.print(" ");
            }
            out.println(")\t" + nasmInst);
        }
    }

    private void createInstructionVertex(NasmInst inst) {
        Node node = graph.newNode();

        node2Inst.put(node, inst);
        inst2Node.put(inst, node);

        if (inst.label != null) {
            String label = inst.label.toString();
            label2Inst.put(label, inst);
        }
    }

    private void addEdgeToNextInstruction(NasmInst inst) {
        int instructionIndex = nasm.listeInst.indexOf(inst);
        int nextInstructionIndex = instructionIndex + 1;

        // There is no next instruction
        if (nextInstructionIndex >= graph.nodeCount())
            return;

        Node instructionNode = nodeArray[instructionIndex];
        Node nextInstructionNode = nodeArray[nextInstructionIndex];

        graph.addEdge(instructionNode, nextInstructionNode);
    }

    private void addEdgeToLabelAtAddress(NasmInst currentInstruction) {
        int currentInstructionIndex = nasm.listeInst.indexOf(currentInstruction);

        String labelName = currentInstruction.address.toString();
        NasmInst instructionAtLabel = label2Inst.get(labelName);

        Node currentInstructionNode = nodeArray[currentInstructionIndex];
        Node instructionAtLabelNode = inst2Node.get(instructionAtLabel);

        // The label may not exist in the source code ; for example the call  to 'iprintLF' is valid, but the method
        // is defined in another file that is imported.
        if (instructionAtLabelNode == null)
            return;

        // TODO: shouldn't the call to iprintLF have an edge to the next instruction ?

        graph.addEdge(currentInstructionNode, instructionAtLabelNode);
    }

    // special
    public Void visit(NasmCall inst) {
        addEdgeToLabelAtAddress(inst);
        return null;
    }

    // special
    public Void visit(NasmJe inst) {
        addEdgeToLabelAtAddress(inst);
        addEdgeToNextInstruction(inst);
        return null;
    }

    // special
    public Void visit(NasmJle inst) {
        addEdgeToLabelAtAddress(inst);
        addEdgeToNextInstruction(inst);
        return null;
    }

    // special
    public Void visit(NasmJne inst) {
        addEdgeToLabelAtAddress(inst);
        addEdgeToNextInstruction(inst);
        return null;
    }

    // special
    public Void visit(NasmJge inst) {
        addEdgeToLabelAtAddress(inst);
        addEdgeToNextInstruction(inst);
        return null;
    }

    // special
    public Void visit(NasmJl inst) {
        addEdgeToLabelAtAddress(inst);
        addEdgeToNextInstruction(inst);
        return null;
    }

    // special
    public Void visit(NasmJg inst) {
        addEdgeToLabelAtAddress(inst);
        addEdgeToNextInstruction(inst);
        return null;
    }

    // special
    public Void visit(NasmJmp inst) {
        addEdgeToLabelAtAddress(inst);
        return null;
    }

    public Void visit(NasmAdd inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmDiv inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmMul inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmOr inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmCmp inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmInst inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmNot inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmPop inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmRet inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmXor inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmAnd inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmMov inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmPush inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmSub inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmEmpty inst) {
        addEdgeToNextInstruction(inst);
        return null;
    }

    public Void visit(NasmAddress operand) {
        throw new RuntimeException("This probably should not be called.");
    }

    public Void visit(NasmConstant operand) {
        throw new RuntimeException("This probably should not be called.");
    }

    public Void visit(NasmLabel operand) {
        throw new RuntimeException("This probably should not be called.");
    }

    public Void visit(NasmRegister operand) {
        throw new RuntimeException("This probably should not be called.");
    }


}
