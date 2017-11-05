package org.eocencle.winger.javassist.compiler;

import org.eocencle.winger.javassist.CannotCompileException;
import org.eocencle.winger.javassist.NotFoundException;

public class CompileError extends Exception {
	private Lex lex;
	private String reason;

	public CompileError(String s, Lex l) {
		this.reason = s;
		this.lex = l;
	}

	public CompileError(String s) {
		this.reason = s;
		this.lex = null;
	}

	public CompileError(CannotCompileException e) {
		this(e.getReason());
	}

	public CompileError(NotFoundException e) {
		this("cannot find " + e.getMessage());
	}

	public Lex getLex() {
		return this.lex;
	}

	public String getMessage() {
		return this.reason;
	}

	public String toString() {
		return "compile error: " + this.reason;
	}
}
