package util.graph;

import java.util.Iterator;

public class NodeList implements Iterable<Node> {
  public Node head;
  public NodeList tail;
  public NodeList(Node h, NodeList t) {head=h; tail=t;}

  @Override
  public Iterator<Node> iterator() {
    NodeList first = this;

    return new Iterator<>() {
      private NodeList current = first;

      @Override
      public boolean hasNext() {
        return current != null && current.head != null;
      }

      @Override
      public Node next() {
        Node head = current.head;
        current = current.tail;
        return head;
      }
    };
  }
}



