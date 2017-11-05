package org.eocencle.winger.ognl;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OgnlParser implements OgnlParserTreeConstants, OgnlParserConstants {
	protected JJTOgnlParserState jjtree;
	public OgnlParserTokenManager token_source;
	JavaCharStream jj_input_stream;
	public Token token;
	public Token jj_nt;
	private int jj_ntk;
	private Token jj_scanpos;
	private Token jj_lastpos;
	private int jj_la;
	private boolean jj_lookingAhead;
	private boolean jj_semLA;
	private int jj_gen;
	private final int[] jj_la1;
	private static int[] jj_la1_0;
	private static int[] jj_la1_1;
	private static int[] jj_la1_2;
	private final OgnlParser.JJCalls[] jj_2_rtns;
	private boolean jj_rescan;
	private int jj_gc;
	private final OgnlParser.LookaheadSuccess jj_ls;
	private List jj_expentries;
	private int[] jj_expentry;
	private int jj_kind;
	private int[] jj_lasttokens;
	private int jj_endpos;

	public final Node topLevelExpression() throws ParseException {
		this.expression();
		this.jj_consume_token(0);
		return this.jjtree.rootNode();
	}

	public final void expression() throws ParseException {
		this.assignmentExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 1:
				this.jj_consume_token(1);
				ASTSequence jjtn001 = new ASTSequence(1);
				boolean jjtc001 = true;
				this.jjtree.openNodeScope(jjtn001);

				try {
					this.assignmentExpression();
					break;
				} catch (Throwable arg6) {
					if (jjtc001) {
						this.jjtree.clearNodeScope(jjtn001);
						jjtc001 = false;
					} else {
						this.jjtree.popNode();
					}

					if (arg6 instanceof RuntimeException) {
						throw (RuntimeException) arg6;
					}

					if (arg6 instanceof ParseException) {
						throw (ParseException) arg6;
					}

					throw (Error) arg6;
				} finally {
					if (jjtc001) {
						this.jjtree.closeNodeScope(jjtn001, 2);
					}

				}
			default:
				this.jj_la1[0] = this.jj_gen;
				return;
			}
		}
	}

	public final void assignmentExpression() throws ParseException {
		this.conditionalTestExpression();
		switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
		case 2:
			this.jj_consume_token(2);
			ASTAssign jjtn001 = new ASTAssign(2);
			boolean jjtc001 = true;
			this.jjtree.openNodeScope(jjtn001);

			try {
				this.assignmentExpression();
				break;
			} catch (Throwable arg6) {
				if (jjtc001) {
					this.jjtree.clearNodeScope(jjtn001);
					jjtc001 = false;
				} else {
					this.jjtree.popNode();
				}

				if (arg6 instanceof RuntimeException) {
					throw (RuntimeException) arg6;
				}

				if (arg6 instanceof ParseException) {
					throw (ParseException) arg6;
				}

				throw (Error) arg6;
			} finally {
				if (jjtc001) {
					this.jjtree.closeNodeScope(jjtn001, 2);
				}

			}
		default:
			this.jj_la1[1] = this.jj_gen;
		}

	}

	public final void conditionalTestExpression() throws ParseException {
		this.logicalOrExpression();
		switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
		case 3:
			this.jj_consume_token(3);
			this.conditionalTestExpression();
			this.jj_consume_token(4);
			ASTTest jjtn001 = new ASTTest(3);
			boolean jjtc001 = true;
			this.jjtree.openNodeScope(jjtn001);

			try {
				this.conditionalTestExpression();
				break;
			} catch (Throwable arg6) {
				if (jjtc001) {
					this.jjtree.clearNodeScope(jjtn001);
					jjtc001 = false;
				} else {
					this.jjtree.popNode();
				}

				if (arg6 instanceof RuntimeException) {
					throw (RuntimeException) arg6;
				}

				if (arg6 instanceof ParseException) {
					throw (ParseException) arg6;
				}

				throw (Error) arg6;
			} finally {
				if (jjtc001) {
					this.jjtree.closeNodeScope(jjtn001, 3);
				}

			}
		default:
			this.jj_la1[2] = this.jj_gen;
		}

	}

	public final void logicalOrExpression() throws ParseException {
		this.logicalAndExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 5:
			case 6:
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 5:
					this.jj_consume_token(5);
					break;
				case 6:
					this.jj_consume_token(6);
					break;
				default:
					this.jj_la1[4] = this.jj_gen;
					this.jj_consume_token(-1);
					throw new ParseException();
				}

				ASTOr jjtn001 = new ASTOr(4);
				boolean jjtc001 = true;
				this.jjtree.openNodeScope(jjtn001);

				try {
					this.logicalAndExpression();
					break;
				} catch (Throwable arg6) {
					if (jjtc001) {
						this.jjtree.clearNodeScope(jjtn001);
						jjtc001 = false;
					} else {
						this.jjtree.popNode();
					}

					if (arg6 instanceof RuntimeException) {
						throw (RuntimeException) arg6;
					}

					if (arg6 instanceof ParseException) {
						throw (ParseException) arg6;
					}

					throw (Error) arg6;
				} finally {
					if (jjtc001) {
						this.jjtree.closeNodeScope(jjtn001, 2);
					}

				}
			default:
				this.jj_la1[3] = this.jj_gen;
				return;
			}
		}
	}

	public final void logicalAndExpression() throws ParseException {
		this.inclusiveOrExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 7:
			case 8:
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 7:
					this.jj_consume_token(7);
					break;
				case 8:
					this.jj_consume_token(8);
					break;
				default:
					this.jj_la1[6] = this.jj_gen;
					this.jj_consume_token(-1);
					throw new ParseException();
				}

				ASTAnd jjtn001 = new ASTAnd(5);
				boolean jjtc001 = true;
				this.jjtree.openNodeScope(jjtn001);

				try {
					this.inclusiveOrExpression();
					break;
				} catch (Throwable arg6) {
					if (jjtc001) {
						this.jjtree.clearNodeScope(jjtn001);
						jjtc001 = false;
					} else {
						this.jjtree.popNode();
					}

					if (arg6 instanceof RuntimeException) {
						throw (RuntimeException) arg6;
					}

					if (arg6 instanceof ParseException) {
						throw (ParseException) arg6;
					}

					throw (Error) arg6;
				} finally {
					if (jjtc001) {
						this.jjtree.closeNodeScope(jjtn001, 2);
					}

				}
			default:
				this.jj_la1[5] = this.jj_gen;
				return;
			}
		}
	}

	public final void inclusiveOrExpression() throws ParseException {
		this.exclusiveOrExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 9:
			case 10:
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 9:
					this.jj_consume_token(9);
					break;
				case 10:
					this.jj_consume_token(10);
					break;
				default:
					this.jj_la1[8] = this.jj_gen;
					this.jj_consume_token(-1);
					throw new ParseException();
				}

				ASTBitOr jjtn001 = new ASTBitOr(6);
				boolean jjtc001 = true;
				this.jjtree.openNodeScope(jjtn001);

				try {
					this.exclusiveOrExpression();
					break;
				} catch (Throwable arg6) {
					if (jjtc001) {
						this.jjtree.clearNodeScope(jjtn001);
						jjtc001 = false;
					} else {
						this.jjtree.popNode();
					}

					if (arg6 instanceof RuntimeException) {
						throw (RuntimeException) arg6;
					}

					if (arg6 instanceof ParseException) {
						throw (ParseException) arg6;
					}

					throw (Error) arg6;
				} finally {
					if (jjtc001) {
						this.jjtree.closeNodeScope(jjtn001, 2);
					}

				}
			default:
				this.jj_la1[7] = this.jj_gen;
				return;
			}
		}
	}

	public final void exclusiveOrExpression() throws ParseException {
		this.andExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 11:
			case 12:
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 11:
					this.jj_consume_token(11);
					break;
				case 12:
					this.jj_consume_token(12);
					break;
				default:
					this.jj_la1[10] = this.jj_gen;
					this.jj_consume_token(-1);
					throw new ParseException();
				}

				ASTXor jjtn001 = new ASTXor(7);
				boolean jjtc001 = true;
				this.jjtree.openNodeScope(jjtn001);

				try {
					this.andExpression();
					break;
				} catch (Throwable arg6) {
					if (jjtc001) {
						this.jjtree.clearNodeScope(jjtn001);
						jjtc001 = false;
					} else {
						this.jjtree.popNode();
					}

					if (arg6 instanceof RuntimeException) {
						throw (RuntimeException) arg6;
					}

					if (arg6 instanceof ParseException) {
						throw (ParseException) arg6;
					}

					throw (Error) arg6;
				} finally {
					if (jjtc001) {
						this.jjtree.closeNodeScope(jjtn001, 2);
					}

				}
			default:
				this.jj_la1[9] = this.jj_gen;
				return;
			}
		}
	}

	public final void andExpression() throws ParseException {
		this.equalityExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 13:
			case 14:
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 13:
					this.jj_consume_token(13);
					break;
				case 14:
					this.jj_consume_token(14);
					break;
				default:
					this.jj_la1[12] = this.jj_gen;
					this.jj_consume_token(-1);
					throw new ParseException();
				}

				ASTBitAnd jjtn001 = new ASTBitAnd(8);
				boolean jjtc001 = true;
				this.jjtree.openNodeScope(jjtn001);

				try {
					this.equalityExpression();
					break;
				} catch (Throwable arg6) {
					if (jjtc001) {
						this.jjtree.clearNodeScope(jjtn001);
						jjtc001 = false;
					} else {
						this.jjtree.popNode();
					}

					if (arg6 instanceof RuntimeException) {
						throw (RuntimeException) arg6;
					}

					if (arg6 instanceof ParseException) {
						throw (ParseException) arg6;
					}

					throw (Error) arg6;
				} finally {
					if (jjtc001) {
						this.jjtree.closeNodeScope(jjtn001, 2);
					}

				}
			default:
				this.jj_la1[11] = this.jj_gen;
				return;
			}
		}
	}

	public final void equalityExpression() throws ParseException {
		this.relationalExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 15:
			case 16:
			case 17:
			case 18:
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 15:
				case 16:
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 15:
						this.jj_consume_token(15);
						break;
					case 16:
						this.jj_consume_token(16);
						break;
					default:
						this.jj_la1[14] = this.jj_gen;
						this.jj_consume_token(-1);
						throw new ParseException();
					}

					ASTEq jjtn001 = new ASTEq(9);
					boolean jjtc001 = true;
					this.jjtree.openNodeScope(jjtn001);

					try {
						this.relationalExpression();
						continue;
					} catch (Throwable arg16) {
						if (jjtc001) {
							this.jjtree.clearNodeScope(jjtn001);
							jjtc001 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg16 instanceof RuntimeException) {
							throw (RuntimeException) arg16;
						}

						if (arg16 instanceof ParseException) {
							throw (ParseException) arg16;
						}

						throw (Error) arg16;
					} finally {
						if (jjtc001) {
							this.jjtree.closeNodeScope(jjtn001, 2);
						}

					}
				case 17:
				case 18:
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 17:
						this.jj_consume_token(17);
						break;
					case 18:
						this.jj_consume_token(18);
						break;
					default:
						this.jj_la1[15] = this.jj_gen;
						this.jj_consume_token(-1);
						throw new ParseException();
					}

					ASTNotEq jjtn002 = new ASTNotEq(10);
					boolean jjtc002 = true;
					this.jjtree.openNodeScope(jjtn002);

					try {
						this.relationalExpression();
						continue;
					} catch (Throwable arg14) {
						if (jjtc002) {
							this.jjtree.clearNodeScope(jjtn002);
							jjtc002 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg14 instanceof RuntimeException) {
							throw (RuntimeException) arg14;
						}

						if (arg14 instanceof ParseException) {
							throw (ParseException) arg14;
						}

						throw (Error) arg14;
					} finally {
						if (jjtc002) {
							this.jjtree.closeNodeScope(jjtn002, 2);
						}

					}
				default:
					this.jj_la1[16] = this.jj_gen;
					this.jj_consume_token(-1);
					throw new ParseException();
				}
			default:
				this.jj_la1[13] = this.jj_gen;
				return;
			}
		}
	}

	public final void relationalExpression() throws ParseException {
		this.shiftExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 19:
				case 20:
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 19:
						this.jj_consume_token(19);
						break;
					case 20:
						this.jj_consume_token(20);
						break;
					default:
						this.jj_la1[18] = this.jj_gen;
						this.jj_consume_token(-1);
						throw new ParseException();
					}

					ASTLess jjtn001 = new ASTLess(11);
					boolean jjtc001 = true;
					this.jjtree.openNodeScope(jjtn001);

					try {
						this.shiftExpression();
						continue;
					} catch (Throwable arg94) {
						if (jjtc001) {
							this.jjtree.clearNodeScope(jjtn001);
							jjtc001 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg94 instanceof RuntimeException) {
							throw (RuntimeException) arg94;
						}

						if (arg94 instanceof ParseException) {
							throw (ParseException) arg94;
						}

						throw (Error) arg94;
					} finally {
						if (jjtc001) {
							this.jjtree.closeNodeScope(jjtn001, 2);
						}

					}
				case 21:
				case 22:
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 21:
						this.jj_consume_token(21);
						break;
					case 22:
						this.jj_consume_token(22);
						break;
					default:
						this.jj_la1[19] = this.jj_gen;
						this.jj_consume_token(-1);
						throw new ParseException();
					}

					ASTGreater jjtn002 = new ASTGreater(12);
					boolean jjtc002 = true;
					this.jjtree.openNodeScope(jjtn002);

					try {
						this.shiftExpression();
						continue;
					} catch (Throwable arg88) {
						if (jjtc002) {
							this.jjtree.clearNodeScope(jjtn002);
							jjtc002 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg88 instanceof RuntimeException) {
							throw (RuntimeException) arg88;
						}

						if (arg88 instanceof ParseException) {
							throw (ParseException) arg88;
						}

						throw (Error) arg88;
					} finally {
						if (jjtc002) {
							this.jjtree.closeNodeScope(jjtn002, 2);
						}

					}
				case 23:
				case 24:
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 23:
						this.jj_consume_token(23);
						break;
					case 24:
						this.jj_consume_token(24);
						break;
					default:
						this.jj_la1[20] = this.jj_gen;
						this.jj_consume_token(-1);
						throw new ParseException();
					}

					ASTLessEq jjtn003 = new ASTLessEq(13);
					boolean jjtc003 = true;
					this.jjtree.openNodeScope(jjtn003);

					try {
						this.shiftExpression();
						continue;
					} catch (Throwable arg90) {
						if (jjtc003) {
							this.jjtree.clearNodeScope(jjtn003);
							jjtc003 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg90 instanceof RuntimeException) {
							throw (RuntimeException) arg90;
						}

						if (arg90 instanceof ParseException) {
							throw (ParseException) arg90;
						}

						throw (Error) arg90;
					} finally {
						if (jjtc003) {
							this.jjtree.closeNodeScope(jjtn003, 2);
						}

					}
				case 25:
				case 26:
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 25:
						this.jj_consume_token(25);
						break;
					case 26:
						this.jj_consume_token(26);
						break;
					default:
						this.jj_la1[21] = this.jj_gen;
						this.jj_consume_token(-1);
						throw new ParseException();
					}

					ASTGreaterEq jjtn004 = new ASTGreaterEq(14);
					boolean jjtc004 = true;
					this.jjtree.openNodeScope(jjtn004);

					try {
						this.shiftExpression();
						continue;
					} catch (Throwable arg96) {
						if (jjtc004) {
							this.jjtree.clearNodeScope(jjtn004);
							jjtc004 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg96 instanceof RuntimeException) {
							throw (RuntimeException) arg96;
						}

						if (arg96 instanceof ParseException) {
							throw (ParseException) arg96;
						}

						throw (Error) arg96;
					} finally {
						if (jjtc004) {
							this.jjtree.closeNodeScope(jjtn004, 2);
						}

					}
				case 27:
					this.jj_consume_token(27);
					ASTIn jjtn005 = new ASTIn(15);
					boolean jjtc005 = true;
					this.jjtree.openNodeScope(jjtn005);

					try {
						this.shiftExpression();
						continue;
					} catch (Throwable arg86) {
						if (jjtc005) {
							this.jjtree.clearNodeScope(jjtn005);
							jjtc005 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg86 instanceof RuntimeException) {
							throw (RuntimeException) arg86;
						}

						if (arg86 instanceof ParseException) {
							throw (ParseException) arg86;
						}

						throw (Error) arg86;
					} finally {
						if (jjtc005) {
							this.jjtree.closeNodeScope(jjtn005, 2);
						}

					}
				case 28:
					this.jj_consume_token(28);
					this.jj_consume_token(27);
					ASTNotIn jjtn006 = new ASTNotIn(16);
					boolean jjtc006 = true;
					this.jjtree.openNodeScope(jjtn006);

					try {
						this.shiftExpression();
						continue;
					} catch (Throwable arg92) {
						if (jjtc006) {
							this.jjtree.clearNodeScope(jjtn006);
							jjtc006 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg92 instanceof RuntimeException) {
							throw (RuntimeException) arg92;
						}

						if (arg92 instanceof ParseException) {
							throw (ParseException) arg92;
						}

						throw (Error) arg92;
					} finally {
						if (jjtc006) {
							this.jjtree.closeNodeScope(jjtn006, 2);
						}

					}
				default:
					this.jj_la1[22] = this.jj_gen;
					this.jj_consume_token(-1);
					throw new ParseException();
				}
			default:
				this.jj_la1[17] = this.jj_gen;
				return;
			}
		}
	}

	public final void shiftExpression() throws ParseException {
		this.additiveExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 29:
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 29:
				case 30:
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 29:
						this.jj_consume_token(29);
						break;
					case 30:
						this.jj_consume_token(30);
						break;
					default:
						this.jj_la1[24] = this.jj_gen;
						this.jj_consume_token(-1);
						throw new ParseException();
					}

					ASTShiftLeft jjtn001 = new ASTShiftLeft(17);
					boolean jjtc001 = true;
					this.jjtree.openNodeScope(jjtn001);

					try {
						this.additiveExpression();
						continue;
					} catch (Throwable arg26) {
						if (jjtc001) {
							this.jjtree.clearNodeScope(jjtn001);
							jjtc001 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg26 instanceof RuntimeException) {
							throw (RuntimeException) arg26;
						}

						if (arg26 instanceof ParseException) {
							throw (ParseException) arg26;
						}

						throw (Error) arg26;
					} finally {
						if (jjtc001) {
							this.jjtree.closeNodeScope(jjtn001, 2);
						}

					}
				case 31:
				case 32:
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 31:
						this.jj_consume_token(31);
						break;
					case 32:
						this.jj_consume_token(32);
						break;
					default:
						this.jj_la1[25] = this.jj_gen;
						this.jj_consume_token(-1);
						throw new ParseException();
					}

					ASTShiftRight jjtn002 = new ASTShiftRight(18);
					boolean jjtc002 = true;
					this.jjtree.openNodeScope(jjtn002);

					try {
						this.additiveExpression();
						continue;
					} catch (Throwable arg28) {
						if (jjtc002) {
							this.jjtree.clearNodeScope(jjtn002);
							jjtc002 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg28 instanceof RuntimeException) {
							throw (RuntimeException) arg28;
						}

						if (arg28 instanceof ParseException) {
							throw (ParseException) arg28;
						}

						throw (Error) arg28;
					} finally {
						if (jjtc002) {
							this.jjtree.closeNodeScope(jjtn002, 2);
						}

					}
				case 33:
				case 34:
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 33:
						this.jj_consume_token(33);
						break;
					case 34:
						this.jj_consume_token(34);
						break;
					default:
						this.jj_la1[26] = this.jj_gen;
						this.jj_consume_token(-1);
						throw new ParseException();
					}

					ASTUnsignedShiftRight jjtn003 = new ASTUnsignedShiftRight(19);
					boolean jjtc003 = true;
					this.jjtree.openNodeScope(jjtn003);

					try {
						this.additiveExpression();
						continue;
					} catch (Throwable arg30) {
						if (jjtc003) {
							this.jjtree.clearNodeScope(jjtn003);
							jjtc003 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg30 instanceof RuntimeException) {
							throw (RuntimeException) arg30;
						}

						if (arg30 instanceof ParseException) {
							throw (ParseException) arg30;
						}

						throw (Error) arg30;
					} finally {
						if (jjtc003) {
							this.jjtree.closeNodeScope(jjtn003, 2);
						}

					}
				default:
					this.jj_la1[27] = this.jj_gen;
					this.jj_consume_token(-1);
					throw new ParseException();
				}
			default:
				this.jj_la1[23] = this.jj_gen;
				return;
			}
		}
	}

	public final void additiveExpression() throws ParseException {
		this.multiplicativeExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 35:
			case 36:
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 35:
					this.jj_consume_token(35);
					ASTAdd jjtn001 = new ASTAdd(20);
					boolean jjtc001 = true;
					this.jjtree.openNodeScope(jjtn001);

					try {
						this.multiplicativeExpression();
						continue;
					} catch (Throwable arg16) {
						if (jjtc001) {
							this.jjtree.clearNodeScope(jjtn001);
							jjtc001 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg16 instanceof RuntimeException) {
							throw (RuntimeException) arg16;
						}

						if (arg16 instanceof ParseException) {
							throw (ParseException) arg16;
						}

						throw (Error) arg16;
					} finally {
						if (jjtc001) {
							this.jjtree.closeNodeScope(jjtn001, 2);
						}

					}
				case 36:
					this.jj_consume_token(36);
					ASTSubtract jjtn002 = new ASTSubtract(21);
					boolean jjtc002 = true;
					this.jjtree.openNodeScope(jjtn002);

					try {
						this.multiplicativeExpression();
						continue;
					} catch (Throwable arg14) {
						if (jjtc002) {
							this.jjtree.clearNodeScope(jjtn002);
							jjtc002 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg14 instanceof RuntimeException) {
							throw (RuntimeException) arg14;
						}

						if (arg14 instanceof ParseException) {
							throw (ParseException) arg14;
						}

						throw (Error) arg14;
					} finally {
						if (jjtc002) {
							this.jjtree.closeNodeScope(jjtn002, 2);
						}

					}
				default:
					this.jj_la1[29] = this.jj_gen;
					this.jj_consume_token(-1);
					throw new ParseException();
				}
			default:
				this.jj_la1[28] = this.jj_gen;
				return;
			}
		}
	}

	public final void multiplicativeExpression() throws ParseException {
		this.unaryExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 37:
			case 38:
			case 39:
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 37:
					this.jj_consume_token(37);
					ASTMultiply jjtn001 = new ASTMultiply(22);
					boolean jjtc001 = true;
					this.jjtree.openNodeScope(jjtn001);

					try {
						this.unaryExpression();
						continue;
					} catch (Throwable arg28) {
						if (jjtc001) {
							this.jjtree.clearNodeScope(jjtn001);
							jjtc001 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg28 instanceof RuntimeException) {
							throw (RuntimeException) arg28;
						}

						if (arg28 instanceof ParseException) {
							throw (ParseException) arg28;
						}

						throw (Error) arg28;
					} finally {
						if (jjtc001) {
							this.jjtree.closeNodeScope(jjtn001, 2);
						}

					}
				case 38:
					this.jj_consume_token(38);
					ASTDivide jjtn002 = new ASTDivide(23);
					boolean jjtc002 = true;
					this.jjtree.openNodeScope(jjtn002);

					try {
						this.unaryExpression();
						continue;
					} catch (Throwable arg30) {
						if (jjtc002) {
							this.jjtree.clearNodeScope(jjtn002);
							jjtc002 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg30 instanceof RuntimeException) {
							throw (RuntimeException) arg30;
						}

						if (arg30 instanceof ParseException) {
							throw (ParseException) arg30;
						}

						throw (Error) arg30;
					} finally {
						if (jjtc002) {
							this.jjtree.closeNodeScope(jjtn002, 2);
						}

					}
				case 39:
					this.jj_consume_token(39);
					ASTRemainder jjtn003 = new ASTRemainder(24);
					boolean jjtc003 = true;
					this.jjtree.openNodeScope(jjtn003);

					try {
						this.unaryExpression();
						continue;
					} catch (Throwable arg26) {
						if (jjtc003) {
							this.jjtree.clearNodeScope(jjtn003);
							jjtc003 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg26 instanceof RuntimeException) {
							throw (RuntimeException) arg26;
						}

						if (arg26 instanceof ParseException) {
							throw (ParseException) arg26;
						}

						throw (Error) arg26;
					} finally {
						if (jjtc003) {
							this.jjtree.closeNodeScope(jjtn003, 2);
						}

					}
				default:
					this.jj_la1[31] = this.jj_gen;
					this.jj_consume_token(-1);
					throw new ParseException();
				}
			default:
				this.jj_la1[30] = this.jj_gen;
				return;
			}
		}
	}

	public final void unaryExpression() throws ParseException {
		switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
		case 4:
		case 44:
		case 46:
		case 47:
		case 48:
		case 49:
		case 50:
		case 51:
		case 52:
		case 54:
		case 56:
		case 57:
		case 64:
		case 67:
		case 73:
		case 76:
		case 79:
		case 80:
		case 81:
			this.navigationChain();
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 42:
				this.jj_consume_token(42);
				Token t = this.jj_consume_token(64);
				ASTInstanceof jjtn004 = new ASTInstanceof(28);
				boolean jjtc004 = true;
				this.jjtree.openNodeScope(jjtn004);

				StringBuffer sb;
				ASTInstanceof ionode;
				try {
					this.jjtree.closeNodeScope(jjtn004, 1);
					jjtc004 = false;
					sb = new StringBuffer(t.image);
					ionode = jjtn004;
				} finally {
					if (jjtc004) {
						this.jjtree.closeNodeScope(jjtn004, 1);
					}

				}

				while (true) {
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 43:
						this.jj_consume_token(43);
						t = this.jj_consume_token(64);
						sb.append('.').append(t.image);
						break;
					default:
						this.jj_la1[33] = this.jj_gen;
						ionode.setTargetType(new String(sb));
						return;
					}
				}
			default:
				this.jj_la1[34] = this.jj_gen;
				return;
			}
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
		case 25:
		case 26:
		case 27:
		case 29:
		case 30:
		case 31:
		case 32:
		case 33:
		case 34:
		case 37:
		case 38:
		case 39:
		case 42:
		case 43:
		case 45:
		case 53:
		case 55:
		case 58:
		case 59:
		case 60:
		case 61:
		case 62:
		case 63:
		case 65:
		case 66:
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
			this.jj_la1[35] = this.jj_gen;
			this.jj_consume_token(-1);
			throw new ParseException();
		case 28:
		case 41:
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 28:
				this.jj_consume_token(28);
				break;
			case 41:
				this.jj_consume_token(41);
				break;
			default:
				this.jj_la1[32] = this.jj_gen;
				this.jj_consume_token(-1);
				throw new ParseException();
			}

			ASTNot jjtn003 = new ASTNot(27);
			boolean jjtc003 = true;
			this.jjtree.openNodeScope(jjtn003);

			try {
				this.unaryExpression();
				break;
			} catch (Throwable arg41) {
				if (jjtc003) {
					this.jjtree.clearNodeScope(jjtn003);
					jjtc003 = false;
				} else {
					this.jjtree.popNode();
				}

				if (arg41 instanceof RuntimeException) {
					throw (RuntimeException) arg41;
				}

				if (arg41 instanceof ParseException) {
					throw (ParseException) arg41;
				}

				throw (Error) arg41;
			} finally {
				if (jjtc003) {
					this.jjtree.closeNodeScope(jjtn003, 1);
				}

			}
		case 35:
			this.jj_consume_token(35);
			this.unaryExpression();
			break;
		case 36:
			this.jj_consume_token(36);
			ASTNegate jjtn001 = new ASTNegate(25);
			boolean jjtc001 = true;
			this.jjtree.openNodeScope(jjtn001);

			try {
				this.unaryExpression();
				break;
			} catch (Throwable arg45) {
				if (jjtc001) {
					this.jjtree.clearNodeScope(jjtn001);
					jjtc001 = false;
				} else {
					this.jjtree.popNode();
				}

				if (arg45 instanceof RuntimeException) {
					throw (RuntimeException) arg45;
				}

				if (arg45 instanceof ParseException) {
					throw (ParseException) arg45;
				}

				throw (Error) arg45;
			} finally {
				if (jjtc001) {
					this.jjtree.closeNodeScope(jjtn001, 1);
				}

			}
		case 40:
			this.jj_consume_token(40);
			ASTBitNegate jjtn002 = new ASTBitNegate(26);
			boolean jjtc002 = true;
			this.jjtree.openNodeScope(jjtn002);

			try {
				this.unaryExpression();
			} catch (Throwable arg43) {
				if (jjtc002) {
					this.jjtree.clearNodeScope(jjtn002);
					jjtc002 = false;
				} else {
					this.jjtree.popNode();
				}

				if (arg43 instanceof RuntimeException) {
					throw (RuntimeException) arg43;
				}

				if (arg43 instanceof ParseException) {
					throw (ParseException) arg43;
				}

				throw (Error) arg43;
			} finally {
				if (jjtc002) {
					this.jjtree.closeNodeScope(jjtn002, 1);
				}

			}
		}

	}

	public final void navigationChain() throws ParseException {
		this.primaryExpression();

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 43:
			case 44:
			case 52:
			case 67:
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 43:
					this.jj_consume_token(43);
					ASTChain jjtn001 = new ASTChain(29);
					boolean jjtc001 = true;
					this.jjtree.openNodeScope(jjtn001);

					try {
						switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
						case 44:
							this.jj_consume_token(44);
							this.expression();
							this.jj_consume_token(45);
							continue;
						case 54:
							if (this.jj_2_2(2)) {
								this.projection();
								continue;
							} else {
								switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
								case 54:
									this.selection();
									continue;
								default:
									this.jj_la1[38] = this.jj_gen;
									this.jj_consume_token(-1);
									throw new ParseException();
								}
							}
						case 64:
							if (this.jj_2_1(2)) {
								this.methodCall();
								continue;
							} else {
								switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
								case 64:
									this.propertyName();
									continue;
								default:
									this.jj_la1[37] = this.jj_gen;
									this.jj_consume_token(-1);
									throw new ParseException();
								}
							}
						default:
							this.jj_la1[39] = this.jj_gen;
							this.jj_consume_token(-1);
							throw new ParseException();
						}
					} catch (Throwable arg25) {
						if (jjtc001) {
							this.jjtree.clearNodeScope(jjtn001);
							jjtc001 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg25 instanceof RuntimeException) {
							throw (RuntimeException) arg25;
						}

						if (arg25 instanceof ParseException) {
							throw (ParseException) arg25;
						}

						throw (Error) arg25;
					} finally {
						if (jjtc001) {
							this.jjtree.closeNodeScope(jjtn001, 2);
						}

					}
				case 44:
					this.jj_consume_token(44);
					this.expression();
					ASTEval jjtn003 = new ASTEval(30);
					boolean jjtc003 = true;
					this.jjtree.openNodeScope(jjtn003);

					try {
						this.jj_consume_token(45);
						continue;
					} finally {
						if (jjtc003) {
							this.jjtree.closeNodeScope(jjtn003, 2);
						}

					}
				case 52:
				case 67:
					ASTChain jjtn002 = new ASTChain(29);
					boolean jjtc002 = true;
					this.jjtree.openNodeScope(jjtn002);

					try {
						this.index();
						continue;
					} catch (Throwable arg22) {
						if (jjtc002) {
							this.jjtree.clearNodeScope(jjtn002);
							jjtc002 = false;
						} else {
							this.jjtree.popNode();
						}

						if (arg22 instanceof RuntimeException) {
							throw (RuntimeException) arg22;
						}

						if (arg22 instanceof ParseException) {
							throw (ParseException) arg22;
						}

						throw (Error) arg22;
					} finally {
						if (jjtc002) {
							this.jjtree.closeNodeScope(jjtn002, 2);
						}

					}
				default:
					this.jj_la1[40] = this.jj_gen;
					this.jj_consume_token(-1);
					throw new ParseException();
				}
			default:
				this.jj_la1[36] = this.jj_gen;
				return;
			}
		}
	}

	public final void primaryExpression() throws ParseException {
		String className = null;
		switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
		case 46:
			this.jj_consume_token(46);
			ASTConst jjtn002 = new ASTConst(31);
			boolean jjtc002 = true;
			this.jjtree.openNodeScope(jjtn002);

			try {
				this.jjtree.closeNodeScope(jjtn002, 0);
				jjtc002 = false;
				jjtn002.setValue(Boolean.TRUE);
				break;
			} finally {
				if (jjtc002) {
					this.jjtree.closeNodeScope(jjtn002, 0);
				}

			}
		case 47:
			this.jj_consume_token(47);
			ASTConst jjtn003 = new ASTConst(31);
			boolean jjtc003 = true;
			this.jjtree.openNodeScope(jjtn003);

			try {
				this.jjtree.closeNodeScope(jjtn003, 0);
				jjtc003 = false;
				jjtn003.setValue(Boolean.FALSE);
				break;
			} finally {
				if (jjtc003) {
					this.jjtree.closeNodeScope(jjtn003, 0);
				}

			}
		case 48:
			ASTConst jjtn004 = new ASTConst(31);
			boolean jjtc004 = true;
			this.jjtree.openNodeScope(jjtn004);

			try {
				this.jj_consume_token(48);
				break;
			} finally {
				if (jjtc004) {
					this.jjtree.closeNodeScope(jjtn004, 0);
				}

			}
		case 73:
		case 76:
		case 79:
		case 80:
		case 81:
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 73:
				this.jj_consume_token(73);
				break;
			case 74:
			case 75:
			case 77:
			case 78:
			default:
				this.jj_la1[41] = this.jj_gen;
				this.jj_consume_token(-1);
				throw new ParseException();
			case 76:
				this.jj_consume_token(76);
				break;
			case 79:
				this.jj_consume_token(79);
				break;
			case 80:
				this.jj_consume_token(80);
				break;
			case 81:
				this.jj_consume_token(81);
			}

			ASTConst jjtn001 = new ASTConst(31);
			boolean jjtc001 = true;
			this.jjtree.openNodeScope(jjtn001);

			try {
				this.jjtree.closeNodeScope(jjtn001, 0);
				jjtc001 = false;
				jjtn001.setValue(this.token_source.literalValue);
				break;
			} finally {
				if (jjtc001) {
					this.jjtree.closeNodeScope(jjtn001, 0);
				}

			}
		default:
			this.jj_la1[48] = this.jj_gen;
			boolean jjtc009;
			if (this.jj_2_4(2)) {
				this.jj_consume_token(49);
				ASTThisVarRef jjtn009 = new ASTThisVarRef(32);
				jjtc009 = true;
				this.jjtree.openNodeScope(jjtn009);

				try {
					this.jjtree.closeNodeScope(jjtn009, 0);
					jjtc009 = false;
					jjtn009.setName("this");
				} finally {
					if (jjtc009) {
						this.jjtree.closeNodeScope(jjtn009, 0);
					}

				}
			} else if (this.jj_2_5(2)) {
				this.jj_consume_token(50);
				ASTRootVarRef jjtn0091 = new ASTRootVarRef(33);
				jjtc009 = true;
				this.jjtree.openNodeScope(jjtn0091);

				try {
					this.jjtree.closeNodeScope(jjtn0091, 0);
					jjtc009 = false;
					jjtn0091.setName("root");
				} finally {
					if (jjtc009) {
						this.jjtree.closeNodeScope(jjtn0091, 0);
					}

				}
			} else if (this.jj_2_6(2)) {
				this.jj_consume_token(51);
				Token t = this.jj_consume_token(64);
				ASTVarRef jjtn0092 = new ASTVarRef(34);
				jjtc009 = true;
				this.jjtree.openNodeScope(jjtn0092);

				try {
					this.jjtree.closeNodeScope(jjtn0092, 0);
					jjtc009 = false;
					jjtn0092.setName(t.image);
				} finally {
					if (jjtc009) {
						this.jjtree.closeNodeScope(jjtn0092, 0);
					}

				}
			} else if (this.jj_2_7(2)) {
				this.jj_consume_token(4);
				this.jj_consume_token(52);
				this.expression();
				this.jj_consume_token(53);
				ASTConst jjtn0093 = new ASTConst(31);
				jjtc009 = true;
				this.jjtree.openNodeScope(jjtn0093);

				try {
					this.jjtree.closeNodeScope(jjtn0093, 1);
					jjtc009 = false;
					jjtn0093.setValue(jjtn0093.jjtGetChild(0));
				} finally {
					if (jjtc009) {
						this.jjtree.closeNodeScope(jjtn0093, 1);
					}

				}
			} else {
				switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 56:
					this.staticReference();
					break;
				default:
					this.jj_la1[49] = this.jj_gen;
					if (this.jj_2_8(2)) {
						this.constructorCall();
					} else {
						switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
						case 44:
							this.jj_consume_token(44);
							this.expression();
							this.jj_consume_token(45);
							break;
						case 52:
						case 67:
							this.index();
							break;
						case 54:
							this.jj_consume_token(54);
							ASTList jjtn0094 = new ASTList(35);
							jjtc009 = true;
							this.jjtree.openNodeScope(jjtn0094);

							try {
								label3289: switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
								case 4:
								case 28:
								case 35:
								case 36:
								case 40:
								case 41:
								case 44:
								case 46:
								case 47:
								case 48:
								case 49:
								case 50:
								case 51:
								case 52:
								case 54:
								case 56:
								case 57:
								case 64:
								case 67:
								case 73:
								case 76:
								case 79:
								case 80:
								case 81:
									this.assignmentExpression();

									while (true) {
										switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
										case 1:
											this.jj_consume_token(1);
											this.assignmentExpression();
											break;
										default:
											this.jj_la1[43] = this.jj_gen;
											break label3289;
										}
									}
								case 5:
								case 6:
								case 7:
								case 8:
								case 9:
								case 10:
								case 11:
								case 12:
								case 13:
								case 14:
								case 15:
								case 16:
								case 17:
								case 18:
								case 19:
								case 20:
								case 21:
								case 22:
								case 23:
								case 24:
								case 25:
								case 26:
								case 27:
								case 29:
								case 30:
								case 31:
								case 32:
								case 33:
								case 34:
								case 37:
								case 38:
								case 39:
								case 42:
								case 43:
								case 45:
								case 53:
								case 55:
								case 58:
								case 59:
								case 60:
								case 61:
								case 62:
								case 63:
								case 65:
								case 66:
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
									this.jj_la1[44] = this.jj_gen;
								}
							} catch (Throwable arg146) {
								if (jjtc009) {
									this.jjtree.clearNodeScope(jjtn0094);
									jjtc009 = false;
								} else {
									this.jjtree.popNode();
								}

								if (arg146 instanceof RuntimeException) {
									throw (RuntimeException) arg146;
								}

								if (arg146 instanceof ParseException) {
									throw (ParseException) arg146;
								}

								throw (Error) arg146;
							} finally {
								if (jjtc009) {
									this.jjtree.closeNodeScope(jjtn0094, true);
								}

							}

							this.jj_consume_token(55);
							break;
						case 64:
							if (this.jj_2_3(2)) {
								this.methodCall();
								break;
							} else {
								switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
								case 64:
									this.propertyName();
									return;
								default:
									this.jj_la1[42] = this.jj_gen;
									this.jj_consume_token(-1);
									throw new ParseException();
								}
							}
						default:
							this.jj_la1[50] = this.jj_gen;
							if (!this.jj_2_9(2)) {
								this.jj_consume_token(-1);
								throw new ParseException();
							}

							ASTMap jjtn010 = new ASTMap(36);
							boolean jjtc010 = true;
							this.jjtree.openNodeScope(jjtn010);

							try {
								this.jj_consume_token(51);
								switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
								case 56:
									className = this.classReference();
									break;
								default:
									this.jj_la1[45] = this.jj_gen;
								}

								this.jj_consume_token(54);
								label3300: switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
								case 4:
								case 28:
								case 35:
								case 36:
								case 40:
								case 41:
								case 44:
								case 46:
								case 47:
								case 48:
								case 49:
								case 50:
								case 51:
								case 52:
								case 54:
								case 56:
								case 57:
								case 64:
								case 67:
								case 73:
								case 76:
								case 79:
								case 80:
								case 81:
									this.keyValueExpression();

									while (true) {
										switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
										case 1:
											this.jj_consume_token(1);
											this.keyValueExpression();
											break;
										default:
											this.jj_la1[46] = this.jj_gen;
											break label3300;
										}
									}
								case 5:
								case 6:
								case 7:
								case 8:
								case 9:
								case 10:
								case 11:
								case 12:
								case 13:
								case 14:
								case 15:
								case 16:
								case 17:
								case 18:
								case 19:
								case 20:
								case 21:
								case 22:
								case 23:
								case 24:
								case 25:
								case 26:
								case 27:
								case 29:
								case 30:
								case 31:
								case 32:
								case 33:
								case 34:
								case 37:
								case 38:
								case 39:
								case 42:
								case 43:
								case 45:
								case 53:
								case 55:
								case 58:
								case 59:
								case 60:
								case 61:
								case 62:
								case 63:
								case 65:
								case 66:
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
									this.jj_la1[47] = this.jj_gen;
								}

								jjtn010.setClassName(className);
								this.jj_consume_token(55);
							} catch (Throwable arg148) {
								if (jjtc010) {
									this.jjtree.clearNodeScope(jjtn010);
									jjtc010 = false;
								} else {
									this.jjtree.popNode();
								}

								if (arg148 instanceof RuntimeException) {
									throw (RuntimeException) arg148;
								}

								if (arg148 instanceof ParseException) {
									throw (ParseException) arg148;
								}

								throw (Error) arg148;
							} finally {
								if (jjtc010) {
									this.jjtree.closeNodeScope(jjtn010, true);
								}

							}
						}
					}
				}
			}
		}

	}

	public final void keyValueExpression() throws ParseException {
		ASTKeyValue jjtn001 = new ASTKeyValue(37);
		boolean jjtc001 = true;
		this.jjtree.openNodeScope(jjtn001);

		try {
			this.assignmentExpression();
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 4:
				this.jj_consume_token(4);
				this.assignmentExpression();
				break;
			default:
				this.jj_la1[51] = this.jj_gen;
			}
		} catch (Throwable arg6) {
			if (jjtc001) {
				this.jjtree.clearNodeScope(jjtn001);
				jjtc001 = false;
			} else {
				this.jjtree.popNode();
			}

			if (arg6 instanceof RuntimeException) {
				throw (RuntimeException) arg6;
			}

			if (arg6 instanceof ParseException) {
				throw (ParseException) arg6;
			}

			throw (Error) arg6;
		} finally {
			if (jjtc001) {
				this.jjtree.closeNodeScope(jjtn001, true);
			}

		}

	}

	public final void staticReference() throws ParseException {
		String className = "java.lang.Math";
		className = this.classReference();
		if (this.jj_2_10(2)) {
			this.staticMethodCall(className);
		} else {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 64:
				Token t = this.jj_consume_token(64);
				ASTStaticField jjtn001 = new ASTStaticField(38);
				boolean jjtc001 = true;
				this.jjtree.openNodeScope(jjtn001);

				try {
					this.jjtree.closeNodeScope(jjtn001, 0);
					jjtc001 = false;
					jjtn001.init(className, t.image);
					break;
				} finally {
					if (jjtc001) {
						this.jjtree.closeNodeScope(jjtn001, 0);
					}

				}
			default:
				this.jj_la1[52] = this.jj_gen;
				this.jj_consume_token(-1);
				throw new ParseException();
			}
		}

	}

	public final String classReference() throws ParseException {
		String result = "java.lang.Math";
		this.jj_consume_token(56);
		switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
		case 64:
			result = this.className();
			break;
		default:
			this.jj_la1[53] = this.jj_gen;
		}

		this.jj_consume_token(56);
		return result;
	}

	public final String className() throws ParseException {
		Token t = this.jj_consume_token(64);
		StringBuffer result = new StringBuffer(t.image);

		while (true) {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 43:
				this.jj_consume_token(43);
				t = this.jj_consume_token(64);
				result.append('.').append(t.image);
				break;
			default:
				this.jj_la1[54] = this.jj_gen;
				return new String(result);
			}
		}
	}

	public final void constructorCall() throws ParseException {
		ASTCtor jjtn000 = new ASTCtor(39);
		boolean jjtc000 = true;
		this.jjtree.openNodeScope(jjtn000);

		try {
			this.jj_consume_token(57);
			String className = this.className();
			if (this.jj_2_11(2)) {
				this.jj_consume_token(44);
				label368: switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
				case 4:
				case 28:
				case 35:
				case 36:
				case 40:
				case 41:
				case 44:
				case 46:
				case 47:
				case 48:
				case 49:
				case 50:
				case 51:
				case 52:
				case 54:
				case 56:
				case 57:
				case 64:
				case 67:
				case 73:
				case 76:
				case 79:
				case 80:
				case 81:
					this.assignmentExpression();

					while (true) {
						switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
						case 1:
							this.jj_consume_token(1);
							this.assignmentExpression();
							break;
						default:
							this.jj_la1[55] = this.jj_gen;
							break label368;
						}
					}
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
				case 18:
				case 19:
				case 20:
				case 21:
				case 22:
				case 23:
				case 24:
				case 25:
				case 26:
				case 27:
				case 29:
				case 30:
				case 31:
				case 32:
				case 33:
				case 34:
				case 37:
				case 38:
				case 39:
				case 42:
				case 43:
				case 45:
				case 53:
				case 55:
				case 58:
				case 59:
				case 60:
				case 61:
				case 62:
				case 63:
				case 65:
				case 66:
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
					this.jj_la1[56] = this.jj_gen;
				}

				this.jj_consume_token(45);
				this.jjtree.closeNodeScope(jjtn000, true);
				jjtc000 = false;
				jjtn000.setClassName(className);
			} else if (this.jj_2_12(2)) {
				this.jj_consume_token(52);
				this.jj_consume_token(53);
				this.jj_consume_token(54);
				ASTList jjte000 = new ASTList(35);
				boolean jjtc001 = true;
				this.jjtree.openNodeScope(jjte000);

				try {
					label356: switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 4:
					case 28:
					case 35:
					case 36:
					case 40:
					case 41:
					case 44:
					case 46:
					case 47:
					case 48:
					case 49:
					case 50:
					case 51:
					case 52:
					case 54:
					case 56:
					case 57:
					case 64:
					case 67:
					case 73:
					case 76:
					case 79:
					case 80:
					case 81:
						this.assignmentExpression();

						while (true) {
							switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
							case 1:
								this.jj_consume_token(1);
								this.assignmentExpression();
								break;
							default:
								this.jj_la1[57] = this.jj_gen;
								break label356;
							}
						}
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 17:
					case 18:
					case 19:
					case 20:
					case 21:
					case 22:
					case 23:
					case 24:
					case 25:
					case 26:
					case 27:
					case 29:
					case 30:
					case 31:
					case 32:
					case 33:
					case 34:
					case 37:
					case 38:
					case 39:
					case 42:
					case 43:
					case 45:
					case 53:
					case 55:
					case 58:
					case 59:
					case 60:
					case 61:
					case 62:
					case 63:
					case 65:
					case 66:
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
						this.jj_la1[58] = this.jj_gen;
					}
				} catch (Throwable arg16) {
					if (jjtc001) {
						this.jjtree.clearNodeScope(jjte000);
						jjtc001 = false;
					} else {
						this.jjtree.popNode();
					}

					if (arg16 instanceof RuntimeException) {
						throw (RuntimeException) arg16;
					}

					if (arg16 instanceof ParseException) {
						throw (ParseException) arg16;
					}

					throw (Error) arg16;
				} finally {
					if (jjtc001) {
						this.jjtree.closeNodeScope(jjte000, true);
					}

				}

				this.jj_consume_token(55);
				this.jjtree.closeNodeScope(jjtn000, true);
				jjtc000 = false;
				jjtn000.setClassName(className);
				jjtn000.setArray(true);
			} else {
				if (!this.jj_2_13(2)) {
					this.jj_consume_token(-1);
					throw new ParseException();
				}

				this.jj_consume_token(52);
				this.assignmentExpression();
				this.jj_consume_token(53);
				this.jjtree.closeNodeScope(jjtn000, true);
				jjtc000 = false;
				jjtn000.setClassName(className);
				jjtn000.setArray(true);
			}
		} catch (Throwable arg18) {
			if (jjtc000) {
				this.jjtree.clearNodeScope(jjtn000);
				jjtc000 = false;
			} else {
				this.jjtree.popNode();
			}

			if (arg18 instanceof RuntimeException) {
				throw (RuntimeException) arg18;
			}

			if (arg18 instanceof ParseException) {
				throw (ParseException) arg18;
			}

			throw (Error) arg18;
		} finally {
			if (jjtc000) {
				this.jjtree.closeNodeScope(jjtn000, true);
			}

		}

	}

	public final void propertyName() throws ParseException {
		ASTProperty jjtn000 = new ASTProperty(40);
		boolean jjtc000 = true;
		this.jjtree.openNodeScope(jjtn000);

		try {
			Token t = this.jj_consume_token(64);
			ASTConst jjtn001 = new ASTConst(31);
			boolean jjtc001 = true;
			this.jjtree.openNodeScope(jjtn001);

			try {
				this.jjtree.closeNodeScope(jjtn001, true);
				jjtc001 = false;
				jjtn001.setValue(t.image);
			} finally {
				if (jjtc001) {
					this.jjtree.closeNodeScope(jjtn001, true);
				}

			}
		} finally {
			if (jjtc000) {
				this.jjtree.closeNodeScope(jjtn000, true);
			}

		}

	}

	public final void staticMethodCall(String className) throws ParseException {
		ASTStaticMethod jjtn000 = new ASTStaticMethod(41);
		boolean jjtc000 = true;
		this.jjtree.openNodeScope(jjtn000);

		try {
			Token t;
			t = this.jj_consume_token(64);
			this.jj_consume_token(44);
			label126: switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 4:
			case 28:
			case 35:
			case 36:
			case 40:
			case 41:
			case 44:
			case 46:
			case 47:
			case 48:
			case 49:
			case 50:
			case 51:
			case 52:
			case 54:
			case 56:
			case 57:
			case 64:
			case 67:
			case 73:
			case 76:
			case 79:
			case 80:
			case 81:
				this.assignmentExpression();

				while (true) {
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 1:
						this.jj_consume_token(1);
						this.assignmentExpression();
						break;
					default:
						this.jj_la1[59] = this.jj_gen;
						break label126;
					}
				}
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 29:
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 37:
			case 38:
			case 39:
			case 42:
			case 43:
			case 45:
			case 53:
			case 55:
			case 58:
			case 59:
			case 60:
			case 61:
			case 62:
			case 63:
			case 65:
			case 66:
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
				this.jj_la1[60] = this.jj_gen;
			}

			this.jj_consume_token(45);
			this.jjtree.closeNodeScope(jjtn000, true);
			jjtc000 = false;
			jjtn000.init(className, t.image);
		} catch (Throwable arg8) {
			if (jjtc000) {
				this.jjtree.clearNodeScope(jjtn000);
				jjtc000 = false;
			} else {
				this.jjtree.popNode();
			}

			if (arg8 instanceof RuntimeException) {
				throw (RuntimeException) arg8;
			}

			if (arg8 instanceof ParseException) {
				throw (ParseException) arg8;
			}

			throw (Error) arg8;
		} finally {
			if (jjtc000) {
				this.jjtree.closeNodeScope(jjtn000, true);
			}

		}

	}

	public final void methodCall() throws ParseException {
		ASTMethod jjtn000 = new ASTMethod(42);
		boolean jjtc000 = true;
		this.jjtree.openNodeScope(jjtn000);

		try {
			Token t;
			t = this.jj_consume_token(64);
			this.jj_consume_token(44);
			label126: switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 4:
			case 28:
			case 35:
			case 36:
			case 40:
			case 41:
			case 44:
			case 46:
			case 47:
			case 48:
			case 49:
			case 50:
			case 51:
			case 52:
			case 54:
			case 56:
			case 57:
			case 64:
			case 67:
			case 73:
			case 76:
			case 79:
			case 80:
			case 81:
				this.assignmentExpression();

				while (true) {
					switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
					case 1:
						this.jj_consume_token(1);
						this.assignmentExpression();
						break;
					default:
						this.jj_la1[61] = this.jj_gen;
						break label126;
					}
				}
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 29:
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 37:
			case 38:
			case 39:
			case 42:
			case 43:
			case 45:
			case 53:
			case 55:
			case 58:
			case 59:
			case 60:
			case 61:
			case 62:
			case 63:
			case 65:
			case 66:
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
				this.jj_la1[62] = this.jj_gen;
			}

			this.jj_consume_token(45);
			this.jjtree.closeNodeScope(jjtn000, true);
			jjtc000 = false;
			jjtn000.setMethodName(t.image);
		} catch (Throwable arg7) {
			if (jjtc000) {
				this.jjtree.clearNodeScope(jjtn000);
				jjtc000 = false;
			} else {
				this.jjtree.popNode();
			}

			if (arg7 instanceof RuntimeException) {
				throw (RuntimeException) arg7;
			}

			if (arg7 instanceof ParseException) {
				throw (ParseException) arg7;
			}

			throw (Error) arg7;
		} finally {
			if (jjtc000) {
				this.jjtree.closeNodeScope(jjtn000, true);
			}

		}

	}

	public final void projection() throws ParseException {
		ASTProject jjtn000 = new ASTProject(43);
		boolean jjtc000 = true;
		this.jjtree.openNodeScope(jjtn000);

		try {
			this.jj_consume_token(54);
			this.expression();
			this.jj_consume_token(55);
		} catch (Throwable arg6) {
			if (jjtc000) {
				this.jjtree.clearNodeScope(jjtn000);
				jjtc000 = false;
			} else {
				this.jjtree.popNode();
			}

			if (arg6 instanceof RuntimeException) {
				throw (RuntimeException) arg6;
			}

			if (arg6 instanceof ParseException) {
				throw (ParseException) arg6;
			}

			throw (Error) arg6;
		} finally {
			if (jjtc000) {
				this.jjtree.closeNodeScope(jjtn000, true);
			}

		}

	}

	public final void selection() throws ParseException {
		if (this.jj_2_14(2)) {
			this.selectAll();
		} else if (this.jj_2_15(2)) {
			this.selectFirst();
		} else {
			if (!this.jj_2_16(2)) {
				this.jj_consume_token(-1);
				throw new ParseException();
			}

			this.selectLast();
		}

	}

	public final void selectAll() throws ParseException {
		ASTSelect jjtn000 = new ASTSelect(44);
		boolean jjtc000 = true;
		this.jjtree.openNodeScope(jjtn000);

		try {
			this.jj_consume_token(54);
			this.jj_consume_token(3);
			this.expression();
			this.jj_consume_token(55);
		} catch (Throwable arg6) {
			if (jjtc000) {
				this.jjtree.clearNodeScope(jjtn000);
				jjtc000 = false;
			} else {
				this.jjtree.popNode();
			}

			if (arg6 instanceof RuntimeException) {
				throw (RuntimeException) arg6;
			}

			if (arg6 instanceof ParseException) {
				throw (ParseException) arg6;
			}

			throw (Error) arg6;
		} finally {
			if (jjtc000) {
				this.jjtree.closeNodeScope(jjtn000, true);
			}

		}

	}

	public final void selectFirst() throws ParseException {
		ASTSelectFirst jjtn000 = new ASTSelectFirst(45);
		boolean jjtc000 = true;
		this.jjtree.openNodeScope(jjtn000);

		try {
			this.jj_consume_token(54);
			this.jj_consume_token(11);
			this.expression();
			this.jj_consume_token(55);
		} catch (Throwable arg6) {
			if (jjtc000) {
				this.jjtree.clearNodeScope(jjtn000);
				jjtc000 = false;
			} else {
				this.jjtree.popNode();
			}

			if (arg6 instanceof RuntimeException) {
				throw (RuntimeException) arg6;
			}

			if (arg6 instanceof ParseException) {
				throw (ParseException) arg6;
			}

			throw (Error) arg6;
		} finally {
			if (jjtc000) {
				this.jjtree.closeNodeScope(jjtn000, true);
			}

		}

	}

	public final void selectLast() throws ParseException {
		ASTSelectLast jjtn000 = new ASTSelectLast(46);
		boolean jjtc000 = true;
		this.jjtree.openNodeScope(jjtn000);

		try {
			this.jj_consume_token(54);
			this.jj_consume_token(58);
			this.expression();
			this.jj_consume_token(55);
		} catch (Throwable arg6) {
			if (jjtc000) {
				this.jjtree.clearNodeScope(jjtn000);
				jjtc000 = false;
			} else {
				this.jjtree.popNode();
			}

			if (arg6 instanceof RuntimeException) {
				throw (RuntimeException) arg6;
			}

			if (arg6 instanceof ParseException) {
				throw (ParseException) arg6;
			}

			throw (Error) arg6;
		} finally {
			if (jjtc000) {
				this.jjtree.closeNodeScope(jjtn000, true);
			}

		}

	}

	public final void index() throws ParseException {
		ASTProperty jjtn000 = new ASTProperty(40);
		boolean jjtc000 = true;
		this.jjtree.openNodeScope(jjtn000);

		try {
			switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
			case 52:
				this.jj_consume_token(52);
				this.expression();
				this.jj_consume_token(53);
				this.jjtree.closeNodeScope(jjtn000, true);
				jjtc000 = false;
				jjtn000.setIndexedAccess(true);
				break;
			case 67:
				this.jj_consume_token(67);
				ASTConst jjte000 = new ASTConst(31);
				boolean jjtc001 = true;
				this.jjtree.openNodeScope(jjte000);

				try {
					this.jjtree.closeNodeScope(jjte000, true);
					jjtc001 = false;
					jjte000.setValue(this.token_source.literalValue);
				} finally {
					if (jjtc001) {
						this.jjtree.closeNodeScope(jjte000, true);
					}

				}

				this.jjtree.closeNodeScope(jjtn000, true);
				jjtc000 = false;
				jjtn000.setIndexedAccess(true);
				break;
			default:
				this.jj_la1[63] = this.jj_gen;
				this.jj_consume_token(-1);
				throw new ParseException();
			}
		} catch (Throwable arg13) {
			if (jjtc000) {
				this.jjtree.clearNodeScope(jjtn000);
				jjtc000 = false;
			} else {
				this.jjtree.popNode();
			}

			if (arg13 instanceof RuntimeException) {
				throw (RuntimeException) arg13;
			}

			if (arg13 instanceof ParseException) {
				throw (ParseException) arg13;
			}

			throw (Error) arg13;
		} finally {
			if (jjtc000) {
				this.jjtree.closeNodeScope(jjtn000, true);
			}

		}

	}

	private boolean jj_2_1(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_1();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(0, xla);
		}

		return arg2;
	}

	private boolean jj_2_2(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_2();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(1, xla);
		}

		return arg2;
	}

	private boolean jj_2_3(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_3();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(2, xla);
		}

		return arg2;
	}

	private boolean jj_2_4(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_4();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(3, xla);
		}

		return arg2;
	}

	private boolean jj_2_5(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_5();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(4, xla);
		}

		return arg2;
	}

	private boolean jj_2_6(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_6();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(5, xla);
		}

		return arg2;
	}

	private boolean jj_2_7(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_7();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(6, xla);
		}

		return arg2;
	}

	private boolean jj_2_8(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_8();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(7, xla);
		}

		return arg2;
	}

	private boolean jj_2_9(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_9();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(8, xla);
		}

		return arg2;
	}

	private boolean jj_2_10(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_10();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(9, xla);
		}

		return arg2;
	}

	private boolean jj_2_11(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_11();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(10, xla);
		}

		return arg2;
	}

	private boolean jj_2_12(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_12();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(11, xla);
		}

		return arg2;
	}

	private boolean jj_2_13(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_13();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(12, xla);
		}

		return arg2;
	}

	private boolean jj_2_14(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_14();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(13, xla);
		}

		return arg2;
	}

	private boolean jj_2_15(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_15();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(14, xla);
		}

		return arg2;
	}

	private boolean jj_2_16(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;

		boolean arg2;
		try {
			boolean ls = !this.jj_3_16();
			return ls;
		} catch (OgnlParser.LookaheadSuccess arg6) {
			arg2 = true;
		} finally {
			this.jj_save(15, xla);
		}

		return arg2;
	}

	private boolean jj_3R_56() {
		return this.jj_scan_token(48);
	}

	private boolean jj_3R_55() {
		return this.jj_scan_token(47);
	}

	private boolean jj_3R_54() {
		return this.jj_scan_token(46);
	}

	private boolean jj_3R_31() {
		return this.jj_3R_27();
	}

	private boolean jj_3_13() {
		return this.jj_scan_token(52) ? true : this.jj_3R_27();
	}

	private boolean jj_3R_53() {
		Token xsp = this.jj_scanpos;
		if (this.jj_scan_token(73)) {
			this.jj_scanpos = xsp;
			if (this.jj_scan_token(76)) {
				this.jj_scanpos = xsp;
				if (this.jj_scan_token(79)) {
					this.jj_scanpos = xsp;
					if (this.jj_scan_token(80)) {
						this.jj_scanpos = xsp;
						if (this.jj_scan_token(81)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private boolean jj_3R_26() {
		return this.jj_3R_27();
	}

	private boolean jj_3R_52() {
		Token xsp = this.jj_scanpos;
		if (this.jj_3R_53()) {
			this.jj_scanpos = xsp;
			if (this.jj_3R_54()) {
				this.jj_scanpos = xsp;
				if (this.jj_3R_55()) {
					this.jj_scanpos = xsp;
					if (this.jj_3R_56()) {
						this.jj_scanpos = xsp;
						if (this.jj_3_4()) {
							this.jj_scanpos = xsp;
							if (this.jj_3_5()) {
								this.jj_scanpos = xsp;
								if (this.jj_3_6()) {
									this.jj_scanpos = xsp;
									if (this.jj_3_7()) {
										this.jj_scanpos = xsp;
										if (this.jj_3R_57()) {
											this.jj_scanpos = xsp;
											if (this.jj_3_8()) {
												this.jj_scanpos = xsp;
												if (this.jj_3R_58()) {
													this.jj_scanpos = xsp;
													if (this.jj_3R_59()) {
														this.jj_scanpos = xsp;
														if (this.jj_3R_60()) {
															this.jj_scanpos = xsp;
															if (this.jj_3R_61()) {
																this.jj_scanpos = xsp;
																if (this.jj_3_9()) {
																	return true;
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return false;
	}

	private boolean jj_3R_42() {
		return this.jj_3R_43();
	}

	private boolean jj_3_12() {
		return this.jj_scan_token(52) ? true : this.jj_scan_token(53);
	}

	private boolean jj_3_11() {
		if (this.jj_scan_token(44)) {
			return true;
		} else {
			Token xsp = this.jj_scanpos;
			if (this.jj_3R_26()) {
				this.jj_scanpos = xsp;
			}

			return this.jj_scan_token(45);
		}
	}

	private boolean jj_3R_67() {
		return this.jj_scan_token(67);
	}

	private boolean jj_3_2() {
		return this.jj_3R_22();
	}

	private boolean jj_3R_66() {
		return this.jj_scan_token(52);
	}

	private boolean jj_3R_64() {
		Token xsp = this.jj_scanpos;
		if (this.jj_3R_66()) {
			this.jj_scanpos = xsp;
			if (this.jj_3R_67()) {
				return true;
			}
		}

		return false;
	}

	private boolean jj_3_1() {
		return this.jj_3R_21();
	}

	private boolean jj_3R_23() {
		return this.jj_scan_token(57) ? true : this.jj_3R_32();
	}

	private boolean jj_3R_41() {
		return this.jj_3R_42();
	}

	private boolean jj_3R_30() {
		return this.jj_scan_token(54) ? true : this.jj_scan_token(58);
	}

	private boolean jj_3R_32() {
		return this.jj_scan_token(64);
	}

	private boolean jj_3R_51() {
		return this.jj_3R_52();
	}

	private boolean jj_3R_29() {
		return this.jj_scan_token(54) ? true : this.jj_scan_token(11);
	}

	private boolean jj_3R_40() {
		return this.jj_3R_41();
	}

	private boolean jj_3R_33() {
		return this.jj_scan_token(56);
	}

	private boolean jj_3R_63() {
		return this.jj_3R_65();
	}

	private boolean jj_3R_28() {
		return this.jj_scan_token(54) ? true : this.jj_scan_token(3);
	}

	private boolean jj_3R_50() {
		return this.jj_3R_51();
	}

	private boolean jj_3R_39() {
		return this.jj_3R_40();
	}

	private boolean jj_3_10() {
		return this.jj_3R_25();
	}

	private boolean jj_3R_24() {
		return this.jj_3R_33();
	}

	private boolean jj_3R_49() {
		Token xsp = this.jj_scanpos;
		if (this.jj_scan_token(41)) {
			this.jj_scanpos = xsp;
			if (this.jj_scan_token(28)) {
				return true;
			}
		}

		return false;
	}

	private boolean jj_3R_48() {
		return this.jj_scan_token(40);
	}

	private boolean jj_3_16() {
		return this.jj_3R_30();
	}

	private boolean jj_3R_47() {
		return this.jj_scan_token(35);
	}

	private boolean jj_3_15() {
		return this.jj_3R_29();
	}

	private boolean jj_3R_38() {
		return this.jj_3R_39();
	}

	private boolean jj_3R_46() {
		return this.jj_scan_token(36);
	}

	private boolean jj_3_14() {
		return this.jj_3R_28();
	}

	private boolean jj_3R_62() {
		return this.jj_3R_33();
	}

	private boolean jj_3R_45() {
		Token xsp = this.jj_scanpos;
		if (this.jj_3R_46()) {
			this.jj_scanpos = xsp;
			if (this.jj_3R_47()) {
				this.jj_scanpos = xsp;
				if (this.jj_3R_48()) {
					this.jj_scanpos = xsp;
					if (this.jj_3R_49()) {
						this.jj_scanpos = xsp;
						if (this.jj_3R_50()) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private boolean jj_3R_37() {
		return this.jj_3R_38();
	}

	private boolean jj_3R_22() {
		return this.jj_scan_token(54) ? true : this.jj_3R_31();
	}

	private boolean jj_3_9() {
		if (this.jj_scan_token(51)) {
			return true;
		} else {
			Token xsp = this.jj_scanpos;
			if (this.jj_3R_24()) {
				this.jj_scanpos = xsp;
			}

			return this.jj_scan_token(54);
		}
	}

	private boolean jj_3R_36() {
		return this.jj_3R_37();
	}

	private boolean jj_3R_61() {
		return this.jj_scan_token(54);
	}

	private boolean jj_3R_60() {
		return this.jj_scan_token(44);
	}

	private boolean jj_3R_59() {
		return this.jj_3R_64();
	}

	private boolean jj_3_3() {
		return this.jj_3R_21();
	}

	private boolean jj_3R_21() {
		return this.jj_scan_token(64) ? true : this.jj_scan_token(44);
	}

	private boolean jj_3R_58() {
		Token xsp = this.jj_scanpos;
		if (this.jj_3_3()) {
			this.jj_scanpos = xsp;
			if (this.jj_3R_63()) {
				return true;
			}
		}

		return false;
	}

	private boolean jj_3R_35() {
		return this.jj_3R_36();
	}

	private boolean jj_3R_44() {
		return this.jj_3R_45();
	}

	private boolean jj_3_8() {
		return this.jj_3R_23();
	}

	private boolean jj_3R_57() {
		return this.jj_3R_62();
	}

	private boolean jj_3R_34() {
		return this.jj_3R_35();
	}

	private boolean jj_3_7() {
		return this.jj_scan_token(4) ? true : this.jj_scan_token(52);
	}

	private boolean jj_3R_25() {
		return this.jj_scan_token(64) ? true : this.jj_scan_token(44);
	}

	private boolean jj_3_6() {
		return this.jj_scan_token(51) ? true : this.jj_scan_token(64);
	}

	private boolean jj_3_5() {
		return this.jj_scan_token(50);
	}

	private boolean jj_3R_27() {
		return this.jj_3R_34();
	}

	private boolean jj_3_4() {
		return this.jj_scan_token(49);
	}

	private boolean jj_3R_65() {
		return this.jj_scan_token(64);
	}

	private boolean jj_3R_43() {
		return this.jj_3R_44();
	}

	private static void jj_la1_init_0() {
		jj_la1_0 = new int[] { 2, 4, 8, 96, 96, 384, 384, 1536, 1536, 6144, 6144, 24576, 24576, 491520, 98304, 393216,
				491520, 536346624, 1572864, 6291456, 25165824, 100663296, 536346624, -536870912, 1610612736,
				Integer.MIN_VALUE, 0, -536870912, 0, 0, 0, 0, 268435456, 0, 0, 268435472, 0, 0, 0, 0, 0, 0, 0, 2,
				268435472, 0, 2, 268435472, 0, 0, 0, 16, 0, 0, 0, 2, 268435472, 2, 268435472, 2, 268435472, 2,
				268435472, 0 };
	}

	private static void jj_la1_init_1() {
		jj_la1_1 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 1, 6, 7, 24,
				24, 224, 224, 512, 2048, 1024, 56611608, 1054720, 0, 4194304, 4198400, 1054720, 0, 0, 0, 56611608,
				16777216, 0, 56611608, 114688, 16777216, 5246976, 0, 0, 0, 2048, 0, 56611608, 0, 56611608, 0, 56611608,
				0, 56611608, 1048576 };
	}

	private static void jj_la1_init_2() {
		jj_la1_2 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 233993, 8, 1, 0, 1, 8, 233984, 1, 0, 233993, 0, 0, 233993, 233984, 0, 9, 0, 1, 1, 0, 0,
				233993, 0, 233993, 0, 233993, 0, 233993, 8 };
	}

	public OgnlParser(InputStream stream) {
		this(stream, (String) null);
	}

	public OgnlParser(InputStream stream, String encoding) {
		this.jjtree = new JJTOgnlParserState();
		this.jj_lookingAhead = false;
		this.jj_la1 = new int[64];
		this.jj_2_rtns = new OgnlParser.JJCalls[16];
		this.jj_rescan = false;
		this.jj_gc = 0;
		this.jj_ls = new OgnlParser.LookaheadSuccess();
		this.jj_expentries = new ArrayList();
		this.jj_kind = -1;
		this.jj_lasttokens = new int[100];

		try {
			this.jj_input_stream = new JavaCharStream(stream, encoding, 1, 1);
		} catch (UnsupportedEncodingException arg3) {
			throw new RuntimeException(arg3);
		}

		this.token_source = new OgnlParserTokenManager(this.jj_input_stream);
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;

		int i;
		for (i = 0; i < 64; ++i) {
			this.jj_la1[i] = -1;
		}

		for (i = 0; i < this.jj_2_rtns.length; ++i) {
			this.jj_2_rtns[i] = new OgnlParser.JJCalls();
		}

	}

	public void ReInit(InputStream stream) {
		this.ReInit(stream, (String) null);
	}

	public void ReInit(InputStream stream, String encoding) {
		try {
			this.jj_input_stream.ReInit(stream, encoding, 1, 1);
		} catch (UnsupportedEncodingException arg3) {
			throw new RuntimeException(arg3);
		}

		this.token_source.ReInit(this.jj_input_stream);
		this.token = new Token();
		this.jj_ntk = -1;
		this.jjtree.reset();
		this.jj_gen = 0;

		int i;
		for (i = 0; i < 64; ++i) {
			this.jj_la1[i] = -1;
		}

		for (i = 0; i < this.jj_2_rtns.length; ++i) {
			this.jj_2_rtns[i] = new OgnlParser.JJCalls();
		}

	}

	public OgnlParser(Reader stream) {
		this.jjtree = new JJTOgnlParserState();
		this.jj_lookingAhead = false;
		this.jj_la1 = new int[64];
		this.jj_2_rtns = new OgnlParser.JJCalls[16];
		this.jj_rescan = false;
		this.jj_gc = 0;
		this.jj_ls = new OgnlParser.LookaheadSuccess();
		this.jj_expentries = new ArrayList();
		this.jj_kind = -1;
		this.jj_lasttokens = new int[100];
		this.jj_input_stream = new JavaCharStream(stream, 1, 1);
		this.token_source = new OgnlParserTokenManager(this.jj_input_stream);
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;

		int i;
		for (i = 0; i < 64; ++i) {
			this.jj_la1[i] = -1;
		}

		for (i = 0; i < this.jj_2_rtns.length; ++i) {
			this.jj_2_rtns[i] = new OgnlParser.JJCalls();
		}

	}

	public void ReInit(Reader stream) {
		this.jj_input_stream.ReInit(stream, 1, 1);
		this.token_source.ReInit(this.jj_input_stream);
		this.token = new Token();
		this.jj_ntk = -1;
		this.jjtree.reset();
		this.jj_gen = 0;

		int i;
		for (i = 0; i < 64; ++i) {
			this.jj_la1[i] = -1;
		}

		for (i = 0; i < this.jj_2_rtns.length; ++i) {
			this.jj_2_rtns[i] = new OgnlParser.JJCalls();
		}

	}

	public OgnlParser(OgnlParserTokenManager tm) {
		this.jjtree = new JJTOgnlParserState();
		this.jj_lookingAhead = false;
		this.jj_la1 = new int[64];
		this.jj_2_rtns = new OgnlParser.JJCalls[16];
		this.jj_rescan = false;
		this.jj_gc = 0;
		this.jj_ls = new OgnlParser.LookaheadSuccess();
		this.jj_expentries = new ArrayList();
		this.jj_kind = -1;
		this.jj_lasttokens = new int[100];
		this.token_source = tm;
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;

		int i;
		for (i = 0; i < 64; ++i) {
			this.jj_la1[i] = -1;
		}

		for (i = 0; i < this.jj_2_rtns.length; ++i) {
			this.jj_2_rtns[i] = new OgnlParser.JJCalls();
		}

	}

	public void ReInit(OgnlParserTokenManager tm) {
		this.token_source = tm;
		this.token = new Token();
		this.jj_ntk = -1;
		this.jjtree.reset();
		this.jj_gen = 0;

		int i;
		for (i = 0; i < 64; ++i) {
			this.jj_la1[i] = -1;
		}

		for (i = 0; i < this.jj_2_rtns.length; ++i) {
			this.jj_2_rtns[i] = new OgnlParser.JJCalls();
		}

	}

	private Token jj_consume_token(int kind) throws ParseException {
		Token oldToken = this.token;
		if (this.token.next != null) {
			this.token = this.token.next;
		} else {
			this.token = this.token.next = this.token_source.getNextToken();
		}

		this.jj_ntk = -1;
		if (this.token.kind != kind) {
			this.token = oldToken;
			this.jj_kind = kind;
			throw this.generateParseException();
		} else {
			++this.jj_gen;
			if (++this.jj_gc > 100) {
				this.jj_gc = 0;

				for (int i = 0; i < this.jj_2_rtns.length; ++i) {
					for (OgnlParser.JJCalls c = this.jj_2_rtns[i]; c != null; c = c.next) {
						if (c.gen < this.jj_gen) {
							c.first = null;
						}
					}
				}
			}

			return this.token;
		}
	}

	private boolean jj_scan_token(int kind) {
		if (this.jj_scanpos == this.jj_lastpos) {
			--this.jj_la;
			if (this.jj_scanpos.next == null) {
				this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
			} else {
				this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
			}
		} else {
			this.jj_scanpos = this.jj_scanpos.next;
		}

		if (this.jj_rescan) {
			int i = 0;

			Token tok;
			for (tok = this.token; tok != null && tok != this.jj_scanpos; tok = tok.next) {
				++i;
			}

			if (tok != null) {
				this.jj_add_error_token(kind, i);
			}
		}

		if (this.jj_scanpos.kind != kind) {
			return true;
		} else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
			throw this.jj_ls;
		} else {
			return false;
		}
	}

	public final Token getNextToken() {
		if (this.token.next != null) {
			this.token = this.token.next;
		} else {
			this.token = this.token.next = this.token_source.getNextToken();
		}

		this.jj_ntk = -1;
		++this.jj_gen;
		return this.token;
	}

	public final Token getToken(int index) {
		Token t = this.jj_lookingAhead ? this.jj_scanpos : this.token;

		for (int i = 0; i < index; ++i) {
			if (t.next != null) {
				t = t.next;
			} else {
				t = t.next = this.token_source.getNextToken();
			}
		}

		return t;
	}

	private int jj_ntk() {
		return (this.jj_nt = this.token.next) == null
				? (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind)
				: (this.jj_ntk = this.jj_nt.kind);
	}

	private void jj_add_error_token(int kind, int pos) {
		if (pos < 100) {
			if (pos == this.jj_endpos + 1) {
				this.jj_lasttokens[this.jj_endpos++] = kind;
			} else if (this.jj_endpos != 0) {
				this.jj_expentry = new int[this.jj_endpos];

				for (int it = 0; it < this.jj_endpos; ++it) {
					this.jj_expentry[it] = this.jj_lasttokens[it];
				}

				Iterator arg5 = this.jj_expentries.iterator();

				label41: while (true) {
					int[] oldentry;
					do {
						if (!arg5.hasNext()) {
							break label41;
						}

						oldentry = (int[]) ((int[]) arg5.next());
					} while (oldentry.length != this.jj_expentry.length);

					for (int i = 0; i < this.jj_expentry.length; ++i) {
						if (oldentry[i] != this.jj_expentry[i]) {
							continue label41;
						}
					}

					this.jj_expentries.add(this.jj_expentry);
					break;
				}

				if (pos != 0) {
					this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
				}
			}

		}
	}

	public ParseException generateParseException() {
		this.jj_expentries.clear();
		boolean[] la1tokens = new boolean[86];
		if (this.jj_kind >= 0) {
			la1tokens[this.jj_kind] = true;
			this.jj_kind = -1;
		}

		int exptokseq;
		int i;
		for (exptokseq = 0; exptokseq < 64; ++exptokseq) {
			if (this.jj_la1[exptokseq] == this.jj_gen) {
				for (i = 0; i < 32; ++i) {
					if ((jj_la1_0[exptokseq] & 1 << i) != 0) {
						la1tokens[i] = true;
					}

					if ((jj_la1_1[exptokseq] & 1 << i) != 0) {
						la1tokens[32 + i] = true;
					}

					if ((jj_la1_2[exptokseq] & 1 << i) != 0) {
						la1tokens[64 + i] = true;
					}
				}
			}
		}

		for (exptokseq = 0; exptokseq < 86; ++exptokseq) {
			if (la1tokens[exptokseq]) {
				this.jj_expentry = new int[1];
				this.jj_expentry[0] = exptokseq;
				this.jj_expentries.add(this.jj_expentry);
			}
		}

		this.jj_endpos = 0;
		this.jj_rescan_token();
		this.jj_add_error_token(0, 0);
		int[][] arg3 = new int[this.jj_expentries.size()][];

		for (i = 0; i < this.jj_expentries.size(); ++i) {
			arg3[i] = (int[]) ((int[]) this.jj_expentries.get(i));
		}

		return new ParseException(this.token, arg3, tokenImage);
	}

	public final void enable_tracing() {
	}

	public final void disable_tracing() {
	}

	private void jj_rescan_token() {
		this.jj_rescan = true;

		for (int i = 0; i < 16; ++i) {
			try {
				OgnlParser.JJCalls p = this.jj_2_rtns[i];

				do {
					if (p.gen > this.jj_gen) {
						this.jj_la = p.arg;
						this.jj_lastpos = this.jj_scanpos = p.first;
						switch (i) {
						case 0:
							this.jj_3_1();
							break;
						case 1:
							this.jj_3_2();
							break;
						case 2:
							this.jj_3_3();
							break;
						case 3:
							this.jj_3_4();
							break;
						case 4:
							this.jj_3_5();
							break;
						case 5:
							this.jj_3_6();
							break;
						case 6:
							this.jj_3_7();
							break;
						case 7:
							this.jj_3_8();
							break;
						case 8:
							this.jj_3_9();
							break;
						case 9:
							this.jj_3_10();
							break;
						case 10:
							this.jj_3_11();
							break;
						case 11:
							this.jj_3_12();
							break;
						case 12:
							this.jj_3_13();
							break;
						case 13:
							this.jj_3_14();
							break;
						case 14:
							this.jj_3_15();
							break;
						case 15:
							this.jj_3_16();
						}
					}

					p = p.next;
				} while (p != null);
			} catch (OgnlParser.LookaheadSuccess arg2) {
				;
			}
		}

		this.jj_rescan = false;
	}

	private void jj_save(int index, int xla) {
		OgnlParser.JJCalls p;
		for (p = this.jj_2_rtns[index]; p.gen > this.jj_gen; p = p.next) {
			if (p.next == null) {
				p = p.next = new OgnlParser.JJCalls();
				break;
			}
		}

		p.gen = this.jj_gen + xla - this.jj_la;
		p.first = this.token;
		p.arg = xla;
	}

	static {
		jj_la1_init_0();
		jj_la1_init_1();
		jj_la1_init_2();
	}

	static final class JJCalls {
		int gen;
		Token first;
		int arg;
		OgnlParser.JJCalls next;
	}

	private static final class LookaheadSuccess extends Error {
		private LookaheadSuccess() {
		}
	}
}
