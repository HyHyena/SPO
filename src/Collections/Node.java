package Collections;

class Node {
    Node next;
    Node prev;
    Object value;

    Node(Node next, Node prev, Object value) {
        this.next = next;
        this.prev = prev;
        this.value = value;
    }
}
