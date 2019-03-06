lexer grammar XQueryLexer;

// Note: string syntax depends on syntactic context, so they are
// handled by the parser and not the lexer.

// NUMBERS

IntegerLiteral: Digits ;
DecimalLiteral: '.' Digits | Digits '.' [0-9]* ;
DoubleLiteral: ('.' Digits | Digits ('.' [0-9]*)?) [eE] [+-]? Digits ;

DFPropertyName: 'decimal-separator'
              | 'grouping-separator'
              | 'infinity'
              | 'minus-sign'
              | 'NaN'
              | 'percent'
              | 'per-mille'
              | 'zero-digit'
              | 'digit'
              | 'pattern-separator'
              | 'exponent-separator'
              ;

fragment
Digits: [0-9]+ ;

// This could be checked elsewhere: http://www.w3.org/TR/REC-xml/#wf-Legalchar
PredefinedEntityRef: '&' ('lt'|'gt'|'amp'|'quot'|'apos') ';' ;

// CharRef is additionally limited by http://www.w3.org/TR/REC-xml/#NT-Char,
CharRef: '&#' [0-9]+ ';' | '&#x' [0-9a-fA-F]+ ';' ;

// Escapes are handled as two Quot or two Apos tokens, to avoid maximal
// munch lexer ambiguity.
Quot: '"'  -> pushMode(QUOT_LITERAL_STRING);
Apos: '\'' -> pushMode(APOS_LITERAL_STRING);

// XML-SPECIFIC

COMMENT : '<!--' ('-' ~[-] | ~[-])* '-->' ;
XMLDECL : '<?' [Xx] [Mm] [Ll] ([ \t\r\n] .*?)? '?>' ;
PI      :      '<?' NCName ([ \t\r\n] .*?)? '?>' ;
CDATA   :   '<![CDATA[' .*? ']]>' ;
PRAGMA  :  '(#' WS? (NCName ':')? NCName (WS .*?)? '#)' ;

// WHITESPACE

// S ::= (#x20 | #x9 | #xD | #xA)+
WS: [ \t\r\n]+ -> channel(HIDDEN);

// OPERATORS

EQUAL           : '='  ;
NOT_EQUAL       : '!=' ;
LPAREN          : '(' ;
RPAREN          : ')' ;
LBRACKET        : '[' ;
RBRACKET        : ']' ;
LBRACE          : '{' ;
RBRACE          :  '}' ;

STAR            : '*' ;
PLUS            : '+' ;
MINUS           : '-' ;

COMMA           : ',' ;
DOT             : '.' ;
DDOT            : '..' ;
COLON           : ':' ;
COLON_EQ        : ':=' ;
SEMICOLON       : ';' ;

SLASH           : '/'  ;
DSLASH          : '//' ;
BACKSLASH       : '\\';
VBAR            : '|'  ;

LANGLE          : '<' ;
RANGLE          : '>' ;

QUESTION        : '?' ;
AT              : '@' ;
DOLLAR          : '$' ;
MOD             : '%' ;
BANG            : '!' ;
HASH            : '#' ;
CARAT           : '^' ;

ARROW           : '=>' ;
GRAVE           : '`' ;
CONCATENATION   : '||' ;
TILDE           : '~' ;


// KEYWORDS

