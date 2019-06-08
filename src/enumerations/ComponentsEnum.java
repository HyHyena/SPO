package enumerations;

public enum ComponentsEnum implements CompilerEnum {
    EXPRESSION,
    FUNC_STMT,
    IF_STMT,
    WHILE_STMT,
    DO_WHILE_STMT,
    PRINT_STMT,
    BREAK_STMT,
    CONTINUE_STMT,
    RETURN_STMT,
    DECLARE_STMT,
    FOR_STMT,
    FOR_ASSIGN,
    FOR_CONDITION,
    FOR_ITERATOR,
    TYPED_VAR,
    TYPE,
    FUNC_VALUE,
    VALUE,
    BLOCK_EXPRESSION,
    BRACES_EXPRESSION,
    ASSIGN_STMT,
    ARGUMENTS,
    B_VALUE_STMT,
    VALUE_STMT,
    SIGN_VALUE,
    SIGN,
    NUMBER,
    BINARY_OP,
    UNARY_OP,
    CLASS_DECLARE_STMT,
    METHOD_VALUE,
    CLASS_KW;

    @Override
    public int getPriority() {
        return 0;
    }
}
