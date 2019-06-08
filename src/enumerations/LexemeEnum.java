package enumerations;

import java.util.HashMap;

public enum LexemeEnum implements CompilerEnum {
    INT_TP(2),
    DOUBLE_TP(2),
    DO_KW,
    WHILE_KW,
    PRINT_KW(1),
    IF_KW,
    ELSE_KW,
    FOR_KW,
    NEW_KW,
    RETURN_KW(1),
    FUNC_KW(1),
    CONTINUE_KW(1),
    BREAK_KW(1),
    LIST_KW,
    HASH_SET_KW,
    REAL,
    INTEGER,
    VAR,
    INC_OP(9),
    DEC_OP(9),
    SUM_ASSIGN_OP(1),
    SUB_ASSIGN_OP(1),
    MUL_ASSIGN_OP(1),
    DIV_ASSIGN_OP(1),
    GREATER_EQUAL_OP(6),
    LESS_EQUAL_OP(6),
    EQUAL_OP(6),
    NOT_EQUAL_OP(6),
    TAB_SP,
    COMMA_SP,
    DOT_SP,
    SPACE_SP,
    COLON_SP,
    SEMICOLON_SP(100),
    LEFT_PARENTHESES(0),
    RIGHT_PARENTHESES(1),
    LEFT_BRACES(0),
    RIGHT_BRACES(1),
    RIGHT_SQUARE_BRACKETS,
    LEFT_SQUARE_BRACKETS,
    ASSIGN_OP(1),
    SUM_OP(7),
    DIV_OP(8),
    MUL_OP(8),
    SUB_OP(7),
    GREATER_OP(6),
    LESS_OP(6),
    NOT_OP(5),
    AND_OP(4),
    OR_OP(3),
    SINGLE_QUOTES,
    DOUBLE_QUOTES,
    UNARY_SUB_OP(9),
    UNARY_SUM_OP(9);

    private int priority;

    LexemeEnum() {
        priority = 0;
    }

    LexemeEnum(int priority) {
        this.priority = priority;
    }

    public static HashMap<LexemeEnum, String> initRegExp() {
        HashMap<LexemeEnum, String> regExp = new HashMap<>();
        regExp.put(LexemeEnum.INT_TP, "^int$");
        regExp.put(LexemeEnum.DOUBLE_TP, "^double$");
        regExp.put(LexemeEnum.IF_KW, "^if$");
        regExp.put(LexemeEnum.PRINT_KW, "^print$");
        regExp.put(LexemeEnum.DO_KW, "^do$");
        regExp.put(LexemeEnum.ELSE_KW, "^else$");
        regExp.put(LexemeEnum.NEW_KW, "^new$");
        regExp.put(LexemeEnum.WHILE_KW, "^while$");
        regExp.put(LexemeEnum.FOR_KW, "^for$");
        regExp.put(LexemeEnum.BREAK_KW, "^break$");
        regExp.put(LexemeEnum.CONTINUE_KW, "^continue$");
        regExp.put(LexemeEnum.FUNC_KW, "^func$");
        regExp.put(LexemeEnum.RETURN_KW, "^return$");
        regExp.put(LexemeEnum.LIST_KW, "^List$");
        regExp.put(LexemeEnum.HASH_SET_KW, "^HashSet$");
        regExp.put(LexemeEnum.REAL, "^(0|[1-9][0-9]*)[.][0-9]*$");
        regExp.put(LexemeEnum.INTEGER, "^(0|([1-9]{1}[0-9]*))$");
        regExp.put(LexemeEnum.VAR, "^([a-zA-Z]{1}([0-9]|[a-zA-Z])*)$");
        regExp.put(LexemeEnum.EQUAL_OP, "^==$");
        regExp.put(LexemeEnum.GREATER_EQUAL_OP, "^>=$");
        regExp.put(LexemeEnum.LESS_EQUAL_OP, "^<=$");
        regExp.put(LexemeEnum.NOT_EQUAL_OP, "^!=$");
        regExp.put(LexemeEnum.NOT_OP, "^!$");
        regExp.put(LexemeEnum.INC_OP, "^\\+\\+$");
        regExp.put(LexemeEnum.DEC_OP, "^--$");
        regExp.put(LexemeEnum.SUM_ASSIGN_OP, "^\\+=$");
        regExp.put(LexemeEnum.SUB_ASSIGN_OP, "^-=$");
        regExp.put(LexemeEnum.MUL_ASSIGN_OP, "^\\*=$");
        regExp.put(LexemeEnum.DIV_ASSIGN_OP, "^/=$");
        regExp.put(LexemeEnum.GREATER_OP, "^>$");
        regExp.put(LexemeEnum.ASSIGN_OP, "^=$");
        regExp.put(LexemeEnum.LESS_OP, "^<$");
        regExp.put(LexemeEnum.SEMICOLON_SP, "^;$");
        regExp.put(LexemeEnum.SUB_OP, "^-$");
        regExp.put(LexemeEnum.SUM_OP, "^\\+$");
        regExp.put(LexemeEnum.DIV_OP, "^/$");
        regExp.put(LexemeEnum.MUL_OP, "^\\*$");
        regExp.put(LexemeEnum.OR_OP, "^\\|\\|$");
        regExp.put(LexemeEnum.AND_OP, "^&&$");
        regExp.put(LexemeEnum.LEFT_PARENTHESES, "^\\($");
        regExp.put(LexemeEnum.RIGHT_PARENTHESES, "^\\)$");
        regExp.put(LexemeEnum.RIGHT_BRACES, "^\\}$");
        regExp.put(LexemeEnum.LEFT_BRACES, "^\\{$");
        regExp.put(LexemeEnum.LEFT_SQUARE_BRACKETS, "^\\[$");
        regExp.put(LexemeEnum.RIGHT_SQUARE_BRACKETS, "^\\]$");
        regExp.put(LexemeEnum.SPACE_SP, "^ $");
        regExp.put(LexemeEnum.COMMA_SP, "^,$");
        regExp.put(LexemeEnum.DOT_SP, "^\\.$");
        regExp.put(LexemeEnum.TAB_SP, "^\t$");
        regExp.put(LexemeEnum.COLON_SP, "^:$");
        regExp.put(LexemeEnum.SINGLE_QUOTES, "^\'$");
        regExp.put(LexemeEnum.DOUBLE_QUOTES, "^\"$");
        regExp.put(LexemeEnum.UNARY_SUB_OP, "^-$");
        regExp.put(LexemeEnum.UNARY_SUM_OP, "^\\+$");
        return regExp;
    }


    @Override
    public int getPriority() {
        return priority;
    }
}
