
import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.LineNumberReader;    
import org.junit.Test;

import compiler.Lexer.Lexer;
import compiler.Parser.Parser;
import compiler.Parser.Program;
import compiler.Parser.TypeChecker;

    //TODO : no return in function
    //TODO : final x = 3;
public class TestSemanticAnalysis{

    public TypeChecker semantics(String filename, int line) throws Exception{
        LineNumberReader reader = new LineNumberReader(new FileReader(filename));
        for(int i = 0; i<line;i++) reader.readLine();
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program p = parser.program();
        TypeChecker semantics = new TypeChecker(p);
        return semantics;
    }

    @Test
    public void TestTypeStructError() throws Exception{
        String filename = "test/test_files/test_typeStructError.txt";
        
        // test1 : string a = 3;
        TypeChecker semantics1 = semantics(filename, 0);
        
        try{
            semantics1.typeCheck();
        } catch(Error e){
            assertEquals("null:1: error: TypeError: Constant Variable type does not match the declaration type: string != int", e.getMessage());
        }

        // test2 : struct int;
        TypeChecker semantics2 = semantics(filename, 1);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            assertEquals("null:2: error: TypeError: Constant Variable type does not match the declaration type: int != string", e.getMessage());
        }

        // test3 : struct if;
        TypeChecker semantics3 = semantics(filename, 2);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            assertEquals("null:3: error: StructError: Structure declaration overwirte existing types : if", e.getMessage());
        }

        // test4 : 2x Point;
        TypeChecker semantics4 = semantics(filename, 6);

        try{
            semantics4.typeCheck();
        } catch(Error e){
            assertEquals("null:11: error: StructError: Structure declaration overwirte a previously defined structure : Point", e.getMessage());
        }
    }

    @Test
    public void TestOperatorError() throws Exception{
        String filename = "test/test_files/test_OperatorError.txt";

        // test1 : "a" == 2
        TypeChecker semantics1 = semantics(filename, 0);
        
        try{
            semantics1.typeCheck();
        } catch(Error e){
            assertEquals("null:1: error: OperatorError: Equality Check Expression Type Error: string is not compatible with int", e.getMessage());
        }

        // test2 : 3 + "a"
        TypeChecker semantics2 = semantics(filename, 1);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            assertEquals("null:2: error: OperatorError: Term Expression Type Error: int is not compatible with string", e.getMessage());
        }

        // test3 : string c = -"a"
        TypeChecker semantics3 = semantics(filename, 2);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            assertEquals("null:3: error: OperatorError: Unary Minus Type Error: Cannot make minus type string", e.getMessage());
        }

        // test4 : int d = !3
        TypeChecker semantics4 = semantics(filename, 3);

        try{
            semantics4.typeCheck();
        } catch(Error e){
            assertEquals("null:4: error: OperatorError: Unary Negate Type Error: Cannot negate type int", e.getMessage());
        }
    }

    @Test
    public void TestArgumentConditionError() throws Exception{
        String filename = "test/test_files/test_ArgumentError.txt";

        // test1 : struct Point{int x; int y;}; Point(3);
        TypeChecker semantics1 = semantics(filename, 0);

        try{
            semantics1.typeCheck();
        } catch(Error e){
            assertEquals("null:6: error: ArgumentError: Wrong number of arguments in Struct init", e.getMessage());
        }


        // test2 : def int foo(string a); foo(3);
        TypeChecker semantics2 = semantics(filename, 7);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            assertEquals("null:12: error: ArgumentError: Argument type mismatch in Function call: int!=string", e.getMessage());
        }

        // test3 : def int foo(string a); foo(1,2,3);
        TypeChecker semantics3 = semantics(filename, 13);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            assertEquals("null:18: error: ArgumentError: Wrong number of arguments in Function call", e.getMessage());
        }

        // test4 : def int foo(string a); foo();
        TypeChecker semantics4 = semantics(filename, 19);
        
        try{
            semantics4.typeCheck();
        } catch(Error e){
            assertEquals("null:24: error: ArgumentError: Wrong number of arguments in Function call", e.getMessage());
        }
    }

    @Test
    public void TestMissingConditionError() throws Exception{
        String filename = "test/test_files/test_MissingConditionError.txt";

        // test1 : if(){}
        TypeChecker semantics1 = semantics(filename, 0);
        
        try{
            semantics1.typeCheck();
        } catch(Error e){
            assertEquals("null:2: error: MissingConditionError : Condition in IfElse statement is empty", e.getMessage());
        }

        // test2 : while(){}
        TypeChecker semantics2 = semantics(filename, 5);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            assertEquals("null:7: error: MissingConditionError : Condition in While statement is empty", e.getMessage());
        }

        // test3 : for(int i = 0;;i++){}
        TypeChecker semantics3 = semantics(filename, 10);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            assertEquals("null:12: error: MissingConditionError : Condition in For statement is empty", e.getMessage());
        }
    }

    @Test
    public void TestReturnError() throws Exception{
        String filename = "test/test_files/test_ReturnError.txt";

        // test1 : def int foo(){return "a";}
        TypeChecker semantics1 = semantics(filename, 0);
        
        try{
            semantics1.typeCheck();
        } catch(Error e){
            assertEquals("null:2: error: ReturnError : Function return type mismatch: foo expects int not string", e.getMessage());
        }

        // test2 : def int foo(){}
        TypeChecker semantics2 = semantics(filename, 3);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            assertEquals("null:5: error: ReturnError : Return expression is empty", e.getMessage());
        }

        // test3 : def void foo(){return 3;}
        TypeChecker semantics3 = semantics(filename, 6);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            assertEquals("null:8: error: ReturnError : Function return type mismatch: foo expects void not int", e.getMessage());
        }
    }

    @Test
    public void TestScopeError() throws Exception{
        String filename = "test/test_files/test_ScopeError.txt";

        // test1 : final int a = 3; final int a = 2;
        TypeChecker semantics1 = semantics(filename, 0);

        try{
            semantics1.typeCheck();
        } catch(Error e){
            assertEquals("null:2: error: ScopeError: The variable a already exists in context", e.getMessage());
        }

        // test2 : final int a = 3; int a = 2;
        TypeChecker semantics2 = semantics(filename, 1);

        try{
            semantics2.typeCheck();
        } catch(Error e){
            assertEquals("null:3: error: ScopeError: Variable already exists in context : a", e.getMessage());
        }

        // test3 : int a = 3 + b;
        TypeChecker semantics3 = semantics(filename, 3);

        try{
            semantics3.typeCheck();
        } catch(Error e){
            assertEquals("null:4: error: ScopeError : Variable does not exist in context : b", e.getMessage());
        }

        // test4 : int a = foo(1);
        TypeChecker semantics4 = semantics(filename, 4);

        try{
            semantics4.typeCheck();
        } catch(Error e){
            System.out.println("aaaa");
            assertEquals("null:5: error: ScopeError : Function does not exist in context : foo", e.getMessage());
        }
    }
}
