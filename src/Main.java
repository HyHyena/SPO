import compiler.*;
import enumerations.ServiceEnum;
import exceptions.*;
import lexer.*;
import parser.Parser;

public class Main {

    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        PolizGenerator polizGenerator = new PolizGenerator();
        PolizCounter polizCounter = new PolizCounter();
        try {
            lexer.tokenize("src/prog.txt");
        } catch (TokenizeException e) {
            e.printStackTrace();
        }
        for (Token token : lexer.getTokens()) {
            System.out.println("type: " + token.getType());
            System.out.println("value: " + token.getValue());
            System.out.println();
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
        /*for (Variable variable : polizGenerator.getTableOfNames().values()) {
            if (variable.getValueType().equals(ServiceEnum.LABEL)) {
                System.out.println("Label: " + variable.getName() + " : " + variable.getValue());
            }
        }*/
    try {
        polizCounter.count(polizGenerator.getPoliz(), polizGenerator.getTableOfNames());
    } catch (CompileException e){
        e.getMessage();
        }
    }
}
