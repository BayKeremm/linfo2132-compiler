
import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.LineNumberReader;    
import org.junit.Test;

import compiler.Lexer.Lexer;
import compiler.Parser.Parser;
import compiler.Parser.Program;
import compiler.Parser.SemanticAnalysis;

public class TestSemanticAnalysis{

    public SemanticAnalysis semantics(String filename, int line) throws Exception{
        LineNumberReader reader = new LineNumberReader(new FileReader(filename));
        for(int i = 0; i<line;i++) reader.readLine();
        Lexer lexer = new Lexer(reader);
        lexer.setFileName(filename);
        Parser parser = new Parser(lexer);
        Program p = parser.program();
        SemanticAnalysis semantics = new SemanticAnalysis(p);
        return semantics;
    }

    @Test
    public void TestTypeStructError() throws Exception{
        String filename = "test/test_files/test_typeStructError.txt";
        int count = 0;

        // test1 : string a = 3;
        SemanticAnalysis semantics1 = semantics(filename, 0);
        
        try{
            semantics1.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_typeStructError.txt:1: -> Semantic Analysis error: TypeError: Constant Variable type does not match the declaration type: string != int", e.getMessage());
        }

        // test2 : struct int;
        SemanticAnalysis semantics2 = semantics(filename, 1);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_typeStructError.txt:2: -> Semantic Analysis error: TypeError: Constant Variable type does not match the declaration type: int != string", e.getMessage());
        }

