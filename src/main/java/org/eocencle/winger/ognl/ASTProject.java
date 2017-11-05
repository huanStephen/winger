package org.eocencle.winger.ognl;

import java.util.ArrayList;
import java.util.Enumeration;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTProject extends SimpleNode {
	public ASTProject(int id) {
		super(id);
	}

	public ASTProject(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Node expr = this._children[0];
		ArrayList answer = new ArrayList();
		ElementsAccessor elementsAccessor = OgnlRuntime.getElementsAccessor(OgnlRuntime.getTargetClass(source));
		Enumeration e = elementsAccessor.getElements(source);

		while (e.hasMoreElements()) {
			answer.add(expr.getValue(context, e.nextElement()));
		}

		return answer;
	}

	public String toString() {
		return "{ " + this._children[0] + " }";
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		throw new UnsupportedCompilationException("Projection expressions not supported as native java yet.");
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		throw new UnsupportedCompilationException("Projection expressions not supported as native java yet.");
	}
}