KW_ALLOWING:           'allowing';
KW_ANCESTOR:           'ancestor';
KW_ANCESTOR_OR_SELF:   'ancestor-or-self';
KW_AND:                'and';
KW_ARRAY:              'array';
KW_AS:                 'as';
KW_ASCENDING:          'ascending';
KW_AT:                 'at';
KW_ATTRIBUTE:          'attribute';
KW_BASE_URI:           'base-uri';
KW_BOUNDARY_SPACE:     'boundary-space';
KW_BINARY:             'binary';
KW_BY:                 'by';
KW_CASE:               'case';
KW_CAST:               'cast';
KW_CASTABLE:           'castable';
KW_CATCH:              'catch';
KW_CHILD:              'child';
KW_COLLATION:          'collation';
KW_COMMENT:            'comment';
KW_CONSTRUCTION:       'construction';
KW_CONTEXT:            'context';
KW_COPY_NS:            'copy-namespaces';
KW_COUNT:              'count';
KW_DECLARE:            'declare';
KW_DEFAULT:            'default';
KW_DESCENDANT:         'descendant';
KW_DESCENDANT_OR_SELF: 'descendant-or-self';
KW_DESCENDING:         'descending';
KW_DECIMAL_FORMAT:     'decimal-format' ;
KW_DIV:                'div';
KW_DOCUMENT:           'document';
KW_DOCUMENT_NODE:      'document-node';
KW_ELEMENT:            'element';
KW_ELSE:               'else';
KW_EMPTY:              'empty';
KW_EMPTY_SEQUENCE:     'empty-sequence';
KW_ENCODING:           'encoding';
KW_END:                'end';
KW_EQ:                 'eq';
KW_EVERY:              'every';
KW_EXCEPT:             'except';
KW_EXTERNAL:           'external';
KW_FOLLOWING:          'following';
KW_FOLLOWING_SIBLING:  'following-sibling';
KW_FOR:                'for';
KW_FUNCTION:           'function';
KW_GE:                 'ge';
KW_GREATEST:           'greatest';
KW_GROUP:              'group';
KW_GT:                 'gt';
KW_IDIV:               'idiv';
KW_IF:                 'if';
KW_IMPORT:             'import';
KW_IN:                 'in';
KW_INHERIT:            'inherit';
KW_INSTANCE:           'instance';
KW_INTERSECT:          'intersect';
KW_IS:                 'is';
KW_ITEM:               'item';
KW_LAX:                'lax';
KW_LE:                 'le';
KW_LEAST:              'least';
KW_LET:                'let';
KW_LT:                 'lt';
KW_MAP:                'map';
KW_MOD:                'mod';
KW_MODULE:             'module';
KW_NAMESPACE:          'namespace';
KW_NE:                 'ne';
KW_NEXT:               'next';
KW_NAMESPACE_NODE:     'namespace-node';
KW_NO_INHERIT:         'no-inherit';
KW_NO_PRESERVE:        'no-preserve';
KW_NODE:               'node';
KW_OF:                 'of';
KW_ONLY:               'only';
KW_OPTION:             'option';
KW_OR:                 'or';
KW_ORDER:              'order';
KW_ORDERED:            'ordered';
KW_ORDERING:           'ordering';
KW_PARENT:             'parent';
KW_PRECEDING:          'preceding';
KW_PRECEDING_SIBLING:  'preceding-sibling';
KW_PRESERVE:           'preserve';
KW_PREVIOUS:           'previous';
KW_PI:                 'processing-instruction';
KW_RETURN:             'return';
KW_SATISFIES:          'satisfies';
KW_SCHEMA:             'schema';
KW_SCHEMA_ATTR:        'schema-attribute';
KW_SCHEMA_ELEM:        'schema-element';
KW_SELF:               'self';
KW_SLIDING:            'sliding';
KW_SOME:               'some';
KW_STABLE:             'stable';
KW_START:              'start';
KW_STRICT:             'strict';
KW_STRIP:              'strip';
KW_SWITCH:             'switch';
KW_TEXT:               'text';
KW_THEN:               'then';
KW_TO:                 'to';
KW_TREAT:              'treat';
KW_TRY:                'try';
KW_TUMBLING:           'tumbling';
KW_TYPE:               'type';
KW_TYPESWITCH:         'typeswitch';
KW_UNION:              'union';
KW_UNORDERED:          'unordered';
KW_UPDATE:             'update';
KW_VALIDATE:           'validate';
KW_VARIABLE:           'variable';
KW_VERSION:            'version';
KW_WHEN:               'when';
KW_WHERE:              'where';
KW_WINDOW:             'window';
KW_XQUERY:             'xquery';

// MarkLogic JSON computed constructor

KW_ARRAY_NODE:         'array-node';
KW_BOOLEAN_NODE:       'boolean-node';
KW_NULL_NODE:          'null-node';
KW_NUMBER_NODE:        'number-node';
KW_OBJECT_NODE:        'object-node';


// eXist-db update keywords

KW_REPLACE:            'replace';
KW_WITH:               'with';
KW_VALUE:              'value';
KW_INSERT:             'insert';
KW_INTO:               'into';
KW_DELETE:             'delete';
KW_RENAME:             'rename';


// NAMES

// Moved URIQualifiedName here to gather all names
URIQualifiedName: 'Q' '{' (PredefinedEntityRef | CharRef | ~[&{}])* '}' NCName ;

