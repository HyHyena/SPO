import Lexer.Lexer;
import Lexer.Token;
import Optimizer.Optimisation;
import Parser.Parser;
import Executor.Executor;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String input =
                "Thread t1;\n" +
                "Thread t2;\n" +
                "function hello(int arg){\n" +
                     "while(arg < 500){\n"+
                        "print 'HHHHEEEELLLLOOOO';\n" +
                        "arg = arg + 1;\n"+
                     "}\n"+
                    "return;\n" +
                "}\n"  +
                "function world(int arg){\n" +
                      "while(arg < 1000){\n"+
                            "print 'world';\n" +
                            "arg = arg + 1;\n"+
                       "}\n"+
                   "return;\n" +
                 "}\n"  +
                "int a = 2 + 1;\n" +
                "int b = a + 50;\n" +
                "t1.run(hello(a));\n" +
                "t2.run(world(b));\n" +
                "t1.join();\n" +
                "t2.join();\n" +
//                "hello(a);\n"+
//                "world(b);\n"+
                "print '!!!!!!!!!!!!!!!!!!!!!!!END OF EXECUTION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!';";
        System.out.println("\nCode: \n" + input);

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.getTokens();

        Parser parser = new Parser(tokens);

        if (parser.lang()) {
            //System.out.println(parser.reversePolishNotation);
            //System.out.println(parser.tableOfVariables);
            Optimisation optimisation = new Optimisation(parser.tableOfVariables, parser.reversePolishNotation);
            optimisation.execute();
            //System.out.println(parser.reversePolishNotation);

            Executor executor = new Executor();
            System.out.println("Results:");
            executor.start(parser);
        }
    }
}
