parser grammar XQueryParser;
options {
  tokenVocab=XQueryLexer;
}

// Mostly taken from http://www.w3.org/TR/xquery/#id-grammar, with some
// simplifications:
//
// 1. The parser itself doesn't really enforce ws:explicit except for some easy
//    cases (QNames and wildcards).  Walkers will need to do this (and also parse
//    wildcards a bit).
//
// 2. When collecting element content, we will need to check the HIDDEN
//    channel as well, for whitespace and XQuery comments (these should be
//    treated as regular text inside elements).

// MODULE HEADER ///////////////////////////////////////////////////////////////

module: xqDocComment? versionDecl? xqDocComment? (libraryModule | (mainModule (SEMICOLON versionDecl? mainModule)* )) ;

xqDocComment: XQDocComment ;

versionDecl: 'xquery' 'version' version=stringLiteral
             ('encoding' encoding=stringLiteral)?
             ';' ;

mainModule: prolog queryBody;

queryBody: expr ;

libraryModule: moduleDecl prolog;

moduleDecl: 'module' 'namespace' ncName '=' uri=stringLiteral ';' ;

// MODULE PROLOG ///////////////////////////////////////////////////////////////

prolog: ((defaultNamespaceDecl | setter | namespaceDecl | schemaImport | moduleImport) ';')*
        ( xqDocComment? (varDecl | functionDecl | contextItemDecl | optionDecl) ';')* ;

defaultNamespaceDecl: 'declare' 'default'
                      type=('element' | 'function')
                      'namespace'
                      uri=stringLiteral ;

setter: boundarySpaceDecl
      | defaultCollationDecl
      | baseURIDecl
      | constructionDecl
      | orderingModeDecl
      | emptyOrderDecl
      | copyNamespacesDecl
      | decimalFormatDecl ;

boundarySpaceDecl: 'declare' 'boundary-space' type=('preserve' | 'strip') ;
defaultCollationDecl: 'declare' 'default' 'collation' uriLiteral ;
baseURIDecl: 'declare' 'base-uri' uriLiteral ;
constructionDecl: 'declare' 'construction' type=('strip' | 'preserve') ;
orderingModeDecl: 'declare' 'ordering' type=('ordered' | 'unordered') ;
emptyOrderDecl: 'declare' 'default' 'order' 'empty' type=('greatest' | 'least') ;
copyNamespacesDecl: 'declare' 'copy-namespaces' preserveMode COMMA inheritMode ;
preserveMode: 'preserve' | 'no-preserve' ;
inheritMode: 'inherit' | 'no-inherit' ;
decimalFormatDecl: 'declare' (
                      ('decimal-format' eqName)
                    | ('default' 'decimal-format')
                   )
                   (DFPropertyName '=' stringLiteral)*;


schemaImport: 'import' 'schema'
              schemaPrefix?
              nsURI=uriLiteral
              ('at' locations+=uriLiteral (',' locations+=uriLiteral)*)? ;

schemaPrefix: ('namespace' ncName '=' | 'default' 'element' 'namespace') ;

moduleImport: 'import' 'module'
              ('namespace' ncName '=')?
              nsURI=uriLiteral
              ('at' locations+=uriLiteral (',' locations+=uriLiteral)*)? ;


namespaceDecl: 'declare' 'namespace' ncName '=' uriLiteral ;

varDecl: 'declare' annotations 'variable' '$' varName typeDeclaration?
         (
            (':=' varValue)
          | ('external'(':=' varDefaultValue)?)
          | ('{' varValue '}')
          | ('external'('{' varDefaultValue '}')?)
         ) ;

varValue: expr ;

varDefaultValue: expr ;

contextItemDecl: 'declare' 'context' 'item'
                 ('as' itemType)?
                 ((COLON_EQ value=exprSingle)
                 | ('external' (COLON_EQ defaultValue=exprSingle)?)) ;

functionDecl: 'declare' annotations 'function' name=eqName '(' functionParams? ')'
              functionReturn?
              ( functionBody | 'external') ;

