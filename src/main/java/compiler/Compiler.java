/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package compiler;

import compiler.Lexer.Lexer;
import compiler.Parser.Parser;
import compiler.Parser.Program;
import compiler.Parser.TestParser;
import compiler.Parser.TypeChecker;

import java.io.FileReader;
import java.io.LineNumberReader;


public class Compiler {
    public static void main(String[] args) throws Exception {

        LineNumberReader reader;

        boolean debug = false;
        boolean test = false;
        String fileName;

        if(args.length > 1){
            if(args[0].equals("-parser")){
                debug = true;
            }
            else if(args[0].equals("-test")){
                test = true;

            }
            fileName = args[1];
        }else{
            fileName = args[0];

        }

        if(test){
            TestParser testParser = new TestParser();
            testParser.testConstantVariable();
            testParser.testProcedure();
            testParser.testStructGlobals();
        }

        else{
            reader = new LineNumberReader(new FileReader(fileName));

            Lexer lexer = new Lexer(reader);
            lexer.setFileName(fileName);

            Parser parser = new Parser(lexer);
            Program p = parser.program();
            p.printNode();
            TypeChecker semantics = new TypeChecker(p);
            semantics.typeCheck();
        }



        //lexer.advanceLexer();
        //Symbol s = lexer.nextSymbol();
        //while(!(s.image().isEmpty())){
        //    if(debug){
        //        System.out.println(s.symbolRep());
        //    }
        //    lexer.advanceLexer();
        //    s = lexer.nextSymbol();
        //}
        //try{
        //    lexer.finish();
        //}catch (Exception e){
        //    System.err.println("Error finishing the lexer");
        //}
    }
}
