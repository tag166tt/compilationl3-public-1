package util.graph;

import util.intset.IntSet;
import util.intset.IntSetUtils;

import java.util.Arrays;
import java.util.Stack;

public class ColorGraph {

    private final static int NOCOLOR = -1;

    private final int tempRegistersNb; // R
    private final int colorsNb; // K
    private final IntSet removed;
    private final IntSet overflowed;
    private final int[] colors;
    private final Node[] int2Node;
    private final Stack<Integer> stack = new Stack<>();

    public ColorGraph(Graph G, int colorsNb, int[] preColored) {
        this.colorsNb = colorsNb;
        tempRegistersNb = G.nodeCount(); // TODO
        colors = new int[tempRegistersNb];
        removed = new IntSet(tempRegistersNb);
        overflowed = new IntSet(tempRegistersNb);
        int2Node = G.nodeArray();
        initializeColors(preColored);
    }

    /**
     * Initializes the {@link ColorGraph#colors} array, setting each element to either
     * {@link ColorGraph#NOCOLOR} or the passed color.
     * @param preColored The starting colors.
     */
    private void initializeColors(int[] preColored) {
        for (int v = 0; v < tempRegistersNb; v++) {
            int preColor = preColored[v];

            if (preColor >= 0 && preColor < colorsNb)
                colors[v] = preColored[v];
            else
                colors[v] = NOCOLOR;
        }
    }

    /**
     * Colors the graph.
     * @return The created colors.
     */
    public int[] color() {
        this.simplify();
        this.overflow();
        this.select();
        return colors;
    }

    public void simplify() {
        int N = tempRegistersNb - countColored();
        boolean modif = true;

        while (stack.size() != N && modif) {
            modif = false;

            for (int s = 0; s < int2Node.length; s++) {
                if (neighborsCount(s) >= colorsNb || removed.isMember(s) || colors[s] != NOCOLOR) {
                    continue;
                }
                stack.add(s);
                removed.add(s);
                modif = true;
            }
        }
    }

    public void overflow() {
        while (stack.size() != tempRegistersNb - countColored()) {
            int s = chooseVertex();
            if (s < 0) throw new RuntimeException("Could not find a vertex."); // TODO
            stack.add(s);
            removed.add(s);
            overflowed.add(s);
            simplify();
        }
    }

    /**
     * Colors all of the vertices in the stack.
     */
    public void select() {
        while (!stack.empty()) {
            int s = stack.pop();
            IntSet C = neighborsColors(s);

            if (C.getSize() != colorsNb - countColored())
                colors[s] = chooseColor(C);
        }
    }

    public IntSet neighborsColors(int t) {
        Node node = int2Node[t];

        return GraphUtils.streamFromNodeList(node.adj())
                .map(successor -> colors[successor.mykey])
                .filter(color -> color != NOCOLOR)
                .collect(IntSetUtils.toIntSet(colorsNb));
    }

    public int chooseColor(IntSet colorSet) {
        for (int i = 0; i < colorsNb; i++)
            if (!colorSet.isMember(i)) return i;

        return NOCOLOR;
    }

    public int neighborsCount(int t) {
        Node node = int2Node[t];

        // Count all adjacent nodes
        return (int) GraphUtils.streamFromNodeList(node.adj())
                .filter(successor -> !removed.isMember(successor.mykey))
                .map(successor -> successor.mykey)
                .distinct()
                .count();
    }

    private int countColored() {
        return (int) Arrays.stream(colors)
                .filter(c -> c != NOCOLOR)
                .count();
    }

    private int chooseVertex() {
        for (int s = 0; s < int2Node.length; s++)
            // pas besoin de vérifier deborde : si x est dans déborde, alors il est dans enleves
            if (!removed.isMember(s) && colors[s] == NOCOLOR) return s;

        return -1;
    }

    void affiche() {
        System.out.println("vertex\tcolor");
        for (int i = 0; i < tempRegistersNb; i++) {
            System.out.println(i + "\t" + colors[i]);
        }
    }


}