functionParams: functionParam (COMMA functionParam)* ;

functionParam: '$' name=qName type=typeDeclaration? ;

annotations: annotation* ;

annotation: MOD qName (LPAREN annotList RPAREN)? ;

annotList: annotationParam ( COMMA annotationParam )* ;

annotationParam: literal ;

functionReturn: 'as' sequenceType ;

optionDecl: 'declare' 'option' name=qName value=stringLiteral ;


// EXPRESSIONS /////////////////////////////////////////////////////////////////

expr: exprSingle (',' exprSingle)* ;

exprSingle: flworExpr
          | quantifiedExpr
          | switchExpr
          | typeswitchExpr
          | existUpdateExpr
          | ifExpr
          | tryCatchExpr
          | orExpr ;

flworExpr: initialClause intermediateClause* returnClause ;

initialClause: forClause | letClause | windowClause ;
intermediateClause: initialClause
                  | whereClause
                  | groupByClause
                  | orderByClause
                  | countClause
                  ;

forClause: 'for' vars+=forBinding (',' vars+=forBinding)* ;

forBinding: '$' name=varName type=typeDeclaration? allowingEmpty? positionalVar?
        'in' in=exprSingle ;

allowingEmpty: 'allowing' 'empty';

positionalVar: 'at' '$' pvar=varName ;

letClause: 'let'  vars+=letBinding (',' vars+=letBinding)* ;

letBinding: '$' varName typeDeclaration? ':=' exprSingle ;

windowClause: 'for' (tumblingWindowClause | slidingWindowClause) ;

tumblingWindowClause: 'tumbling' 'window' '$' name=qName
                          type=typeDeclaration? 'in' exprSingle
                          windowStartCondition windowEndCondition? ;

slidingWindowClause: 'sliding' 'window' '$' name=qName
                          type=typeDeclaration? 'in' exprSingle
                          windowStartCondition windowEndCondition ;

windowStartCondition: 'start' windowVars 'when' exprSingle ;

windowEndCondition: 'only'? 'end' windowVars 'when' exprSingle ;

windowVars: ('$' currentItem=eqName)? positionalVar?
                          ('previous' '$' previousItem=eqName)?
                          ('next' '$' nextItem=eqName)?;

countClause: 'count' '$' varName ;

whereClause: 'where' whereExpr=exprSingle ;

groupByClause: 'group' 'by' groupingSpecList ;

groupingSpecList: groupingSpec (COMMA groupingSpec)* ;

groupingSpec: '$' name=varName
                    (type=typeDeclaration? COLON_EQ exprSingle)?
                    ('collation' uri=uriLiteral)? ;

orderByClause: 'stable'? 'order' 'by' specs+=orderSpec (',' specs+=orderSpec)* ;

orderSpec: value=exprSingle
           order=('ascending' | 'descending')?
           ('empty' empty=('greatest'|'least'))?
           ('collation' collation=uriLiteral)?
         ;

returnClause: 'return' exprSingle ;

quantifiedExpr: quantifier=('some' | 'every') quantifiedVar (',' quantifiedVar)*
                'satisfies' value=exprSingle ;

quantifiedVar: '$' varName typeDeclaration? 'in' exprSingle ;

switchExpr: 'switch' '(' expr ')'
                switchCaseClause+
                'default' 'return' returnExpr=exprSingle ;

switchCaseClause: ('case' switchCaseOperand)+ 'return' exprSingle ;

switchCaseOperand: exprSingle ;

typeswitchExpr: 'typeswitch' '(' expr ')'
                clauses=caseClause+
                'default' ('$' var=varName)? 'return' returnExpr=exprSingle ;

caseClause: 'case' ('$' var=varName 'as')? type=sequenceUnionType 'return'
            returnExpr=exprSingle ;

sequenceUnionType: sequenceType ( '|' sequenceType )* ;

ifExpr: 'if' '(' conditionExpr=expr ')'
        'then' thenExpr=exprSingle
        'else' elseExpr=exprSingle ;

