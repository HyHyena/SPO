package parser;

import enumerations.CompilerEnum;
import lexer.Token;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ParseTree {
    private Node root;

    public ParseTree(CompilerEnum type) {
        root = new Node(type);
        root.children = new ArrayList<>();
    }

    public Node getRoot() {
        return root;
    }

    public void printTree() {
        System.out.println(root.type);
        List<Node> nodes = new LinkedList<>(root.getChildren());
        nodes.add(null);
        while (!nodes.isEmpty()) {
            if (nodes.get(0) == null) {
                System.out.println();
                nodes.remove(0);
                if (nodes.isEmpty()) return;
                nodes.add(null);
            } else {
                nodes.addAll(nodes.get(0).getChildren());
                System.out.print(nodes.remove(0).type + "\t");
            }
        }
    }

    public static class Node {
        private Token data;
        private CompilerEnum type;
        private Node parent;
        private List<Node> children;

        public Node(Token data, CompilerEnum type) {
            this.data = data;
            this.type = type;
            children = new ArrayList<>();
        }

        public Node(CompilerEnum type) {
            this.type = type;
            data = null;
            children = new ArrayList<>();
        }

        public Node getNode() {
            if (children.size() != 0)
                return children.get(children.size() - 1);
            return null;
        }

        public void addNode(Node node) {
            node.setParent(this);
            children.add(node);
        }

        public void deleteNode() {
            if (children.size() != 0)
                children.remove(children.size() - 1);
        }

        public Token getData() {
            return data;
        }

        public CompilerEnum getType() {
            return type;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public List<Node> getChildren() {
            return children;
        }
    }
}