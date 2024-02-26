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
    public static List<String> readSymbolRepsFromFile(String filename) {
        List<String> tokens = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokensInLine = line.split("\\n+"); // Split by newline
                tokens.addAll(Arrays.asList(tokensInLine));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }
    @Test
    public void testStringLiterals() throws FileNotFoundException {
        String fileName = "./test_files/test_string_literals.lang";
        String expectedLexing = "./test_files/test_string_literals.txt";
        LineNumberReader reader = new LineNumberReader(new FileReader(fileName));

        Lexer lexer = new Lexer(reader);
        lexer.setFileName(fileName);

        List<String> expectedImages = readSymbolRepsFromFile(expectedLexing);
        List<String> actualImages = new ArrayList<>();
        Symbol s = lexer.getNextSymbol();
        while(!(s.image().isEmpty())){
            actualImages.add(s.symbolRep());
            //System.out.println(s.symbolRep());
            s = lexer.getNextSymbol();
        }
        try{
            lexer.finish();
        }catch (Exception e){
            System.err.println("Error finishing the lexer");
        }
        assertEquals(expectedImages, actualImages);
    }
    @Test
    public void testKeywords() throws FileNotFoundException {
        String fileName = "./test_files/test_keywords.lang";
        String expectedLexing = "./test_files/test_keywords.lang";
        LineNumberReader reader = new LineNumberReader(new FileReader(fileName));

        Lexer lexer = new Lexer(reader);
        lexer.setFileName(fileName);

        List<String> expectedImages = readSymbolRepsFromFile(expectedLexing);
        List<String> actualImages = new ArrayList<>();
        Symbol s = lexer.getNextSymbol();
        while(!(s.image().isEmpty())){
            actualImages.add(s.symbolRep());
            //System.out.println(s.symbolRep());
            s = lexer.getNextSymbol();
        }
        try{
            lexer.finish();
        }catch (Exception e){
            System.err.println("Error finishing the lexer");
        }
        assertEquals(expectedImages, actualImages);
    }
    @Test
    public void testOperators() throws FileNotFoundException {
        String fileName ="./test_files/test_operators.lang" ;
        LineNumberReader reader = new LineNumberReader(new FileReader(fileName));
        Lexer lexer = new Lexer(reader);
        lexer.setFileName(fileName);
        List<String> expectedImages = readSymbolRepsFromFile(fileName);
        List<String> actualImages = new ArrayList<>();
        Symbol s = lexer.getNextSymbol();
        while(!(s.image().isEmpty())){
            actualImages.add(s.symbolRep());
            //System.out.println(s.symbolRep());
            s = lexer.getNextSymbol();
        }
        try{
            lexer.finish();
        }catch (Exception e){
           System.err.println("Error finishing the lexer");
        }
        assertEquals(expectedImages, actualImages);
    }

}