tryCatchExpr: tryClause catchClause+ ;
tryClause: 'try' enclosedTryTargetExpression ;
enclosedTryTargetExpression: enclosedExpression ;
catchClause: 'catch' (catchErrorList | ('(' '$' varName ')'))  enclosedExpression ;
enclosedExpression: '{' expr? '}' ;

catchErrorList: nameTest ('|' nameTest)* ;


existUpdateExpr: 'update' ( existReplaceExpr | existValueExpr | existInsertExpr | existDeleteExpr | existRenameExpr ) ;

existReplaceExpr: 'replace' expr 'with' exprSingle ;
existValueExpr: 'value' expr 'with' exprSingle ;
existInsertExpr: 'insert' exprSingle ('into' | 'preceding' | 'following') exprSingle;
existDeleteExpr: 'delete' exprSingle;
existRenameExpr: 'rename' exprSingle 'as' exprSingle;


orExpr: andExpr ('or' andExpr)* ;

andExpr: comparisonExpr ('and' comparisonExpr)* ;

comparisonExpr: stringConcatExpr ( (valueComp | generalComp | nodeComp) stringConcatExpr )? ;

stringConcatExpr: rangeExpr (CONCATENATION rangeExpr)* ;

rangeExpr: additiveExpr ('to' additiveExpr)? ;

additiveExpr: multiplicativeExpr ( ('+' | '-') multiplicativeExpr )* ;

multiplicativeExpr: unionExpr ( ('*' | 'div' | 'idiv' | 'mod') unionExpr )* ;

unionExpr: intersectExceptExpr ( (KW_UNION | '|') intersectExceptExpr)* ;

intersectExceptExpr: instanceOfExpr ( ('intersect' | 'except') instanceOfExpr)* ;

instanceOfExpr: treatExpr ( 'instance' 'of' sequenceType)? ;

treatExpr: castableExpr ( 'treat' 'as' sequenceType)? ;

castableExpr: castExpr ('castable' 'as' singleType)?;

castExpr: arrowExpr ('cast' 'as' singleType)? ;

arrowExpr: unaryExpression (ARROW arrowFunctionSpecifier argumentList)* ;

unaryExpression: ('-' | '+')* valueExpr ;

valueExpr: validateExpr | extensionExpr | simpleMapExpr ;

generalComp: '=' | '!=' | '<' | ('<' '=') | '>' | ('>' '=') ;

valueComp: 'eq' | 'ne' | 'lt' | 'le' | 'gt' | 'ge' ;

nodeComp: 'is' | ('<' '<') | ('>' '>') ;

validateExpr: 'validate' ( validationMode | ( ( 'type' | 'as' ) typeName) )? enclosedExpression ;

validationMode: 'lax' | 'strict' ;

extensionExpr: PRAGMA+ '{' expr '}' ;

simpleMapExpr: pathExpr ('!' pathExpr)* ;

// PATHS ///////////////////////////////////////////////////////////////////////

pathExpr: ('/' relativePathExpr?) | ('//' relativePathExpr) | relativePathExpr ;

relativePathExpr: stepExpr (sep=('/'|'//') stepExpr)* ;

stepExpr: postfixExpr | axisStep ;

axisStep: (reverseStep | forwardStep) predicateList ;

forwardStep: forwardAxis nodeTest | abbrevForwardStep ;

forwardAxis: ( 'child'
             | 'descendant'
             | 'attribute'
             | 'self'
             | 'descendant-or-self'
             | 'following-sibling'
             | 'following' ) ':' ':' ;

abbrevForwardStep: '@'? nodeTest ;

reverseStep: reverseAxis nodeTest | abbrevReverseStep ;

reverseAxis: ( 'parent'
             | 'ancestor'
             | 'preceding-sibling'
             | 'preceding'
             | 'ancestor-or-self' ) ':' ':';

abbrevReverseStep: '..' ;

nodeTest: nameTest | kindTest ;

