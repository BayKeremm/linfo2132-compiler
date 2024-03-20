package compiler.Parser;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.swing.plaf.nimbus.State;

import org.junit.Test;

import compiler.Parser.*;
import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

public class TestParser {
    public static List<String> readRepsFromFile(String filename) {
        List<String> tokens = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                tokens.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    public static IdentifierExpression id(String s){
        return new IdentifierExpression(0,new Symbol(Token.IDENTIFIER, s,0));
    }

    public static LiteralExpression num(int n){
        return new LiteralExpression(0,new Symbol(Token.IDENTIFIER, String.valueOf(n),0));
    }
    @Test
    public void testConstantVariable() throws Exception{
        String filename = "test_constvariables.txt";
        LineNumberReader reader = new LineNumberReader(new FileReader(filename));
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program p = parser.program();  

        Program Test = new Program("test1", new ArrayList<ConstantVariable>(), new ArrayList<Statement>(), new ArrayList<StructDeclaration>(), new ArrayList<Procedure>());
        TypeDeclaration testTypeDeclaration1 = new TypeDeclaration(0, new Symbol(Token.INTEGER,"int",0),false);
        Expression testIdentifier1 = new IdentifierExpression(0, new Symbol(Token.IDENTIFIER, "a", 0));
        Expression testDeclarator1 = new LiteralExpression(0, new Symbol(Token.NATURAL_LITERAL,"3",0)) ;
        Test.addConstantVariable(new ConstantVariable(0, testTypeDeclaration1, testIdentifier1,testDeclarator1));

        TypeDeclaration testTypeDeclaration2 = new TypeDeclaration(0, new Symbol(Token.STRING,"string",0),false);
        IdentifierExpression testIdentifier2 = new IdentifierExpression(0, new Symbol(Token.IDENTIFIER, "k", 0));
        LiteralExpression testDeclarator2 = new LiteralExpression(0, new Symbol(Token.STRING_LITERAL,'"' + "test2" + '"',0)) ;
        Test.addConstantVariable(new ConstantVariable(1,testTypeDeclaration2,testIdentifier2,testDeclarator2));

        TypeDeclaration testTypeDeclaration3 = new TypeDeclaration(0, new Symbol(Token.FLOAT,"float",0),false);
        IdentifierExpression testIdentifier3 = new IdentifierExpression(0, new Symbol(Token.IDENTIFIER, "i", 0));
        Expression lhs3 = new LiteralExpression(2, new Symbol(Token.FLOAT_LITERAL,"3.4",0));
        Expression rhs3 = new MultiplyOperation(2,num(2),num(4));
        MultiplyOperation testDeclarator3 = new MultiplyOperation(2,lhs3,rhs3);
        Test.addConstantVariable(new ConstantVariable(2,testTypeDeclaration3,testIdentifier3,testDeclarator3));

        assertEquals(Test, p);
    }

    @Test
    public void testStructDeclaration() throws Exception{
        String filename = "test_structdeclaration.txt";
        LineNumberReader reader = new LineNumberReader(new FileReader(filename));
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program p = parser.program();  

        Program Test = new Program("test2", new ArrayList<ConstantVariable>(), new ArrayList<Statement>(), new ArrayList<StructDeclaration>(), new ArrayList<Procedure>());
        
        ArrayList<Statement> statements = new ArrayList<Statement>();
        IdentifierExpression testIdentifier1 = new IdentifierExpression(0, new Symbol(Token.IDENTIFIER, "a", 0));
        IdentifierExpression testIdentifier2 = new IdentifierExpression(0, new Symbol(Token.IDENTIFIER, "b", 0));
        IdentifierExpression testIdentifierPoint = new IdentifierExpression(0, new Symbol(Token.IDENTIFIER,"Point", 0));
        statements.add(new UninitVariable(0, new TypeDeclaration(0, new Symbol(Token.INTEGER,"int",0), false),testIdentifier1));
        statements.add(new UninitVariable(0, new TypeDeclaration(0, new Symbol(Token.INTEGER,"int",0), false),testIdentifier2));
        Block block = new Block(0, statements);
        StructDeclaration struct1 = new StructDeclaration(0, testIdentifierPoint, block);

        Test.addStructDeclaration(struct1);
        
        assertEquals(Test, p);
    }

    @Test
    public void testProcedure() throws Exception{
        String filename = "test_procedure.txt";
        LineNumberReader reader = new LineNumberReader(new FileReader(filename));
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program p = parser.program();  

        Program Test = new Program("test3", new ArrayList<ConstantVariable>(), new ArrayList<Statement>(), new ArrayList<StructDeclaration>(), new ArrayList<Procedure>());

        ArrayList<Expression> parameters = new ArrayList<Expression>();
        ArrayList<Statement> statements = new ArrayList<Statement>();
        ArrayList<Statement> wstatements = new ArrayList<Statement>();

        TypeDeclaration testTypes = new TypeDeclaration(0, new Symbol(Token.INTEGER,"int",0), false);
        TypeDeclaration retType = new TypeDeclaration(0, new Symbol(Token.FLOAT,"float",0), false);
        

        parameters.add(new Parameter(0, testTypes, id("x")));
        parameters.add(new Parameter(0, testTypes, id("y")));
        parameters.add(new Parameter(0, testTypes, id("z")));

        statements.add(new UninitVariable(0, testTypes,id("i")));        
        wstatements.add(new ScopeVariable(0, id("i"), new PlusOperation(0, id("i"), num(1))));

        LTComparison condition = new LTComparison(0, id("i"), num(3));
        Block wblock = new Block(0, wstatements);
        WhileStatement whiles = new WhileStatement(0, condition, wblock);
        statements.add(whiles);

        Block block = new Block(0, statements);

        ProcedureDeclarator testDeclarator = new ProcedureDeclarator(0, parameters, block);
        Procedure testProcedure = new Procedure(0, testDeclarator, retType, new Symbol(Token.IDENTIFIER,"func", 0));

        Test.addProcedure(testProcedure);

        assertEquals(Test, p);
    }
}