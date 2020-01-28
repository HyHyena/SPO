package Parser;

import Collections.HashSet;
import Collections.LinkedList;
import Lexer.Lexeme;
import Lexer.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Parser {
    private List<Token> tokens;
    private Stack<String> stack = new Stack<>();
    public HashMap<String, Map<String, Object>> tableOfVariables = new HashMap<>();
    public HashMap<String, List<String>> reversePolishNotation = new HashMap<>();
    private String currentFunction;
    private String prevFunction;
    private int currentToken;
    private int p0;
    private int p1;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        currentToken = 0;
        currentFunction = "main";
        reversePolishNotation.put(currentFunction, new ArrayList<>());
        tableOfVariables.put(currentFunction, new HashMap<>());
    }

    public boolean lang() {
        boolean lang = false;
        while (this.tokens.size() != currentToken) {
            if (!expr()) {
                System.err.println("Expr error");
                System.exit(5);
            } else {
                lang = true;
            }
        }
        //System.out.println(reversePolishNotation);
        return lang;
    }

    private boolean expr() {
        boolean expr = false;
        if (declare_stmt() || init() || assign() || while_expr() || if_expr() || for_expr() || set_stmt() ||
                print() || function() || functionExecute() || thread() || threadJoin()) {
            expr = true;
        }
        return expr;
    }

    private boolean thread() {
        boolean thread = false;
        int oldPos = currentToken;
        if (getTokenLexeme() == Lexeme.VAR && tableOfVariables.get(currentFunction).get(getLastTokenValue()) instanceof Thread) {
            String threadVar = getLastTokenValue();
            if (getTokenLexeme() == Lexeme.DOT) {
                if (getTokenLexeme() == Lexeme.RUN) {
                    if (getTokenLexeme() == Lexeme.L_RB) {
                        if (getTokenLexeme() == Lexeme.VAR && reversePolishNotation.containsKey("function_" + getLastTokenValue())) {
                            String funcName = getLastTokenValue();
                            if (getTokenLexeme() == Lexeme.R_RB) {
                                if (getTokenLexeme() == Lexeme.SEMI) {
                                    reversePolishNotation.get(currentFunction).add("thread_" + funcName);
                                    reversePolishNotation.get(currentFunction).add(threadVar);
                                    reversePolishNotation.get(currentFunction).add("run");
                                    thread = true;
                                }
                            } else {
                                currentToken--;
                                if (getTokenLexeme() == Lexeme.L_RB) {
                                    if (params(funcName)) {
                                        if (getTokenLexeme() == Lexeme.R_RB) {
                                            if (getTokenLexeme() == Lexeme.R_RB) {
                                                if (getTokenLexeme() == Lexeme.SEMI) {
                                                    reversePolishNotation.get(currentFunction).add("thread_" + funcName);
                                                    reversePolishNotation.get(currentFunction).add(threadVar);
                                                    reversePolishNotation.get(currentFunction).add("run");
                                                    thread = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        currentToken = thread ? currentToken : oldPos;
        return thread;
    }

    private boolean threadJoin() {
        boolean thread = false;
        int oldPos = currentToken;
        if (getTokenLexeme() == Lexeme.VAR && tableOfVariables.get(currentFunction).get(getLastTokenValue()) instanceof Thread) {
            String threadVar = getLastTokenValue();
            if (getTokenLexeme() == Lexeme.DOT) {
                if (getTokenLexeme() == Lexeme.JOIN) {
                    if (getTokenLexeme() == Lexeme.L_RB) {
                        if (getTokenLexeme() == Lexeme.R_RB) {
                            if (getTokenLexeme() == Lexeme.SEMI) {
                                reversePolishNotation.get(currentFunction).add(threadVar);
                                reversePolishNotation.get(currentFunction).add("join");
                                thread = true;
                            }
                        }
                    }
                }
            }
        }

        currentToken = thread ? currentToken : oldPos;
        return thread;
    }

    private boolean function() {
        boolean func = false;
        int oldPos = currentToken;
        if (getTokenLexeme() == Lexeme.FUNCTION) {
            if (getTokenLexeme() == Lexeme.TYPE) {
                if (getTokenLexeme() == Lexeme.VAR) {
                    String funcName = "function_" + getLastTokenValue();
                    tableOfVariables.put(funcName, new HashMap<>());
                    if (getTokenLexeme() == Lexeme.L_RB) {
                        if (paramsInit(funcName)) {
                            if (getTokenLexeme() == Lexeme.R_RB) {
                                if (getTokenLexeme() == Lexeme.L_FB) {
                                    prevFunction = currentFunction;
                                    currentFunction = funcName;
                                    reversePolishNotation.put(funcName, new ArrayList<>());
                                    while (expr()) {
                                    }
                                    if (getTokenLexeme() == Lexeme.RETURN) {
                                        tableOfVariables.get(currentFunction).put(getLastTokenValue(), null);
                                        if (getTokenLexeme() == Lexeme.VAR) {
                                            reversePolishNotation.get(currentFunction).add(getLastTokenValue());
                                            reversePolishNotation.get(currentFunction).add("return");
                                            if (getTokenLexeme() == Lexeme.SEMI) {
                                                if (getTokenLexeme() == Lexeme.R_FB) {
                                                    currentFunction = prevFunction;
                                                    reversePolishNotation.get(currentFunction).add(funcName);
                                                    func = true;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (getTokenLexeme() == Lexeme.R_FB) {
                                        currentFunction = prevFunction;
                                        reversePolishNotation.get(currentFunction).add(funcName);
                                        func = true;
                                    }
                                }
                            }
                        }
                    }
                    if (!func) {
                        reversePolishNotation.remove(funcName);
                    }
                }
            } else {
                currentToken--;
                if (getTokenLexeme() == Lexeme.VAR) {
                    String funcName = "function_" + getLastTokenValue();
                    tableOfVariables.put(funcName, new HashMap<>());
                    if (getTokenLexeme() == Lexeme.L_RB) {
                        if (paramsInit(funcName)) {
                            if (getTokenLexeme() == Lexeme.R_RB) {
                                if (getTokenLexeme() == Lexeme.L_FB) {
                                    prevFunction = currentFunction;
                                    currentFunction = funcName;
                                    reversePolishNotation.put(funcName, new ArrayList<>());
                                    while (expr()) {
                                    }
                                    if (getTokenLexeme() == Lexeme.RETURN) {
                                        tableOfVariables.get(currentFunction).put(getLastTokenValue(), null);
                                        if (getTokenLexeme() == Lexeme.VAR) {
                                            reversePolishNotation.get(currentFunction).add(getLastTokenValue());
                                            reversePolishNotation.get(currentFunction).add("return");
                                            if (getTokenLexeme() == Lexeme.SEMI) {
                                                if (getTokenLexeme() == Lexeme.R_FB) {
                                                    currentFunction = prevFunction;
                                                    //reversePolishNotation.get(currentFunction).add(funcName);
                                                    func = true;
                                                }
                                            }
                                        } else {
                                            currentToken--;
                                            if (getTokenLexeme() == Lexeme.SEMI) {
                                                if (getTokenLexeme() == Lexeme.R_FB) {
                                                    currentFunction = prevFunction;
                                                    //reversePolishNotation.get(currentFunction).add(funcName);
                                                    func = true;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (getTokenLexeme() == Lexeme.R_FB) {
                                        currentFunction = prevFunction;
                                        //reversePolishNotation.get(currentFunction).add(funcName);
                                        func = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        currentToken = func ? currentToken : oldPos;
        return func;
    }

    private boolean paramsInit(String funcName) {
        boolean param = false;
        int oldPos = currentToken;
        if (getTokenLexeme() == Lexeme.TYPE) {
            if (getTokenLexeme() == Lexeme.VAR) {
                tableOfVariables.get(funcName).put(getLastTokenValue(), null);
                if (getTokenLexeme() == Lexeme.COM) {
                    if (paramsInit(funcName)) {
                        param = true;
                    }
                } else {
                    currentToken--;
                    param = true;
                }
            }
        } else {
            currentToken--;
            if (getTokenLexeme() == Lexeme.R_RB) {
                currentToken--;
                param = true;
            }
        }

        currentToken = param ? currentToken : oldPos;
        return param;
    }

    private boolean init() {
        boolean init = false;
        int oldPos = currentToken;
        if (getTokenLexeme() == Lexeme.TYPE) {
            if (assign_op()) {
                if (getTokenLexeme() == Lexeme.SEMI) {
                    init = true;
                }
            }
        }
        currentToken = init ? currentToken : oldPos;
        return init;
    }

    private boolean assign() {
        boolean assign = false;
        int oldPos = currentToken;
        if (assign_op()) {
            if (getTokenLexeme() == Lexeme.SEMI) {
                assign = true;
            }
        }
        currentToken = assign ? currentToken : oldPos;
        return assign;
    }

    private boolean declare_stmt() {
        boolean declare = false;
        String var, type;
        int oldPos = currentToken;
        if (getTokenLexeme() == Lexeme.TYPE) {
            type = getLastTokenValue();
            if (getTokenLexeme() == Lexeme.VAR && !tableOfVariables.get(currentFunction).containsKey(getLastTokenValue())) {
                var = getLastTokenValue();
                if (getTokenLexeme() == Lexeme.SEMI) {
                    declare = true;
                    switch (type) {
                        case "HashSet":
                            tableOfVariables.get(currentFunction).put(var, new HashSet(3));
                            break;
                        case "LinkedList":
                            tableOfVariables.get(currentFunction).put(var, new LinkedList());
                            break;
                        case "Thread":
                            tableOfVariables.get(currentFunction).put(var, new Thread());
                            break;
                        default:
                            tableOfVariables.get(currentFunction).put(var, null);
                            break;
                    }
                }
            }
        }
        currentToken = declare ? currentToken : oldPos;
        return declare;
    }

    private boolean set_stmt() {
        boolean set = false;
        int oldPos = currentToken;
        String col_op;
        if (getTokenLexeme() == Lexeme.VAR && tableOfVariables.get(currentFunction).containsKey(getLastTokenValue())) {
            reversePolishNotation.get(currentFunction).add(getLastTokenValue());
            if (getTokenLexeme() == Lexeme.DOT) {
                if (getTokenLexeme() == Lexeme.COL_OP) {
                    col_op = getLastTokenValue();
                    if (bkt_value()) {
                        if (getTokenLexeme() == Lexeme.SEMI) {
                            set = true;
                            reversePolishNotation.get(currentFunction).add(col_op);
                        } else {
                            reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
                        }
                    } else {
                        reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
                    }
                } else {
                    reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
                }
            } else {
                reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
            }
        }
        currentToken = set ? currentToken : oldPos;
        return set;
    }

    private boolean if_expr() {
        boolean ifExpr = false;
        int oldPos = currentToken;
        if (getTokenLexeme() == Lexeme.IF) {
            if (condition_stmt()) {
                if (if_body()) {
                    ifExpr = true;
                    reversePolishNotation.get(currentFunction).set(p0, String.valueOf(reversePolishNotation.get(currentFunction).size() - 1));
                }
            }
        }
        currentToken = ifExpr ? currentToken : oldPos;
        return ifExpr;
    }

    private boolean if_body() {
        boolean if_body = false;
        int oldPos = currentToken;

        if (getTokenLexeme() == Lexeme.L_FB) {
            //noinspection StatementWithEmptyBody
            while (figure_bkt()) {
            }

            if (getTokenLexeme() == Lexeme.R_FB) {
                if_body = true;
            }
        }
        currentToken = if_body ? currentToken : oldPos;
        return if_body;
    }

    private boolean print() {
        boolean print = false;
        int old_position = currentToken;

        if (getTokenLexeme() == Lexeme.PRINT) {
            if (getTokenLexeme() == Lexeme.SEMI) {
                reversePolishNotation.get(currentFunction).add("print");
                print = true;
            } else {
                currentToken--;
                if (getTokenLexeme() == Lexeme.VAR) {
                    reversePolishNotation.get(currentFunction).add(getLastTokenValue());
                    if (getTokenLexeme() == Lexeme.SEMI) {
                        reversePolishNotation.get(currentFunction).add("print");
                        print = true;
                    } else {
                        reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
                    }
                } else {
                    currentToken--;
                    if (getTokenLexeme() == Lexeme.STRING) {
                        reversePolishNotation.get(currentFunction).add(getLastTokenValue());
                        if (getTokenLexeme() == Lexeme.SEMI) {
                            reversePolishNotation.get(currentFunction).add("print");
                            print = true;
                        } else {
                            reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
                        }
                    } else {
                        reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
                    }
                }
            }
        }

        currentToken = print ? currentToken : old_position;
        return print;
    }

    private boolean while_expr() {
        boolean whileExpr = false;
        int oldPos = currentToken;

        if (getTokenLexeme() == Lexeme.WHILE) {
            if (condition_stmt()) {
                if (while_body()) {
                    whileExpr = true;
                    reversePolishNotation.get(currentFunction).set(p0, String.valueOf(reversePolishNotation.get(currentFunction).size() + 1));
                    reversePolishNotation.get(currentFunction).add(String.valueOf(p1));
                    reversePolishNotation.get(currentFunction).add("!");
                }
            }
        }
        currentToken = whileExpr ? currentToken : oldPos;
        return whileExpr;
    }

    private boolean while_body() {
        boolean while_body = false;
        int oldPos = currentToken;

        if (getTokenLexeme() == Lexeme.L_FB) {
            //noinspection StatementWithEmptyBody
            while (expr()) { //
            }
            if (getTokenLexeme() == Lexeme.R_FB) {
                while_body = true;
            }
        }
        currentToken = while_body ? currentToken : oldPos;
        return while_body;
    }

    private boolean for_expr() {
        boolean for_loop = false;
        int oldPos = currentToken;
        if (getTokenLexeme() == Lexeme.FOR) {
            if (for_stmt()) {
                if (for_body()) {
                    for_loop = true;
                    reversePolishNotation.get(currentFunction).set(p0, String.valueOf(reversePolishNotation.get(currentFunction).size() + 1));
                    reversePolishNotation.get(currentFunction).add(String.valueOf(p1));
                    reversePolishNotation.get(currentFunction).add("!");
                }
            }
        }
        currentToken = for_loop ? currentToken : oldPos;
        return for_loop;
    }

    private boolean for_body() {
        boolean for_body = false;
        int oldPos = currentToken;

        if (getTokenLexeme() == Lexeme.L_FB) {
            //noinspection StatementWithEmptyBody
            while (figure_bkt()) {
            }
            if (getTokenLexeme() == Lexeme.R_FB) {
                for_body = true;
            }
        }
        currentToken = for_body ? currentToken : oldPos;
        return for_body;
    }

    private boolean for_stmt() {
        boolean for_expr = false;
        int oldPos = currentToken;

        if (getTokenLexeme() == Lexeme.L_RB) {
            if (start_stmt()) {
                if (if_log_expr()) {
                    if (assign_op()) {
                        if (getTokenLexeme() == Lexeme.R_RB) {
                            for_expr = true;
                        }
                    }
                }
            }
        }
        currentToken = for_expr ? currentToken : oldPos;
        return for_expr;
    }

    private boolean condition_stmt() {
        int oldPos = currentToken;
        boolean condition = false;
        if (getTokenLexeme() == Lexeme.L_RB) {
            if (log_stmt()) {//log_stmt
                if (getTokenLexeme() == Lexeme.R_RB) {
                    condition = true;
                }
            }
        }
        currentToken = condition ? currentToken : oldPos;
        return condition;
    }

    private boolean start_stmt() {
        boolean start_expr = false;

        if (init() || assign()) {
            start_expr = true;
        }
        return start_expr;
    }

    private boolean log_stmt() {
        boolean log_expr = false;
        int oldPos = currentToken;
        p1 = reversePolishNotation.get(currentFunction).size();
        if (stmt()) {
            if (getTokenLexeme() == Lexeme.COMP_OP) {
                String log_op = getLastTokenValue();
                if (stmt()) {
                    log_expr = true;
                    reversePolishNotation.get(currentFunction).add(log_op);
                    p0 = reversePolishNotation.get(currentFunction).size();
                    reversePolishNotation.get(currentFunction).add("" + p0);
                    reversePolishNotation.get(currentFunction).add("!F");
                }
            } else {
                reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
                reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
            }
        }

        currentToken = log_expr ? currentToken : oldPos;


        if (getTokenLexeme() == Lexeme.VAR) {
            reversePolishNotation.get(currentFunction).add(getLastTokenValue());
            if (getTokenLexeme() == Lexeme.DOT) {
                if (getTokenLexeme() == Lexeme.COL_OP && getLastTokenValue().equals("contains")) {
                    String col_op = getLastTokenValue();
                    if (bkt_value()) {
                        log_expr = true;
                        reversePolishNotation.get(currentFunction).add(col_op);
                        p0 = reversePolishNotation.get(currentFunction).size();
                        reversePolishNotation.get(currentFunction).add("" + p0);
                        reversePolishNotation.get(currentFunction).add("!F");
                    }
                }
            } else {
                reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
            }
        } else {
            currentToken--;
        }

        currentToken = log_expr ? currentToken : oldPos;
        return log_expr;
    }

    private boolean if_log_expr() {
        boolean if_log_expr = false;
        int oldPos = currentToken;
        if (assign_op() || stmt()) {
            if (getTokenLexeme() == Lexeme.COMP_OP) {
                String if_log_op = getLastTokenValue();
                if (assign_op() || stmt()) {
                    if (getTokenLexeme() == Lexeme.SEMI) {
                        if_log_expr = true;
                        reversePolishNotation.get(currentFunction).add(if_log_op);
                        int p2 = reversePolishNotation.get(currentFunction).size();
                        reversePolishNotation.get(currentFunction).add(p2 + "");
                        reversePolishNotation.get(currentFunction).add("!F");
                    }
                }
            }
        }
        currentToken = if_log_expr ? currentToken : oldPos;
        return if_log_expr;
    }

    private boolean assign_op() {
        boolean assign_op = false;
        int oldPos = currentToken;
        boolean add = false;
        String var;

        if (getTokenLexeme() == Lexeme.VAR) {
            var = getLastTokenValue();
            add = reversePolishNotation.get(currentFunction).add(var);
            if (getTokenLexeme() == Lexeme.ASSIGN_OP) {
                stack.push(getLastTokenValue());
                if (stmt()) {
                    assign_op = true;
                    tableOfVariables.get(currentFunction).put(var, null);
                } else if (functionExecute()) {
                    assign_op = true;
                }
            }
        }
        if (add && !assign_op) {
            reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
        }
        if (assign_op) {
            while (!stack.empty()) {
                reversePolishNotation.get(currentFunction).add(stack.pop());
            }
        }
        currentToken = assign_op ? currentToken : oldPos;
        return assign_op;
    }

    private boolean functionExecute() {
        boolean func = false;
        int oldPos = currentToken;
        String funcName;
        if (getTokenLexeme() == Lexeme.VAR && tableOfVariables.containsKey("function_" + getLastTokenValue())) {
            funcName = getLastTokenValue();
            if (getTokenLexeme() == Lexeme.L_RB) {
                if (params(funcName)) {
                    if (getTokenLexeme() == Lexeme.R_RB) {
                        if (getTokenLexeme() == Lexeme.SEMI) {
                            reversePolishNotation.get(currentFunction).add(funcName);
                            func = true;
                        }
                    }
                } else if (getTokenLexeme() == Lexeme.R_RB) {
                    if (getTokenLexeme() == Lexeme.SEMI) {
                        reversePolishNotation.get(currentFunction).add(funcName);
                        func = true;
                    }
                }
            }
        }

        currentToken = func ? currentToken : oldPos;
        return func;
    }

    private boolean params(String funcName) {
        boolean param = false;
        int oldPos = currentToken;
        if (getTokenLexeme() == Lexeme.VAR && tableOfVariables.get(currentFunction).containsKey(getLastTokenValue())) {
            reversePolishNotation.get(currentFunction).add("arg_" + getLastTokenValue());
            if (getTokenLexeme() == Lexeme.COM) {
                if (params(currentFunction)) {
                    param = true;
                }
            } else {
                currentToken--;
                param = true;
            }
        } else {
            currentToken--;
            if (getTokenLexeme() == Lexeme.R_RB && tableOfVariables.get("function_" + funcName).size() == 1) {
                currentToken--;
                param = true;
            }
        }

        currentToken = param ? currentToken : oldPos;
        return param;
    }

    private boolean stmt() {
        boolean value = false;

        if (value()) {
            //noinspection StatementWithEmptyBody
            while (opValue()) {
            }
            value = true;
        }
        return value;
    }

    private boolean opValue() {
        boolean opVal = false;
        int oldPos = currentToken;

        if (getTokenLexeme() == Lexeme.OP) {
            String op = getLastTokenValue();
            while (getPriority(op) <= getPriority(stack.peek())) {
                reversePolishNotation.get(currentFunction).add(stack.pop());
            }
            stack.push(op);
            if (value()) {
                opVal = true;
            }
        }
        currentToken = opVal ? currentToken : oldPos;
        return opVal;
    }

    private boolean value() {
        if (getTokenLexeme() == Lexeme.VAR) {
            reversePolishNotation.get(currentFunction).add(getLastTokenValue());
            if (!tableOfVariables.get(currentFunction).containsKey(getLastTokenValue())) {
                reversePolishNotation.get(currentFunction).remove(reversePolishNotation.get(currentFunction).size() - 1);
                currentToken--;
                return false;
            }
            if (tableOfVariables.get(currentFunction).get(getLastTokenValue()) instanceof LinkedList
                    || tableOfVariables.get(currentFunction).get(getLastTokenValue()) instanceof HashSet) {
                if (getTokenLexeme() == Lexeme.DOT) {
                    if (getTokenLexeme() == Lexeme.COL_OP && (getLastTokenValue().equals("get")
                            || getLastTokenValue().equals("size"))) {
                        String col_op = getLastTokenValue();
                        if (bkt_value()) {
                            reversePolishNotation.get(currentFunction).add(col_op);
                            return true;
                        }
                    }
                }

            } else {
                return true;
            }

        } else {
            currentToken--;
        }

        if (getTokenLexeme() == Lexeme.DIGIT) {
            reversePolishNotation.get(currentFunction).add(getLastTokenValue());
            return true;
        } else {
            currentToken--;
        }

        String last = getLastTokenValue();
        if (getTokenLexeme() == Lexeme.R_RB && last.equals("(")) {
            currentToken--;
            return true;
        } else {
            currentToken--;
        }

        return bkt_value();
    }

    private boolean bkt_value() {
        boolean bkt = false;
        int oldPos = currentToken;

        if (getTokenLexeme() == Lexeme.L_RB) {
            stack.push(getLastTokenValue());
            if (stmt()) {
                if (getTokenLexeme() == Lexeme.R_RB) {
                    while (!stack.peek().equals("(")) {
                        reversePolishNotation.get(currentFunction).add(stack.pop());
                    }
                    stack.pop();
                    bkt = true;
                }
            }
        }
        currentToken = bkt ? currentToken : oldPos;
        return bkt;
    }


    private boolean figure_bkt() {
        boolean bkt = false;
        if (init() || assign() || set_stmt()) {
            bkt = true;
        }
        return bkt;
    }


    private Lexeme getTokenLexeme() {
        try {
            return tokens.get(currentToken++).getLexeme();
        } catch (IndexOutOfBoundsException ex) {
            System.err.println("Error: Lexer.Lexer.Lexeme expected");
            System.exit(2);
        }
        return null;
    }


    private String getLastTokenValue() {
        return tokens.get(currentToken - 1).getValue();
    }

    private int getPriority(String str) {
        switch (str) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "^":
            case "/":
            case "%":
                return 2;
            case "=":
            case "(":
                return 0;
            default:
                System.err.println("Error: in " + str);
                System.exit(5);
                return 0;
        }
    }
}
