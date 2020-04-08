package util.graph;

import java.util.stream.Stream;

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
        Stream.Builder<Node> streamBuilder = Stream.builder();

        while (nodeList != null) {
            streamBuilder.add(nodeList.head);
            nodeList = nodeList.tail;
        }

        return streamBuilder.build();
    }
}
