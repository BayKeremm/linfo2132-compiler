import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.StringReader;
import compiler.Lexer.Lexer;

public class TestLexer {
    
    @Test
    public void test() throws FileNotFoundException {
        //String input = "var x int = 2;";
        //StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer("test.lang");
        int i= 0;
        while(i < 20){
            try{
                lexer.debugChar();
            }catch (Exception e){
                break;
            }
            i++;
        }

    }

}
