package util.graph;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GraphUtils {
    /**
     * Generates a stream from the given node list.
     *
     * Example usage:
     * Node node = ...;
     * streamFromNodeList(node.adj()).forEach(adj -> System.out.println(adj.mykey));
     *
     * @param nodeList The node list to use.
     * @return The generated stream.
     */
    public static Stream<Node> streamFromNodeList(NodeList nodeList) {
        if (nodeList == null)
            return Stream.of();

        return StreamSupport.stream(nodeList.spliterator(), false);
    }
}
