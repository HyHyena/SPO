package lexer;

import enumerations.LexemeEnum;
import exceptions.TokenizeException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Lexer {
    private ArrayList<Token> tokens;
    private HashMap<LexemeEnum, String> regExp;

    private void init() {
        tokens = new ArrayList<>();
        regExp = LexemeEnum.initRegExp();
    }

    public void tokenize(String path) throws TokenizeException {
        init();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            long stIndex = 1;
            long row = 0;
            StringBuilder lexeme = new StringBuilder();
            tokens.add(null);
            for (String buf = br.readLine(); buf != null; buf = br.readLine()) {
                row++;
                long column = 0;
                long currentRow = row;
                long currentColumn = column;
                for (int i = 0; i < buf.length(); i++) {
                    lexeme.append(buf.charAt(i));
                    Token token = tokenSeparation(lexeme.toString(), stIndex, currentRow, currentColumn);
                    if (token != null) {
                        tokens.set(tokens.size() - 1, token);
                    } else {
                        if (tokens.get(tokens.size() - 1) == null)
                            throw new TokenizeException("Incorrect lexeme in line: " + row + " column: " + column);
                        lexeme.delete(0, lexeme.length() - 1);
                        deleteToken();
                        currentRow = row;
                        currentColumn = column + 1;
                        tokens.add(tokenSeparation(lexeme.toString(), stIndex, currentRow, currentColumn));
                    }
                    column++;
                    stIndex++;
                }
                lexeme.delete(0, lexeme.length() - 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteToken() {
        if (tokens.get(tokens.size() - 1) != null) {
            if (tokens.get(tokens.size() - 1).getType().equals(LexemeEnum.SPACE_SP)
                    || tokens.get(tokens.size() - 1).getType().equals(LexemeEnum.TAB_SP)) {
                tokens.remove(tokens.size() - 1);
            }
        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    private Token tokenSeparation(String lexeme, long stIndex, long row, long column) {
        for (LexemeEnum type : LexemeEnum.values()) {
            Pattern pattern = Pattern.compile(regExp.get(type));
            if (pattern.matcher(lexeme).matches()) {
                return new Token(stIndex, stIndex + lexeme.length(), type, lexeme, row, column);
            }
        }
        return null;
    }
}
