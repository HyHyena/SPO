package Lexer;

import java.util.regex.Pattern;

public enum Lexeme {
    OP("^(\\-|\\+|\\*|\\/)"),
    COMP_OP("(==|!=|<=|>=|<|>)"),
    LOG_OP("(\\&\\&|\\|\\|)"),
    ASSIGN_OP("="),
    DOT("\\."),
    COL_OP("(add|get|remove|size|contains)"),
    RUN("run"),
    JOIN("join"),
    L_RB("\\("),
    R_RB("\\)"),
    L_FB("\\{"),
    RETURN("return"),
    R_FB("\\}"),
    COM(","),
    SEMI(";"),
    IF("if"),
    WHILE("while"),
    THEN("then"),
    ELSE("else"),
    FOR("for"),
    PRINT("print"),
    TYPE("(int|LinkedList|HashSet|Thread|void)"),
    STRING("'([^']*?)'"),
    FUNCTION("function"),
    VAR("([a-zA-Z]|_)+\\w*"),
    DIGIT("(0|[1-9][0-9]*)");

    private Pattern pattern;

    Lexeme(String str) {
        this.pattern = Pattern.compile("^" + str);
    }

    public Pattern getPattern() {
        return pattern;
    }

}