nameTest: eqName | wildcard ;

wildcard: '*'            # allNames
        | NCNameWithLocalWildcard  # allWithNS    // walkers must strip out the trailing :*
        | NCNameWithPrefixWildcard # allWithLocal // walkers must strip out the leading *:
        ;


postfixExpr: primaryExpr (predicate | argumentList | lookup)* ;

argumentList: '(' (argument (COMMA argument)*)? ')' ;

predicateList: predicate*;

predicate: '[' expr ']' ;

lookup: '?' keySpecifier ;

keySpecifier: ncName | IntegerLiteral | parenthesizedExpr | '*' ;

arrowFunctionSpecifier: eqName | varRef | parenthesizedExpr ;

primaryExpr: literal
           | varRef
           | parenthesizedExpr
           | contextItemExpr
           | functionCall
           | orderedExpr
           | unorderedExpr
           | nodeConstructor
           | functionItemExpr
           | mapConstructor
           | arrayConstructor
//           | stringConstructor
           | unaryLookup
           ;

literal: numericLiteral | stringLiteral ;

numericLiteral: IntegerLiteral | DecimalLiteral | DoubleLiteral ;

varRef: '$' eqName;

varName: eqName ;

parenthesizedExpr: '(' expr? ')' ;

contextItemExpr: '.' ;

orderedExpr: 'ordered' enclosedExpression ;

unorderedExpr: 'unordered' enclosedExpression ;

functionCall: eqName argumentList  ;

argument: exprSingle | '?' ;

// CONSTRUCTORS ////////////////////////////////////////////////////////////////

nodeConstructor: directConstructor | computedConstructor ;

directConstructor: dirElemConstructorOpenClose
                 | dirElemConstructorSingleTag
                 | (COMMENT | PI)
                 ;

// [96]: we don't check that the closing tag is the same here. It should be
// done elsewhere, if we really want to know. We've also simplified the rule
// by removing the S? bits from ws:explicit. Tree walkers could handle this.
dirElemConstructorOpenClose: '<' openName=qName dirAttributeList endOpen='>'
                             dirElemContent*
                             startClose='<' slashClose='/' closeName=qName '>' ;

dirElemConstructorSingleTag: '<' openName=qName dirAttributeList slashClose='/' '>' ;

// [97]: again, ws:explicit is better handled through the walker.
dirAttributeList: (qName '=' dirAttributeValue)* ;

dirAttributeValue: '"'  ( commonContent
                        | '"' '"'
                        // ~["{}<&] = ' + ~['"{}<&]
                        | Apos 
                        | noQuotesNoBracesNoAmpNoLAng
                        )*
                   '"'
                 | '\'' (commonContent
                        | '\'' '\''
                        // ~['{}<&] = " + ~['"{}<&"]
                        | Quot
                        | noQuotesNoBracesNoAmpNoLAng
                        )*
                   '\''
                 ;

dirElemContent: directConstructor
              | commonContent
              | CDATA
              // ~[{}<&] = '" + ~['"{}<&]
              | Quot
              | Apos
              | noQuotesNoBracesNoAmpNoLAng
              ;

commonContent: (PredefinedEntityRef | CharRef) | '{' '{' | '}' '}' | '{' expr '}' ;

computedConstructor: compDocConstructor
                   | compElemConstructor
                   | compAttrConstructor
                   | compNamespaceConstructor
                   | compTextConstructor
                   | compCommentConstructor
                   | compPIConstructor
                   | compMLJSONConstructor
                   ;

compMLJSONConstructor: compMLJSONArrayConstructor
                     | compMLJSONObjectConstructor
                     | compMLJSONNumberConstructor
                     | compMLJSONBooleanConstructor
                     | compMLJSONNullConstructor
                     ;

compMLJSONArrayConstructor: 'array-node' '{' expr '}' ;
compMLJSONObjectConstructor: 'object-node' '{' exprSingle COLON exprSingle (COMMA exprSingle COLON exprSingle)* '}' ;
compMLJSONNumberConstructor: 'number-node' '{' exprSingle '}' ;
compMLJSONBooleanConstructor: 'boolean-node' '{' exprSingle '}' ;
compMLJSONNullConstructor: 'null-node' '{' '}' ;

