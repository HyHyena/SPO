import compiler.PolizCounter;
import compiler.PolizGenerator;
import enumerations.ServiceEnum;
import exceptions.CompileException;
import exceptions.ParseException;
import exceptions.TokenizeException;
import lexer.*;
import parser.Parser;

public class Main {

    public static void main(String[] args) throws CompileException {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        PolizGenerator polizGenerator = new PolizGenerator();
        PolizCounter polizCounter = new PolizCounter();
        try {
            lexer.tokenize("src/test.txt");
        } catch (TokenizeException e) {
            e.printStackTrace();
        }
        for (Token token : lexer.getTokens()) {
            System.out.println(token.getType() + " " + token.getValue());
        }
        System.out.println();
        try {
            parser.parse(lexer.getTokens());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("\nTree:");
        parser.getTree().printTree();


        polizGenerator.generate(parser.getTree());
        System.out.println("\nPoliz:");
        int i = 0;
        for (Token token : polizGenerator.getPoliz()) {
            System.out.println(i++ + ": " + token.getType() + " " + token.getValue());
        }
        System.out.println();
        for (Variable variable : polizGenerator.getTableOfNames().values()) {
            if (variable.getValueType().equals(ServiceEnum.LABEL)) {
                System.out.println("Label: " + variable.getName() + " : " + variable.getValue());
            }
        }
    try {
        polizCounter.count(polizGenerator.getPoliz(), polizGenerator.getTableOfNames());
    }
    catch (CompileException e){
        e.getMessage();
    }
    }
}
