package setta;

public class SettaParser {
    



    /* 
    example on how to test lox parser, code up to ch10
        public static void main(String[] args) {
        // Create some test tokens for: "print 123 + 456;"
        List<Token> tokens = Arrays.asList(
            new Token(TokenType.PRINT, "print", null, 1),
            new Token(TokenType.NUMBER, "123", 123.0, 1),
            new Token(TokenType.PLUS, "+", null, 1),
            new Token(TokenType.NUMBER, "456", 456.0, 1),
            new Token(TokenType.COMMA, ";", null, 1),
            new Token(TokenType.EOF, "", null, 1)
        );

        // Create parser with test tokens
        Parser parser = new Parser(tokens);

        // Parse and print the result
        try {
            List<Stmt> statements = parser.parse();
            for (Stmt stmt : statements) {
                System.out.println(stmt.toString());
            }
        } catch (ParseError error) {
            System.err.println("Parse error occurred!");
        }
    }
    */

}
