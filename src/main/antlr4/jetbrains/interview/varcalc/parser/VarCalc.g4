grammar VarCalc;

script : statement* EOF ;

statement : KEYWORD_VAR ID '=' expr # varDecl
          | KEYWORD_OUT expr # printExpr
          | KEYWORD_PRINT QUOTED_STRING # printString
          ;

expr : ID # id
     | NUMBER # number
     | INTEGER # integer
     | base=expr op=POWER exp=expr # pow
     | left=expr op=(MULTIPLICATION | DIVISION) right=expr # mul
     | left=expr op=(PLUS | MINUS) right=expr # sum
     | MINUS expr # negation
     | LEFT_PARENTESIS expr RIGHT_PARENTESIS # group
     | LEFT_BRACE begin=expr COMMA end=expr RIGHT_BRACE # sequence
     | (ID)+ '->' expr # lambda
     | functionName LEFT_PARENTESIS expr (COMMA expr)* RIGHT_PARENTESIS # functionCall
     ;

//negation : '-' expr ;
//group : LEFT_PARENTESIS expr RIGHT_PARENTESIS ;
//sequence : LEFT_BRACE expr ',' expr RIGHT_BRACE ;
//lambda : (ID)+ '->' expr ;
//function_call : function_name LEFT_PARENTESIS expr (',' expr)* RIGHT_PARENTESIS ;
functionName : FUNCTION_MAP | FUNCTION_REDUCE ;

KEYWORD_VAR : 'var' ;
KEYWORD_OUT : 'out' ;
KEYWORD_PRINT : 'print' ;

FUNCTION_MAP : 'map' ;
FUNCTION_REDUCE : 'reduce' ;

LEFT_PARENTESIS : '(' ;
RIGHT_PARENTESIS : ')' ;
LEFT_BRACE : '{' ;
RIGHT_BRACE : '}' ;
COMMA : ',' ;
PLUS : '+' ;
MINUS : '-' ;
MULTIPLICATION : '*' ;
DIVISION : '/' ;
POWER : '^' ;

INTEGER : [0-9]+ ;
NUMBER : ([0-9]*[.])?[0-9]+ ;
ID : [a-z]+ ;
QUOTED_STRING : '"' ~ ["\r\n]* '"' ;
WHITESPACE : [ \t\r\n]+ -> skip ;