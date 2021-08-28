grammar Expr;

function : 'SUM(' NUMBER (',' NUMBER)* ')' ;

NUMBER : [0-9]|[1-9][0-9]* ;
SPACE : [ \t\r\n]+ -> skip;