compBinaryConstructor: 'binary' enclosedContentExpr ;


compDocConstructor: 'document' enclosedExpression ;

compElemConstructor: 'element' ( eqName |('{' expr '}')) enclosedContentExpr ;

enclosedContentExpr: enclosedExpression ;

compAttrConstructor: 'attribute' (eqName | ('{' expr '}')) enclosedExpression ;

compNamespaceConstructor: 'namespace' (prefix | enclosedPrefixExpr) enclosedURIExpr ;

prefix: ncName ;

enclosedPrefixExpr: enclosedExpression ;

enclosedURIExpr: enclosedExpression ;

compTextConstructor: 'text' enclosedExpression ;

compCommentConstructor: 'comment' enclosedExpression ;

compPIConstructor: 'processing-instruction' (ncName | ('{' expr '}')) enclosedExpression ;

functionItemExpr: namedFunctionRef | inlineFunctionRef ;

namedFunctionRef: eqName '#' IntegerLiteral ;

inlineFunctionRef: annotations 'function' '(' functionParams? ')' ('as' sequenceType)? functionBody ;

functionBody: enclosedExpression ;

mapConstructor: 'map' '{' (mapConstructorEntry (',' mapConstructorEntry)*)? '}' ;

mapConstructorEntry: mapKey=exprSingle (COLON | COLON_EQ) mapValue=exprSingle ;

arrayConstructor: squareArrayConstructor | curlyArrayConstructor ;

squareArrayConstructor: '[' (exprSingle (',' exprSingle)*)? ']' ;

curlyArrayConstructor: 'array' enclosedExpression ;

/*
stringConstructor: '`' '`' '[' stringConstructorContent ']' '`' '`' ;

stringConstructorContent: stringConstructorChars (stringConstructorInterpolation stringConstructorChars)* ;

stringConstructorChars: (CHAR* ~ (CHAR* ('`' '{' | ']' '`' '`') CHAR*)) ;

stringConstructorInterpolation: '`' '{' expr '}' '`' ;
*/

unaryLookup: '?' keySpecifier ;

// TYPES AND TYPE TESTS ////////////////////////////////////////////////////////

singleType: simpleTypeName '?'? ;

typeDeclaration: 'as' sequenceType ;

sequenceType: ('empty-sequence' '(' ')') | (itemType occurrence=('?'|'*'|'+')? );

itemType: kindTest
        | ('item' '(' ')')
        | functionTest
        | mapTest
        | arrayTest
        | atomicOrUnionType
        | parenthesizedItemTest ;

atomicOrUnionType: eqName ;

kindTest: documentTest
        | elementTest
        | attributeTest
        | schemaElementTest
        | schemaAttributeTest
        | piTest
        | commentTest
        | textTest
        | namespaceNodeTest
        | mlNodeTest
        | binaryNodeTest
        | anyKindTest
        ;

anyKindTest: 'node' '(' '*'? ')' ;

binaryNodeTest: 'binary' '(' ')' ;

documentTest: 'document-node' '(' (elementTest | schemaElementTest)? ')' ;

textTest: 'text' '(' ')' ;

commentTest: 'comment' '(' ')' ;

namespaceNodeTest: 'namespace-node' '(' ')' ;

piTest: 'processing-instruction' '(' (ncName | stringLiteral)? ')' ;

attributeTest: 'attribute' '(' (attributeNameOrWildcard (',' type=typeName)?)? ')' ;

attributeNameOrWildcard: attributeName | '*' ;

schemaAttributeTest: 'schema-attribute' '(' attributeDeclaration ')' ;

elementTest: 'element' '(' (elementNameOrWildcard (',' typeName optional='?'?)?)? ')' ;

elementNameOrWildcard: elementName | '*' ;

