lexer grammar PascalLexer;

PROGRAM_SYMBOL: 'program';
FUNCTION_SYMBOL: 'function';
DEFINE_SYMBOL: ':=';
VAR_SYMBOL: 'var';

PROCEDURE_SYMBOL: 'procedure';

BEGIN_SYMBOL: 'begin';
END_SYMBOL: 'end';

WHILE_SYMBOL: 'while';
DO: 'do';
IF_SYMBOL: 'if';
THEN_SYMBOL: 'then';
ELSE_SYMBOL: 'else';

WRITELN_SYMBOL: 'writeln';

// Typen
INT_SYMBOL: 'integer';
DOUBLE_SYMBOL: 'double';
BOOL_SYMBOL: 'boolean';
STRING_SYMBOL: 'string';


// Kommentare
COMMENT_SINGLE_LINE: '//' .*? '\n' -> skip;
COMMENT_MULTIPLE_LINE_PAR: '(*' .*? '*)' -> skip;
COMMENT_MULTIPLE_LINE_BRACE: '{' .*? '}' -> skip;


LEFT_PARENTHESIS: '(';
RIGHT_PARENTHESIS: ')';

// Operationen
PLUS: '+';
MINUS: '-';
SEMICOLON: ';';
COLON: ':';
MULT: '*';
DIV: '/';
MOD: 'mod';
COMMA: ',';

EQUALS: '=';
LESS_THAN: '<';
GREATHER_THAN: '>';
LESS_EQUAL: '<=';
GREATER_EQUALS: '>=';
UNEQUAL: '<>';

AND: 'and';
OR: 'or';
NOT: 'not';

TRUE: 'true';
FALSE: 'false';


INTCONST: [0-9]+;
DOUBLECONST: [0-9]+'.'[0-9]+;
STRINGCONST: '\''~('\'')*'\'';


IDENTIFIER: [a-zA-Z_][a-zA-Z0-9]*;

WHITESPACE : [ \t\r\n]+ -> skip;

END_DOT:'.';
