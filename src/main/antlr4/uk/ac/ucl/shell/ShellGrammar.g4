grammar ShellGrammar;

/*
 * Parser Rules
 */

command: pipe | seq | call;

pipe: call '|' (pipe | seq | call);
seq: call ';' (pipe | seq | call)?;
call:
	whitespace* (redirection whitespace)* (argument) (
		whitespace* (redirection | argument)
	)* (whitespace)*;

nonKeyword: NONSPECIAL;
singleQuoted: '\'' singleQuoteContent '\'';
doubleQuoted: '"' (doubleQuoteContent | backQuoted)* '"';
backQuoted: '`' backQuotedContent '`';

backQuotedContent: (~('\n' | '\r' | '`'))*;
singleQuoteContent: (~('\r' | '\n' | '\''))*;
doubleQuoteContent: (~('\n' | '\r' | '"' | '`'))+;

whitespace: WS+;
comment: '#'~('\n' | '\r')*;

redirection:
	'<' whitespace* argument
	| '>' whitespace* argument;
argument: nonKeyword | doubleQuoted | singleQuoted | backQuoted | comment;

/*
 * Lexer Rules
 */

NONSPECIAL: ~[ \t\n'"`|;><#]+;
WS: (' ' | '\t');