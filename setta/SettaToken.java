package setta;

public class SettaToken {
    final SettaTokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    SettaToken(SettaTokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
    if (literal != null) {
        return type + " (" + lexeme + ") -> " + literal; //points to the literal value of the token
    } else {
        return type + " (" + lexeme + ")";
    }
    }

    
}
