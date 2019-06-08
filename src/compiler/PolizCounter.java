package compiler;

import enumerations.CompilerEnum;
import enumerations.LexemeEnum;
import enumerations.ServiceEnum;
import exceptions.CompileException;
import implementations.CHashSet;
import implementations.CList;
import implementations.Parsable;
import lexer.Token;
import lexer.Variable;

import java.util.*;

import static enumerations.LexemeEnum.*;

public class PolizCounter {
    private int currentIndex;
    private Stack<Token> stack;
    private Stack<Integer> callStack;
    private ArrayList<Token> poliz;
    private HashMap<String, Variable> globalVariableVisibility;
    private Stack<Deque<HashMap<String, Variable>>> localVisibilityStack;

    public void count(ArrayList<Token> poliz, HashMap<String, Variable> tableOfNames) throws CompileException {
        stack = new Stack<>();
        localVisibilityStack = new Stack<>();
        callStack = new Stack<>();
        this.poliz = poliz;
        this.globalVariableVisibility = tableOfNames;
        init();
        run();
    }

    private void run() throws CompileException {
        if (globalVariableVisibility.containsKey("main")) {
            currentIndex = (int) globalVariableVisibility.get("main").getValue();
            while (currentIndex < poliz.size()) {
                doOp(poliz.get(currentIndex));
                currentIndex++;
            }
        }
        System.out.println("Program finished with exit code " + stack.pop().getValue());
    }

    private void init() throws CompileException {
        boolean isFunc = false;
        for (currentIndex = 0; currentIndex < poliz.size(); currentIndex++) {
            Token token = poliz.get(currentIndex);
            if (token.getType().equals(ServiceEnum.START_FUNC)) {
                isFunc = true;
            }
            if (!isFunc)
                doOp(token);
            if (token.getType().equals(ServiceEnum.END_FUNC)) {
                isFunc = false;
            }
        }
    }

    private void doOp(Token token) throws CompileException {
        if (isValue(token)) return;
        CompilerEnum type = token.getType();
        if (type.equals(DOUBLE_TP) || type.equals(INT_TP)) {
            setType(type);
        } else if (type.equals(ASSIGN_OP) || type.equals(SUM_ASSIGN_OP) || type.equals(SUB_ASSIGN_OP)
                || type.equals(MUL_ASSIGN_OP) || type.equals(DIV_ASSIGN_OP)) {
            assignOp(type);
        } else if (type.equals(SUM_OP) || type.equals(SUB_OP) || type.equals(MUL_OP)
                || type.equals(DIV_OP) || type.equals(AND_OP) || type.equals(OR_OP)
                || type.equals(GREATER_EQUAL_OP) || type.equals(GREATER_OP) || type.equals(LESS_EQUAL_OP)
                || type.equals(LESS_OP) || type.equals(EQUAL_OP) || type.equals(NOT_EQUAL_OP)) {
            binaryOp(type);
        } else if (type.equals(PRINT_KW)) {
            printKw();
        } else if (type.equals(UNARY_SUB_OP) || type.equals(UNARY_SUM_OP) || type.equals(INC_OP)
                || type.equals(DEC_OP) || type.equals(NOT_OP)) {
            unaryOp(type);
        } else if (type.equals(ServiceEnum.GOTO) || type.equals(ServiceEnum.GOTO_LIE)) {
            gotoOp(type);
        } else if (type.equals(ServiceEnum.START_FUNC) || type.equals(ServiceEnum.END_FUNC)) {
            funcVisibility(token);
        } else if (type.equals(ServiceEnum.START_VISIBILITY_AREA) || type.equals(ServiceEnum.END_VISIBILITY_AREA)) {
            blockVisibility(token);
        } else if (type.equals(LexemeEnum.FUNC_KW)) {
            funcKw();
        } else if (type.equals(LexemeEnum.RETURN_KW)) {
            returnKw();
        } else if (type.equals(ServiceEnum.FUNC)) {
            functionCall(token);
        } else if (type.equals(LIST_KW) || type.equals(HASH_SET_KW)) {
            classKw(type);
        }
    }

    private void funcKw() throws CompileException {
        Token token = stack.pop();
        if (!token.getType().equals(VAR))
            throw new CompileException("Unknown error");
        Variable variable = getVariable(token.getValue());
        variable.setVariableType(ServiceEnum.FUNC);
        variable.setValue(currentIndex + 1);
    }

