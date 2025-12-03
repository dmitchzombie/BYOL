package setta;
public enum SettaTokenType {
    //single character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE,
    RIGHT_BRACE, COMMA, PIPE, MINUS, PLUS,
    SLASH, STAR , PERCENT , SEMICOLON , EQUAL , BANG,

   SUBSETEQ , IN , UNION, INTERSECT, GREATER , GREATER_EQUAL 
   , LESS , LESS_EQUAL, BANG_EQUAL, EQUAL_EQUAL, TIMES,

    //Literals
    IDENTIFIER, STRING, NUMBER,

    //Keywords
    LET , DEF , PRINT , TRUE , FALSE ,

    //Special 
    EOF
}