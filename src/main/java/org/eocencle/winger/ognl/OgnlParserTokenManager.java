package org.eocencle.winger.ognl;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class OgnlParserTokenManager implements OgnlParserConstants {
	Object literalValue;
	private char charValue;
	private char charLiteralStartQuote;
	private StringBuffer stringBuffer;
	public PrintStream debugStream;
	static final long[] jjbitVec0 = new long[] { 2301339413881290750L, -16384L, 4294967295L, 432345564227567616L };
	static final long[] jjbitVec2 = new long[] { 0L, 0L, 0L, -36028797027352577L };
	static final long[] jjbitVec3 = new long[] { 0L, -1L, -1L, -1L };
	static final long[] jjbitVec4 = new long[] { -1L, -1L, 65535L, 0L };
	static final long[] jjbitVec5 = new long[] { -1L, -1L, 0L, 0L };
	static final long[] jjbitVec6 = new long[] { 70368744177663L, 0L, 0L, 0L };
	static final long[] jjbitVec7 = new long[] { -2L, -1L, -1L, -1L };
	static final long[] jjbitVec8 = new long[] { 0L, 0L, -1L, -1L };
	static final int[] jjnextStates = new int[] { 15, 16, 18, 19, 22, 13, 24, 25, 7, 9, 10, 13, 17, 10, 13, 11, 12, 20,
			21, 1, 2, 3 };
	public static final String[] jjstrLiteralImages = new String[] { "", ",", "=", "?", ":", "||", "or", "&&", "and",
			"|", "bor", "^", "xor", "&", "band", "==", "eq", "!=", "neq", "<", "lt", ">", "gt", "<=", "lte", ">=",
			"gte", "in", "not", "<<", "shl", ">>", "shr", ">>>", "ushr", "+", "-", "*", "/", "%", "~", "!",
			"instanceof", ".", "(", ")", "true", "false", "null", "#this", "#root", "#", "[", "]", "{", "}", "@", "new",
			"$", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null };
	public static final String[] lexStateNames = new String[] { "DEFAULT", "WithinCharLiteral", "WithinBackCharLiteral",
			"WithinStringLiteral" };
	public static final int[] jjnewLexState = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2,
			1, 3, -1, -1, 0, -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1 };
	static final long[] jjtoToken = new long[] { 576460752303423487L, 233993L };
	static final long[] jjtoSkip = new long[] { -576460752303423488L, 0L };
	static final long[] jjtoMore = new long[] { 0L, 28144L };
	protected JavaCharStream input_stream;
	private final int[] jjrounds;
	private final int[] jjstateSet;
	private final StringBuffer image;
	private int jjimageLen;
	private int lengthOfMatch;
	protected char curChar;
	int curLexState;
	int defaultLexState;
	int jjnewStateCnt;
	int jjround;
	int jjmatchedPos;
	int jjmatchedKind;

	private char escapeChar() {
		int ofs = this.image.length() - 1;
		switch (this.image.charAt(ofs)) {
		case '\"':
			return '\"';
		case '\'':
			return '\'';
		case '\\':
			return '\\';
		case 'b':
			return '\b';
		case 'f':
			return '\f';
		case 'n':
			return '\n';
		case 'r':
			return '\r';
		case 't':
			return '\t';
		default:
			do {
				--ofs;
			} while (this.image.charAt(ofs) != 92);

			int value = 0;

			while (true) {
				++ofs;
				if (ofs >= this.image.length()) {
					return (char) value;
				}

				value = value << 3 | this.image.charAt(ofs) - 48;
			}
		}
	}

	private Object makeInt() {
		String s = this.image.toString();
		int base = 10;
		if (s.charAt(0) == 48) {
			base = s.length() <= 1 || s.charAt(1) != 120 && s.charAt(1) != 88 ? 8 : 16;
		}

		if (base == 16) {
			s = s.substring(2);
		}

		Object result;
		switch (s.charAt(s.length() - 1)) {
		case 'H':
		case 'h':
			result = new BigInteger(s.substring(0, s.length() - 1), base);
			break;
		case 'L':
		case 'l':
			result = Long.valueOf(s.substring(0, s.length() - 1), base);
			break;
		default:
			result = Integer.valueOf(s, base);
		}

		return result;
	}

	private Object makeFloat() {
		String s = this.image.toString();
		switch (s.charAt(s.length() - 1)) {
		case 'B':
		case 'b':
			return new BigDecimal(s.substring(0, s.length() - 1));
		case 'D':
		case 'd':
		default:
			return Double.valueOf(s);
		case 'F':
		case 'f':
			return Float.valueOf(s);
		}
	}

	public void setDebugStream(PrintStream ds) {
		this.debugStream = ds;
	}

	private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1) {
		switch (pos) {
		case 0:
			if ((active0 & 144612190372320576L) != 0L) {
				this.jjmatchedKind = 64;
				return 1;
			} else if ((active0 & 288230376151711744L) != 0L) {
				return 1;
			} else if ((active0 & 4503599627370496L) != 0L) {
				return 3;
			} else {
				if ((active0 & 8796093022208L) != 0L) {
					return 9;
				}

				return -1;
			}
		case 1:
			if ((active0 & 144607792102397184L) != 0L) {
				if (this.jjmatchedPos != 1) {
					this.jjmatchedKind = 64;
					this.jjmatchedPos = 1;
				}

				return 1;
			} else {
				if ((active0 & 4398269923392L) != 0L) {
					return 1;
				}

				return -1;
			}
		case 2:
			if ((active0 & 496996435640320L) != 0L) {
				this.jjmatchedKind = 64;
				this.jjmatchedPos = 2;
				return 1;
			} else {
				if ((active0 & 144115193797154048L) != 0L) {
					return 1;
				}

				return -1;
			}
		case 3:
			if ((active0 & 351860900773888L) != 0L) {
				return 1;
			} else {
				if ((active0 & 145135534866432L) != 0L) {
					this.jjmatchedKind = 64;
					this.jjmatchedPos = 3;
					return 1;
				}

				return -1;
			}
		case 4:
			if ((active0 & 140737488355328L) != 0L) {
				return 1;
			} else {
				if ((active0 & 4398046511104L) != 0L) {
					this.jjmatchedKind = 64;
					this.jjmatchedPos = 4;
					return 1;
				}

				return -1;
			}
		case 5:
			if ((active0 & 4398046511104L) != 0L) {
				this.jjmatchedKind = 64;
				this.jjmatchedPos = 5;
				return 1;
			}

			return -1;
		case 6:
			if ((active0 & 4398046511104L) != 0L) {
				this.jjmatchedKind = 64;
				this.jjmatchedPos = 6;
				return 1;
			}

			return -1;
		case 7:
			if ((active0 & 4398046511104L) != 0L) {
				this.jjmatchedKind = 64;
				this.jjmatchedPos = 7;
				return 1;
			}

			return -1;
		case 8:
			if ((active0 & 4398046511104L) != 0L) {
				this.jjmatchedKind = 64;
				this.jjmatchedPos = 8;
				return 1;
			}

			return -1;
		default:
			return -1;
		}
	}

	private final int jjStartNfa_0(int pos, long active0, long active1) {
		return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
	}

	private int jjStopAtPos(int pos, int kind) {
		this.jjmatchedKind = kind;
		this.jjmatchedPos = pos;
		return pos + 1;
	}

	private int jjMoveStringLiteralDfa0_0() {
		switch (this.curChar) {
		case '!':
			this.jjmatchedKind = 41;
			return this.jjMoveStringLiteralDfa1_0(131072L);
		case '\"':
			return this.jjStopAtPos(0, 70);
		case '#':
			this.jjmatchedKind = 51;
			return this.jjMoveStringLiteralDfa1_0(1688849860263936L);
		case '$':
			return this.jjStartNfaWithStates_0(0, 58, 1);
		case '%':
			return this.jjStopAtPos(0, 39);
		case '&':
			this.jjmatchedKind = 13;
			return this.jjMoveStringLiteralDfa1_0(128L);
		case '\'':
			return this.jjStopAtPos(0, 69);
		case '(':
			return this.jjStopAtPos(0, 44);
		case ')':
			return this.jjStopAtPos(0, 45);
		case '*':
			return this.jjStopAtPos(0, 37);
		case '+':
			return this.jjStopAtPos(0, 35);
		case ',':
			return this.jjStopAtPos(0, 1);
		case '-':
			return this.jjStopAtPos(0, 36);
		case '.':
			return this.jjStartNfaWithStates_0(0, 43, 9);
		case '/':
			return this.jjStopAtPos(0, 38);
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case ';':
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
		case '\\':
		case '_':
		case 'c':
		case 'd':
		case 'h':
		case 'j':
		case 'k':
		case 'm':
		case 'p':
		case 'q':
		case 'r':
		case 'v':
		case 'w':
		case 'y':
		case 'z':
		default:
			return this.jjMoveNfa_0(0, 0);
		case ':':
			return this.jjStopAtPos(0, 4);
		case '<':
			this.jjmatchedKind = 19;
			return this.jjMoveStringLiteralDfa1_0(545259520L);
		case '=':
			this.jjmatchedKind = 2;
			return this.jjMoveStringLiteralDfa1_0(32768L);
		case '>':
			this.jjmatchedKind = 21;
			return this.jjMoveStringLiteralDfa1_0(10770972672L);
		case '?':
			return this.jjStopAtPos(0, 3);
		case '@':
			return this.jjStopAtPos(0, 56);
		case '[':
			return this.jjStartNfaWithStates_0(0, 52, 3);
		case ']':
			return this.jjStopAtPos(0, 53);
		case '^':
			return this.jjStopAtPos(0, 11);
		case '`':
			return this.jjStopAtPos(0, 68);
		case 'a':
			return this.jjMoveStringLiteralDfa1_0(256L);
		case 'b':
			return this.jjMoveStringLiteralDfa1_0(17408L);
		case 'e':
			return this.jjMoveStringLiteralDfa1_0(65536L);
		case 'f':
			return this.jjMoveStringLiteralDfa1_0(140737488355328L);
		case 'g':
			return this.jjMoveStringLiteralDfa1_0(71303168L);
		case 'i':
			return this.jjMoveStringLiteralDfa1_0(4398180728832L);
		case 'l':
			return this.jjMoveStringLiteralDfa1_0(17825792L);
		case 'n':
			return this.jjMoveStringLiteralDfa1_0(144396663321264128L);
		case 'o':
			return this.jjMoveStringLiteralDfa1_0(64L);
		case 's':
			return this.jjMoveStringLiteralDfa1_0(5368709120L);
		case 't':
			return this.jjMoveStringLiteralDfa1_0(70368744177664L);
		case 'u':
			return this.jjMoveStringLiteralDfa1_0(17179869184L);
		case 'x':
			return this.jjMoveStringLiteralDfa1_0(4096L);
		case '{':
			return this.jjStopAtPos(0, 54);
		case '|':
			this.jjmatchedKind = 9;
			return this.jjMoveStringLiteralDfa1_0(32L);
		case '}':
			return this.jjStopAtPos(0, 55);
		case '~':
			return this.jjStopAtPos(0, 40);
		}
	}

	private int jjMoveStringLiteralDfa1_0(long active0) {
		try {
			this.curChar = this.input_stream.readChar();
		} catch (IOException arg3) {
			this.jjStopStringLiteralDfa_0(0, active0, 0L);
			return 1;
		}

		switch (this.curChar) {
		case '&':
			if ((active0 & 128L) != 0L) {
				return this.jjStopAtPos(1, 7);
			}
			break;
		case '<':
			if ((active0 & 536870912L) != 0L) {
				return this.jjStopAtPos(1, 29);
			}
			break;
		case '=':
			if ((active0 & 32768L) != 0L) {
				return this.jjStopAtPos(1, 15);
			}

			if ((active0 & 131072L) != 0L) {
				return this.jjStopAtPos(1, 17);
			}

			if ((active0 & 8388608L) != 0L) {
				return this.jjStopAtPos(1, 23);
			}

			if ((active0 & 33554432L) != 0L) {
				return this.jjStopAtPos(1, 25);
			}
			break;
		case '>':
			if ((active0 & 2147483648L) != 0L) {
				this.jjmatchedKind = 31;
				this.jjmatchedPos = 1;
			}

			return this.jjMoveStringLiteralDfa2_0(active0, 8589934592L);
		case 'a':
			return this.jjMoveStringLiteralDfa2_0(active0, 140737488371712L);
		case 'e':
			return this.jjMoveStringLiteralDfa2_0(active0, 144115188076118016L);
		case 'h':
			return this.jjMoveStringLiteralDfa2_0(active0, 5368709120L);
		case 'n':
			if ((active0 & 134217728L) != 0L) {
				this.jjmatchedKind = 27;
				this.jjmatchedPos = 1;
			}

			return this.jjMoveStringLiteralDfa2_0(active0, 4398046511360L);
		case 'o':
			return this.jjMoveStringLiteralDfa2_0(active0, 268440576L);
		case 'q':
			if ((active0 & 65536L) != 0L) {
				return this.jjStartNfaWithStates_0(1, 16, 1);
			}
			break;
		case 'r':
			if ((active0 & 64L) != 0L) {
				return this.jjStartNfaWithStates_0(1, 6, 1);
			}

			return this.jjMoveStringLiteralDfa2_0(active0, 1196268651020288L);
		case 's':
			return this.jjMoveStringLiteralDfa2_0(active0, 17179869184L);
		case 't':
			if ((active0 & 1048576L) != 0L) {
				this.jjmatchedKind = 20;
				this.jjmatchedPos = 1;
			} else if ((active0 & 4194304L) != 0L) {
				this.jjmatchedKind = 22;
				this.jjmatchedPos = 1;
			}

			return this.jjMoveStringLiteralDfa2_0(active0, 562950037307392L);
		case 'u':
			return this.jjMoveStringLiteralDfa2_0(active0, 281474976710656L);
		case '|':
			if ((active0 & 32L) != 0L) {
				return this.jjStopAtPos(1, 5);
			}
		}

		return this.jjStartNfa_0(0, active0, 0L);
	}

	private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
		if ((active0 &= old0) == 0L) {
			return this.jjStartNfa_0(0, old0, 0L);
		} else {
			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg5) {
				this.jjStopStringLiteralDfa_0(1, active0, 0L);
				return 2;
			}

			switch (this.curChar) {
			case '>':
				if ((active0 & 8589934592L) != 0L) {
					return this.jjStopAtPos(2, 33);
				}
				break;
			case 'd':
				if ((active0 & 256L) != 0L) {
					return this.jjStartNfaWithStates_0(2, 8, 1);
				}
				break;
			case 'e':
				if ((active0 & 16777216L) != 0L) {
					return this.jjStartNfaWithStates_0(2, 24, 1);
				}

				if ((active0 & 67108864L) != 0L) {
					return this.jjStartNfaWithStates_0(2, 26, 1);
				}
				break;
			case 'h':
				return this.jjMoveStringLiteralDfa3_0(active0, 562967133290496L);
			case 'l':
				if ((active0 & 1073741824L) != 0L) {
					return this.jjStartNfaWithStates_0(2, 30, 1);
				}

				return this.jjMoveStringLiteralDfa3_0(active0, 422212465065984L);
			case 'n':
				return this.jjMoveStringLiteralDfa3_0(active0, 16384L);
			case 'o':
				return this.jjMoveStringLiteralDfa3_0(active0, 1125899906842624L);
			case 'q':
				if ((active0 & 262144L) != 0L) {
					return this.jjStartNfaWithStates_0(2, 18, 1);
				}
				break;
			case 'r':
				if ((active0 & 1024L) != 0L) {
					return this.jjStartNfaWithStates_0(2, 10, 1);
				}

				if ((active0 & 4096L) != 0L) {
					return this.jjStartNfaWithStates_0(2, 12, 1);
				}

				if ((active0 & 4294967296L) != 0L) {
					return this.jjStartNfaWithStates_0(2, 32, 1);
				}
				break;
			case 's':
				return this.jjMoveStringLiteralDfa3_0(active0, 4398046511104L);
			case 't':
				if ((active0 & 268435456L) != 0L) {
					return this.jjStartNfaWithStates_0(2, 28, 1);
				}
				break;
			case 'u':
				return this.jjMoveStringLiteralDfa3_0(active0, 70368744177664L);
			case 'w':
				if ((active0 & 144115188075855872L) != 0L) {
					return this.jjStartNfaWithStates_0(2, 57, 1);
				}
			}

			return this.jjStartNfa_0(1, active0, 0L);
		}
	}

	private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
		if ((active0 &= old0) == 0L) {
			return this.jjStartNfa_0(1, old0, 0L);
		} else {
			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg5) {
				this.jjStopStringLiteralDfa_0(2, active0, 0L);
				return 3;
			}

			switch (this.curChar) {
			case 'd':
				if ((active0 & 16384L) != 0L) {
					return this.jjStartNfaWithStates_0(3, 14, 1);
				}
				break;
			case 'e':
				if ((active0 & 70368744177664L) != 0L) {
					return this.jjStartNfaWithStates_0(3, 46, 1);
				}
			case 'f':
			case 'g':
			case 'h':
			case 'j':
			case 'k':
			case 'm':
			case 'n':
			case 'p':
			case 'q':
			default:
				break;
			case 'i':
				return this.jjMoveStringLiteralDfa4_0(active0, 562949953421312L);
			case 'l':
				if ((active0 & 281474976710656L) != 0L) {
					return this.jjStartNfaWithStates_0(3, 48, 1);
				}
				break;
			case 'o':
				return this.jjMoveStringLiteralDfa4_0(active0, 1125899906842624L);
			case 'r':
				if ((active0 & 17179869184L) != 0L) {
					return this.jjStartNfaWithStates_0(3, 34, 1);
				}
				break;
			case 's':
				return this.jjMoveStringLiteralDfa4_0(active0, 140737488355328L);
			case 't':
				return this.jjMoveStringLiteralDfa4_0(active0, 4398046511104L);
			}

			return this.jjStartNfa_0(2, active0, 0L);
		}
	}

	private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
		if ((active0 &= old0) == 0L) {
			return this.jjStartNfa_0(2, old0, 0L);
		} else {
			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg5) {
				this.jjStopStringLiteralDfa_0(3, active0, 0L);
				return 4;
			}

			switch (this.curChar) {
			case 'a':
				return this.jjMoveStringLiteralDfa5_0(active0, 4398046511104L);
			case 'e':
				if ((active0 & 140737488355328L) != 0L) {
					return this.jjStartNfaWithStates_0(4, 47, 1);
				}
				break;
			case 's':
				if ((active0 & 562949953421312L) != 0L) {
					return this.jjStopAtPos(4, 49);
				}
				break;
			case 't':
				if ((active0 & 1125899906842624L) != 0L) {
					return this.jjStopAtPos(4, 50);
				}
			}

			return this.jjStartNfa_0(3, active0, 0L);
		}
	}

	private int jjMoveStringLiteralDfa5_0(long old0, long active0) {
		if ((active0 &= old0) == 0L) {
			return this.jjStartNfa_0(3, old0, 0L);
		} else {
			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg5) {
				this.jjStopStringLiteralDfa_0(4, active0, 0L);
				return 5;
			}

			switch (this.curChar) {
			case 'n':
				return this.jjMoveStringLiteralDfa6_0(active0, 4398046511104L);
			default:
				return this.jjStartNfa_0(4, active0, 0L);
			}
		}
	}

	private int jjMoveStringLiteralDfa6_0(long old0, long active0) {
		if ((active0 &= old0) == 0L) {
			return this.jjStartNfa_0(4, old0, 0L);
		} else {
			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg5) {
				this.jjStopStringLiteralDfa_0(5, active0, 0L);
				return 6;
			}

			switch (this.curChar) {
			case 'c':
				return this.jjMoveStringLiteralDfa7_0(active0, 4398046511104L);
			default:
				return this.jjStartNfa_0(5, active0, 0L);
			}
		}
	}

	private int jjMoveStringLiteralDfa7_0(long old0, long active0) {
		if ((active0 &= old0) == 0L) {
			return this.jjStartNfa_0(5, old0, 0L);
		} else {
			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg5) {
				this.jjStopStringLiteralDfa_0(6, active0, 0L);
				return 7;
			}

			switch (this.curChar) {
			case 'e':
				return this.jjMoveStringLiteralDfa8_0(active0, 4398046511104L);
			default:
				return this.jjStartNfa_0(6, active0, 0L);
			}
		}
	}

	private int jjMoveStringLiteralDfa8_0(long old0, long active0) {
		if ((active0 &= old0) == 0L) {
			return this.jjStartNfa_0(6, old0, 0L);
		} else {
			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg5) {
				this.jjStopStringLiteralDfa_0(7, active0, 0L);
				return 8;
			}

			switch (this.curChar) {
			case 'o':
				return this.jjMoveStringLiteralDfa9_0(active0, 4398046511104L);
			default:
				return this.jjStartNfa_0(7, active0, 0L);
			}
		}
	}

	private int jjMoveStringLiteralDfa9_0(long old0, long active0) {
		if ((active0 &= old0) == 0L) {
			return this.jjStartNfa_0(7, old0, 0L);
		} else {
			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg5) {
				this.jjStopStringLiteralDfa_0(8, active0, 0L);
				return 9;
			}

			switch (this.curChar) {
			case 'f':
				if ((active0 & 4398046511104L) != 0L) {
					return this.jjStartNfaWithStates_0(9, 42, 1);
				}
			default:
				return this.jjStartNfa_0(8, active0, 0L);
			}
		}
	}

	private int jjStartNfaWithStates_0(int pos, int kind, int state) {
		this.jjmatchedKind = kind;
		this.jjmatchedPos = pos;

		try {
			this.curChar = this.input_stream.readChar();
		} catch (IOException arg4) {
			return pos + 1;
		}

		return this.jjMoveNfa_0(state, pos + 1);
	}

	private int jjMoveNfa_0(int startState, int curPos) {
		int startsAt = 0;
		this.jjnewStateCnt = 27;
		int i = 1;
		this.jjstateSet[0] = startState;
		int kind = Integer.MAX_VALUE;

		while (true) {
			if (++this.jjround == Integer.MAX_VALUE) {
				this.ReInitRounds();
			}

			long l;
			if (this.curChar < 64) {
				l = 1L << this.curChar;

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
						if ((287948901175001088L & l) != 0L) {
							this.jjCheckNAddStates(0, 5);
						} else if (this.curChar == 46) {
							this.jjCheckNAdd(9);
						} else if (this.curChar == 36) {
							if (kind > 64) {
								kind = 64;
							}

							this.jjCheckNAdd(1);
						}

						if ((287667426198290432L & l) != 0L) {
							if (kind > 80) {
								kind = 80;
							}

							this.jjCheckNAddTwoStates(6, 7);
						} else if (this.curChar == 48) {
							if (kind > 80) {
								kind = 80;
							}

							this.jjCheckNAddStates(6, 8);
						}
						break;
					case 1:
						if ((287948969894477824L & l) != 0L) {
							if (kind > 64) {
								kind = 64;
							}

							this.jjCheckNAdd(1);
						}
					case 2:
					case 4:
					case 7:
					case 10:
					case 13:
					case 19:
					case 25:
					default:
						break;
					case 3:
						if ((4466765987840L & l) != 0L) {
							this.jjstateSet[this.jjnewStateCnt++] = 4;
						}
						break;
					case 5:
						if ((287667426198290432L & l) != 0L) {
							if (kind > 80) {
								kind = 80;
							}

							this.jjCheckNAddTwoStates(6, 7);
						}
						break;
					case 6:
						if ((287948901175001088L & l) != 0L) {
							if (kind > 80) {
								kind = 80;
							}

							this.jjCheckNAddTwoStates(6, 7);
						}
						break;
					case 8:
						if (this.curChar == 46) {
							this.jjCheckNAdd(9);
						}
						break;
					case 9:
						if ((287948901175001088L & l) != 0L) {
							if (kind > 81) {
								kind = 81;
							}

							this.jjCheckNAddStates(9, 11);
						}
						break;
					case 11:
						if ((43980465111040L & l) != 0L) {
							this.jjCheckNAdd(12);
						}
						break;
					case 12:
						if ((287948901175001088L & l) != 0L) {
							if (kind > 81) {
								kind = 81;
							}

							this.jjCheckNAddTwoStates(12, 13);
						}
						break;
					case 14:
						if ((287948901175001088L & l) != 0L) {
							this.jjCheckNAddStates(0, 5);
						}
						break;
					case 15:
						if ((287948901175001088L & l) != 0L) {
							this.jjCheckNAddTwoStates(15, 16);
						}
						break;
					case 16:
						if (this.curChar == 46) {
							if (kind > 81) {
								kind = 81;
							}

							this.jjCheckNAddStates(12, 14);
						}
						break;
					case 17:
						if ((287948901175001088L & l) != 0L) {
							if (kind > 81) {
								kind = 81;
							}

							this.jjCheckNAddStates(12, 14);
						}
						break;
					case 18:
						if ((287948901175001088L & l) != 0L) {
							this.jjCheckNAddTwoStates(18, 19);
						}
						break;
					case 20:
						if ((43980465111040L & l) != 0L) {
							this.jjCheckNAdd(21);
						}
						break;
					case 21:
						if ((287948901175001088L & l) != 0L) {
							if (kind > 81) {
								kind = 81;
							}

							this.jjCheckNAddTwoStates(21, 13);
						}
						break;
					case 22:
						if ((287948901175001088L & l) != 0L) {
							this.jjCheckNAddTwoStates(22, 13);
						}
						break;
					case 23:
						if (this.curChar == 48) {
							if (kind > 80) {
								kind = 80;
							}

							this.jjCheckNAddStates(6, 8);
						}
						break;
					case 24:
						if ((71776119061217280L & l) != 0L) {
							if (kind > 80) {
								kind = 80;
							}

							this.jjCheckNAddTwoStates(24, 7);
						}
						break;
					case 26:
						if ((287948901175001088L & l) != 0L) {
							if (kind > 80) {
								kind = 80;
							}

							this.jjCheckNAddTwoStates(26, 7);
						}
					}
				} while (i != startsAt);
			} else if (this.curChar < 128) {
				l = 1L << (this.curChar & 63);

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
						if ((576460745995190270L & l) != 0L) {
							if (kind > 64) {
								kind = 64;
							}

							this.jjCheckNAdd(1);
						} else if (this.curChar == 91) {
							this.jjstateSet[this.jjnewStateCnt++] = 3;
						}
						break;
					case 1:
						if ((576460745995190270L & l) != 0L) {
							if (kind > 64) {
								kind = 64;
							}

							this.jjCheckNAdd(1);
						}
						break;
					case 2:
						if (this.curChar == 91) {
							this.jjstateSet[this.jjnewStateCnt++] = 3;
						}
						break;
					case 3:
						if ((1152921505680588800L & l) != 0L) {
							this.jjstateSet[this.jjnewStateCnt++] = 4;
						}
						break;
					case 4:
						if (this.curChar == 93) {
							kind = 67;
						}
					case 5:
					case 6:
					case 8:
					case 9:
					case 11:
					case 12:
					case 14:
					case 15:
					case 16:
					case 17:
					case 18:
					case 20:
					case 21:
					case 22:
					case 23:
					case 24:
					default:
						break;
					case 7:
						if ((18691697676544L & l) != 0L && kind > 80) {
							kind = 80;
						}
						break;
					case 10:
						if ((137438953504L & l) != 0L) {
							this.jjAddStates(15, 16);
						}
						break;
					case 13:
						if ((360777252948L & l) != 0L && kind > 81) {
							kind = 81;
						}
						break;
					case 19:
						if ((137438953504L & l) != 0L) {
							this.jjAddStates(17, 18);
						}
						break;
					case 25:
						if ((72057594054705152L & l) != 0L) {
							this.jjCheckNAdd(26);
						}
						break;
					case 26:
						if ((541165879422L & l) != 0L) {
							if (kind > 80) {
								kind = 80;
							}

							this.jjCheckNAddTwoStates(26, 7);
						}
					}
				} while (i != startsAt);
			} else {
				int e = this.curChar >> 8;
				int i1 = e >> 6;
				long l1 = 1L << (e & 63);
				int i2 = (this.curChar & 255) >> 6;
				long l2 = 1L << (this.curChar & 63);

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
					case 1:
						if (jjCanMove_0(e, i1, i2, l1, l2)) {
							if (kind > 64) {
								kind = 64;
							}

							this.jjCheckNAdd(1);
						}
					}
				} while (i != startsAt);
			}

			if (kind != Integer.MAX_VALUE) {
				this.jjmatchedKind = kind;
				this.jjmatchedPos = curPos;
				kind = Integer.MAX_VALUE;
			}

			++curPos;
			if ((i = this.jjnewStateCnt) == (startsAt = 27 - (this.jjnewStateCnt = startsAt))) {
				return curPos;
			}

			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg14) {
				return curPos;
			}
		}
	}

	private final int jjStopStringLiteralDfa_2(int pos, long active0, long active1) {
		switch (pos) {
		default:
			return -1;
		}
	}

	private final int jjStartNfa_2(int pos, long active0, long active1) {
		return this.jjMoveNfa_2(this.jjStopStringLiteralDfa_2(pos, active0, active1), pos + 1);
	}

	private int jjMoveStringLiteralDfa0_2() {
		switch (this.curChar) {
		case '`':
			return this.jjStopAtPos(0, 76);
		default:
			return this.jjMoveNfa_2(0, 0);
		}
	}

	private int jjMoveNfa_2(int startState, int curPos) {
		int startsAt = 0;
		this.jjnewStateCnt = 6;
		int i = 1;
		this.jjstateSet[0] = startState;
		int kind = Integer.MAX_VALUE;

		while (true) {
			if (++this.jjround == Integer.MAX_VALUE) {
				this.ReInitRounds();
			}

			long l;
			if (this.curChar < 64) {
				l = 1L << this.curChar;

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
						if (kind > 75) {
							kind = 75;
						}
						break;
					case 1:
						if ((566935683072L & l) != 0L && kind > 74) {
							kind = 74;
						}
						break;
					case 2:
						if ((4222124650659840L & l) != 0L) {
							this.jjstateSet[this.jjnewStateCnt++] = 3;
						}
						break;
					case 3:
						if ((71776119061217280L & l) != 0L) {
							if (kind > 74) {
								kind = 74;
							}

							this.jjstateSet[this.jjnewStateCnt++] = 4;
						}
						break;
					case 4:
						if ((71776119061217280L & l) != 0L && kind > 74) {
							kind = 74;
						}
					}
				} while (i != startsAt);
			} else if (this.curChar < 128) {
				l = 1L << (this.curChar & 63);

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
						if ((-4563402753L & l) != 0L) {
							if (kind > 75) {
								kind = 75;
							}
						} else if (this.curChar == 92) {
							this.jjAddStates(19, 21);
						}
						break;
					case 1:
						if ((5700164899569664L & l) != 0L && kind > 74) {
							kind = 74;
						}
						break;
					case 5:
						if ((-4563402753L & l) != 0L && kind > 75) {
							kind = 75;
						}
					}
				} while (i != startsAt);
			} else {
				int e = this.curChar >> 8;
				int i1 = e >> 6;
				long l1 = 1L << (e & 63);
				int i2 = (this.curChar & 255) >> 6;
				long l2 = 1L << (this.curChar & 63);

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
						if (jjCanMove_1(e, i1, i2, l1, l2) && kind > 75) {
							kind = 75;
						}
					}
				} while (i != startsAt);
			}

			if (kind != Integer.MAX_VALUE) {
				this.jjmatchedKind = kind;
				this.jjmatchedPos = curPos;
				kind = Integer.MAX_VALUE;
			}

			++curPos;
			if ((i = this.jjnewStateCnt) == (startsAt = 6 - (this.jjnewStateCnt = startsAt))) {
				return curPos;
			}

			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg14) {
				return curPos;
			}
		}
	}

	private final int jjStopStringLiteralDfa_1(int pos, long active0, long active1) {
		switch (pos) {
		default:
			return -1;
		}
	}

	private final int jjStartNfa_1(int pos, long active0, long active1) {
		return this.jjMoveNfa_1(this.jjStopStringLiteralDfa_1(pos, active0, active1), pos + 1);
	}

	private int jjMoveStringLiteralDfa0_1() {
		switch (this.curChar) {
		case '\'':
			return this.jjStopAtPos(0, 73);
		default:
			return this.jjMoveNfa_1(0, 0);
		}
	}

	private int jjMoveNfa_1(int startState, int curPos) {
		int startsAt = 0;
		this.jjnewStateCnt = 6;
		int i = 1;
		this.jjstateSet[0] = startState;
		int kind = Integer.MAX_VALUE;

		while (true) {
			if (++this.jjround == Integer.MAX_VALUE) {
				this.ReInitRounds();
			}

			long l;
			if (this.curChar < 64) {
				l = 1L << this.curChar;

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
						if ((-549755813889L & l) != 0L && kind > 72) {
							kind = 72;
						}
						break;
					case 1:
						if ((566935683072L & l) != 0L && kind > 71) {
							kind = 71;
						}
						break;
					case 2:
						if ((4222124650659840L & l) != 0L) {
							this.jjstateSet[this.jjnewStateCnt++] = 3;
						}
						break;
					case 3:
						if ((71776119061217280L & l) != 0L) {
							if (kind > 71) {
								kind = 71;
							}

							this.jjstateSet[this.jjnewStateCnt++] = 4;
						}
						break;
					case 4:
						if ((71776119061217280L & l) != 0L && kind > 71) {
							kind = 71;
						}
					}
				} while (i != startsAt);
			} else if (this.curChar < 128) {
				l = 1L << (this.curChar & 63);

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
						if ((-268435457L & l) != 0L) {
							if (kind > 72) {
								kind = 72;
							}
						} else if (this.curChar == 92) {
							this.jjAddStates(19, 21);
						}
						break;
					case 1:
						if ((5700164899569664L & l) != 0L && kind > 71) {
							kind = 71;
						}
						break;
					case 5:
						if ((-268435457L & l) != 0L && kind > 72) {
							kind = 72;
						}
					}
				} while (i != startsAt);
			} else {
				int e = this.curChar >> 8;
				int i1 = e >> 6;
				long l1 = 1L << (e & 63);
				int i2 = (this.curChar & 255) >> 6;
				long l2 = 1L << (this.curChar & 63);

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
						if (jjCanMove_1(e, i1, i2, l1, l2) && kind > 72) {
							kind = 72;
						}
					}
				} while (i != startsAt);
			}

			if (kind != Integer.MAX_VALUE) {
				this.jjmatchedKind = kind;
				this.jjmatchedPos = curPos;
				kind = Integer.MAX_VALUE;
			}

			++curPos;
			if ((i = this.jjnewStateCnt) == (startsAt = 6 - (this.jjnewStateCnt = startsAt))) {
				return curPos;
			}

			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg14) {
				return curPos;
			}
		}
	}

	private final int jjStopStringLiteralDfa_3(int pos, long active0, long active1) {
		switch (pos) {
		default:
			return -1;
		}
	}

	private final int jjStartNfa_3(int pos, long active0, long active1) {
		return this.jjMoveNfa_3(this.jjStopStringLiteralDfa_3(pos, active0, active1), pos + 1);
	}

	private int jjMoveStringLiteralDfa0_3() {
		switch (this.curChar) {
		case '\"':
			return this.jjStopAtPos(0, 79);
		default:
			return this.jjMoveNfa_3(0, 0);
		}
	}

	private int jjMoveNfa_3(int startState, int curPos) {
		int startsAt = 0;
		this.jjnewStateCnt = 6;
		int i = 1;
		this.jjstateSet[0] = startState;
		int kind = Integer.MAX_VALUE;

		while (true) {
			if (++this.jjround == Integer.MAX_VALUE) {
				this.ReInitRounds();
			}

			long l;
			if (this.curChar < 64) {
				l = 1L << this.curChar;

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
						if ((-17179869185L & l) != 0L && kind > 78) {
							kind = 78;
						}
						break;
					case 1:
						if ((566935683072L & l) != 0L && kind > 77) {
							kind = 77;
						}
						break;
					case 2:
						if ((4222124650659840L & l) != 0L) {
							this.jjstateSet[this.jjnewStateCnt++] = 3;
						}
						break;
					case 3:
						if ((71776119061217280L & l) != 0L) {
							if (kind > 77) {
								kind = 77;
							}

							this.jjstateSet[this.jjnewStateCnt++] = 4;
						}
						break;
					case 4:
						if ((71776119061217280L & l) != 0L && kind > 77) {
							kind = 77;
						}
					}
				} while (i != startsAt);
			} else if (this.curChar < 128) {
				l = 1L << (this.curChar & 63);

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
						if ((-268435457L & l) != 0L) {
							if (kind > 78) {
								kind = 78;
							}
						} else if (this.curChar == 92) {
							this.jjAddStates(19, 21);
						}
						break;
					case 1:
						if ((5700164899569664L & l) != 0L && kind > 77) {
							kind = 77;
						}
						break;
					case 5:
						if ((-268435457L & l) != 0L && kind > 78) {
							kind = 78;
						}
					}
				} while (i != startsAt);
			} else {
				int e = this.curChar >> 8;
				int i1 = e >> 6;
				long l1 = 1L << (e & 63);
				int i2 = (this.curChar & 255) >> 6;
				long l2 = 1L << (this.curChar & 63);

				do {
					--i;
					switch (this.jjstateSet[i]) {
					case 0:
						if (jjCanMove_1(e, i1, i2, l1, l2) && kind > 78) {
							kind = 78;
						}
					}
				} while (i != startsAt);
			}

			if (kind != Integer.MAX_VALUE) {
				this.jjmatchedKind = kind;
				this.jjmatchedPos = curPos;
				kind = Integer.MAX_VALUE;
			}

			++curPos;
			if ((i = this.jjnewStateCnt) == (startsAt = 6 - (this.jjnewStateCnt = startsAt))) {
				return curPos;
			}

			try {
				this.curChar = this.input_stream.readChar();
			} catch (IOException arg14) {
				return curPos;
			}
		}
	}

	private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
		switch (hiByte) {
		case 0:
			return (jjbitVec2[i2] & l2) != 0L;
		case 48:
			return (jjbitVec3[i2] & l2) != 0L;
		case 49:
			return (jjbitVec4[i2] & l2) != 0L;
		case 51:
			return (jjbitVec5[i2] & l2) != 0L;
		case 61:
			return (jjbitVec6[i2] & l2) != 0L;
		default:
			return (jjbitVec0[i1] & l1) != 0L;
		}
	}

	private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
		switch (hiByte) {
		case 0:
			return (jjbitVec8[i2] & l2) != 0L;
		default:
			return (jjbitVec7[i1] & l1) != 0L;
		}
	}

	public OgnlParserTokenManager(JavaCharStream stream) {
		this.debugStream = System.out;
		this.jjrounds = new int[27];
		this.jjstateSet = new int[54];
		this.image = new StringBuffer();
		this.curLexState = 0;
		this.defaultLexState = 0;
		this.input_stream = stream;
	}

	public OgnlParserTokenManager(JavaCharStream stream, int lexState) {
		this(stream);
		this.SwitchTo(lexState);
	}

	public void ReInit(JavaCharStream stream) {
		this.jjmatchedPos = this.jjnewStateCnt = 0;
		this.curLexState = this.defaultLexState;
		this.input_stream = stream;
		this.ReInitRounds();
	}

	private void ReInitRounds() {
		this.jjround = -2147483647;

		for (int i = 27; i-- > 0; this.jjrounds[i] = Integer.MIN_VALUE) {
			;
		}

	}

	public void ReInit(JavaCharStream stream, int lexState) {
		this.ReInit(stream);
		this.SwitchTo(lexState);
	}

	public void SwitchTo(int lexState) {
		if (lexState < 4 && lexState >= 0) {
			this.curLexState = lexState;
		} else {
			throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
		}
	}

	protected Token jjFillToken() {
		String im = jjstrLiteralImages[this.jjmatchedKind];
		String tokenImage = im == null ? this.input_stream.GetImage() : im;
		int beginLine = this.input_stream.getBeginLine();
		int beginColumn = this.input_stream.getBeginColumn();
		int endLine = this.input_stream.getEndLine();
		int endColumn = this.input_stream.getEndColumn();
		Token t = Token.newToken(this.jjmatchedKind, tokenImage);
		t.beginLine = beginLine;
		t.endLine = endLine;
		t.beginColumn = beginColumn;
		t.endColumn = endColumn;
		return t;
	}

	public Token getNextToken() {
		int curPos = 0;

		label106: while (true) {
			Token matchedToken;
			try {
				this.curChar = this.input_stream.BeginToken();
			} catch (IOException arg7) {
				this.jjmatchedKind = 0;
				matchedToken = this.jjFillToken();
				return matchedToken;
			}

			this.image.setLength(0);
			this.jjimageLen = 0;

			while (true) {
				switch (this.curLexState) {
				case 0:
					try {
						this.input_stream.backup(0);

						while (this.curChar <= 32 && (4294981120L & 1L << this.curChar) != 0L) {
							this.curChar = this.input_stream.BeginToken();
						}
					} catch (IOException arg10) {
						continue label106;
					}

					this.jjmatchedKind = Integer.MAX_VALUE;
					this.jjmatchedPos = 0;
					curPos = this.jjMoveStringLiteralDfa0_0();
					break;
				case 1:
					this.jjmatchedKind = Integer.MAX_VALUE;
					this.jjmatchedPos = 0;
					curPos = this.jjMoveStringLiteralDfa0_1();
					break;
				case 2:
					this.jjmatchedKind = Integer.MAX_VALUE;
					this.jjmatchedPos = 0;
					curPos = this.jjMoveStringLiteralDfa0_2();
					break;
				case 3:
					this.jjmatchedKind = Integer.MAX_VALUE;
					this.jjmatchedPos = 0;
					curPos = this.jjMoveStringLiteralDfa0_3();
				}

				if (this.jjmatchedKind == Integer.MAX_VALUE) {
					break label106;
				}

				if (this.jjmatchedPos + 1 < curPos) {
					this.input_stream.backup(curPos - this.jjmatchedPos - 1);
				}

				if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
					matchedToken = this.jjFillToken();
					this.TokenLexicalActions(matchedToken);
					if (jjnewLexState[this.jjmatchedKind] != -1) {
						this.curLexState = jjnewLexState[this.jjmatchedKind];
					}

					return matchedToken;
				}

				if ((jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
					if (jjnewLexState[this.jjmatchedKind] != -1) {
						this.curLexState = jjnewLexState[this.jjmatchedKind];
					}
					break;
				}

				this.MoreLexicalActions();
				if (jjnewLexState[this.jjmatchedKind] != -1) {
					this.curLexState = jjnewLexState[this.jjmatchedKind];
				}

				curPos = 0;
				this.jjmatchedKind = Integer.MAX_VALUE;

				try {
					this.curChar = this.input_stream.readChar();
				} catch (IOException arg9) {
					break label106;
				}
			}
		}

		int error_line = this.input_stream.getEndLine();
		int error_column = this.input_stream.getEndColumn();
		String error_after = null;
		boolean EOFSeen = false;

		try {
			this.input_stream.readChar();
			this.input_stream.backup(1);
		} catch (IOException arg8) {
			EOFSeen = true;
			error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
			if (this.curChar != 10 && this.curChar != 13) {
				++error_column;
			} else {
				++error_line;
				error_column = 0;
			}
		}

		if (!EOFSeen) {
			this.input_stream.backup(1);
			error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
		}

		throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
	}

	void MoreLexicalActions() {
		this.jjimageLen += this.lengthOfMatch = this.jjmatchedPos + 1;
		switch (this.jjmatchedKind) {
		case 69:
			this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
			this.jjimageLen = 0;
			this.stringBuffer = new StringBuffer();
			break;
		case 70:
			this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
			this.jjimageLen = 0;
			this.stringBuffer = new StringBuffer();
			break;
		case 71:
			this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
			this.jjimageLen = 0;
			this.charValue = this.escapeChar();
			this.stringBuffer.append(this.charValue);
			break;
		case 72:
			this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
			this.jjimageLen = 0;
			this.charValue = this.image.charAt(this.image.length() - 1);
			this.stringBuffer.append(this.charValue);
		case 73:
		case 76:
		default:
			break;
		case 74:
			this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
			this.jjimageLen = 0;
			this.charValue = this.escapeChar();
			break;
		case 75:
			this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
			this.jjimageLen = 0;
			this.charValue = this.image.charAt(this.image.length() - 1);
			break;
		case 77:
			this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
			this.jjimageLen = 0;
			this.stringBuffer.append(this.escapeChar());
			break;
		case 78:
			this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
			this.jjimageLen = 0;
			this.stringBuffer.append(this.image.charAt(this.image.length() - 1));
		}

	}

	void TokenLexicalActions(Token matchedToken) {
		switch (this.jjmatchedKind) {
		case 67:
			this.image.append(
					this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
			switch (this.image.charAt(1)) {
			case '$':
				this.literalValue = DynamicSubscript.last;
				break;
			case '*':
				this.literalValue = DynamicSubscript.all;
				break;
			case '^':
				this.literalValue = DynamicSubscript.first;
				break;
			case '|':
				this.literalValue = DynamicSubscript.mid;
			}
		case 68:
		case 69:
		case 70:
		case 71:
		case 72:
		case 74:
		case 75:
		case 77:
		case 78:
		default:
			break;
		case 73:
			this.image.append(
					this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
			if (this.stringBuffer.length() == 1) {
				this.literalValue = new Character(this.charValue);
			} else {
				this.literalValue = new String(this.stringBuffer);
			}
			break;
		case 76:
			this.image.append(
					this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
			this.literalValue = new Character(this.charValue);
			break;
		case 79:
			this.image.append(
					this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
			this.literalValue = new String(this.stringBuffer);
			break;
		case 80:
			this.image.append(
					this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
			this.literalValue = this.makeInt();
			break;
		case 81:
			this.image.append(
					this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
			this.literalValue = this.makeFloat();
		}

	}

	private void jjCheckNAdd(int state) {
		if (this.jjrounds[state] != this.jjround) {
			this.jjstateSet[this.jjnewStateCnt++] = state;
			this.jjrounds[state] = this.jjround;
		}

	}

	private void jjAddStates(int start, int end) {
		do {
			this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
		} while (start++ != end);

	}

	private void jjCheckNAddTwoStates(int state1, int state2) {
		this.jjCheckNAdd(state1);
		this.jjCheckNAdd(state2);
	}

	private void jjCheckNAddStates(int start, int end) {
		do {
			this.jjCheckNAdd(jjnextStates[start]);
		} while (start++ != end);

	}
}