schemaElementTest: 'schema-element' '(' elementDeclaration ')' ;

elementDeclaration: elementName ;

attributeName: eqName ;

elementName: eqName ;

simpleTypeName: typeName ;

typeName: eqName;

functionTest: annotation* (anyFunctionTest | typedFunctionTest) ;

anyFunctionTest: 'function' '(' '*' ')' ;

typedFunctionTest: 'function' '(' (sequenceType (COMMA sequenceType)*)? ')' 'as' sequenceType ;

mapTest: anyMapTest | typedMapTest ;

anyMapTest: 'map' '(' '*' ')' ;

typedMapTest: 'map' '(' eqName COMMA sequenceType ')' ;

arrayTest: anyArrayTest | typedArrayTest ;

anyArrayTest: 'array' '(' '*' ')' ;

typedArrayTest: 'array' '(' sequenceType ')' ;

parenthesizedItemTest: '(' itemType ')' ;

attributeDeclaration: attributeName ;




mlNodeTest: mlArrayNodeTest
          | mlObjectNodeTest
          | mlNumberNodeTest
          | mlBooleanNodeTest
          | mlNullNodeTest
          ;

mlArrayNodeTest: 'array-node' '(' stringLiteral? ')' ;

mlObjectNodeTest: 'object-node' '(' stringLiteral? ')' ;

mlNumberNodeTest: 'number-node' '(' stringLiteral? ')' ;

mlBooleanNodeTest: 'boolean-node' '(' stringLiteral? ')' ;

mlNullNodeTest: 'null-node' '(' stringLiteral? ')' ;

// NAMES ///////////////////////////////////////////////////////////////////////

// walkers need to split into prefix+localpart by the ':'
eqName: qName | URIQualifiedName ;

qName: FullQName | ncName ;


ncName: NCName | keyword ;

functionName: FullQName | NCName | URIQualifiedName | keywordOKForFunction ;

keyword: keywordOKForFunction | keywordNotOKForFunction ;

keywordNotOKForFunction:
         KW_ATTRIBUTE
       | KW_COMMENT
       | KW_DOCUMENT_NODE
       | KW_ELEMENT
       | KW_EMPTY_SEQUENCE
       | KW_IF
       | KW_ITEM
       | KW_CONTEXT
       | KW_NODE
       | KW_PI
       | KW_SCHEMA_ATTR
       | KW_SCHEMA_ELEM
       | KW_BINARY
       | KW_TEXT
       | KW_TYPESWITCH
       | KW_SWITCH
       | KW_NAMESPACE_NODE
       | KW_TYPE
       | KW_TUMBLING
       | KW_TRY
       | KW_CATCH
       | KW_ONLY
       | KW_WHEN
       | KW_SLIDING
       | KW_DECIMAL_FORMAT
       | KW_WINDOW
       | KW_COUNT
       | KW_MAP
       | KW_END
       | KW_ALLOWING
       | KW_ARRAY
       | DFPropertyName
// MarkLogic JSON computed constructor
       | KW_ARRAY_NODE
       | KW_BOOLEAN_NODE
       | KW_NULL_NODE
       | KW_NUMBER_NODE
       | KW_OBJECT_NODE
// eXist-db update keywords
       | KW_UPDATE
       | KW_REPLACE
       | KW_WITH
       | KW_VALUE
       | KW_INSERT
       | KW_INTO
       | KW_DELETE
       | KW_NEXT
       | KW_RENAME
       ;

