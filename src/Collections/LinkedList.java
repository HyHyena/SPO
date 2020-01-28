package Collections;

import java.util.Objects;

public class LinkedList {
    private Node firstNode;
    private Node lastNode;
    private int size = 0;

    public void add(Object value) {
        if (firstNode == null) {
            firstNode = new Node(null, null, value);
            lastNode = firstNode;
        } else {
            Node node = new Node(null, lastNode, value);
            this.lastNode.next = node;
            this.lastNode = node;
        }
        size++;
    }

    public Object get(int index) {
        Object element = null;
        if (index < size) {
            Node node = firstNode;
            if (index == 0) return (firstNode.value);
            else
                while (index > 0) {
                    index--;
                    node = node.next;
                    element = node.value;
                }

        } else System.out.println("This element doesn't exist");
        return element;
    }

    public Object remove() {
        Object value = null;
        if (firstNode != null && firstNode.next != null) {
            value = firstNode.value;
            firstNode = firstNode.next;
            size--;
        }
        return value;
    }

    private Object remove(int index) {
        Object value = null;
        Node node = firstNode;

        if (index < size - 1 && index > 0) {
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
            value = node.value;
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        if (index == 0) {
            value = firstNode.value;
            firstNode = firstNode.next;
        }

        if (index == size - 1) {
            value = lastNode.value;
            lastNode = lastNode.prev;
        }

        return value;
    }

    public boolean remove(Object o) {
        boolean removed = false;
        int index = contains(o);
        if (index != -1) {
            remove(index);
            removed = true;
        }

        return removed;
    }

    public int contains(Object o) {
        Node node = firstNode;
        for (int i = 0; i < size; i++) {
            if (Objects.equals(node.value, o)) {
                return i;
            } else {
                node = node.next;
            }
        }
        return -1;
    }

    public int size() {
        return size;
    }

    public String toString() {
        Node node = firstNode;
        StringBuilder s = new StringBuilder("[");
        if (node != null) {
            while (node.next != null) {
                s.append(" ").append(node.value);
                node = node.next;
            }
            s.append(" ").append(node.value);
        }
        s.append("]");
        return s.toString();
    }
}
