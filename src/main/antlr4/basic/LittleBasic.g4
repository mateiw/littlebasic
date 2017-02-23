grammar LittleBasic;
import LBExpression, LBTokens;

prog: block EOF;

statement
    : letstmt
    | printstmt
    | inputstmt
    | ifstmt
    | forstmt
    | continueForStmt
    | exitForStmt
    | COMMENT;

block
    : (statement (NEWLINE+ | EOF))*
    ;

letstmt
    : LET? vardecl EQ expression
    ;

vardecl
    : varname varsuffix?
    ;

varname
    : ID
    ;

varsuffix
    : DOLLAR
    ;

printstmt
    : PRINT expression;

inputstmt
    : INPUT string vardecl
    ;

ifstmt
    : IF expression NEWLINE* THEN NEWLINE block elifstmt* elsestmt? END
    ;

elifstmt
    : ELSE IF expression NEWLINE* THEN NEWLINE block
    ;

elsestmt
    : ELSE NEWLINE block
    ;

forstmt
    : FOR vardecl EQ expression TO expression (STEP expression)? NEWLINE+ block NEXT
    ;

continueForStmt
    : CONTINUE FOR
    ;

exitForStmt
    : EXIT FOR
    ;



