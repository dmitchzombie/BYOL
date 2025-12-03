# BYOL
 Setta is a domain-specific language designed for working with sets and set operations, inspired by concepts from discrete mathematics. Users can define sets, perform common operations such as union and intersection, and check properties like membership, equality, and subset relations. Setta also supports set comprehensions and simple set-transforming procedures, allowing expressive manipulation of sets in a concise, math-like syntax. The language’s goal is to provide a clear and focused way to model problems in set theory while remaining approachable to beginners.

## How to Run!
For single line, or small programs simply run the ```Setta.java``` file, and type in your program to the REPL. 

For longer files, or to attach a file, simply create a new ```.setta``` file in the directory, insert your text file (making sure that all of the text is in the setta langauge as outlined below), and change line 17 in ```Setta.java``` to match your file name.
For example line 17 currently reads, ```runFile("setta/expo.setta"); ``` however if you want to upload your own file (i.e; test1.setta), change the file line to be ```runFile("setta/test1.setta");```. Save the file changes using ctrl + s, and then click on the run button at the top of the ```Setta.java``` file.


 
## Setta Grammar
```nginx
program        → declaration* EOF ;

declaration    → letDecl
               | statement ;

letDecl        → "let" IDENTIFIER "=" expression ";" ;

statement      → printStmt ;
               | exprStmt

exprStmt       → expression ";" ;

printStmt      → "print" expression ";" ;

expression     → assignment ;

assignment     → IDENTIFIER "=" assignment
               | equality ;

equality       → comparison ( ( "==" | "!=" ) comparison )* ;

comparison     → subset ( ( "subseteq" | ">" | "<" | ">=" | "<=" ) subset )* ;

subset         → union ( "in" union )* ;

union          → intersection ( "union" intersection )* ;
intersection   → product ( "intersect" product )* ;

product        → difference ( "X" difference )* ;

difference     → term ( "-" term )* ;

term           → factor ( ( "+" | "-" ) factor )* ;
factor         → unary ( ( "*" | "/" | "%" ) unary )* ;
unary          → ( "-" | "!" ) unary | primary ;

primary        → NUMBER
               | STRING
               | "true"
               | "false"
               | "|" expression "|"         // cardinality
               | IDENTIFIER
               | "(" expression ")"
               | setLiteralOrComprehension ;

setLiteralOrComprehension
               →  "{" "}"
               |  "{" expression ( "," expression )* "}"
               |  "{" expression "|" IDENTIFIER "in" expression ( "," expression )? "}" ;

```