    private void classKw(CompilerEnum type) throws CompileException {
        Token token = stack.pop();
        if (!token.getType().equals(VAR))
            throw new CompileException("Unknown error");
        Variable variable = getVariable(token.getValue());
        variable.setVariableType(ServiceEnum.OBJECT);
        CompilerEnum varType = getTokenType(token);
        if (type.equals(LIST_KW)) {
            if (varType.equals(INT_TP))
                variable.setValue(new CList<Integer>());
            else if (varType.equals(DOUBLE_TP))
                variable.setValue(new CList<Double>());
        } else if (type.equals(HASH_SET_KW)) {
            if (varType.equals(INT_TP))
                variable.setValue(new CHashSet<Integer>());
            else if (varType.equals(DOUBLE_TP))
                variable.setValue(new CHashSet<Double>());
        }
    }

    private void blockVisibility(Token token) {
        CompilerEnum type = token.getType();
        if (type.equals(ServiceEnum.START_VISIBILITY_AREA)) {
            localVisibilityStack.peek().addFirst(new HashMap<>());
        }
        if (type.equals(ServiceEnum.END_VISIBILITY_AREA)) {
            localVisibilityStack.peek().removeFirst();
        }
    }

    private void funcVisibility(Token token) throws CompileException {
        CompilerEnum type = token.getType();
        if (type.equals(ServiceEnum.START_FUNC)) {
            Deque<HashMap<String, Variable>> funcDeq = new ArrayDeque<>();
            funcDeq.addFirst(new HashMap<>());
            localVisibilityStack.push(funcDeq);
            stack.push(new Token(ServiceEnum.START_FUNC, token.getValue()));
        }
        if (type.equals(ServiceEnum.END_FUNC)) {
            throw new CompileException("Function '" + token.getValue() + "' did not return a value");
        }
    }

    private void returnKw() throws CompileException {
        Token tmp = stack.pop();
        double res = (double) getValue(tmp);
        Token result;
        while (!stack.isEmpty() && !stack.peek().getType().equals(ServiceEnum.START_FUNC))
            stack.pop();
        if (callStack.isEmpty())
            currentIndex = poliz.size();
        else
            currentIndex = callStack.pop();

        if (getVariable(stack.peek().getValue()).getValueType().equals(INT_TP)) {
            result = new Token(INTEGER, ((Integer) ((int) res)).toString());
        } else {
            result = new Token(REAL, ((Double) (res)).toString());
        }
        stack.pop();
        stack.push(result);
        localVisibilityStack.pop();
    }

    private boolean methodCall(Token token) throws CompileException {
        Variable variable = getVariable(stack.pop().getValue());
        Parsable parsable = (Parsable) variable.getValue();
        ArrayList<Object> arguments = new ArrayList<>();
        while (!stack.peek().getType().equals(ServiceEnum.ARGUMENTS)) {
            Token tmp = stack.pop();
            double value = (double) getValue(tmp);
            CompilerEnum type = getTokenType(tmp);
            if (type.equals(INT_TP))
                arguments.add(0, (int) value);
            else
                arguments.add(0, value);
        }
        stack.pop();
        Object obj = parsable.doMethod(token.getValue(), arguments, variable.getValueType());
        if (obj == null)
            throw new CompileException("Wrong arguments in func '" + token.getValue() + "'");
        if (obj instanceof Integer)
            stack.push(new Token(INTEGER, obj.toString()));
        else if (obj instanceof Double)
            stack.push(new Token(REAL, obj.toString()));
        return true;
    }

    private boolean functionCall(Token token) throws CompileException {
        if (stack.peek().getType().equals(ServiceEnum.OBJECT))
            return methodCall(token);
        callStack.push(currentIndex);
        Stack<Token> tmpStack = new Stack<>();
        while (!stack.peek().getType().equals(ServiceEnum.ARGUMENTS)) {
            Token tmp = stack.pop();
            double value = (double) getValue(tmp);
            CompilerEnum type = getTokenType(tmp);
            if (type.equals(INT_TP))
                tmpStack.push(new Token(INTEGER, ((Integer) ((int) value)).toString()));
            else
                tmpStack.push(new Token(REAL, ((Double) (value)).toString()));
        }
        stack.pop();
        currentIndex = (int) getVariable(token.getValue()).getValue();
        while (!poliz.get(currentIndex).getType().equals(ServiceEnum.ARGUMENTS)) {
            doOp(poliz.get(currentIndex));
            if (poliz.get(currentIndex).getType().equals(INT_TP) || poliz.get(currentIndex).getType().equals(DOUBLE_TP)) {
                stack.push(tmpStack.pop());
                doOp(new Token(ASSIGN_OP, ASSIGN_OP.toString()));
            }
            ++currentIndex;
        }
        return true;
    }

