import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import compiler.Lexer.Symbol;
import compiler.Parser.*;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
    public void testSpecialSymbols() throws Exception {
        String fileName = "./test/test_files/test_special_symbols.lang";
        String expectedLexing = "./test/test_files/test_special_symbols.txt";
        LineNumberReader reader = new LineNumberReader(new FileReader(fileName));

        Lexer lexer = new Lexer(reader);
        lexer.setFileName(fileName);

        List<String> expectedImages = readSymbolRepsFromFile(expectedLexing);
        List<String> actualImages = new ArrayList<>();
        lexer.advanceLexer();
        Symbol s = lexer.nextSymbol();
        while(!(s.image().isEmpty())){
            actualImages.add(s.symbolRep());
            //System.out.println(s.symbolRep());
            //System.out.println(s.symbolRep());
            lexer.advanceLexer();
            s = lexer.nextSymbol();
        }
        try{
            lexer.finish();
        }catch (Exception e){
            System.err.println("Error finishing the lexer");
        }
        assertEquals(expectedImages, actualImages);
    }
    @Test
    public void testStringLiterals() throws Exception {
        String fileName = "./test/test_files/test_string_literals.lang";
        String expectedLexing = "./test/test_files/test_string_literals.txt";
        LineNumberReader reader = new LineNumberReader(new FileReader(fileName));

        Lexer lexer = new Lexer(reader);
        lexer.setFileName(fileName);

        List<String> expectedImages = readSymbolRepsFromFile(expectedLexing);
        List<String> actualImages = new ArrayList<>();
        lexer.advanceLexer();
        Symbol s = lexer.nextSymbol();
        while(!(s.image().isEmpty())){
            actualImages.add(s.symbolRep());
            //System.out.println(s.symbolRep());
            lexer.advanceLexer();
            s = lexer.nextSymbol();
        }
        try{
            lexer.finish();
        }catch (Exception e){
            System.err.println("Error finishing the lexer");
        }
        assertEquals(expectedImages, actualImages);
    }
    @Test
    public void testKeywords() throws Exception {
        String fileName = "./test/test_files/test_keywords.lang";
        String expectedLexing = "./test/test_files/test_keywords.txt";
        LineNumberReader reader = new LineNumberReader(new FileReader(fileName));

        Lexer lexer = new Lexer(reader);
        lexer.setFileName(fileName);

        List<String> expectedImages = readSymbolRepsFromFile(expectedLexing);
        List<String> actualImages = new ArrayList<>();
        lexer.advanceLexer();
        Symbol s = lexer.nextSymbol();
        while(!(s.image().isEmpty())){
            actualImages.add(s.symbolRep());
            //System.out.println(s.symbolRep());
            lexer.advanceLexer();
            s = lexer.nextSymbol();
        }
        try{
            lexer.finish();
        }catch (Exception e){
            System.err.println("Error finishing the lexer");
        }
        assertEquals(expectedImages, actualImages);
    }
    @Test
    public void testOperators() throws Exception {
        String fileName ="./test/test_files/test_operators.lang" ;
        String expectedLexing ="./test/test_files/test_operators.txt" ;
        LineNumberReader reader = new LineNumberReader(new FileReader(fileName));
        Lexer lexer = new Lexer(reader);
        lexer.setFileName(fileName);
        List<String> expectedImages = readSymbolRepsFromFile(expectedLexing);
        List<String> actualImages = new ArrayList<>();
        lexer.advanceLexer();
        Symbol s = lexer.nextSymbol();
        while(!(s.image().isEmpty())){
            actualImages.add(s.symbolRep());
            //System.out.println(s.symbolRep());
            lexer.advanceLexer();
            s = lexer.nextSymbol();
        }
        try{
            lexer.finish();
        }catch (Exception e){
           System.err.println("Error finishing the lexer");
        }
        assertEquals(expectedImages, actualImages);
    }
    @Test
    public void testLiterals() throws Exception {
        String fileName = "./test/test_files/test_literals.lang";
        String expectedLexing = "./test/test_files/test_literals.txt";
        LineNumberReader reader = new LineNumberReader(new FileReader(fileName));
        Lexer lexer = new Lexer(reader);
        lexer.setFileName(fileName);
        List<String> expectedImages = readSymbolRepsFromFile(expectedLexing);
        List<String> actualImages = new ArrayList<>();
        lexer.advanceLexer();
        Symbol s = lexer.nextSymbol();
        while(!(s.image().isEmpty())){
            actualImages.add(s.symbolRep());
            //System.out.println(s.symbolRep());
            lexer.advanceLexer();
            s = lexer.nextSymbol();
        }
        try{
            lexer.finish();
        }catch (Exception e){
            System.err.println("Error finishing the lexer");
        }
        assertEquals(expectedImages, actualImages);
    }
}