// We create these basic variants in order to honor ws:explicit in some basic cases
FullQName: NCName ':' NCName ;
NCNameWithLocalWildcard:  NCName ':' '*' ;
NCNameWithPrefixWildcard: '*' ':' NCName ; 

// According to http://www.w3.org/TR/REC-xml-names/#NT-NCName,
// it is 'an XML Name, minus the ":"'
NCName: NameStartChar NameChar*;

fragment
NameStartChar: [_a-zA-Z]
    | '\u00C0'..'\u00D6'
    | '\u00D8'..'\u00F6'
    | '\u00F8'..'\u02FF'
    | '\u0370'..'\u037D'
    | '\u037F'..'\u1FFF'
    | '\u200C'..'\u200D'
    | '\u2070'..'\u218F'
    | '\u2C00'..'\u2FEF'
    | '\u3001'..'\uD7FF'
    | '\uF900'..'\uFDCF'
    | '\uFDF0'..'\uFFFD'
    ;

fragment
NameChar: NameStartChar
        | '-'
        | '.'
        | [0-9]
        | '\u00A1'..'\u00BF'
        | '\u0300'..'\u036F'
        | '\u203F'..'\u2040'
        ;


// XQuery comments
//
// Element content can have an unbalanced set of (: :) pairs (as XQuery
// comments do not really exist inside them), so it is better to treat
// this as a single token with a recursive rule, rather than using a
// mode.

XQDOC_COMMENT_START: '(:~' ;
XQDOC_COMMENT_END: ':'+ ')' ;

XQDocComment: 	'(' ':' '~' ( CHAR | ( ':' ~( ')' ) ) )* ':' ')' ;

XQComment: '(' ':' ~'~' (XQComment | '(' ~[:] | ':' ~[)] | ~[:(])* ':'* ':'+ ')' -> channel(HIDDEN);

CHAR: ( '\t' | '\n' | '\r' | '\u0020'..'\u0039' | '\u003B'..'\uD7FF' | '\uE000'..'\uFFFD' ) ;

// These rules have been added to enter and exit the String mode
ENTER_STRING        : GRAVE GRAVE LBRACKET -> pushMode(STRING_MODE);
EXIT_INTERPOLATION  : RBRACE GRAVE -> popMode;

// This is an intersection of:
//
// [148] ElementContentChar  ::= Char - [{}<&]
// [149] QuotAttrContentChar ::= Char - ["{}<&]
// [150] AposAttrContentChar ::= Char - ['{}<&]
//
// Therefore, we would have something like:
//
// ElementContentChar  ::= ContentChar | ["']
// QuotAttrContentChar ::= ContentChar | [']
// AposAttrContentChar ::= ContentChar | ["]
//
// This rule needs to be the very last one, so it has the lowest priority.

