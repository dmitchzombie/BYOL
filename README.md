# BYOL
 Setta is a domain-specific language designed for working with sets and set operations, inspired by concepts from discrete mathematics. Users can define sets, perform common operations such as union and intersection, and check properties like membership, equality, and subset relations. Setta also supports set comprehensions and simple set-transforming procedures, allowing expressive manipulation of sets in a concise, math-like syntax. The language’s goal is to provide a clear and focused way to model problems in set theory while remaining approachable to beginners.
 
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
