package Stack_Maschine;

import Collections.HashSet;
import Collections.LinkedList;
import Lexer.Lexer;
import Parser.Parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Executor {
    HashMap<String, Map<String, Object>> tableOfVariablesMap = new HashMap<>();
    HashMap<String, List<String>> rnpMap = new HashMap<>();
    String currentFunction;

    public void start(Parser parser) {
        this.rnpMap = parser.reversePolishNotation;
        this.tableOfVariablesMap = parser.tableOfVariables;
        currentFunction = "main";
        execute(rnpMap.get(currentFunction), tableOfVariablesMap.get(currentFunction), new Stack<>());
    }

    void execute(List<String> reversePolishNotation, Map<String, Object> tableOfVar, Stack<String> stack) {
        Object first, second, result;

        for (int i = 0; i <= reversePolishNotation.size() - 1; i++) {
            //System.out.println(reversePolishNotation.get(i));
            switch (reversePolishNotation.get(i)) {
                case "run":
                    String threadName = stack.peek();
                    first = tableOfVar.get(stack.pop());
                    if (first instanceof Thread) {
                        String funcName = stack.pop().replace("thread_", "function_");
                        if (rnpMap.containsKey(funcName)) {
                            if (stack.peek().contains("arg_")) {
                                tableOfVariablesMap.get(funcName).put("arg",
                                        tableOfVariablesMap.get(currentFunction).get(stack.pop().replace("arg_", "")));
                            }
                            Runnable task = () -> {
                                execute(rnpMap.get(funcName), tableOfVariablesMap.get(funcName), new Stack<>());
                            };
                            first = new Thread(task);
                            tableOfVar.replace(threadName, first);
                            ((Thread) first).start();
                        }
                    } else {
                        System.out.println(first + " is not thread");
                    }
                    break;

                case "join":
                    first = getValue(tableOfVar, stack);
                    if (first instanceof Thread) {
                        try {
                            ((Thread) first).join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "add":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    if (second instanceof HashSet) {
                        ((HashSet) second).add(first);
                    } else {
                        ((LinkedList) second).add(first);
                    }
                    break;

                case "remove":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    if (second instanceof HashSet) {
                        ((HashSet) second).remove(first);
                    } else {
                        ((LinkedList) second).remove(first);
                    }
                    break;

                case "get":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    stack.push("" + (((LinkedList) second).get((Integer) first)));
                    break;

                case "contains":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    boolean b;
                    if (second instanceof LinkedList) {
                        b = ((LinkedList) second).contains(first) != -1;
                        stack.push(String.valueOf(b));
                    } else {
                        b = ((HashSet) second).contains(first);
                        stack.push(String.valueOf(b));
                    }
                    break;

                case "size":
                    first = getValue(tableOfVar, stack);
                    stack.push("" + ((HashSet) first).size());
                    break;

                case "print":
                    if (stack.empty()) {
                        System.out.println(tableOfVar);
                    } else if (tableOfVar.containsKey(stack.peek())) {
                        System.out.println(stack.peek() + " = " + tableOfVar.get(stack.pop()));
                    } else {
                        System.out.println(stack.pop());
                    }
                    break;

                case "=":
                    first = getValue(tableOfVar, stack);
                    tableOfVar.put(stack.pop(), first);
                    break;

                case "+":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    result = (Integer) first + (Integer) second;
                    stack.push(String.valueOf(result));
                    break;

                case "-":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    result = (Integer) second - (Integer) first;
                    stack.push(String.valueOf(result));
                    break;

                case "/":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    result = (Double) second / (Double) first;
                    stack.push(String.valueOf(result));
                    break;

                case "%":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    result = (Double) second % (Double) first;
                    stack.push(String.valueOf(result));
                    break;

                case "^":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    result = (int) Math.pow((Integer) first, (Integer) second);
                    stack.push(String.valueOf(result));
                    break;

                case "*":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    result = (Integer) first * (Integer) second;
                    stack.push(String.valueOf(result));
                    break;

                case "!=":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    boolean b1 = first.equals(second);
                    stack.push(String.valueOf(b1));
                    break;

                case "==":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    b1 = first == second;
                    stack.push(String.valueOf(b1));
                    break;

                case "<":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    b1 = (Integer) second < (Integer) first;
                    stack.push(String.valueOf(b1));
                    break;

                case ">":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    b1 = (Integer) second > (Integer) first;
                    stack.push(String.valueOf(b1));
                    break;

                case "<=":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    b1 = (Integer) second <= (Integer) first;
                    stack.push(String.valueOf(b1));
                    break;

                case ">=":
                    first = getValue(tableOfVar, stack);
                    second = getValue(tableOfVar, stack);
                    b1 = (Integer) second >= (Integer) first;
                    stack.push(String.valueOf(b1));
                    break;

                case "!F":
                    first = getValue(tableOfVar, stack);
                    boolean c = stack.pop().equals("true");
                    i = c ? i : (Integer) first;
                    break;

                case "!":
                    i = (Integer) getValue(tableOfVar, stack) - 1;
                    break;

                case "return":
                    tableOfVar.put("return", tableOfVar.get(stack.pop()));
                    break;

                default:
                    if (!reversePolishNotation.get(i).contains("function_") && !rnpMap.containsKey("function_" + reversePolishNotation.get(i))) {
                        stack.push(String.valueOf(reversePolishNotation.get(i)));
                    }
                    if (rnpMap.containsKey("function_" + reversePolishNotation.get(i))) {
                        if (stack.peek().contains("arg_")) {
                            tableOfVariablesMap.get("function_" + reversePolishNotation.get(i)).put("arg",
                                    tableOfVariablesMap.get(currentFunction).get(stack.pop().replace("arg_", "")));
                        }
                        execute(rnpMap.get("function_" + reversePolishNotation.get(i)), tableOfVariablesMap.get("function_" + reversePolishNotation.get(i)), new Stack<>());
                    }
                    break;
            }
        }
        System.out.println();
    }

    private Object getValue(Map<String, Object> tableOfVar, Stack<String> stack) {
        String value = stack.peek();
        if (tableOfVariablesMap.containsKey("function_" + value)) {
            tableOfVariablesMap.get("function_" + stack.pop()).put("arg",
                    tableOfVariablesMap.get(currentFunction).get(stack.pop()));
            execute(rnpMap.get("function_" + value), tableOfVariablesMap.get("function_" + value), new Stack<>());
            return tableOfVariablesMap.get("function_" + value).get("return");
        }
        Lexer lexer = new Lexer(stack.peek());
        switch (lexer.getTokens().get(0).getLexeme()) {
            case VAR:
                return tableOfVar.get(stack.pop());
            case DIGIT:
                return Integer.valueOf(stack.pop());
            default:
                System.err.println();
                System.exit(50);
        }
        return -1;
    }
}