    private void gotoOp(CompilerEnum type) throws CompileException {
        Token token1 = stack.pop();
        double value1 = (double) getValue(token1);
        if (type.equals(ServiceEnum.GOTO)) {
            currentIndex = (int) value1 - 1;
        } else if (type.equals(ServiceEnum.GOTO_LIE)) {
            double value2 = (double) getValue(stack.pop());
            if (value2 == 0)
                currentIndex = (int) value1 - 1;
        }
    }

    private void unaryOp(CompilerEnum type) throws CompileException {
        Token token = stack.pop();
        double value = (double) getValue(token);
        CompilerEnum valueType = getTokenType(token);
        if (type.equals(UNARY_SUB_OP)) {
            if (valueType.equals(INT_TP))
                stack.push(new Token(INTEGER, ((Integer) ((int) (value * -1))).toString()));
            else if (valueType.equals(DOUBLE_TP)) {
                stack.push(new Token(REAL, ((Double) ((value * -1))).toString()));
            }
        } else if (type.equals(UNARY_SUM_OP)) {
            stack.push(token);
        } else if (type.equals(INC_OP)) {
            if (valueType.equals(INT_TP))
                stack.push(new Token(INTEGER, ((Integer) ((int) (value + 1))).toString()));
            else if (valueType.equals(DOUBLE_TP)) {
                stack.push(new Token(REAL, ((Double) ((value + 1d))).toString()));
            }
        } else if (type.equals(DEC_OP)) {
            if (valueType.equals(INT_TP))
                stack.push(new Token(INTEGER, ((Integer) ((int) (value - 1))).toString()));
            else if (valueType.equals(DOUBLE_TP)) {
                stack.push(new Token(REAL, ((Double) ((value - 1d))).toString()));
            }
        } else if (type.equals(NOT_OP)) {
            if (value != 0) {
                stack.push(new Token(LexemeEnum.INT_TP, "0"));
            } else {
                stack.push(new Token(LexemeEnum.INT_TP, "1"));
            }
        }
    }

    private void binaryOp(CompilerEnum type) throws CompileException {
        Token token1 = stack.pop();
        Token token2 = stack.pop();
        double value1 = (double) getValue(token1);
        double value2 = (double) getValue(token2);
        CompilerEnum type1 = getTokenType(token1);
        CompilerEnum type2 = getTokenType(token2);
        if (type.equals(LexemeEnum.SUM_OP)) {
            Token res = type1.equals(LexemeEnum.DOUBLE_TP) || type2.equals(LexemeEnum.DOUBLE_TP) ?
                    new Token(LexemeEnum.DOUBLE_TP, ((Double) (value2 + value1)).toString()) :
                    new Token(LexemeEnum.INT_TP, ((Integer) (((int) value2 + (int) value1))).toString());
            stack.push(res);
        } else if (type.equals(LexemeEnum.SUB_OP)) {
            Token res = type1.equals(LexemeEnum.DOUBLE_TP) || type2.equals(LexemeEnum.DOUBLE_TP) ?
                    new Token(LexemeEnum.DOUBLE_TP, ((Double) (value2 - value1)).toString()) :
                    new Token(LexemeEnum.INT_TP, ((Integer) (((int) value2 - (int) value1))).toString());
            stack.push(res);
        } else if (type.equals(LexemeEnum.MUL_OP)) {
            Token res = type1.equals(LexemeEnum.DOUBLE_TP) || type2.equals(LexemeEnum.DOUBLE_TP) ?
                    new Token(LexemeEnum.DOUBLE_TP, ((Double) (value2 * value1)).toString()) :
                    new Token(LexemeEnum.INT_TP, ((Integer) (((int) value2 * (int) value1))).toString());
            stack.push(res);
        } else if (type.equals(LexemeEnum.DIV_OP)) {
            Token res = type1.equals(LexemeEnum.DOUBLE_TP) || type2.equals(LexemeEnum.DOUBLE_TP) ?
                    new Token(LexemeEnum.DOUBLE_TP, ((Double) (value2 / value1)).toString()) :
                    new Token(LexemeEnum.INT_TP, ((Integer) (((int) value2 / (int) value1))).toString());
            stack.push(res);
        } else if (type.equals(LexemeEnum.AND_OP)) {
            Token res = value2 != 0 && value1 != 0 ?
                    new Token(LexemeEnum.INT_TP, "1") :
                    new Token(LexemeEnum.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexemeEnum.OR_OP)) {
            Token res = value2 != 0 || value1 != 0 ?
                    new Token(LexemeEnum.INT_TP, "1") :
                    new Token(LexemeEnum.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexemeEnum.GREATER_OP)) {
            Token res = value2 > value1 ?
                    new Token(LexemeEnum.INT_TP, "1") :
                    new Token(LexemeEnum.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexemeEnum.GREATER_EQUAL_OP)) {
            Token res = value2 >= value1 ?
                    new Token(LexemeEnum.INT_TP, "1") :
                    new Token(LexemeEnum.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexemeEnum.LESS_OP)) {
            Token res = value2 < value1 ?
                    new Token(LexemeEnum.INT_TP, "1") :
                    new Token(LexemeEnum.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexemeEnum.LESS_EQUAL_OP)) {
            Token res = value2 <= value1 ?
                    new Token(LexemeEnum.INT_TP, "1") :
                    new Token(LexemeEnum.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexemeEnum.NOT_EQUAL_OP)) {
            Token res = value2 != value1 ?
                    new Token(LexemeEnum.INT_TP, "1") :
                    new Token(LexemeEnum.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexemeEnum.EQUAL_OP)) {
            Token res = value2 == value1 ?
                    new Token(LexemeEnum.INT_TP, "1") :
                    new Token(LexemeEnum.INT_TP, "0");
            stack.push(res);
        }
    }