ContentChar:  ~["'{}<&] ;


// Lexical modes to parse Strings
mode STRING_MODE;

BASIC_CHAR          : ( '\t' 
                        | '\u000A'
                        | '\u000D'
                        | '\u0020'..'\u005C'                        
                        | '\u005E'..'\u005F'
                        | '\u0061'..'\u007A'
                        | '\u007C'..'\uD7FF'
                        | '\uE000'..'\uFFFD'
                        | '\u{10000}'..'\u{10FFFF}' ) ;


GRAVE_STRING        : '`' -> type(GRAVE);
RBRACKET_STRING     : ']' -> type(RBRACKET);
LBRACE_STRING       : '{' -> type(LBRACE);

ENTER_INTERPOLATION : GRAVE LBRACE -> pushMode(DEFAULT_MODE);
EXIT_STRING         : RBRACKET GRAVE GRAVE -> popMode;

mode QUOT_LITERAL_STRING;

Quot_QuotString                 : '"' -> type(Quot), popMode;

DOUBLE_LBRACE_QuotString        : '{{' ;
DOUBLE_RBRACE_QuotString        : '}}' ;
LBRACE_QuotString               : '{' -> type(LBRACE), pushMode(STRING_INTERPOLATION_MODE);
RBRACE_QuotString               : '}' -> type(RBRACE);
PredefinedEntityRef_QuotString  : '&' ('lt'|'gt'|'amp'|'quot'|'apos') ';'  -> type(PredefinedEntityRef);
CharRef_QuotString              : ('&#' [0-9]+ ';' | '&#x' [0-9a-fA-F]+ ';') -> type(CharRef);
ContentChar_QuotString          : ~["&{}] -> type(ContentChar);

mode APOS_LITERAL_STRING;

Apos_AposString                 : '\'' -> type(Apos), popMode;

DOUBLE_LBRACE_AposString        : '{{' ;
DOUBLE_RBRACE_AposString        : '}}' ;
LBRACE_AposString               : '{' -> type(LBRACE), pushMode(STRING_INTERPOLATION_MODE);
RBRACE_AposString               : '}' -> type(RBRACE);
PredefinedEntityRef_AposString  : '&' ('lt'|'gt'|'amp'|'quot'|'apos') ';'  -> type(PredefinedEntityRef);
CharRef_AposString              : ('&#' [0-9]+ ';' | '&#x' [0-9a-fA-F]+ ';') -> type(CharRef);
ContentChar_AposString          : ~['&{}] -> type(ContentChar);

mode STRING_INTERPOLATION_MODE;

INT_IntegerLiteral: Digits -> type(IntegerLiteral);
INT_DecimalLiteral: ('.' Digits | Digits '.' [0-9]*) -> type(DecimalLiteral) ;
INT_DoubleLiteral: ('.' Digits | Digits ('.' [0-9]*)?) [eE] [+-]? Digits -> type(DoubleLiteral);

INT_DFPropertyName: ('decimal-separator'
              | 'grouping-separator'
              | 'infinity'
              | 'minus-sign'
              | 'NaN'
              | 'percent'
              | 'per-mille'
              | 'zero-digit'
              | 'digit'
              | 'pattern-separator'
              | 'exponent-separator' )
              -> type(DFPropertyName);

// This could be checked elsewhere: http://www.w3.org/TR/REC-xml/#wf-Legalchar
INT_PredefinedEntityRef: '&' ('lt'|'gt'|'amp'|'quot'|'apos') ';' -> type(PredefinedEntityRef);

// CharRef is additionally limited by http://www.w3.org/TR/REC-xml/#NT-Char,
INT_CharRef: ('&#' [0-9]+ ';' | '&#x' [0-9a-fA-F]+ ';') -> type(CharRef);

// Escapes are handled as two Quot or two Apos tokens, to avoid maximal
// munch lexer ambiguity.
INT_Quot: '"'  -> pushMode(QUOT_LITERAL_STRING), type(Quot);
INT_Apos: '\'' -> pushMode(APOS_LITERAL_STRING), type(Apos);

// XML-SPECIFIC

INT_COMMENT : '<!--' ('-' ~[-] | ~[-])* '-->'  -> type(COMMENT);
INT_XMLDECL : '<?' [Xx] [Mm] [Ll] ([ \t\r\n] .*?)? '?>'  -> type(XMLDECL);
INT_PI      :      '<?' NCName ([ \t\r\n] .*?)? '?>'  -> type(PI);
INT_CDATA   :   '<![CDATA[' .*? ']]>'  -> type(CDATA);
INT_PRAGMA  :  '(#' WS? (NCName ':')? NCName (WS .*?)? '#)' -> type(PRAGMA);

// WHITESPACE

// S ::= (#x20 | #x9 | #xD | #xA)+
INT_WS: [ \t\r\n]+ -> channel(HIDDEN), type(WS);

// OPERATORS

INT_EQUAL           : '='  -> type(EQUAL) ;
INT_NOT_EQUAL       : '!=' -> type(NOT_EQUAL);
INT_LPAREN          : '(' -> type(LPAREN);
INT_RPAREN          : ')' -> type(RPAREN);
INT_LBRACKET        : '[' -> type(LBRACKET);
INT_RBRACKET        : ']' -> type(RBRACKET);
INT_LBRACE          : '{' -> type(LBRACE);
INT_RBRACE          :  '}' -> type(RBRACE), popMode ;

INT_STAR            : '*' -> type(STAR);
INT_PLUS            : '+' -> type(PLUS);
INT_MINUS           : '-' -> type(MINUS);

INT_COMMA           : ',' -> type(COMMA);
INT_DOT             : '.' -> type(DOT);
INT_DDOT            : '..' -> type(DDOT);
INT_COLON           : ':' -> type(COLON);
INT_COLON_EQ        : ':=' -> type(COLON_EQ);
INT_SEMICOLON       : ';' -> type(SEMICOLON);

INT_SLASH           : '/' -> type(SLASH);
INT_DSLASH          : '//' -> type(DSLASH);
INT_BACKSLASH       : '\\' -> type(BACKSLASH);
INT_VBAR            : '|' -> type(VBAR);

INT_LANGLE          : '<' -> type(LANGLE);
INT_RANGLE          : '>' -> type(RANGLE);

INT_QUESTION        : '?' -> type(QUESTION);
INT_AT              : '@' -> type(AT);
INT_DOLLAR          : '$' -> type(DOLLAR);
INT_MOD             : '%' -> type(MOD);
INT_BANG            : '!' -> type(BANG);
INT_HASH            : '#' -> type(HASH);
INT_CARAT           : '^' -> type(CARAT);

INT_ARROW           : '=>' -> type(ARROW);
INT_GRAVE           : '`' -> type(GRAVE);
INT_CONCATENATION   : '||' -> type(CONCATENATION);
INT_TILDE           : '~' -> type(TILDE);


// KEYWORDS

INT_KW_ALLOWING:           'allowing' -> type(KW_ALLOWING);
INT_KW_ANCESTOR:           'ancestor' -> type(KW_ANCESTOR);
INT_KW_ANCESTOR_OR_SELF:   'ancestor-or-self' -> type(KW_ANCESTOR_OR_SELF);
INT_KW_AND:                'and' -> type(KW_AND);
INT_KW_ARRAY:              'array' -> type(KW_ARRAY);
INT_KW_AS:                 'as' -> type(KW_AS);
INT_KW_ASCENDING:          'ascending' -> type(KW_ASCENDING);
INT_KW_AT:                 'at' -> type(KW_AT);
INT_KW_ATTRIBUTE:          'attribute' -> type(KW_ATTRIBUTE);
INT_KW_BASE_URI:           'base-uri' -> type(KW_BASE_URI);
INT_KW_BOUNDARY_SPACE:     'boundary-space' -> type(KW_BOUNDARY_SPACE);
INT_KW_BINARY:             'binary' -> type(KW_BINARY);
INT_KW_BY:                 'by' -> type(KW_BY);
INT_KW_CASE:               'case' -> type(KW_CASE);
INT_KW_CAST:               'cast' -> type(KW_CAST);
INT_KW_CASTABLE:           'castable' -> type(KW_CASTABLE);
INT_KW_CATCH:              'catch' -> type(KW_CATCH);
INT_KW_CHILD:              'child' -> type(KW_CHILD);
INT_KW_COLLATION:          'collation' -> type(KW_COLLATION);
INT_KW_COMMENT:            'comment' -> type(KW_COMMENT);
INT_KW_CONSTRUCTION:       'construction' -> type(KW_CONSTRUCTION);
INT_KW_CONTEXT:            'context' -> type(KW_CONTEXT);
INT_KW_COPY_NS:            'copy-namespaces' -> type(KW_COPY_NS);
INT_KW_COUNT:              'count' -> type(KW_COUNT);
INT_KW_DECLARE:            'declare' -> type(KW_DECLARE);
INT_KW_DEFAULT:            'default' -> type(KW_DEFAULT);
INT_KW_DESCENDANT:         'descendant' -> type(KW_DESCENDANT);
INT_KW_DESCENDANT_OR_SELF: 'descendant-or-self' -> type(KW_DESCENDANT_OR_SELF);
INT_KW_DESCENDING:         'descending' -> type(KW_DESCENDING);
INT_KW_DECIMAL_FORMAT:     'decimal-format'  -> type(KW_DECIMAL_FORMAT);
INT_KW_DIV:                'div' -> type(KW_DIV);
INT_KW_DOCUMENT:           'document' -> type(KW_DOCUMENT);
INT_KW_DOCUMENT_NODE:      'document-node' -> type(KW_DOCUMENT_NODE);
INT_KW_ELEMENT:            'element' -> type(KW_ELEMENT);
INT_KW_ELSE:               'else' -> type(KW_ELSE);
INT_KW_EMPTY:              'empty' -> type(KW_EMPTY);
INT_KW_EMPTY_SEQUENCE:     'empty-sequence' -> type(KW_EMPTY_SEQUENCE);
INT_KW_ENCODING:           'encoding' -> type(KW_ENCODING);
INT_KW_END:                'end' -> type(KW_END);
INT_KW_EQ:                 'eq' -> type(KW_EQ);
INT_KW_EVERY:              'every' -> type(KW_EVERY);
INT_KW_EXCEPT:             'except' -> type(KW_EXCEPT);
INT_KW_EXTERNAL:           'external' -> type(KW_EXTERNAL);
INT_KW_FOLLOWING:          'following' -> type(KW_FOLLOWING);
INT_KW_FOLLOWING_SIBLING:  'following-sibling' -> type(KW_FOLLOWING_SIBLING);
INT_KW_FOR:                'for' -> type(KW_FOR);
INT_KW_FUNCTION:           'function' -> type(KW_FUNCTION);
INT_KW_GE:                 'ge' -> type(KW_GE);
INT_KW_GREATEST:           'greatest' -> type(KW_GREATEST);
INT_KW_GROUP:              'group' -> type(KW_GROUP);
INT_KW_GT:                 'gt' -> type(KW_GT);
INT_KW_IDIV:               'idiv' -> type(KW_IDIV);
INT_KW_IF:                 'if' -> type(KW_IF);
INT_KW_IMPORT:             'import' -> type(KW_IMPORT);
INT_KW_IN:                 'in' -> type(KW_IN);
INT_KW_INHERIT:            'inherit' -> type(KW_INHERIT);
INT_KW_INSTANCE:           'instance' -> type(KW_INSTANCE);
INT_KW_INTERSECT:          'intersect' -> type(KW_INTERSECT);
INT_KW_IS:                 'is' -> type(KW_IS);
INT_KW_ITEM:               'item' -> type(KW_ITEM);
INT_KW_LAX:                'lax' -> type(KW_LAX);
INT_KW_LE:                 'le' -> type(KW_LE);
INT_KW_LEAST:              'least' -> type(KW_LEAST);
INT_KW_LET:                'let' -> type(KW_LET);
INT_KW_LT:                 'lt' -> type(KW_LT);
INT_KW_MAP:                'map' -> type(KW_MAP);
INT_KW_MOD:                'mod' -> type(KW_MOD);
INT_KW_MODULE:             'module' -> type(KW_MODULE);
INT_KW_NAMESPACE:          'namespace' -> type(KW_NAMESPACE);
INT_KW_NE:                 'ne' -> type(KW_NE);
INT_KW_NEXT:               'next' -> type(KW_NEXT);
INT_KW_NAMESPACE_NODE:     'namespace-node' -> type(KW_NAMESPACE_NODE);
INT_KW_NO_INHERIT:         'no-inherit' -> type(KW_NO_INHERIT);
INT_KW_NO_PRESERVE:        'no-preserve' -> type(KW_NO_PRESERVE);
INT_KW_NODE:               'node' -> type(KW_NODE);
INT_KW_OF:                 'of' -> type(KW_OF);
INT_KW_ONLY:               'only' -> type(KW_ONLY);
INT_KW_OPTION:             'option' -> type(KW_OPTION);
INT_KW_OR:                 'or' -> type(KW_OR);
INT_KW_ORDER:              'order' -> type(KW_ORDER);
INT_KW_ORDERED:            'ordered' -> type(KW_ORDERED);
INT_KW_ORDERING:           'ordering' -> type(KW_ORDERING);
INT_KW_PARENT:             'parent' -> type(KW_PARENT);
INT_KW_PRECEDING:          'preceding' -> type(KW_PRECEDING);
INT_KW_PRECEDING_SIBLING:  'preceding-sibling' -> type(KW_PRECEDING_SIBLING);
INT_KW_PRESERVE:           'preserve' -> type(KW_PRESERVE);
INT_KW_PREVIOUS:           'previous' -> type(KW_PREVIOUS);
INT_KW_PI:                 'processing-instruction' -> type(KW_PI);
INT_KW_RETURN:             'return' -> type(KW_RETURN);
INT_KW_SATISFIES:          'satisfies' -> type(KW_SATISFIES);
INT_KW_SCHEMA:             'schema' -> type(KW_SCHEMA);
INT_KW_SCHEMA_ATTR:        'schema-attribute' -> type(KW_SCHEMA_ATTR);
INT_KW_SCHEMA_ELEM:        'schema-element' -> type(KW_SCHEMA_ELEM);
INT_KW_SELF:               'self' -> type(KW_SELF);
INT_KW_SLIDING:            'sliding' -> type(KW_SLIDING);
INT_KW_SOME:               'some' -> type(KW_SOME);
INT_KW_STABLE:             'stable' -> type(KW_STABLE);
INT_KW_START:              'start' -> type(KW_START);
INT_KW_STRICT:             'strict' -> type(KW_STRICT);
INT_KW_STRIP:              'strip' -> type(KW_STRIP);
INT_KW_SWITCH:             'switch' -> type(KW_SWITCH);
INT_KW_TEXT:               'text' -> type(KW_TEXT);
INT_KW_THEN:               'then' -> type(KW_THEN);
INT_KW_TO:                 'to' -> type(KW_TO);
INT_KW_TREAT:              'treat' -> type(KW_TREAT);
INT_KW_TRY:                'try' -> type(KW_TRY);
INT_KW_TUMBLING:           'tumbling' -> type(KW_TUMBLING);
INT_KW_TYPE:               'type' -> type(KW_TYPE);
INT_KW_TYPESWITCH:         'typeswitch' -> type(KW_TYPESWITCH);
INT_KW_UNION:              'union' -> type(KW_UNION);
INT_KW_UNORDERED:          'unordered' -> type(KW_UNORDERED);
INT_KW_UPDATE:             'update' -> type(KW_UPDATE);
INT_KW_VALIDATE:           'validate' -> type(KW_VALIDATE);
INT_KW_VARIABLE:           'variable' -> type(KW_VARIABLE);
INT_KW_VERSION:            'version' -> type(KW_VERSION);
INT_KW_WHEN:               'when' -> type(KW_WHEN);
INT_KW_WHERE:              'where' -> type(KW_WHERE);
INT_KW_WINDOW:             'window' -> type(KW_WINDOW);
INT_KW_XQUERY:             'xquery' -> type(KW_XQUERY);

// MarkLogic JSON computed constructor

INT_KW_ARRAY_NODE:         'array-node' -> type(KW_ARRAY_NODE);
INT_KW_BOOLEAN_NODE:       'boolean-node' -> type(KW_BOOLEAN_NODE);
INT_KW_NULL_NODE:          'null-node' -> type(KW_NULL_NODE);
INT_KW_NUMBER_NODE:        'number-node' -> type(KW_NUMBER_NODE);
INT_KW_OBJECT_NODE:        'object-node' -> type(KW_OBJECT_NODE);


// eXist-db update keywords

INT_KW_REPLACE:            'replace' -> type(KW_REPLACE);
INT_KW_WITH:               'with' -> type(KW_WITH);
INT_KW_VALUE:              'value' -> type(KW_VALUE);
INT_KW_INSERT:             'insert' -> type(KW_INSERT);
INT_KW_INTO:               'into' -> type(KW_INTO);
INT_KW_DELETE:             'delete' -> type(KW_DELETE);
INT_KW_RENAME:             'rename' -> type(KW_RENAME);


// NAMES

INT_URIQualifiedName: 'Q' '{' (PredefinedEntityRef | CharRef | ~[&{}])* '}' NCName -> type(URIQualifiedName);
INT_FullQName: NCName ':' NCName -> type(FullQName);
INT_NCNameWithLocalWildcard:  NCName ':' '*' -> type(NCNameWithLocalWildcard);
INT_NCNameWithPrefixWildcard: '*' ':' NCName -> type(NCNameWithPrefixWildcard); 

INT_NCName: NameStartChar NameChar* -> type(NCName);

INT_XQDOC_COMMENT_START: '(:~' -> type(XQDOC_COMMENT_START);
INT_XQDOC_COMMENT_END: ':'+ ')' -> type(XQDOC_COMMENT_END);

INT_XQDocComment: 	'(' ':' '~' ( CHAR | ( ':' ~( ')' ) ) )* ':' ')' -> type(XQDocComment);

INT_XQComment: '(' ':' ~'~' (XQComment | '(' ~[:] | ':' ~[)] | ~[:(])* ':'* ':'+ ')' -> channel(HIDDEN), type(XQComment);

INT_CHAR: ( '\t' | '\n' | '\r' | '\u0020'..'\u0039' | '\u003B'..'\uD7FF' | '\uE000'..'\uFFFD' ) -> type(CHAR);


INT_ENTER_STRING        : GRAVE GRAVE LBRACKET -> pushMode(STRING_MODE), type(ENTER_STRING);
INT_EXIT_INTERPOLATION  : RBRACE GRAVE -> popMode, type(ENTER_INTERPOLATION);

INT_ContentChar:  ~["'{}<&] -> type(ContentChar);