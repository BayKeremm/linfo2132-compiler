import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import compiler.Lexer.Symbol;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import compiler.Lexer.Lexer;

public class TestLexer {
    public static List<String> readImagesFromFile(String filename) {
        List<String> tokens = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokensInLine = line.split("\\s+"); // Split by whitespace
                tokens.addAll(Arrays.asList(tokensInLine));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }
    @Test
    public void testOperators() throws FileNotFoundException {
        Lexer lexer = new Lexer("./test_files/test_operators.lang");
        List<String> expectedImages = readImagesFromFile("./test_files/test_operators.txt");
        List<String> actualImages = new ArrayList<>();
        Symbol s = lexer.getNextSymbol();
        while(s != null){
            actualImages.add(s.image());
            s = lexer.getNextSymbol();
        }
        assertEquals(expectedImages, actualImages);
    }

}
