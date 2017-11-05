package org.eocencle.winger.ognl;

public interface OgnlParserTreeConstants {
	int JJTVOID = 0;
	int JJTSEQUENCE = 1;
	int JJTASSIGN = 2;
	int JJTTEST = 3;
	int JJTOR = 4;
	int JJTAND = 5;
	int JJTBITOR = 6;
	int JJTXOR = 7;
	int JJTBITAND = 8;
	int JJTEQ = 9;
	int JJTNOTEQ = 10;
	int JJTLESS = 11;
	int JJTGREATER = 12;
	int JJTLESSEQ = 13;
	int JJTGREATEREQ = 14;
	int JJTIN = 15;
	int JJTNOTIN = 16;
	int JJTSHIFTLEFT = 17;
	int JJTSHIFTRIGHT = 18;
	int JJTUNSIGNEDSHIFTRIGHT = 19;
	int JJTADD = 20;
	int JJTSUBTRACT = 21;
	int JJTMULTIPLY = 22;
	int JJTDIVIDE = 23;
	int JJTREMAINDER = 24;
	int JJTNEGATE = 25;
	int JJTBITNEGATE = 26;
	int JJTNOT = 27;
	int JJTINSTANCEOF = 28;
	int JJTCHAIN = 29;
	int JJTEVAL = 30;
	int JJTCONST = 31;
	int JJTTHISVARREF = 32;
	int JJTROOTVARREF = 33;
	int JJTVARREF = 34;
	int JJTLIST = 35;
	int JJTMAP = 36;
	int JJTKEYVALUE = 37;
	int JJTSTATICFIELD = 38;
	int JJTCTOR = 39;
	int JJTPROPERTY = 40;
	int JJTSTATICMETHOD = 41;
	int JJTMETHOD = 42;
	int JJTPROJECT = 43;
	int JJTSELECT = 44;
	int JJTSELECTFIRST = 45;
	int JJTSELECTLAST = 46;
	String[] jjtNodeName = new String[] { "void", "Sequence", "Assign", "Test", "Or", "And", "BitOr", "Xor", "BitAnd",
			"Eq", "NotEq", "Less", "Greater", "LessEq", "GreaterEq", "In", "NotIn", "ShiftLeft", "ShiftRight",
			"UnsignedShiftRight", "Add", "Subtract", "Multiply", "Divide", "Remainder", "Negate", "BitNegate", "Not",
			"Instanceof", "Chain", "Eval", "Const", "ThisVarRef", "RootVarRef", "VarRef", "List", "Map", "KeyValue",
			"StaticField", "Ctor", "Property", "StaticMethod", "Method", "Project", "Select", "SelectFirst",
			"SelectLast" };
}
