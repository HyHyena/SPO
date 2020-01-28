package Optimizer;

import java.util.*;

public class Optimisation {
    private HashMap<String, Map<String, Object>> tableOfVariables;
    private List<String> reversePolishNotation;
    private HashMap<String, List<String>> rpnSet;
    private List<String> optimisedRPN;
    private List<String> linear;
    private List<String> reversePolishNotationCopy;
    private String currentFunc;

    public Optimisation(HashMap<String, Map<String, Object>> tableOfVariables, HashMap<String, List<String>> rpnSet) {
        this.tableOfVariables = tableOfVariables;
        this.rpnSet = rpnSet;
        this.optimisedRPN = new ArrayList<>();
        this.linear = new ArrayList<>();
    }

    public void execute() {

        for (String funcName : rpnSet.keySet()) {
            currentFunc = funcName;
            reversePolishNotation = new ArrayList<>(rpnSet.get(funcName));
            reversePolishNotationCopy = new ArrayList<>(reversePolishNotation);
            optimisedRPN = new ArrayList<>();
            System.out.println("\nOptimizer.Optimisation of " + funcName);
            System.out.println("RPN before optimisation:");
            System.out.println(reversePolishNotation);
            while (findLinear()) {
                optimise();
            }
            if (optimisedRPN.size() < reversePolishNotationCopy.size()) {
                System.out.println("RPN after optimisation:\n" + optimisedRPN);
                rpnSet.replace(funcName, optimisedRPN);
            } else {
                System.out.println("No optimisation:\n" + reversePolishNotationCopy);
            }
        }
    }

    private void optimise() {
        String tmp;
        String[] trd;
        int result;

        for (int i = 0; i < linear.size(); i++) {

            tmp = linear.get(i).replaceAll("[\\[|\\]]", "");
            trd = tmp.split(", ");

            if (trd[2].matches("^([-+*/])")) {
                if (trd[0].matches("([a-zA-Z]|_)") && tableOfVariables.get(currentFunc).get(trd[0]) != null) {
                    trd[0] = String.valueOf(tableOfVariables.get(currentFunc).get(trd[0]));
                }
                if (trd[1].matches("([a-zA-Z]|_)") && tableOfVariables.get(currentFunc).get(trd[1]) != null) {
                    trd[1] = String.valueOf(tableOfVariables.get(currentFunc).get(trd[1]));
                }
                if (isNumeric(trd[0]) && isNumeric(trd[0])) {
                    switch (trd[2]) {
                        case "+":
                            result = Integer.parseInt(trd[0]) + Integer.parseInt(trd[1]);
                            linear.set(i, "C(" + result + ", 0)");
                            break;
                        case "-":
                            result = Integer.parseInt(trd[0]) - Integer.parseInt(trd[1]);
                            linear.set(i, "C(" + result + ", 0)");
                            break;
                        case "*":
                            result = Integer.parseInt(trd[0]) * Integer.parseInt(trd[1]);
                            linear.set(i, "C(" + result + ", 0)");
                            break;
                        case "/":
                            result = Integer.parseInt(trd[0]) / Integer.parseInt(trd[1]);
                            linear.set(i, "C(" + result + ", 0)");
                            break;

                    }
                }
            }

            if (trd[0].matches("([a-zA-Z]|_)+\\w") && !trd[1].matches("([a-zA-Z]|_|\\^*)+\\w") && trd[2].equals("=")) {
                if (!tableOfVariables.containsKey(trd[0])) {
                    tableOfVariables.get(currentFunc).put(trd[0], Integer.parseInt(trd[1]));
                } else {
                    tableOfVariables.get(currentFunc).replace(trd[0], Integer.parseInt(trd[1]));
                }
            }

            if (trd[1].contains("^") && linear.get(Integer.parseInt(trd[1].replace("^", "")) - 1).contains("C(")) {
                tmp = linear.get(Integer.parseInt(trd[1].replace("^", "")) - 1).replaceAll("[C(|)]", "");
                trd[1] = tmp.split(", ")[0];
                linear.set(i, Arrays.toString(trd));
                if (!tableOfVariables.get(currentFunc).containsKey(trd[0])) {
                    tableOfVariables.get(currentFunc).put(trd[0], Integer.parseInt(trd[1]));
                } else {
                    tableOfVariables.get(currentFunc).replace(trd[0], Integer.parseInt(trd[1]));
                }
            }
            System.out.println(linear);
        }

        for (String s : linear) {
            if (!s.contains("C(")) {
                optimisedRPN.addAll(Arrays.asList(s.replaceAll("[\\[|\\]]", "").split(", ")));
            }
        }
        if (optimisedRPN.contains("~")) {
            optimisedRPN.set(optimisedRPN.indexOf("~"), optimisedRPN.size() + "");
        }
        //System.out.println(tableOfVariables);
        linear.clear();
    }

