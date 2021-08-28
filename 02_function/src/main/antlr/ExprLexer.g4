lexer grammar ExprLexer;

ID: [a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER : '-'?[0-9]|[1-9][0-9]* ;
LEFT : '(' ;
RIGHT: ')' ;
COMMA: ',' ;
SPACE : [ \t\r\n]+ -> skip;
