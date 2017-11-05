package org.eocencle.winger.javassist.bytecode.annotation;

public interface MemberValueVisitor {
	void visitAnnotationMemberValue(AnnotationMemberValue arg0);

	void visitArrayMemberValue(ArrayMemberValue arg0);

	void visitBooleanMemberValue(BooleanMemberValue arg0);

	void visitByteMemberValue(ByteMemberValue arg0);

	void visitCharMemberValue(CharMemberValue arg0);

	void visitDoubleMemberValue(DoubleMemberValue arg0);

	void visitEnumMemberValue(EnumMemberValue arg0);

	void visitFloatMemberValue(FloatMemberValue arg0);

	void visitIntegerMemberValue(IntegerMemberValue arg0);

	void visitLongMemberValue(LongMemberValue arg0);

	void visitShortMemberValue(ShortMemberValue arg0);

	void visitStringMemberValue(StringMemberValue arg0);

	void visitClassMemberValue(ClassMemberValue arg0);
}
