package Lexer;

public class Token {
    private Lexeme typeOfLexeme;
    private String value;

    Token(Lexeme typeOfLexeme, String value) {
        this.typeOfLexeme = typeOfLexeme;
        this.value = value;
    }

    public Lexeme getLexeme() {
        return typeOfLexeme;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return typeOfLexeme + " '" + value + "'";
    }
}