    private void printKw() {
        printGlobalVariables();
        printLocalVariables();
    }

    private void printGlobalVariables() {
        printUnderscore(41);
        System.out.print("GLOBAL");
        printUnderscore(42);
        System.out.println();
        System.out.printf("|%-29s|%-29s|%-29s|\n", "Name", "Value", "Type");
        printUnderscore(90);
        System.out.println();
        for (Variable variable : globalVariableVisibility.values()) {
            if (variable.getVariableType().equals(VAR) || variable.getVariableType().equals(ServiceEnum.OBJECT)) {
                System.out.printf("|%-29s|%-29s|%-29s|\n", variable.getName(), variable.getValue(), variable.getValueType());
            }
        }
        printUnderscore(90);
        System.out.println("\n");
    }

    private void printLocalVariables() {
        printUnderscore(42);
        System.out.print("LOCAL");
        printUnderscore(42);
        System.out.println();
        System.out.printf("|%-29s|%-29s|%-29s|\n", "Name", "Value", "Type");
        printUnderscore(90);
        System.out.println();
        if (!localVisibilityStack.isEmpty()) {
            for (HashMap<String, Variable> field : localVisibilityStack.peek()) {
                for (Variable variable : field.values()) {
                    if (variable.getValueType() != null && !variable.getValueType().equals(ServiceEnum.LABEL)) {
                        System.out.printf("|%-29s|%-29s|%-29s|\n", variable.getName(), variable.getValue(), variable.getValueType());
                    }
                }
            }
        }
        printUnderscore(90);
        System.out.println("\n");
    }

    private void printUnderscore(int length) {
        System.out.print("|");
        for (int i = 0; i < length - 1; i++) {
            System.out.print("-");
        }
        System.out.print("|");
    }

    private void addVariable(Token token) throws CompileException {
        if (!token.getType().equals(VAR))
            throw new CompileException("Unknown error");
        if (localVisibilityStack.isEmpty()) {
            if (!globalVariableVisibility.containsKey(token.getValue())) {
                globalVariableVisibility.put(token.getValue()
                        , new Variable(token.getValue(), null, null, VAR));
            } else {
                throw new CompileException("Variable '" + token.getValue() + "' has been already declared");
            }
        } else {
            for (HashMap<String, Variable> field : localVisibilityStack.peek()) {
                if (field.containsKey(token.getValue())) {
                    throw new CompileException("Variable '" + token.getValue() + "' has been already declared");
                }
            }
            localVisibilityStack.peek().peekFirst().put(token.getValue()
                    , new Variable(token.getValue(), null, null, VAR));
        }
    }