        // test3 : struct if;
        SemanticAnalysis semantics3 = semantics(filename, 2);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_typeStructError.txt:3: -> Semantic Analysis error: StructError: Structure declaration overwirte existing types : if", e.getMessage());
        }

        // test4 : 2x Point;
        SemanticAnalysis semantics4 = semantics(filename, 6);

        try{
            semantics4.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_typeStructError.txt:11: -> Semantic Analysis error: StructError: Structure declaration overwirte a previously defined structure : Point", e.getMessage());
        }

        assertEquals(4, count);
    }

    @Test
    public void TestOperatorError() throws Exception{
        String filename = "test/test_files/test_OperatorError.txt";
        int count = 0;

        // test1 : "a" == 2
        SemanticAnalysis semantics1 = semantics(filename, 0);
        
        try{
            semantics1.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_OperatorError.txt:1: -> Semantic Analysis error: OperatorError: Equality Check Expression Type Error: string is not compatible with int", e.getMessage());
        }

        // test2 : 3 + "a"
        SemanticAnalysis semantics2 = semantics(filename, 1);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_OperatorError.txt:2: -> Semantic Analysis error: OperatorError: Term Expression Type Error: int is not compatible with string", e.getMessage());
        }

        // test3 : string c = -"a"
        SemanticAnalysis semantics3 = semantics(filename, 2);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_OperatorError.txt:3: -> Semantic Analysis error: OperatorError: Unary Minus Type Error: Cannot make minus type string", e.getMessage());
        }

        // test4 : int d = !3
        SemanticAnalysis semantics4 = semantics(filename, 3);

        try{
            semantics4.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_OperatorError.txt:4: -> Semantic Analysis error: OperatorError: Unary Negate Type Error: Cannot negate type int", e.getMessage());
        }

        assertEquals(4, count);
    }

    @Test
    public void TestArgumentConditionError() throws Exception{
        String filename = "test/test_files/test_ArgumentError.txt";
        int count = 0;

        // test1 : struct Point{int x; int y;}; Point(3);
        SemanticAnalysis semantics1 = semantics(filename, 0);

        try{
            semantics1.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_ArgumentError.txt:6: -> Semantic Analysis error: ArgumentError: Wrong number of arguments in Struct init", e.getMessage());
        }


        // test2 : def int foo(string a); foo(3);
        SemanticAnalysis semantics2 = semantics(filename, 7);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_ArgumentError.txt:12: -> Semantic Analysis error: ArgumentError: Argument type mismatch in Function call: int!=string", e.getMessage());
        }

        // test3 : def int foo(string a); foo(1,2,3);
        SemanticAnalysis semantics3 = semantics(filename, 13);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_ArgumentError.txt:18: -> Semantic Analysis error: ArgumentError: Wrong number of arguments in Function call", e.getMessage());
        }

        // test4 : def int foo(string a); foo();
        SemanticAnalysis semantics4 = semantics(filename, 19);
        
        try{
            semantics4.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_ArgumentError.txt:24: -> Semantic Analysis error: ArgumentError: Wrong number of arguments in Function call", e.getMessage());
        }

        assertEquals(4, count);
    }

    @Test
    public void TestMissingConditionError() throws Exception{
        String filename = "test/test_files/test_MissingConditionError.txt";
        int count = 0;

        // test1 : if(){}
        SemanticAnalysis semantics1 = semantics(filename, 0);
        
        try{
            semantics1.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_MissingConditionError.txt:2: -> Semantic Analysis error: MissingConditionError : Condition in IfElse statement is empty", e.getMessage());
        }

        // test2 : while(){}
        SemanticAnalysis semantics2 = semantics(filename, 5);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_MissingConditionError.txt:7: -> Semantic Analysis error: MissingConditionError : Condition in While statement is empty", e.getMessage());
        }

        // test3 : for(int i = 0;;i++){}
        SemanticAnalysis semantics3 = semantics(filename, 10);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_MissingConditionError.txt:12: -> Semantic Analysis error: MissingConditionError : Condition in For statement is empty", e.getMessage());
        }

        assertEquals(3, count);
    }

    @Test
    public void TestReturnError() throws Exception{
        String filename = "test/test_files/test_ReturnError.txt";
        int count = 0;

        // test1 : def int foo(){return "a";}
        SemanticAnalysis semantics1 = semantics(filename, 0);
        
        try{
            semantics1.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_ReturnError.txt:2: -> Semantic Analysis error: ReturnError : Function return type mismatch: foo expects int not string", e.getMessage());
        }

        // test2 : def int foo(){}
        SemanticAnalysis semantics2 = semantics(filename, 3);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_ReturnError.txt:5: -> Semantic Analysis error: ReturnError : Function return type mismatch: foo expects int not WEIRD THING HAPPENED IN LIT EXP", e.getMessage());
        }

        // test3 : def void foo(){return 3;}
        SemanticAnalysis semantics3 = semantics(filename, 6);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_ReturnError.txt:8: -> Semantic Analysis error: ReturnError : Function return type mismatch: foo expects void not int", e.getMessage());
        }

        assertEquals(3, count);
    }

    @Test
    public void TestScopeError() throws Exception{
        String filename = "test/test_files/test_ScopeError.txt";
        int count = 0;

        // test1 : final int a = 3; final int a = 2;
        SemanticAnalysis semantics1 = semantics(filename, 0);

        try{
            semantics1.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_ScopeError.txt:2: -> Semantic Analysis error: ScopeError: The variable a already exists in context", e.getMessage());
        }

        // test2 : final int a = 3; int a = 2;
        SemanticAnalysis semantics2 = semantics(filename, 1);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_ScopeError.txt:3: -> Semantic Analysis error: ScopeError: Variable already exists in context : a", e.getMessage());
        }

        // test3 : int a = 3 + b;
        SemanticAnalysis semantics3 = semantics(filename, 3);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_ScopeError.txt:4: -> Semantic Analysis error: ScopeError : Variable does not exist in context : b", e.getMessage());
        }

        // test4 : int a = foo(1);
        SemanticAnalysis semantics4 = semantics(filename, 4);

        try{
            semantics4.typeCheck();
        } catch(Error e){
            count += 1;
            assertEquals("test/test_files/test_ScopeError.txt:5: -> Semantic Analysis error: ScopeError : Function does not exist in context : foo", e.getMessage());
        }

        assertEquals(4, count);
    }
}
