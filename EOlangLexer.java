// Generated from EOlang.g4 by ANTLR 4.12.0
                                                     //*** https://github.com/antlr/antlr4/blob/master/doc/grammars.md#actions-at-the-grammar-level
import org.antlr.v4.runtime.misc.Interval;
import java.util.*;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class EOlangLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, OB=2, NEWLINE=3, OPEN_PAREN=4, CLOSE_PAREN=5, OPEN_BRACK=6, CLOSE_BRACK=7, 
		SKIP_=8, COMMENT=9;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "OB", "NEWLINE", "OPEN_PAREN", "CLOSE_PAREN", "OPEN_BRACK", "CLOSE_BRACK", 
			"SKIP_", "SPACES", "COMMENT", "LINE_JOINING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'>'", null, null, "'('", "')'", "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "OB", "NEWLINE", "OPEN_PAREN", "CLOSE_PAREN", "OPEN_BRACK", 
			"CLOSE_BRACK", "SKIP_", "COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


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




	public EOlangLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "EOlang.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\tg\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0001\u0000"+
		"\u0001\u0000\u0001\u0001\u0001\u0001\u0004\u0001\u001c\b\u0001\u000b\u0001"+
		"\f\u0001\u001d\u0001\u0001\u0005\u0001!\b\u0001\n\u0001\f\u0001$\t\u0001"+
		"\u0001\u0001\u0001\u0001\u0005\u0001(\b\u0001\n\u0001\f\u0001+\t\u0001"+
		"\u0001\u0001\u0003\u0001.\b\u0001\u0001\u0002\u0003\u00021\b\u0002\u0001"+
		"\u0002\u0001\u0002\u0003\u00025\b\u0002\u0001\u0002\u0003\u00028\b\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0003\u0007D\b\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0004\bI\b\b\u000b\b\f\bJ\u0001\t\u0001"+
		"\t\u0005\tO\b\t\n\t\f\tR\t\t\u0001\t\u0001\t\u0005\tV\b\t\n\t\f\tY\t\t"+
		"\u0003\t[\b\t\u0001\n\u0001\n\u0003\n_\b\n\u0001\n\u0003\nb\b\n\u0001"+
		"\n\u0001\n\u0003\nf\b\n\u0001)\u0000\u000b\u0001\u0001\u0003\u0002\u0005"+
		"\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\u0000\u0013"+
		"\t\u0015\u0000\u0001\u0000\u0005\u0003\u0000!!$$@@\u0005\u0000--09AZ_"+
		"_az\u0005\u0000-.0:AZ__az\u0002\u0000\t\t  \u0002\u0000\n\n\f\rt\u0000"+
		"\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000"+
		"\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000"+
		"\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r"+
		"\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0013"+
		"\u0001\u0000\u0000\u0000\u0001\u0017\u0001\u0000\u0000\u0000\u0003-\u0001"+
		"\u0000\u0000\u0000\u00054\u0001\u0000\u0000\u0000\u00079\u0001\u0000\u0000"+
		"\u0000\t;\u0001\u0000\u0000\u0000\u000b=\u0001\u0000\u0000\u0000\r?\u0001"+
		"\u0000\u0000\u0000\u000fC\u0001\u0000\u0000\u0000\u0011H\u0001\u0000\u0000"+
		"\u0000\u0013Z\u0001\u0000\u0000\u0000\u0015\\\u0001\u0000\u0000\u0000"+
		"\u0017\u0018\u0005>\u0000\u0000\u0018\u0002\u0001\u0000\u0000\u0000\u0019"+
		".\u0007\u0000\u0000\u0000\u001a\u001c\u0007\u0001\u0000\u0000\u001b\u001a"+
		"\u0001\u0000\u0000\u0000\u001c\u001d\u0001\u0000\u0000\u0000\u001d\u001b"+
		"\u0001\u0000\u0000\u0000\u001d\u001e\u0001\u0000\u0000\u0000\u001e\"\u0001"+
		"\u0000\u0000\u0000\u001f!\u0007\u0002\u0000\u0000 \u001f\u0001\u0000\u0000"+
		"\u0000!$\u0001\u0000\u0000\u0000\" \u0001\u0000\u0000\u0000\"#\u0001\u0000"+
		"\u0000\u0000#.\u0001\u0000\u0000\u0000$\"\u0001\u0000\u0000\u0000%)\u0005"+
		"\"\u0000\u0000&(\t\u0000\u0000\u0000\'&\u0001\u0000\u0000\u0000(+\u0001"+
		"\u0000\u0000\u0000)*\u0001\u0000\u0000\u0000)\'\u0001\u0000\u0000\u0000"+
		"*,\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000,.\u0005\"\u0000\u0000"+
		"-\u0019\u0001\u0000\u0000\u0000-\u001b\u0001\u0000\u0000\u0000-%\u0001"+
		"\u0000\u0000\u0000.\u0004\u0001\u0000\u0000\u0000/1\u0005\r\u0000\u0000"+
		"0/\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u000012\u0001\u0000\u0000"+
		"\u000025\u0005\n\u0000\u000035\u0002\f\r\u000040\u0001\u0000\u0000\u0000"+
		"43\u0001\u0000\u0000\u000057\u0001\u0000\u0000\u000068\u0003\u0011\b\u0000"+
		"76\u0001\u0000\u0000\u000078\u0001\u0000\u0000\u00008\u0006\u0001\u0000"+
		"\u0000\u00009:\u0005(\u0000\u0000:\b\u0001\u0000\u0000\u0000;<\u0005)"+
		"\u0000\u0000<\n\u0001\u0000\u0000\u0000=>\u0005[\u0000\u0000>\f\u0001"+
		"\u0000\u0000\u0000?@\u0005]\u0000\u0000@\u000e\u0001\u0000\u0000\u0000"+
		"AD\u0003\u0011\b\u0000BD\u0003\u0015\n\u0000CA\u0001\u0000\u0000\u0000"+
		"CB\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EF\u0006\u0007\u0000"+
		"\u0000F\u0010\u0001\u0000\u0000\u0000GI\u0007\u0003\u0000\u0000HG\u0001"+
		"\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000JH\u0001\u0000\u0000\u0000"+
		"JK\u0001\u0000\u0000\u0000K\u0012\u0001\u0000\u0000\u0000LP\u0005#\u0000"+
		"\u0000MO\b\u0004\u0000\u0000NM\u0001\u0000\u0000\u0000OR\u0001\u0000\u0000"+
		"\u0000PN\u0001\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000Q[\u0001\u0000"+
		"\u0000\u0000RP\u0001\u0000\u0000\u0000SW\u0005+\u0000\u0000TV\b\u0004"+
		"\u0000\u0000UT\u0001\u0000\u0000\u0000VY\u0001\u0000\u0000\u0000WU\u0001"+
		"\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000X[\u0001\u0000\u0000\u0000"+
		"YW\u0001\u0000\u0000\u0000ZL\u0001\u0000\u0000\u0000ZS\u0001\u0000\u0000"+
		"\u0000[\u0014\u0001\u0000\u0000\u0000\\^\u0005\\\u0000\u0000]_\u0003\u0011"+
		"\b\u0000^]\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_e\u0001\u0000"+
		"\u0000\u0000`b\u0005\r\u0000\u0000a`\u0001\u0000\u0000\u0000ab\u0001\u0000"+
		"\u0000\u0000bc\u0001\u0000\u0000\u0000cf\u0005\n\u0000\u0000df\u0002\f"+
		"\r\u0000ea\u0001\u0000\u0000\u0000ed\u0001\u0000\u0000\u0000f\u0016\u0001"+
		"\u0000\u0000\u0000\u0010\u0000\u001d\")-047CJPWZ^ae\u0001\u0006\u0000"+
		"\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}