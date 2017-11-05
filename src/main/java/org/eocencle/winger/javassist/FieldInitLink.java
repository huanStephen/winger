package org.eocencle.winger.javassist;

import org.eocencle.winger.javassist.CtField.Initializer;

public class FieldInitLink {
	FieldInitLink next = null;
	CtField field;
	Initializer init;

	FieldInitLink(CtField f, Initializer i) {
		this.field = f;
		this.init = i;
	}
}
