parser grammar ExprParser;

options {
    tokenVocab=ExprLexer;
}

function: ID LEFT arguments? RIGHT ;
arguments: NUMBER (COMMA NUMBER)* ;