keywordOKForFunction: KW_ANCESTOR
       | KW_ANCESTOR_OR_SELF
       | KW_AND
       | KW_AS
       | KW_ASCENDING
       | KW_AT
       | KW_BASE_URI
       | KW_BOUNDARY_SPACE
       | KW_BY
       | KW_CASE
       | KW_CAST
       | KW_CASTABLE
       | KW_CHILD
       | KW_COLLATION
       | KW_CONSTRUCTION
       | KW_COPY_NS
       | KW_DECLARE
       | KW_DEFAULT
       | KW_DESCENDANT
       | KW_DESCENDANT_OR_SELF
       | KW_DESCENDING
       | KW_DIV
       | KW_DOCUMENT
       | KW_ELSE
       | KW_EMPTY
       | KW_ENCODING
       | KW_EQ
       | KW_EVERY
       | KW_EXCEPT
       | KW_EXTERNAL
       | KW_FOLLOWING
       | KW_FOLLOWING_SIBLING
       | KW_FOR
       | KW_FUNCTION
       | KW_GE
       | KW_GREATEST
       | KW_GROUP
       | KW_GT
       | KW_IDIV
       | KW_IMPORT
       | KW_IN
       | KW_INHERIT
       | KW_INSTANCE
       | KW_INTERSECT
       | KW_IS
       | KW_LAX
       | KW_LE
       | KW_LEAST
       | KW_LET
       | KW_LT
       | KW_MOD
       | KW_MODULE
       | KW_NAMESPACE
       | KW_NE
       | KW_NO_INHERIT
       | KW_NO_PRESERVE
       | KW_OF
       | KW_OPTION
       | KW_OR
       | KW_ORDER
       | KW_ORDERED
       | KW_ORDERING
       | KW_PARENT
       | KW_PRECEDING
       | KW_PRECEDING_SIBLING
       | KW_PRESERVE
       | KW_RETURN
       | KW_SATISFIES
       | KW_SCHEMA
       | KW_SELF
       | KW_SOME
       | KW_STABLE
       | KW_START
       | KW_STRICT
       | KW_STRIP
       | KW_THEN
       | KW_TO
       | KW_TREAT
       | KW_UNION
       | KW_UNORDERED
       | KW_VALIDATE
       | KW_VARIABLE
       | KW_VERSION
       | KW_WHERE
       | KW_XQUERY
       ;

// STRING LITERALS /////////////////////////////////////////////////////////////

uriLiteral: stringLiteral ;

stringLiteral: '"' ('"' '"'
                   | PredefinedEntityRef
                   | CharRef
                   // ~["&] = '{}< + ~['"{}<&]
                   | Apos
                   | LBRACE
                   | RBRACE
                   | LANGLE
                   | noQuotesNoBracesNoAmpNoLAng
                   // WS and XQComment are in the HIDDEN channel
                   )*
               '"'
             | '\'' ('\'' '\''
                    | PredefinedEntityRef
                    | CharRef
                    // ~['&] = "{}< + ~['"{}<&]
                    | Quot
                    | LBRACE
                    | RBRACE
                    | LANGLE
                    | noQuotesNoBracesNoAmpNoLAng
                    // WS and XQComment are in the HIDDEN channel
                    )* '\''
             ;

// ~['"{}<&]: a very common (and long!) subexpression in the W3C EBNF grammar //

noQuotesNoBracesNoAmpNoLAng:
                   ( keyword
                   | ( IntegerLiteral
                     | DecimalLiteral
                     | DoubleLiteral
                     | PRAGMA
                     | EQUAL
                     | HASH
                     | NOT_EQUAL
                     | LPAREN
                     | RPAREN
                     | LBRACKET
                     | RBRACKET
                     | STAR
                     | PLUS
                     | MINUS
                     | TILDE
                     | COMMA
                     | ARROW
                     | KW_NEXT
                     | KW_PREVIOUS
                     | MOD
                     | DOT
                     | GRAVE
                     | DDOT
                     | XQDOC_COMMENT_START
                     | COLON
                     | CARAT
                     | COLON_EQ
                     | SEMICOLON
                     | SLASH
                     | DSLASH
                     | BACKSLASH
                     | COMMENT
                     | VBAR
                     | RANGLE
                     | QUESTION
                     | AT
                     | DOLLAR
                     | BANG
                     | FullQName
                     | URIQualifiedName
                     | NCNameWithLocalWildcard
                     | NCNameWithPrefixWildcard
                     | NCName
                     | ContentChar
                     )
                   )+
 ;
