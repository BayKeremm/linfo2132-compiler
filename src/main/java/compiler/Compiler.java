/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package compiler;

import compiler.Lexer.Lexer;
import compiler.Parser.*;
import compiler.bytecodegen.*;
import compiler.semantics.*;

import java.io.FileReader;
import java.io.LineNumberReader;

import static java.lang.System.exit;
import static java.lang.System.out;


public class Compiler {
    public static void main(String[] args) throws Exception {

        LineNumberReader reader;

        boolean debug = false;
        boolean test = false;
        String fileName = null;
        String outputName = null;

        if(args.length == 3){
            fileName = args[0];
            outputName = args[2];
        }else if(args.length == 1){
            fileName = args[0];
            outputName = fileName.replace(".lang","");
            int lastIndex = fileName.lastIndexOf("/");
            if(lastIndex != -1){
                outputName = outputName.substring(lastIndex + 1);
            }

        }else{
            System.err.println("Usage: gradle run --args=\"" +
                    "<file_to_compile> Optional<-o <output_file_name>>\"");
            exit(1);
        }

        reader = new LineNumberReader(new FileReader(fileName));

        Lexer lexer = new Lexer(reader);
        lexer.setFileName(fileName);

        Parser parser = new Parser(lexer);
        Program p = parser.program();
        p.printNode();

        SemanticAnalysis semantics = new SemanticAnalysis(p);
        semantics.typeCheck();
        semantics.debug();
        System.out.println("-----------------------CODEGEN----------------------------");

        ByteCodeWizard wiz = new ByteCodeWizard(p, outputName);
        wiz.codeGen();

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
