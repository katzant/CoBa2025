parser grammar PascalParser;

options { tokenVocab=PascalLexer;
}

start:  program
        (var_area)?
        (function | procedure)*
        BEGIN_SYMBOL
        statements
        END_SYMBOL
        END_DOT;

program:    PROGRAM_SYMBOL
            IDENTIFIER
            SEMICOLON;

function:   function_head
            (var_area)?
            BEGIN_SYMBOL
            statements
            END_SYMBOL
            SEMICOLON;

procedure:  procedure_head
            (var_area)?
            BEGIN_SYMBOL
            statements
            END_SYMBOL
            SEMICOLON;

statements: (statement (SEMICOLON statement)*)?
            (SEMICOLON)?;

statement:  if_statement
            | while_statement
            | writeln
            | function_call
            | assignment
            | begin_end_block;

assignment: IDENTIFIER
            DEFINE_SYMBOL
            expression;

writeln:    WRITELN_SYMBOL
                    LEFT_PARENTHESIS
                    expression
                    RIGHT_PARENTHESIS;

begin_end_block:    BEGIN_SYMBOL
                    statements
                    END_SYMBOL;

if_statement: IF_SYMBOL
    (LEFT_PARENTHESIS expression RIGHT_PARENTHESIS | expression)
    THEN_SYMBOL
    statement
    (ELSE_SYMBOL statement)?;

while_statement:    WHILE_SYMBOL
                    (LEFT_PARENTHESIS expression RIGHT_PARENTHESIS | expression)
                    DO
                    (statement | begin_end_block);

procedure_head: PROCEDURE_SYMBOL
                IDENTIFIER
                LEFT_PARENTHESIS
                (parameter)?
                RIGHT_PARENTHESIS
                SEMICOLON;

function_head:  FUNCTION_SYMBOL
                IDENTIFIER
                LEFT_PARENTHESIS
                (parameter)?
                RIGHT_PARENTHESIS
                COLON
                data_type
                SEMICOLON;

var_area:   VAR_SYMBOL
            (var_declaration)+;

//parameter:  (IDENTIFIER (COMMA IDENTIFIER)*
//            COLON
//            data_type
//            (SEMICOLON IDENTIFIER (COMMA IDENTIFIER)* COLON data_type)*)?;

parameter:  param_tuple (SEMICOLON param_tuple)*;

param_tuple:    IDENTIFIER (COMMA IDENTIFIER)* COLON data_type;

var_declaration:    (IDENTIFIER (COMMA IDENTIFIER)* COLON data_type SEMICOLON)
                    | (IDENTIFIER COLON data_type EQUALS expression SEMICOLON);

expression: LEFT_PARENTHESIS expression RIGHT_PARENTHESIS   # ParenExpression
            | (NOT | MINUS | PLUS) expression               # UnaryExpression
            | function_call                                 # FuncExpression
            | expression (MULT | DIV | MOD | AND) expression    # MultExpression
            | expression (PLUS | MINUS | OR) expression         # AdditExpression
            | expression (LESS_THAN | GREATHER_THAN | LESS_EQUAL | GREATER_EQUALS | EQUALS | UNEQUAL) expression    # BoolExpression
            | literal   # LiteralExpression
            ;

literal:    IDENTIFIER
            | INTCONST
            | DOUBLECONST
            | STRINGCONST
            | TRUE
            | FALSE;

function_call: IDENTIFIER LEFT_PARENTHESIS (expression (COMMA expression)*)? RIGHT_PARENTHESIS;

data_type:  INT_SYMBOL
            | DOUBLE_SYMBOL
            | BOOL_SYMBOL
            | STRING_SYMBOL;

