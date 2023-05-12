/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Bart Kiers
 * Copyright (c) 2019 Robert Einhorn
 * Copyright (c) 2023 Maksim Kislov
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * Project      : Python3-parser; an ANTLR4 grammar for Python 3
 *                https://github.com/bkiers/Python3-parser
 * Developed by : Bart Kiers, bart@big-o.nl
 *
 * Project      : an ANTLR4 grammar for Tiny Python with embedded actions
 *                https://github.com/antlr/grammars-v4/tree/master/python/tiny-python/tiny-grammar-with-actions
 * Developed by : Robert Einhorn, robert.einhorn.hu@gmail.com
 */

// Based on the Bart Kiers's ANTLR4 Python 3.3 grammar: https://github.com/bkiers/Python3-parser
// and the Python 3.3.7 Language Reference:             https://docs.python.org/3.3/reference/grammar.html

grammar EOlang; // tiny version

tokens { INDENT, DEDENT }


// this embedded code section will be copied to the generated file: EOlangLexer.java
@lexer::header {                                                     //*** https://github.com/antlr/antlr4/blob/master/doc/grammars.md#actions-at-the-grammar-level
import org.antlr.v4.runtime.misc.Interval;
import java.util.*;
}


// this embedded code section will be copied to the generated file: EOlangLexer.java
@lexer::members {
// The stack that keeps track of the indentation lengths
private Stack<Integer> indentLengths = new Stack<>();
// A linked list where extra tokens are pushed on
private LinkedList<Token> pendingTokens = new LinkedList<>();
// An int that stores the last pending token type (including the inserted INDENT/DEDENT/NEWLINE token types also)
private int lastPendingTokenType;

// The amount of opened braces, brackets and parenthesis
private int opened = 0;

// Was there space char in the indentations?
private boolean wasSpaceIndentation = false;
// Was there tab char in the indentations?
private boolean wasTabIndentation = false;


// Patterns for the custom error listener to recognize error messages
public static final String TEXT_LEXER = "lexer --> ";
public static final String TEXT_INSERTED_INDENT = "inserted INDENT";

@Override
public Token nextToken() {
    if (_input.size() == 0) {
        return new CommonToken(EOF, "<EOF>"); // processing of the input stream until the first returning EOF
    } else {
        checkNextToken();
        return this.pendingTokens.pollFirst(); // append the token stream with the upcoming pending token
    }
}

private void checkNextToken() {
    if (this.indentLengths != null) { // after the first incoming EOF token the indentLengths stack will be set to null
        final int startSize = this.pendingTokens.size();
        Token curToken;
        do {
            curToken = super.nextToken(); // get the next token from the input stream
            checkStartOfInput(curToken);
            switch (curToken.getType()) {
                case OPEN_PAREN:
                case OPEN_BRACK:
                    this.opened++;
                    this.pendingTokens.addLast(curToken);
                    break;
                case CLOSE_PAREN:
                case CLOSE_BRACK:
                    this.opened--;
                    this.pendingTokens.addLast(curToken);
                    break;
                case NEWLINE:
                    handleNewLineToken(curToken);
                    break;
                case EOF:
                    handleEofToken(curToken); // indentLengths stack will be set to null
                    break;
                default:
                    this.pendingTokens.addLast(curToken); // insert the current token
            }
        } while (this.pendingTokens.size() == startSize);
        this.lastPendingTokenType = curToken.getType();
    }
}

private void checkStartOfInput(Token curToken) {
    if (indentLengths.size() == 0) { // We're at the first token
        indentLengths.push(0);  // initialize the stack with default 0 indentation length
        if (_input.getText(new Interval(0, 0)).trim().length() == 0) { // the first char of the input is a whitespace
            this.insertLeadingTokens(curToken.getType(), curToken.getStartIndex());
        }
    }
}

private void handleNewLineToken(Token curToken) {
    if (this.opened == 0) { //*** https://docs.python.org/3/reference/lexical_analysis.html#implicit-line-joining
        switch (_input.LA(1) /* next symbol */) { //*** https://www.antlr.org/api/Java/org/antlr/v4/runtime/IntStream.html#LA(int)
            case '\r':
            case '\n':
            case '\f':
            case EOF:   // skip the trailing inconsistent dedent or the trailing unexpected indent (or the trailing indent)
                return; // We're on a blank line or before a comment or before the EOF, skip the NEWLINE token
            default:
                this.pendingTokens.addLast(curToken); // insert the current NEWLINE token
                this.insertIndentDedentTokens(this.getIndentationLength(curToken.getText())); //*** https://docs.python.org/3/reference/lexical_analysis.html#indentation
        }
    }
}

private void handleEofToken(Token curToken) {
    this.insertTrailingTokens(this.lastPendingTokenType); // indentLengths stack will be null!
    this.pendingTokens.addLast(curToken); // insert the current EOF token
}

private void insertLeadingTokens(int type, int startIndex) {
    if (type != NEWLINE && type != EOF) { // (after a whitespace) The first token is visible, so We insert a NEWLINE and an INDENT token before it to raise an 'unexpected indent' error later by the parser
        this.insertToken(0, startIndex - 1, "<inserted leading NEWLINE>" + " ".repeat(startIndex), NEWLINE, 1, 0);
        this.insertToken(startIndex, startIndex - 1, "<" + TEXT_INSERTED_INDENT + ", " + this.getIndentationDescription(startIndex) + ">", EOlangParser.INDENT, 1, startIndex);
        this.indentLengths.push(startIndex);
    }
}

private void insertIndentDedentTokens(int curIndentLength) {
    int prevIndentLength = this.indentLengths.peek();
    if (curIndentLength > prevIndentLength) { // insert an INDENT token
        this.insertToken("<" + TEXT_INSERTED_INDENT + ", " + this.getIndentationDescription(curIndentLength) + ">", EOlangParser.INDENT);
        this.indentLengths.push(curIndentLength);
    } else {
        while (curIndentLength < prevIndentLength) {   // More than 1 DEDENT token may be inserted
            this.indentLengths.pop();
            prevIndentLength = this.indentLengths.peek();
            if (curIndentLength <= prevIndentLength) {
                this.insertToken("<inserted DEDENT, " + this.getIndentationDescription(prevIndentLength) + ">", EOlangParser.DEDENT);
            } else {
                this.insertToken("<inserted inconsistent DEDENT, " + "length=" + curIndentLength + ">", EOlangParser.DEDENT);
            }
        }
    }
}

private void insertTrailingTokens(int type) {
    if (type != NEWLINE && type != EOlangParser.DEDENT) { // If the last pending token was not a NEWLINE and not a DEDENT then
        this.insertToken("<inserted trailing NEWLINE>", NEWLINE); // insert an extra trailing NEWLINE token that serves as the end of the statement
    }

    while (this.indentLengths.size() > 1) { // Now insert as much trailing DEDENT tokens as needed
        this.insertToken("<inserted trailing DEDENT, " + this.getIndentationDescription(this.indentLengths.pop()) + ">", EOlangParser.DEDENT);
    }
    this.indentLengths = null; // there will be no more token read from the input stream
}

private String getIndentationDescription(int lengthOfIndent) {
    return "length=" + lengthOfIndent + ", level=" + this.indentLengths.size();
}

private void insertToken(String text, int type) {
    final int startIndex = _tokenStartCharIndex + getText().length(); //*** https://www.antlr.org/api/Java/org/antlr/v4/runtime/Lexer.html#_tokenStartCharIndex
    this.insertToken(startIndex, startIndex - 1, text, type, getLine(), getCharPositionInLine());
}

private void insertToken(int startIndex, int stopIndex, String text, int type, int line, int charPositionInLine) {
    CommonToken token = new CommonToken(_tokenFactorySourcePair, type, DEFAULT_TOKEN_CHANNEL, startIndex, stopIndex); //*** https://www.antlr.org/api/Java/org/antlr/v4/runtime/CommonToken.html
    token.setText(text);
    token.setLine(line);
    token.setCharPositionInLine(charPositionInLine);
    this.pendingTokens.addLast(token);
}


// Calculates the indentation of the provided spaces, taking the
// following rules into account:
//
// "Tabs are replaced (from left to right) by one to eight spaces
//  such that the total number of characters up to and including
//  the replacement is a multiple of eight [...]"
//
//  -- https://docs.python.org/3.1/reference/lexical_analysis.html#indentation
private int getIndentationLength(String textOfMatchedNEWLINE) {
    int count = 0;
    for (char ch : textOfMatchedNEWLINE.toCharArray()) {
        switch (ch) {
            case ' ': // A New BLock
                this.wasSpaceIndentation = true;
                count++;
                break;
            case '\t':
                this.wasTabIndentation = true;
                count += 8 - (count % 8);
                break;
        }
    }
    return count;
}


}

