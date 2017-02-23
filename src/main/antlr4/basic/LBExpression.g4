grammar LBExpression;
import LBTokens;

prog: stat+;

stat
    : expression NEWLINE
    | NEWLINE
    ;

expression
    : string                                    # StringExpr
    | number                                    # NumberExpr
    | func                                      # FuncExpr
    | id                                        # IdExpr
    | (LPAREN expression RPAREN)                # ParenExpr
    | expression op=(MUL|DIV) expression        # MulDivExpr
    | expression op=(ADD|SUB) expression        # AddSubExpr
    | expression op=(GTE|GT|LTE|LT|EQ|NEQ) expression   # RelExpr
    | NOT expression                            # NotExpr
    | expression AND expression                 # AndExpr
    | expression OR expression                  # OrExpr
    | <assoc=right> expression EXP expression   # ExpExpr
    ;

func
    : intfunc
    | lenfunc
    ;

string
    : STRINGLITERAL
    ;

number
    : NUMBER
    ;

id
    : ID
    ;

intfunc
    : INT LPAREN expression RPAREN
    ;

lenfunc
    : LEN LPAREN expression RPAREN
    ;

exprlist
    : expression (COMMA expression)*
    ;

