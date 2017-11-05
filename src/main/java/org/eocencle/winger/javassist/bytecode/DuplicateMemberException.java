package org.eocencle.winger.javassist.bytecode;

import org.eocencle.winger.javassist.CannotCompileException;

public class DuplicateMemberException extends CannotCompileException {
	public DuplicateMemberException(String msg) {
		super(msg);
	}
}
