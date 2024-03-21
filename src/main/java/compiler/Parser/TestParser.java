package compiler.Parser;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat.Type;

import org.junit.Test;

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

    public static TypeDeclaration type(String s, Token t, boolean isArray){
        return new TypeDeclaration(0,new Symbol(t, s,0),isArray);
    }
    @Test
    public void testConstantVariable() throws Exception{
        String filename = "test_constvariables.txt";
        LineNumberReader reader = new LineNumberReader(new FileReader(filename));
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program p = parser.program();  

        Program Test = new Program("test1", new ArrayList<ConstantVariable>(), new ArrayList<Statement>(), new ArrayList<StructDeclaration>(), new ArrayList<Procedure>());
        TypeDeclaration testTypeDeclaration1 = type("int",Token.INTEGER,false);
        Expression testIdentifier1 = id("a");
        Expression testDeclarator1 = num(3) ;
        Test.addConstantVariable(new ConstantVariable(0, testTypeDeclaration1, testIdentifier1,testDeclarator1));

        TypeDeclaration testTypeDeclaration2 = type("string",Token.STRING,false);
        IdentifierExpression testIdentifier2 = id("k");
        LiteralExpression testDeclarator2 = new LiteralExpression(0, new Symbol(Token.STRING_LITERAL,'"' + "test2" + '"',0)) ;
        Test.addConstantVariable(new ConstantVariable(1,testTypeDeclaration2,testIdentifier2,testDeclarator2));

        TypeDeclaration testTypeDeclaration3 = type("float",Token.FLOAT,false);
        IdentifierExpression testIdentifier3 = id("i");
        Expression lhs3 = new LiteralExpression(2, new Symbol(Token.FLOAT_LITERAL,"3.4",0));
        Expression rhs3 = new MultiplyOperation(2,num(2),num(4));
        MultiplyOperation testDeclarator3 = new MultiplyOperation(2,lhs3,rhs3);
        Test.addConstantVariable(new ConstantVariable(2,testTypeDeclaration3,testIdentifier3,testDeclarator3));

        TypeDeclaration testTypeDeclaration4 = type("bool",Token.BOOLEAN,true);
        IdentifierExpression testIdentifier4 = id("b");
        Expression testDeclarator4 = new ArrayInitializer(0, testTypeDeclaration4, num(3));
        Test.addConstantVariable(new ConstantVariable(0, testTypeDeclaration4, testIdentifier4, testDeclarator4));

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

        TypeDeclaration testTypes = type("int",Token.INTEGER,false);
        TypeDeclaration retType = type("float",Token.FLOAT,false);
        

        parameters.add(new Parameter(0, testTypes, id("x")));
        parameters.add(new Parameter(0, testTypes, id("y")));
        parameters.add(new Parameter(0, testTypes, id("z")));

        statements.add(new UninitVariable(0, testTypes,id("i")));        
        wstatements.add(new ScopeVariable(0, id("i"), new PlusOperation(0, id("i"), num(1))));

        EqualComparison condition = new EqualComparison(0, id("i"), num(3));
        Block wblock = new Block(0, wstatements);
        WhileStatement whiles = new WhileStatement(0, condition, wblock);
        statements.add(whiles);

        Block block = new Block(0, statements);

        ProcedureDeclarator testDeclarator = new ProcedureDeclarator(0, parameters, block);
        Procedure testProcedure = new Procedure(0, testDeclarator, retType, new Symbol(Token.IDENTIFIER,"func", 0));

        Test.addProcedure(testProcedure);

        ScopeVariable pos0 = new ScopeVariable(1, id("i"), num(0));
        LEComparison pos1 = new LEComparison(1, id("i"), num(3));
        ScopeVariable pos2 = new ScopeVariable(1, id("i"), new PlusOperation(1, id("i"), num(1)));
        ArrayList<Statement> pos3 = new ArrayList<Statement>();
        Block forBlock = new Block(1, pos3);
        pos3.add(new ScopeVariable(0, id("i"), new PlusOperation(1, id("i"), num(1))));

        ForStatement forStatement = new ForStatement(1, pos0, pos1, pos2, forBlock);
        statements.add(forStatement);

        NotEqualComparison condition3 = new NotEqualComparison(0, id("i"), num(5));   
        ArrayList<Statement> ifStatements = new ArrayList<Statement>();     
        ArrayList<Statement> elseStatements = new ArrayList<Statement>();
        Block ifBlock = new Block(0, ifStatements);
        Block elseBlock = new Block(0, elseStatements);
        IfElseStatement ifElse = new IfElseStatement(0, condition3, ifBlock, elseBlock);

        ifStatements.add(new Variable(0, type("int",Token.INTEGER,false), id("x"), new PlusOperation(0, id("i"), num(1))));
        elseStatements.add(new Variable(0, type("int",Token.INTEGER,false), id("y"), new PlusOperation(0, id("i"), num(2))));
        statements.add(ifElse);

        PlusOperation ret = new PlusOperation(0, id("x"), new PlusOperation(0, id("y"), id("z")));
        ReturnStatement retStatement = new ReturnStatement(0, ret);
        statements.add(retStatement);

        assertEquals(Test, p);
    }

    @Test
    public void testStructGlobals() throws Exception{
        String filename = "test_struct_globals.txt";
        LineNumberReader reader = new LineNumberReader(new FileReader(filename));
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program p = parser.program();  

        Program Test = new Program("test4", new ArrayList<ConstantVariable>(), new ArrayList<Statement>(), new ArrayList<StructDeclaration>(), new ArrayList<Procedure>());

        ArrayList<Statement> statements = new ArrayList<Statement>();
        Block block = new Block(0, statements);
        IdentifierExpression testIdentifier1 = id("a");
        ArrayInitializer ArrC = new ArrayInitializer(0, type("int", Token.INTEGER,true), num(3));
        IdentifierExpression testIdentifierPoint = id("Point");
        statements.add(new UninitVariable(0, type("int",Token.INTEGER,false),testIdentifier1));
        statements.add(new Variable(0, type("int",Token.INTEGER,true), id("c"), ArrC));
        StructDeclaration struct1 = new StructDeclaration(0, testIdentifierPoint, block);

        Test.addStructDeclaration(struct1);

        TypeDeclaration varType = type("Point",Token.IDENTIFIER,false);
        IdentifierExpression varId = id("p");
        ArrayList<Expression> expression = new ArrayList<Expression>();
        FunctionCallExpression call = new FunctionCallExpression(1, new Symbol(Token.IDENTIFIER,"Point" ,0), expression);
        Variable var = new Variable(1, varType, varId, call);
        
        expression.add(num(3));

        Test.addGlobal(var);        

        ArrayInitializer arr = new ArrayInitializer(2, type("int",Token.INTEGER,true), num(5));
        Variable var2 = new Variable(2, type("int",Token.INTEGER,true), id("arr"), arr);

        Test.addGlobal(var2);

        TypeDeclaration varType3 = type("int",Token.INTEGER,false);
        IdentifierExpression varId3 = id("b");
        DotOperation dot = new DotOperation(3, id("p"), id("a"));
        Variable var3 = new Variable(3, varType3, varId3, dot);
        
        Test.addGlobal(var3);

        TypeDeclaration varType4 = type("int",Token.INTEGER,false);
        IdentifierExpression varId4 = id("c");
        IndexOp arr2 = new IndexOp(4, new Symbol(Token.IDENTIFIER, "c", 0), num(0));
        DotOperation dot2 = new DotOperation(4, id("p"), arr2);
        Variable var4 = new Variable(4, varType4, varId4, dot2);

        Test.addGlobal(var4);

        assertEquals(Test, p);
    }
}