    private Variable getVariable(String name) throws CompileException {
        if (!localVisibilityStack.isEmpty()) {
            for (HashMap<String, Variable> field : localVisibilityStack.peek()) {
                if (field.containsKey(name))
                    return field.get(name);
            }
        }
        if (globalVariableVisibility.containsKey(name))
            return globalVariableVisibility.get(name);
        throw new CompileException("Variable '" + name + "' has not been declared");
    }

    private void setType(CompilerEnum type) throws CompileException {
        Token token = stack.pop();
        addVariable(token);
        Variable variable = getVariable(token.getValue());
        if (variable.getValueType() != null)
            throw new CompileException("Variable '" + variable.getName() + "' has already been declared");
        variable.setValueType(type);
        stack.push(token);
    }

    private Object getValue(Token token) throws CompileException {
        if (token.getType().equals(VAR) || token.getType().equals(ServiceEnum.LABEL)) {
            Variable variable = getVariable(token.getValue());
            if (variable.getValueType() == null)
                throw new CompileException("Variable '" + variable.getName() + "' has not been declared");
            if (variable.getValue() == null)
                throw new CompileException("Variable '" + variable.getName() + "' has not been initialized");
            if (variable.getValueType().equals(DOUBLE_TP))
                return variable.getValue();
            else
                return (double) ((int) variable.getValue());
        } else {
            return Double.parseDouble(token.getValue());
        }
    }

    private CompilerEnum getTokenType(Token token) throws CompileException {
        if (token.getType().equals(LexemeEnum.VAR)) {
            Variable variable = getVariable(token.getValue());
            if (variable.getValueType() == null)
                throw new CompileException("Variable '" + variable.getName() + "' has not been declared");
            return variable.getValueType();
        } else {
            return token.getType().equals(INTEGER) ? INT_TP : DOUBLE_TP;
        }
    }

    private void assignOp(CompilerEnum type) throws CompileException {
        Token token1 = stack.pop();
        Token token2 = stack.pop();
        Object value = getValue(token1);
        Variable variable2 = getVariable(token2.getValue());
        if (variable2.getValueType() == null)
            throw new CompileException("Variable '" + variable2.getName() + "' has not been initialized");
        if (variable2.getValueType().equals(DOUBLE_TP)) {
            double tmp = Double.parseDouble(value.toString());
            if (type.equals(ASSIGN_OP))
                variable2.setValue(tmp);
            if (variable2.getValue() == null)
                throw new CompileException("Variable '" + variable2.getName() + "' has not been initialized");
            if (type.equals(SUM_ASSIGN_OP))
                variable2.setValue((double) variable2.getValue() + tmp);
            else if (type.equals(SUB_ASSIGN_OP))
                variable2.setValue((double) variable2.getValue() - tmp);
            else if (type.equals(MUL_ASSIGN_OP))
                variable2.setValue((double) variable2.getValue() * tmp);
            else if (type.equals(DIV_ASSIGN_OP))
                variable2.setValue((double) variable2.getValue() / tmp);
        } else if (variable2.getValueType().equals(LexemeEnum.INT_TP)) {
            int tmp = (int) Double.parseDouble(value.toString());
            if (type.equals(ASSIGN_OP))
                variable2.setValue(tmp);
            if (variable2.getValue() == null)
                throw new CompileException("Variable '" + variable2.getName() + "' has not been initialized");
            if (type.equals(SUM_ASSIGN_OP))
                variable2.setValue((int) variable2.getValue() + tmp);
            else if (type.equals(SUB_ASSIGN_OP))
                variable2.setValue((int) variable2.getValue() - tmp);
            else if (type.equals(MUL_ASSIGN_OP))
                variable2.setValue((int) variable2.getValue() * tmp);
            else if (type.equals(DIV_ASSIGN_OP))
                variable2.setValue((int) variable2.getValue() / tmp);
        }
    }

    private boolean isValue(Token token) {
        if (token.getType().equals(LexemeEnum.VAR) || token.getType().equals(LexemeEnum.REAL)
                || token.getType().equals(LexemeEnum.INTEGER) || token.getType().equals(ServiceEnum.LABEL)
                || token.getType().equals(ServiceEnum.ARGUMENTS) || token.getType().equals(ServiceEnum.OBJECT)) {
            stack.push(token);
            return true;
        } else {
            return false;
        }
    }
}
