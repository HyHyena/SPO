package parser;

import enumerations.ComponentsEnum;
import enumerations.LexemeEnum;
import exceptions.ParseException;
import lexer.Token;
import parser.ParseTree.Node;

import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private ParseTree tree;
    private int offset;

    public void parse(ArrayList<Token> tokens) throws ParseException {
        init(tokens);
        while (offset + 1 < tokens.size()) {
            if (!expr(tree.getRoot())) {
                throw new ParseException(generateTokenNotFoundException(ComponentsEnum.FUNC_STMT.toString()));
            }
        }
    }

    public ParseTree getTree() {
        return tree;
    }

    private void init(ArrayList<Token> tokens) {
        this.tokens = tokens;
        tree = new ParseTree(ComponentsEnum.EXPRESSION);
        offset = -1;
    }

    private Token getValidToken(int offset) throws ParseException {
        if (offset >= tokens.size())
            throw new ParseException();
        return tokens.get(offset);
    }

    private String generateTokenNotFoundException(String expectedToken) {
        Token token = tokens.get(offset);
        StringBuilder sb = new StringBuilder();
        sb.append("\nRequired: ");
        sb.append(expectedToken);
        sb.append("\nFounded: ");
        sb.append(token.getType());
        sb.append(" with value \"");
        sb.append(token.getValue());
        sb.append("\" in row ");
        sb.append(token.getRow());
        sb.append(" and column ");
        sb.append(token.getColumn());
        return sb.toString();
    }

    private String generateEndOfFileException(String expectedToken) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nRequired: ");
        sb.append(expectedToken);
        sb.append("\nFounded: end of file");
        return sb.toString();
    }

    private boolean expr(Node root) throws ParseException {
        return declareStmt(root) || assignStmt(root) || funcStmt(root) || classDeclareStmt(root);
    }

    private boolean blockExpr(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.BLOCK_EXPRESSION));
        if (declareStmt(node.getNode()) || assignStmt(node.getNode()) || whileStmt(node.getNode())
                || ifStmt(node.getNode()) || forStmt(node.getNode()) || doWhileStmt(node.getNode())
                || printStmt(node.getNode()) || returnStmt(node.getNode()) || breakStmt(node.getNode())
                || continueStmt(node.getNode()) || classDeclareStmt(node.getNode()))
            return true;
        node.deleteNode();
        return false;
    }

    private boolean bracesExpr(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.BRACES_EXPRESSION));
        if (!leftBraces(++offset, node.getNode()))
            return false;
        while (blockExpr(node.getNode())) ;
        if (!rightBraces(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.RIGHT_BRACES.toString()));
        return true;
    }

    private boolean assignStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.ASSIGN_STMT));
        if (!typedVar(node.getNode()) && !var(++offset, node.getNode())) {
            --offset;
            node.deleteNode();
            return false;
        }
        if (!assignOp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.ASSIGN_OP.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.VALUE.toString()));
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean printStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.PRINT_STMT));
        if (!printKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean breakStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.BREAK_STMT));
        if (!breakKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean continueStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.CONTINUE_STMT));
        if (!continueKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean returnStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.RETURN_STMT));
        if (!returnKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.VALUE.toString()));
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.SEMICOLON_SP.toString()));
        return true;
    }


    private boolean typedVar(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.TYPED_VAR));
        if (type(++offset, node.getNode())) {
            if (!var(++offset, node.getNode()))
                throw new ParseException(generateTokenNotFoundException(LexemeEnum.VAR.toString()));
        } else {
            node.deleteNode();
            --offset;
            return false;
        }
        return true;
    }

    private boolean whileStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.WHILE_STMT));
        if (!whileKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!leftParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.LEFT_PARENTHESES.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.VALUE.toString()));
        if (!rightParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.RIGHT_PARENTHESES.toString()));
        if (!bracesExpr(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.EXPRESSION.toString()));
        return true;
    }

    private boolean doWhileStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.DO_WHILE_STMT));
        if (!doKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!bracesExpr(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.EXPRESSION.toString()));
        if (!whileKw(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.WHILE_KW.toString()));
        if (!leftParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.LEFT_PARENTHESES.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.VALUE.toString()));
        if (!rightParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.RIGHT_PARENTHESES.toString()));
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean declareStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.DECLARE_STMT));
        if (!typedVar(node.getNode())) {
            node.deleteNode();
            return false;
        }
        if (!semicolonSp(++offset, node.getNode())) {
            offset -= 3;
            node.deleteNode();
            return false;
        }
        return true;
    }

    private boolean classDeclareStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.CLASS_DECLARE_STMT));
        if (!classKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!lessOp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.COLON_SP.toString()));
        if (!type(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.TYPE.toString()));
        if (!greaterOp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.COLON_SP.toString()));
        if (!var(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.VAR.toString()));
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean ifStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.IF_STMT));
        if (!ifKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!leftParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.LEFT_PARENTHESES.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.VALUE.toString()));
        if (!rightParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.RIGHT_PARENTHESES.toString()));
        if (!bracesExpr(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.EXPRESSION.toString()));
        if (!elseKw(++offset, node.getNode())) {
            --offset;
            return true;
        }
        if (!ifStmt(node.getNode()))
            if (!bracesExpr(node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ComponentsEnum.EXPRESSION.toString()));
        return true;
    }

    private boolean funcStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.FUNC_STMT));
        if (!funcKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!type(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.TYPE.toString()));
        if (!var(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.FUNC_STMT.toString()));
        if (!argumentsWithType(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.ARGUMENTS.toString()));
        if (semicolonSp(++offset, node.getNode()))
            return true;
        --offset;
        if (!bracesExpr(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.EXPRESSION.toString()));
        return true;
    }

    private boolean argumentsWithType(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.ARGUMENTS));
        if (!leftParentheses(++offset, node.getNode())) {
            --offset;
            node.deleteNode();
            return false;
        }
        if (!type(++offset, node.getNode()))
            return rightParentheses(offset, node.getNode());
        if (!var(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.VAR.toString()));
        while (commaSp(++offset, node.getNode())) {
            if (!type(++offset, node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ComponentsEnum.TYPE.toString()));
            if (!var(++offset, node.getNode()))
                throw new ParseException(generateTokenNotFoundException(LexemeEnum.VAR.toString()));
        }
        return rightParentheses(offset, node.getNode());
    }

    private boolean argumentsWithoutType(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.ARGUMENTS));
        if (!leftParentheses(++offset, node.getNode())) {
            --offset;
            node.deleteNode();
            return false;
        }
        if (!valueStmt(node.getNode()))
            return rightParentheses(offset, node.getNode());
        while (commaSp(++offset, node.getNode())) {
            if (!valueStmt(node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ComponentsEnum.VALUE.toString()));
        }
        return rightParentheses(offset, node.getNode());
    }

    private boolean forStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.FOR_STMT));
        if (!forKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!leftParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.LEFT_PARENTHESES.toString()));
        if (!forAssign(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.FOR_ASSIGN.toString()));
        if (!forCondition(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.FOR_CONDITION.toString()));
        if (!forIterator(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.FOR_ITERATOR.toString()));
        if (!rightParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.RIGHT_PARENTHESES.toString()));
        if (!bracesExpr(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.EXPRESSION.toString()));
        return true;
    }

    private boolean forAssign(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.FOR_ASSIGN));
        multipleAssignStmt(node.getNode());
        while (commaSp(++offset, node.getNode())) {
            if (!multipleAssignStmt(node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ComponentsEnum.ASSIGN_STMT.toString()));
        }
        if (!semicolonSp(offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean forCondition(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.FOR_CONDITION));
        valueStmt(node.getNode());
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean forIterator(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.FOR_ITERATOR));
        multipleAssignStmtWithoutType(node.getNode());
        while (commaSp(++offset, node.getNode())) {
            if (!multipleAssignStmtWithoutType(node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ComponentsEnum.ASSIGN_STMT.toString()));
        }
        --offset;
        return true;
    }

    private boolean multipleAssignStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.ASSIGN_STMT));
        if (!typedVar(node.getNode()) && !var(++offset, node.getNode())) {
            --offset;
            node.deleteNode();
            return false;
        }
        if (!assignOp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.ASSIGN_OP.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.VALUE.toString()));
        return true;
    }

    private boolean multipleAssignStmtWithoutType(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.ASSIGN_STMT));
        if (!var(++offset, node.getNode())) {
            --offset;
            node.deleteNode();
            return false;
        }
        if (!assignOp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.ASSIGN_OP.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.VALUE.toString()));
        return true;
    }

    private boolean valueStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.VALUE_STMT));
        if (signValue(node.getNode())) {
            while (true) {
                if (!binaryOp(++offset, node.getNode())) {
                    --offset;
                    return true;
                }
                if (!signValue(node.getNode()))
                    throw new ParseException(generateTokenNotFoundException(ComponentsEnum.VALUE.toString()));
            }
        } else {
            node.deleteNode();
            return false;
        }
    }

    private boolean signValue(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.SIGN_VALUE));
        if (!unaryOp(++offset, node.getNode())) {
            if (sign(offset, node.getNode())) {
                ++offset;
            }
        } else {
            ++offset;
        }
        if (!value(offset, node.getNode())) {
            node.deleteNode();
            return false;
        } else {
            return true;
        }
    }

    private boolean sign(int offset, Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.SIGN));
        try {
            Token token = getValidToken(offset);
            if (token.getType().equals(LexemeEnum.SUM_OP) || token.getType().equals(LexemeEnum.SUB_OP)) {
                Token tmpToken = new Token(token);
                tmpToken.setType(tmpToken.getType() == LexemeEnum.SUM_OP ? LexemeEnum.UNARY_SUM_OP : LexemeEnum.UNARY_SUB_OP);
                tmpToken.setValue(":" + tmpToken.getValue());
                node.addNode(new Node(tmpToken, tmpToken.getType()));
                return true;
            } else {
                node.deleteNode();
                return false;
            }
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ComponentsEnum.SIGN.toString()));
        }
    }

    private boolean value(int offset, Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.VALUE));
        if (funcValue(node.getNode()) || methodValue(node.getNode()) || var(offset, node.getNode())
                || number(offset, node.getNode()) || bValueStmt(node.getNode())) {
            return true;
        } else {
            node.deleteNode();
            return false;
        }
    }

    private boolean bValueStmt(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.B_VALUE_STMT));
        if (leftParentheses(offset, node.getNode())) {
            if (!valueStmt(node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ComponentsEnum.VALUE.toString()));
            if (!rightParentheses(++offset, node.getNode()))
                throw new ParseException(generateTokenNotFoundException(LexemeEnum.RIGHT_PARENTHESES.toString()));
        } else {
            node.deleteNode();
            return false;
        }
        return true;
    }

    private boolean funcValue(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.FUNC_VALUE));
        if (var(offset, node.getNode()) && argumentsWithoutType(node.getNode())) {
            return true;
        } else {
            node.deleteNode();
            return false;
        }
    }

    private boolean methodValue(Node node) throws ParseException {
        node.addNode(new Node(ComponentsEnum.METHOD_VALUE));
        if (!var(offset, node.getNode())) {
            node.deleteNode();
            return false;
        }
        if (!colonSp(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!colonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexemeEnum.COLON_SP.toString()));
        ++offset;
        if (!funcValue(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ComponentsEnum.FUNC_VALUE.toString()));
        return true;
    }

    private boolean var(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.VAR, offset, node);
    }

    private boolean commaSp(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.COMMA_SP, offset, node);
    }

    private boolean colonSp(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.COLON_SP, offset, node);
    }

    private boolean number(int offset, Node node) throws ParseException {
        try {
            return isNeededToken(LexemeEnum.INTEGER, offset, node) || isNeededToken(LexemeEnum.REAL, offset, node);
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ComponentsEnum.NUMBER.toString()));
        }
    }

    private boolean type(int offset, Node node) throws ParseException {
        try {
            return isNeededToken(LexemeEnum.INT_TP, offset, node) || isNeededToken(LexemeEnum.DOUBLE_TP, offset, node);
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ComponentsEnum.TYPE.toString()));
        }
    }

    private boolean assignOp(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.ASSIGN_OP, offset, node) || isNeededToken(LexemeEnum.SUB_ASSIGN_OP, offset, node)
                || isNeededToken(LexemeEnum.SUM_ASSIGN_OP, offset, node) || isNeededToken(LexemeEnum.MUL_ASSIGN_OP, offset, node)
                || isNeededToken(LexemeEnum.DIV_ASSIGN_OP, offset, node);
    }

    private boolean funcKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.FUNC_KW, offset, node);
    }

    private boolean whileKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.WHILE_KW, offset, node);
    }

    private boolean printKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.PRINT_KW, offset, node);
    }

    private boolean breakKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.BREAK_KW, offset, node);
    }

    private boolean continueKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.CONTINUE_KW, offset, node);
    }

    private boolean returnKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.RETURN_KW, offset, node);
    }

    private boolean ifKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.IF_KW, offset, node);
    }

    private boolean elseKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.ELSE_KW, offset, node);
    }

    private boolean forKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.FOR_KW, offset, node);
    }

    private boolean doKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.DO_KW, offset, node);
    }


    private boolean unaryOp(int offset, Node node) throws ParseException {
        try {
            return isNeededToken(LexemeEnum.INC_OP, offset, node) || isNeededToken(LexemeEnum.DEC_OP, offset, node)
                    || isNeededToken(LexemeEnum.NOT_OP, offset, node);
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ComponentsEnum.UNARY_OP.toString()));
        }
    }

    private boolean classKw(int offset, Node node) throws ParseException {
        try {
            return isNeededToken(LexemeEnum.LIST_KW, offset, node) || isNeededToken(LexemeEnum.HASH_SET_KW, offset, node);
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ComponentsEnum.CLASS_KW.toString()));
        }
    }

    private boolean binaryOp(int offset, Node node) throws ParseException {
        try {
            return isNeededToken(LexemeEnum.SUB_OP, offset, node) || isNeededToken(LexemeEnum.DIV_OP, offset, node)
                    || isNeededToken(LexemeEnum.MUL_OP, offset, node) || isNeededToken(LexemeEnum.SUM_OP, offset, node)
                    || isNeededToken(LexemeEnum.AND_OP, offset, node) || isNeededToken(LexemeEnum.OR_OP, offset, node)
                    || isNeededToken(LexemeEnum.GREATER_EQUAL_OP, offset, node) || isNeededToken(LexemeEnum.LESS_EQUAL_OP, offset, node)
                    || isNeededToken(LexemeEnum.GREATER_OP, offset, node) || isNeededToken(LexemeEnum.LESS_OP, offset, node)
                    || isNeededToken(LexemeEnum.EQUAL_OP, offset, node) || isNeededToken(LexemeEnum.NOT_EQUAL_OP, offset, node);
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ComponentsEnum.BINARY_OP.toString()));
        }
    }

    private boolean semicolonSp(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.SEMICOLON_SP, offset, node);
    }

    private boolean lessOp(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.LESS_OP, offset, node);
    }

    private boolean greaterOp(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.GREATER_OP, offset, node);
    }


    private boolean leftParentheses(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.LEFT_PARENTHESES, offset, node);
    }

    private boolean rightParentheses(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.RIGHT_PARENTHESES, offset, node);
    }

    private boolean leftBraces(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.LEFT_BRACES, offset, node);
    }

    private boolean rightBraces(int offset, Node node) throws ParseException {
        return isNeededToken(LexemeEnum.RIGHT_BRACES, offset, node);
    }

    private boolean isNeededToken(LexemeEnum lexeme, int offset, Node node) throws ParseException {
        try {
            Token token = getValidToken(offset);
            if (token.getType().equals(lexeme)) {
                node.addNode(new Node(token, lexeme));
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(lexeme.toString()));
        }
    }
}
