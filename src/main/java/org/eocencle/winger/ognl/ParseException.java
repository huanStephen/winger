package org.eocencle.winger.ognl;

public class ParseException extends Exception {
	protected boolean specialConstructor;
	public Token currentToken;
	public int[][] expectedTokenSequences;
	public String[] tokenImage;
	protected String eol = System.getProperty("line.separator", "\n");

	public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
		super("");
		this.specialConstructor = true;
		this.currentToken = currentTokenVal;
		this.expectedTokenSequences = expectedTokenSequencesVal;
		this.tokenImage = tokenImageVal;
	}

	public ParseException() {
		this.specialConstructor = false;
	}

	public ParseException(String message) {
		super(message);
		this.specialConstructor = false;
	}

	public String getMessage() {
		if (!this.specialConstructor) {
			return super.getMessage();
		} else {
			StringBuffer expected = new StringBuffer();
			int maxSize = 0;

			for (int retval = 0; retval < this.expectedTokenSequences.length; ++retval) {
				if (maxSize < this.expectedTokenSequences[retval].length) {
					maxSize = this.expectedTokenSequences[retval].length;
				}

				for (int tok = 0; tok < this.expectedTokenSequences[retval].length; ++tok) {
					expected.append(this.tokenImage[this.expectedTokenSequences[retval][tok]]).append(' ');
				}

				if (this.expectedTokenSequences[retval][this.expectedTokenSequences[retval].length - 1] != 0) {
					expected.append("...");
				}

				expected.append(this.eol).append("    ");
			}

			String arg5 = "Encountered \"";
			Token arg6 = this.currentToken.next;

			for (int i = 0; i < maxSize; ++i) {
				if (i != 0) {
					arg5 = arg5 + " ";
				}

				if (arg6.kind == 0) {
					arg5 = arg5 + this.tokenImage[0];
					break;
				}

				arg5 = arg5 + " " + this.tokenImage[arg6.kind];
				arg5 = arg5 + " \"";
				arg5 = arg5 + this.add_escapes(arg6.image);
				arg5 = arg5 + " \"";
				arg6 = arg6.next;
			}

			arg5 = arg5 + "\" at line " + this.currentToken.next.beginLine + ", column "
					+ this.currentToken.next.beginColumn;
			arg5 = arg5 + "." + this.eol;
			if (this.expectedTokenSequences.length == 1) {
				arg5 = arg5 + "Was expecting:" + this.eol + "    ";
			} else {
				arg5 = arg5 + "Was expecting one of:" + this.eol + "    ";
			}

			arg5 = arg5 + expected.toString();
			return arg5;
		}
	}

	protected String add_escapes(String str) {
		StringBuffer retval = new StringBuffer();

		for (int i = 0; i < str.length(); ++i) {
			switch (str.charAt(i)) {
			case ' ':
				break;
			case '\b':
				retval.append("\\b");
				break;
			case '\t':
				retval.append("\\t");
				break;
			case '\n':
				retval.append("\\n");
				break;
			case '\f':
				retval.append("\\f");
				break;
			case '\r':
				retval.append("\\r");
				break;
			case '\"':
				retval.append("\\\"");
				break;
			case '\'':
				retval.append("\\\'");
				break;
			case '\\':
				retval.append("\\\\");
				break;
			default:
				char ch;
				if ((ch = str.charAt(i)) >= 32 && ch <= 126) {
					retval.append(ch);
				} else {
					String s = "0000" + Integer.toString(ch, 16);
					retval.append("\\u" + s.substring(s.length() - 4, s.length()));
				}
			}
		}

		return retval.toString();
	}
}