/*
 * parser rules
 */

file_input:  (NEWLINE | stmt)* EOF;  

stmt: simple_stmt | compound_stmt; 

simple_stmt: expr NEWLINE | COMMENT NEWLINE; 

compound_stmt:  expr suite| COMMENT suite;        
suite: simple_stmt | NEWLINE INDENT stmt+ DEDENT;


expr: '[' expr ']' '>' OB  # abstractExpr
    |  expr '>' OB         # namingExpr
    |'(' expr ')'          # parensExpr
    | OB+                  # objectExpr
    ;


/*
 * lexer rules
 */
 
// Lexer rules
OB  : '@' | '$' | '!'
    | [a-zA-Z0-9_-]+ [a-zA-Z0-9:._-]*
    | '"' .*? '"'        
    ;

NEWLINE
 : ( '\r'? '\n' | '\r' | '\f' ) SPACES?
 ;

OPEN_PAREN : '(';
CLOSE_PAREN : ')';
OPEN_BRACK : '[';
CLOSE_BRACK : ']';

SKIP_
 : ( SPACES | LINE_JOINING ) -> skip
 ;

/* 
 * fragments 
 */


fragment SPACES
 : [ \t]+
 ;
 
COMMENT
 : '#'~[\r\n\f]*
 | '+'~[\r\n\f]*
 ;

fragment LINE_JOINING
 : '\\' SPACES? ( '\r'? '\n' | '\r' | '\f' )
 ;