    private boolean findLinear() {
        int i = 0;

        while (reversePolishNotation.size() > 0) {
            if (reversePolishNotation.get(i).matches("(==|!=|<=|>=|<|>|print|(function_+\\w*)|(thread_+\\w*))|'([^']*?)'")) {
                if (linear.size() > 0) {
                    System.out.println("Найдено линейное выражение:\n" + linear);
                    return true;
                }

                if (reversePolishNotation.get(i).matches("'([^']*?)'") && reversePolishNotation.get(i + 1).equals("print")) {
                    optimisedRPN.addAll(reversePolishNotation.subList(0, i + 2));
                    reversePolishNotation = reversePolishNotation.subList(i + 2, reversePolishNotation.size());
                    i = 0;
                } else if (!reversePolishNotation.get(i).equals("print") && !reversePolishNotation.get(i).contains("function_")) {
                    optimisedRPN.addAll(reversePolishNotation.subList(0, i + 3));
                    reversePolishNotation = reversePolishNotation.subList(i + 3, reversePolishNotation.size());
                    i = 0;
                } else {
                    optimisedRPN.add(reversePolishNotation.get(i));
                    reversePolishNotation = reversePolishNotation.subList(i + 1, reversePolishNotation.size());
                    i = 0;
                }
            } else if (reversePolishNotation.get(i).matches("^([-+*/=])")) {
                if (i - 2 < 0) {/*если [b, = ]*/
                    linear.add(String.valueOf(reversePolishNotation.subList(0, i + 1)));
                    reversePolishNotation.subList(0, i + 1).clear();
                    i = 0;
                } else {
                    linear.add(String.valueOf(reversePolishNotation.subList(i - 2, i + 1)));
                    reversePolishNotation.subList(i - 2, i + 1).clear();
                    i = 0;
                    if (reversePolishNotation.size() > 1 && reversePolishNotation.get(1).equals("=")) {
                        reversePolishNotation.add(1, "^" + linear.size());
                    }
                }
            } else if (reversePolishNotation.get(i).equals("!") && Integer.parseInt(reversePolishNotation.get(i - 1)) < Collections.indexOfSubList(reversePolishNotationCopy, reversePolishNotation)) {
                optimisedRPN.addAll(reversePolishNotationCopy.subList(Integer.parseInt(reversePolishNotation.get(i - 1)) + 5, Collections.indexOfSubList(reversePolishNotationCopy, reversePolishNotation) + 2));
                reversePolishNotation.subList(0, i + 1).clear();
                i = 0;
                linear.clear();
            } else if (optimisedRPN.size() > 2 && optimisedRPN.get(optimisedRPN.size() - 1).equals("!F")) {
                optimisedRPN.set(optimisedRPN.size() - 2, "~");
                i++;
            } else if (tableOfVariables.containsKey("function_" + reversePolishNotation.get(i))) {
                if (linear.size() > 0) {
                    return true;
                } else {
                    optimisedRPN.addAll(reversePolishNotation.subList(0, i + 2));
                    reversePolishNotation.subList(0, i + 2).clear();
                    i = 0;
                }
            } else if (reversePolishNotation.get(i).equals("return")) {
                if (linear.size() > 0) {
                    return true;
                } else {
                    optimisedRPN.addAll(reversePolishNotation.subList(0, i + 1));
                    reversePolishNotation.subList(0, i + 1).clear();
                    i = 0;
                }
            } else {
                i++;
            }
        }
        return linear.size() != 0;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
