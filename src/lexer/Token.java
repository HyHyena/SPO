package lexer;

import enumerations.CompilerEnum;

public class Token {
    private long startingIndex;
    private long endingIndex;
    private CompilerEnum type;
    private String value;
    private long row;
    private long column;

    public Token(long startingIndex, long endingIndex, CompilerEnum type, String value, long row, long column) {
        this.startingIndex = startingIndex;
        this.endingIndex = endingIndex;
        this.type = type;
        this.value = value;
        this.row = row;
        this.column = column;
    }

    public Token(CompilerEnum type, String value) {
        this.startingIndex = -1;
        this.endingIndex = -1;
        this.type = type;
        this.value = value;
        this.row = -1;
        this.column = -1;
    }

    public Token(Token token) {
        startingIndex = token.getStartingIndex();
        endingIndex = token.getEndingIndex();
        type = token.getType();
        value = token.getValue();
        row = token.getRow();
        column = token.getColumn();
    }

    public long getStartingIndex() {
        return startingIndex;
    }

    public long getEndingIndex() {
        return endingIndex;
    }

    public CompilerEnum getType() {
        return type;
    }

    public void setType(CompilerEnum type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getRow() {
        return row;
    }

    public long getColumn() {
        return column;
    }
}
