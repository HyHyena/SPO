package compiler;

import enumerations.CompilerEnum;
import enumerations.ComponentsEnum;
import enumerations.LexemeEnum;
import enumerations.ServiceEnum;
import lexer.Token;
import lexer.Variable;
import parser.AST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class PolizGenerator {
    
    private ArrayList<Token> poliz;
    private Stack<Token> polizStack;
    private Stack<Variable> labelStack;
    private Stack<Variable> breakStack;
    private Stack<Variable> continueStack;
    private HashMap<String, Variable> tableOfNames;
    private int labelNum;

    public void init() {
        poliz = new ArrayList<>();
        polizStack = new Stack<>();
        labelStack = new Stack<>();
        continueStack = new Stack<>();
        breakStack = new Stack<>();
        tableOfNames = new HashMap<>();
        labelNum = 0;
    }

    public void generate(AST tree) {
        init();
        depthFirst(tree.getRoot());
        clearPolizStack();
    }

    public HashMap<String, Variable> getTableOfNames() {
        return tableOfNames;
    }

    private void depthFirst(AST.Node parent) {
        for (AST.Node node : parent.getChildren()) {
            if (node.getData() != null) {
                addToken(node.getData());
            } else {
                if (node.getType().equals(ComponentsEnum.FUNC_VALUE))
                    funcValue(node);
                else if (node.getType().equals(ComponentsEnum.FUNC_STMT))
                    funcStmt(node);
                else if (node.getType().equals(ComponentsEnum.WHILE_STMT))
                    whileStmt(node);
                else if (node.getType().equals(ComponentsEnum.IF_STMT))
                    ifStmt(node);
                else if (node.getType().equals(ComponentsEnum.FOR_STMT))
                    forStmt(node);
                else if (node.getType().equals(ComponentsEnum.DO_WHILE_STMT))
                    doWhileStmt(node);
                else if (node.getType().equals(ComponentsEnum.BREAK_STMT))
                    breakStmt();
                else if (node.getType().equals(ComponentsEnum.CONTINUE_STMT))
                    continueStmt();
                else if (node.getType().equals(ComponentsEnum.CLASS_DECLARE_STMT))
                    classDeclareStmt(node);
                else if (node.getType().equals(ComponentsEnum.METHOD_VALUE))
                    methodValue(node);
                else {
                    depthFirst(node);
                }
            }
        }
    }

    private void breakStmt() {
        addLabel(ServiceEnum.GOTO, breakStack);
        clearLocalPolizStack();
    }

    private void continueStmt() {
        addLabel(ServiceEnum.GOTO, continueStack);
        clearLocalPolizStack();
    }

    private void funcValue(AST.Node node) {
        poliz.add(new Token(ServiceEnum.ARGUMENTS, node.getChildren().get(0).getData().getValue()));
        depthFirst(node.getChildren().get(1));
        node.getChildren().get(0).getData().setType(ServiceEnum.FUNC);
        poliz.add(node.getChildren().get(0).getData());
    }

    private void methodValue(AST.Node node) {
        poliz.add(new Token(ServiceEnum.ARGUMENTS, node.getChildren().get(0).getData().getValue()));
        depthFirst(node.getChildren().get(3).getChildren().get(1));
        node.getChildren().get(0).getData().setType(ServiceEnum.OBJECT);
        poliz.add(node.getChildren().get(0).getData());
        node.getChildren().get(3).getChildren().get(0).getData().setType(ServiceEnum.FUNC);
        poliz.add(node.getChildren().get(3).getChildren().get(0).getData());
    }

    private void funcStmt(AST.Node node) {
        addToken(node.getChildren().get(2).getData());
        addToken(node.getChildren().get(1).getData());
        addToken(node.getChildren().get(0).getData());
        clearPolizStack();
        poliz.add(new Token(ServiceEnum.START_FUNC, node.getChildren().get(2).getData().getValue()));
        depthFirst(node.getChildren().get(3));
        addToken(new Token(ServiceEnum.ARGUMENTS, node.getChildren().get(2).getData().getValue()));
        if (node.getChildren().get(4).getData() == null)
            depthFirst(node.getChildren().get(4));
        else
            addToken(node.getChildren().get(4).getData());
        poliz.add(new Token(ServiceEnum.END_FUNC, node.getChildren().get(2).getData().getValue()));
    }


    private void whileStmt(AST.Node node) {
        int labelPos = poliz.size();
        depthFirst(node.getChildren().get(2));
        addLabel(ServiceEnum.GOTO_LIE, labelStack);
        clearLocalPolizStack();
        poliz.add(new Token(ServiceEnum.START_VISIBILITY_AREA, "WHILE"));
        depthFirst(node.getChildren().get(4));
        clearLocalPolizStack();
        poliz.add(new Token(ServiceEnum.END_VISIBILITY_AREA, "WHILE"));
        addLabel(ServiceEnum.GOTO, labelStack);
        labelStack.pop().setValue(labelPos);
        labelStack.pop().setValue(poliz.size() + 1);
        setCycleOpLabelPos(labelPos);
        clearLocalPolizStack();
    }

    private void doWhileStmt(AST.Node node) {
        int labelPos = poliz.size();
        poliz.add(new Token(ServiceEnum.START_VISIBILITY_AREA, "DO_WHILE"));
        depthFirst(node.getChildren().get(1));
        depthFirst(node.getChildren().get(4));
        clearLocalPolizStack();
        poliz.add(new Token(ServiceEnum.END_VISIBILITY_AREA, "DO_WHILE"));
        addLabel(ServiceEnum.GOTO_LIE, labelStack);
        addLabel(ServiceEnum.GOTO, labelStack);
        labelStack.pop().setValue(labelPos);
        labelStack.pop().setValue(poliz.size() + 1);
        setCycleOpLabelPos(labelPos);
        clearLocalPolizStack();
    }

    private void classDeclareStmt(AST.Node node) {
        poliz.add(node.getChildren().get(4).getData());
        poliz.add(node.getChildren().get(2).getData());
        poliz.add(node.getChildren().get(0).getData());
        clearLocalPolizStack();
    }

    private void forStmt(AST.Node node) {
        poliz.add(new Token(ServiceEnum.START_VISIBILITY_AREA, "FOR"));
        depthFirst(node.getChildren().get(2));
        clearLocalPolizStack();
        int labelPos = poliz.size();
        depthFirst(node.getChildren().get(3));
        addLabel(ServiceEnum.GOTO_LIE, labelStack);
        clearLocalPolizStack();
        poliz.add(new Token(ServiceEnum.START_VISIBILITY_AREA, "FOR"));
        depthFirst(node.getChildren().get(6));
        depthFirst(node.getChildren().get(4));
        clearLocalPolizStack();
        poliz.add(new Token(ServiceEnum.END_VISIBILITY_AREA, "FOR"));
        addLabel(ServiceEnum.GOTO, labelStack);
        labelStack.pop().setValue(labelPos);
        labelStack.pop().setValue(poliz.size() + 1);
        setCycleOpLabelPos(labelPos);
        clearLocalPolizStack();
        poliz.add(new Token(ServiceEnum.END_VISIBILITY_AREA, "FOR"));
    }

    public void setCycleOpLabelPos(int labelPos) {
        while (!continueStack.isEmpty())
            continueStack.pop().setValue(labelPos);
        while (!breakStack.isEmpty())
            breakStack.pop().setValue(poliz.size() + 1);
    }

    private void ifStmt(AST.Node node) {
        depthFirst(node.getChildren().get(2));
        addLabel(ServiceEnum.GOTO_LIE, labelStack);
        depthFirst(node.getChildren().get(4));
        labelStack.pop().setValue(poliz.size() + 2);
        addLabel(ServiceEnum.GOTO, labelStack);
        clearLocalPolizStack();
        if (node.getChildren().size() > 5) {
            if (node.getChildren().get(6).getType().equals(ComponentsEnum.IF_STMT))
                ifStmt(node.getChildren().get(6));
            else
                depthFirst(node.getChildren().get(6));
        }
        labelStack.pop().setValue(poliz.size());
    }

    private void addLabel(ServiceEnum gotoType, Stack<Variable> stack) {
        addToken(new Token(ServiceEnum.LABEL, "!label" + labelNum));
        Variable variable = new Variable(("!label" + labelNum), ServiceEnum.LABEL, null, ServiceEnum.LABEL);
        labelNum++;
        stack.push(variable);
        tableOfNames.put(variable.getName(), variable);
        addToken(new Token(gotoType, gotoType.toString()));
    }

    private void addToken(Token token) {
        if (isValue(token.getType())) {
            poliz.add(token);
        } else {
            if (token.getType().equals(LexemeEnum.RIGHT_PARENTHESES)) {
                while (!polizStack.isEmpty() && !polizStack.peek().getType().equals(LexemeEnum.LEFT_PARENTHESES)) {
                    poliz.add(polizStack.pop());
                }
                polizStack.pop();
            } else if (token.getType().equals(LexemeEnum.RIGHT_BRACES)) {
                clearLocalPolizStack();
                polizStack.pop();
            } else if (token.getType().equals(LexemeEnum.COMMA_SP)) {
                while (!polizStack.isEmpty() && (!polizStack.peek().getType().equals(LexemeEnum.LEFT_PARENTHESES)
                        && !polizStack.peek().getType().equals(LexemeEnum.LEFT_BRACES))) {
                    poliz.add(polizStack.pop());
                }
            } else if (token.getType().equals(LexemeEnum.SEMICOLON_SP)) {
                clearLocalPolizStack();
            } else if (token.getType().equals(LexemeEnum.LEFT_PARENTHESES)) {
                polizStack.push(token);
            } else if (token.getType().equals(LexemeEnum.LEFT_BRACES)) {
                clearLocalPolizStack();
                polizStack.push(token);
            } else {
                while (!polizStack.isEmpty() && polizStack.peek().getType().getPriority() >= token.getType().getPriority()) {
                    poliz.add(polizStack.pop());
                }
                polizStack.push(token);
            }
        }
    }

    private void clearPolizStack() {
        while (!polizStack.isEmpty())
            poliz.add(polizStack.pop());
    }

    private void clearLocalPolizStack() {
        while (!polizStack.isEmpty() && !polizStack.peek().getType().equals(LexemeEnum.LEFT_BRACES))
            poliz.add(polizStack.pop());
    }

    public ArrayList<Token> getPoliz() {
        return poliz;
    }

    private boolean isValue(CompilerEnum token) {
        return token.equals(LexemeEnum.INTEGER) || token.equals(LexemeEnum.REAL) || token.equals(LexemeEnum.VAR);
    }
}
