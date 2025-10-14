# BYOL
 Setta is a domain-specific language designed for working with sets and set operations, inspired by concepts from discrete mathematics. Users can define sets, perform common operations such as union and intersection, and check properties like membership, equality, and subset relations. Setta also supports set comprehensions and simple set-transforming procedures, allowing expressive manipulation of sets in a concise, math-like syntax. The language’s goal is to provide a clear and focused way to model problems in set theory while remaining approachable to beginners.
 
## Setta Grammar
```
program        → declaration* EOF ;

declaration    → funDecl
               | letDecl
               | statement ;

funDecl        → "def" IDENTIFIER "(" parameters? ")" "=" expression ";" ;
parameters     → IDENTIFIER ( "," IDENTIFIER )* ;

letDecl        → "let" IDENTIFIER "=" expression ";" ;

statement      → printStmt ;
printStmt      → "print" expression ";" ;

expression     → equality ;

equality       → comparison ( "=" comparison )* ;

comparison     → subset ( ( "subseteq" ) subset )* ;

subset         → union ( "in" union )* ;

union          → intersection ( "union" intersection )* ;
intersection   → difference ( "intersect" difference )* ;
difference     → term ( "-" term )* ;  

term           → factor ( ( "+" | "-" ) factor )* ;
factor         → unary ( ( "*" | "/" | "%" ) unary )* ;
unary          → ( "-" | "!" ) unary
               | primary ;

primary        → NUMBER
               | STRING
               | "true"
               | "false"
               | IDENTIFIER
               | "|" IDENTIFIER "|"          // cardinality
               | "{" elements? "}"
               | "(" expression ")" ;

elements       → expression ( "," expression )*
               | comprehension ;

comprehension  → expression "|" IDENTIFIER "in" expression
                 ( "," expression )? ;

```