package setta;
public enum SettaTokenType {
    //single character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE,
    RIGHT_BRACE, COMMA, PIPE, MINUS, PLUS,
    SLASH, STAR , PERCENT , SEMICOLON , EQUAL ,

    //multi-character operators
   SUBSETEQ , IN , UNION, INTERSECT,

    //Literals
    IDENTIFIER, STRING, NUMBER,

    //Keywords
    LET , DEF , PRINT , TRUE , FALSE ,

    //Special 
    EOF
}