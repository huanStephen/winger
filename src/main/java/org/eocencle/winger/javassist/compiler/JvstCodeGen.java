package org.eocencle.winger.javassist.compiler;

import org.eocencle.winger.javassist.ClassPool;
import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.CtPrimitiveType;
import org.eocencle.winger.javassist.NotFoundException;
import org.eocencle.winger.javassist.bytecode.Bytecode;
import org.eocencle.winger.javassist.bytecode.Descriptor;
import org.eocencle.winger.javassist.compiler.ast.ASTList;
import org.eocencle.winger.javassist.compiler.ast.ASTree;
import org.eocencle.winger.javassist.compiler.ast.CallExpr;
import org.eocencle.winger.javassist.compiler.ast.CastExpr;
import org.eocencle.winger.javassist.compiler.ast.Declarator;
import org.eocencle.winger.javassist.compiler.ast.Expr;
import org.eocencle.winger.javassist.compiler.ast.Member;
import org.eocencle.winger.javassist.compiler.ast.Stmnt;
import org.eocencle.winger.javassist.compiler.ast.Symbol;

public class JvstCodeGen extends MemberCodeGen {
	String paramArrayName = null;
	String paramListName = null;
	CtClass[] paramTypeList = null;
	private int paramVarBase = 0;
	private boolean useParam0 = false;
	private String param0Type = null;
	public static final String sigName = "$sig";
	public static final String dollarTypeName = "$type";
	public static final String clazzName = "$class";
	private CtClass dollarType = null;
	CtClass returnType = null;
	String returnCastName = null;
	private String returnVarName = null;
	public static final String wrapperCastName = "$w";
	String proceedName = null;
	public static final String cflowName = "$cflow";
	ProceedHandler procHandler = null;

	public JvstCodeGen(Bytecode b, CtClass cc, ClassPool cp) {
		super(b, cc, cp);
		this.setTypeChecker(new JvstTypeChecker(cc, cp, this));
	}

	private int indexOfParam1() {
		return this.paramVarBase + (this.useParam0 ? 1 : 0);
	}

	public void setProceedHandler(ProceedHandler h, String name) {
		this.proceedName = name;
		this.procHandler = h;
	}

	public void addNullIfVoid() {
		if (this.exprType == 344) {
			this.bytecode.addOpcode(1);
			this.exprType = 307;
			this.arrayDim = 0;
			this.className = "java/lang/Object";
		}

	}

	public void atMember(Member mem) throws CompileError {
		String name = mem.get();
		if (name.equals(this.paramArrayName)) {
			compileParameterList(this.bytecode, this.paramTypeList, this.indexOfParam1());
			this.exprType = 307;
			this.arrayDim = 1;
			this.className = "java/lang/Object";
		} else if (name.equals("$sig")) {
			this.bytecode.addLdc(Descriptor.ofMethod(this.returnType, this.paramTypeList));
			this.bytecode.addInvokestatic("org/apache/ibatis/javassist/runtime/Desc", "getParams",
					"(Ljava/lang/String;)[Ljava/lang/Class;");
			this.exprType = 307;
			this.arrayDim = 1;
			this.className = "java/lang/Class";
		} else if (name.equals("$type")) {
			if (this.dollarType == null) {
				throw new CompileError("$type is not available");
			}

			this.bytecode.addLdc(Descriptor.of(this.dollarType));
			this.callGetType("getType");
		} else if (name.equals("$class")) {
			if (this.param0Type == null) {
				throw new CompileError("$class is not available");
			}

			this.bytecode.addLdc(this.param0Type);
			this.callGetType("getClazz");
		} else {
			super.atMember(mem);
		}

	}

	private void callGetType(String method) {
		this.bytecode.addInvokestatic("org/apache/ibatis/javassist/runtime/Desc", method,
				"(Ljava/lang/String;)Ljava/lang/Class;");
		this.exprType = 307;
		this.arrayDim = 0;
		this.className = "java/lang/Class";
	}

	protected void atFieldAssign(Expr expr, int op, ASTree left, ASTree right, boolean doDup) throws CompileError {
		if (left instanceof Member && ((Member) left).get().equals(this.paramArrayName)) {
			if (op != 61) {
				throw new CompileError("bad operator for " + this.paramArrayName);
			}

			right.accept(this);
			if (this.arrayDim != 1 || this.exprType != 307) {
				throw new CompileError("invalid type for " + this.paramArrayName);
			}

			this.atAssignParamList(this.paramTypeList, this.bytecode);
			if (!doDup) {
				this.bytecode.addOpcode(87);
			}
		} else {
			super.atFieldAssign(expr, op, left, right, doDup);
		}

	}

	protected void atAssignParamList(CtClass[] params, Bytecode code) throws CompileError {
		if (params != null) {
			int varNo = this.indexOfParam1();
			int n = params.length;

			for (int i = 0; i < n; ++i) {
				code.addOpcode(89);
				code.addIconst(i);
				code.addOpcode(50);
				this.compileUnwrapValue(params[i], code);
				code.addStore(varNo, params[i]);
				varNo += is2word(this.exprType, this.arrayDim) ? 2 : 1;
			}

		}
	}

	public void atCastExpr(CastExpr expr) throws CompileError {
		ASTList classname = expr.getClassName();
		if (classname != null && expr.getArrayDim() == 0) {
			ASTree p = classname.head();
			if (p instanceof Symbol && classname.tail() == null) {
				String typename = ((Symbol) p).get();
				if (typename.equals(this.returnCastName)) {
					this.atCastToRtype(expr);
					return;
				}

				if (typename.equals("$w")) {
					this.atCastToWrapper(expr);
					return;
				}
			}
		}

		super.atCastExpr(expr);
	}

	protected void atCastToRtype(CastExpr expr) throws CompileError {
		expr.getOprand().accept(this);
		if (this.exprType != 344 && !isRefType(this.exprType) && this.arrayDim <= 0) {
			if (!(this.returnType instanceof CtPrimitiveType)) {
				throw new CompileError("invalid cast");
			}

			CtPrimitiveType pt = (CtPrimitiveType) this.returnType;
			int destType = MemberResolver.descToType(pt.getDescriptor());
			this.atNumCastExpr(this.exprType, destType);
			this.exprType = destType;
			this.arrayDim = 0;
			this.className = null;
		} else {
			this.compileUnwrapValue(this.returnType, this.bytecode);
		}

	}

	protected void atCastToWrapper(CastExpr expr) throws CompileError {
		expr.getOprand().accept(this);
		if (!isRefType(this.exprType) && this.arrayDim <= 0) {
			CtClass clazz = this.resolver.lookupClass(this.exprType, this.arrayDim, this.className);
			if (clazz instanceof CtPrimitiveType) {
				CtPrimitiveType pt = (CtPrimitiveType) clazz;
				String wrapper = pt.getWrapperName();
				this.bytecode.addNew(wrapper);
				this.bytecode.addOpcode(89);
				if (pt.getDataSize() > 1) {
					this.bytecode.addOpcode(94);
				} else {
					this.bytecode.addOpcode(93);
				}

				this.bytecode.addOpcode(88);
				this.bytecode.addInvokespecial(wrapper, "<init>", "(" + pt.getDescriptor() + ")V");
				this.exprType = 307;
				this.arrayDim = 0;
				this.className = "java/lang/Object";
			}

		}
	}

	public void atCallExpr(CallExpr expr) throws CompileError {
		ASTree method = expr.oprand1();
		if (method instanceof Member) {
			String name = ((Member) method).get();
			if (this.procHandler != null && name.equals(this.proceedName)) {
				this.procHandler.doit(this, this.bytecode, (ASTList) expr.oprand2());
				return;
			}

			if (name.equals("$cflow")) {
				this.atCflow((ASTList) expr.oprand2());
				return;
			}
		}

		super.atCallExpr(expr);
	}

	protected void atCflow(ASTList cname) throws CompileError {
		StringBuffer sbuf = new StringBuffer();
		if (cname != null && cname.tail() == null) {
			makeCflowName(sbuf, cname.head());
			String name = sbuf.toString();
			Object[] names = this.resolver.getClassPool().lookupCflow(name);
			if (names == null) {
				throw new CompileError("no such $cflow: " + name);
			} else {
				this.bytecode.addGetstatic((String) names[0], (String) names[1],
						"Lorg/apache/ibatis/javassist/runtime/Cflow;");
				this.bytecode.addInvokevirtual("org.eocencle.winger.javassist.runtime.Cflow", "value", "()I");
				this.exprType = 324;
				this.arrayDim = 0;
				this.className = null;
			}
		} else {
			throw new CompileError("bad $cflow");
		}
	}

	private static void makeCflowName(StringBuffer sbuf, ASTree name) throws CompileError {
		if (name instanceof Symbol) {
			sbuf.append(((Symbol) name).get());
		} else {
			if (name instanceof Expr) {
				Expr expr = (Expr) name;
				if (expr.getOperator() == 46) {
					makeCflowName(sbuf, expr.oprand1());
					sbuf.append('.');
					makeCflowName(sbuf, expr.oprand2());
					return;
				}
			}

			throw new CompileError("bad $cflow");
		}
	}

	public boolean isParamListName(ASTList args) {
		if (this.paramTypeList != null && args != null && args.tail() == null) {
			ASTree left = args.head();
			return left instanceof Member && ((Member) left).get().equals(this.paramListName);
		} else {
			return false;
		}
	}

	public int getMethodArgsLength(ASTList args) {
		String pname = this.paramListName;

		int n;
		for (n = 0; args != null; args = args.tail()) {
			ASTree a = args.head();
			if (a instanceof Member && ((Member) a).get().equals(pname)) {
				if (this.paramTypeList != null) {
					n += this.paramTypeList.length;
				}
			} else {
				++n;
			}
		}

		return n;
	}

	public void atMethodArgs(ASTList args, int[] types, int[] dims, String[] cnames) throws CompileError {
		CtClass[] params = this.paramTypeList;
		String pname = this.paramListName;

		for (int i = 0; args != null; args = args.tail()) {
			ASTree a = args.head();
			if (a instanceof Member && ((Member) a).get().equals(pname)) {
				if (params != null) {
					int n = params.length;
					int regno = this.indexOfParam1();

					for (int k = 0; k < n; ++k) {
						CtClass p = params[k];
						regno += this.bytecode.addLoad(regno, p);
						this.setType(p);
						types[i] = this.exprType;
						dims[i] = this.arrayDim;
						cnames[i] = this.className;
						++i;
					}
				}
			} else {
				a.accept(this);
				types[i] = this.exprType;
				dims[i] = this.arrayDim;
				cnames[i] = this.className;
				++i;
			}
		}

	}

	void compileInvokeSpecial(ASTree target, String classname, String methodname, String descriptor, ASTList args)
			throws CompileError {
		target.accept(this);
		int nargs = this.getMethodArgsLength(args);
		this.atMethodArgs(args, new int[nargs], new int[nargs], new String[nargs]);
		this.bytecode.addInvokespecial(classname, methodname, descriptor);
		this.setReturnType(descriptor, false, false);
		this.addNullIfVoid();
	}

	protected void atReturnStmnt(Stmnt st) throws CompileError {
		ASTree result = st.getLeft();
		if (result != null && this.returnType == CtClass.voidType) {
			this.compileExpr(result);
			if (is2word(this.exprType, this.arrayDim)) {
				this.bytecode.addOpcode(88);
			} else if (this.exprType != 344) {
				this.bytecode.addOpcode(87);
			}

			result = null;
		}

		this.atReturnStmnt2(result);
	}

	public int recordReturnType(CtClass type, String castName, String resultName, SymbolTable tbl) throws CompileError {
		this.returnType = type;
		this.returnCastName = castName;
		this.returnVarName = resultName;
		if (resultName == null) {
			return -1;
		} else {
			int varNo = this.getMaxLocals();
			int locals = varNo + this.recordVar(type, resultName, varNo, tbl);
			this.setMaxLocals(locals);
			return varNo;
		}
	}

	public void recordType(CtClass t) {
		this.dollarType = t;
	}

	public int recordParams(CtClass[] params, boolean isStatic, String prefix, String paramVarName, String paramsName,
			SymbolTable tbl) throws CompileError {
		return this.recordParams(params, isStatic, prefix, paramVarName, paramsName, !isStatic, 0, this.getThisName(),
				tbl);
	}

	public int recordParams(CtClass[] params, boolean isStatic, String prefix, String paramVarName, String paramsName,
			boolean use0, int paramBase, String target, SymbolTable tbl) throws CompileError {
		this.paramTypeList = params;
		this.paramArrayName = paramVarName;
		this.paramListName = paramsName;
		this.paramVarBase = paramBase;
		this.useParam0 = use0;
		if (target != null) {
			this.param0Type = MemberResolver.jvmToJavaName(target);
		}

		this.inStaticMethod = isStatic;
		int varNo = paramBase;
		if (use0) {
			String i = prefix + "0";
			String arg10002 = MemberResolver.javaToJvmName(target);
			varNo = paramBase + 1;
			Declarator decl = new Declarator(307, arg10002, 0, paramBase, new Symbol(i));
			tbl.append(i, decl);
		}

		for (int arg12 = 0; arg12 < params.length; ++arg12) {
			varNo += this.recordVar(params[arg12], prefix + (arg12 + 1), varNo, tbl);
		}

		if (this.getMaxLocals() < varNo) {
			this.setMaxLocals(varNo);
		}

		return varNo;
	}

	public int recordVariable(CtClass type, String varName, SymbolTable tbl) throws CompileError {
		if (varName == null) {
			return -1;
		} else {
			int varNo = this.getMaxLocals();
			int locals = varNo + this.recordVar(type, varName, varNo, tbl);
			this.setMaxLocals(locals);
			return varNo;
		}
	}

	private int recordVar(CtClass cc, String varName, int varNo, SymbolTable tbl) throws CompileError {
		if (cc == CtClass.voidType) {
			this.exprType = 307;
			this.arrayDim = 0;
			this.className = "java/lang/Object";
		} else {
			this.setType(cc);
		}

		Declarator decl = new Declarator(this.exprType, this.className, this.arrayDim, varNo, new Symbol(varName));
		tbl.append(varName, decl);
		return is2word(this.exprType, this.arrayDim) ? 2 : 1;
	}

	public void recordVariable(String typeDesc, String varName, int varNo, SymbolTable tbl) throws CompileError {
		int dim;
		char c;
		for (dim = 0; (c = typeDesc.charAt(dim)) == 91; ++dim) {
			;
		}

		int type = MemberResolver.descToType(c);
		String cname = null;
		if (type == 307) {
			if (dim == 0) {
				cname = typeDesc.substring(1, typeDesc.length() - 1);
			} else {
				cname = typeDesc.substring(dim + 1, typeDesc.length() - 1);
			}
		}

		Declarator decl = new Declarator(type, cname, dim, varNo, new Symbol(varName));
		tbl.append(varName, decl);
	}

	public static int compileParameterList(Bytecode code, CtClass[] params, int regno) {
		if (params == null) {
			code.addIconst(0);
			code.addAnewarray("java.lang.Object");
			return 1;
		} else {
			CtClass[] args = new CtClass[1];
			int n = params.length;
			code.addIconst(n);
			code.addAnewarray("java.lang.Object");

			for (int i = 0; i < n; ++i) {
				code.addOpcode(89);
				code.addIconst(i);
				if (params[i].isPrimitive()) {
					CtPrimitiveType pt = (CtPrimitiveType) params[i];
					String wrapper = pt.getWrapperName();
					code.addNew(wrapper);
					code.addOpcode(89);
					int s = code.addLoad(regno, pt);
					regno += s;
					args[0] = pt;
					code.addInvokespecial(wrapper, "<init>", Descriptor.ofMethod(CtClass.voidType, args));
				} else {
					code.addAload(regno);
					++regno;
				}

				code.addOpcode(83);
			}

			return 8;
		}
	}

	protected void compileUnwrapValue(CtClass type, Bytecode code) throws CompileError {
		if (type == CtClass.voidType) {
			this.addNullIfVoid();
		} else if (this.exprType == 344) {
			throw new CompileError("invalid type for " + this.returnCastName);
		} else {
			if (type instanceof CtPrimitiveType) {
				CtPrimitiveType pt = (CtPrimitiveType) type;
				String wrapper = pt.getWrapperName();
				code.addCheckcast(wrapper);
				code.addInvokevirtual(wrapper, pt.getGetMethodName(), pt.getGetMethodDescriptor());
				this.setType(type);
			} else {
				code.addCheckcast(type);
				this.setType(type);
			}

		}
	}

	public void setType(CtClass type) throws CompileError {
		this.setType(type, 0);
	}

	private void setType(CtClass type, int dim) throws CompileError {
		if (type.isPrimitive()) {
			CtPrimitiveType e = (CtPrimitiveType) type;
			this.exprType = MemberResolver.descToType(e.getDescriptor());
			this.arrayDim = dim;
			this.className = null;
		} else if (type.isArray()) {
			try {
				this.setType(type.getComponentType(), dim + 1);
			} catch (NotFoundException arg3) {
				throw new CompileError("undefined type: " + type.getName());
			}
		} else {
			this.exprType = 307;
			this.arrayDim = dim;
			this.className = MemberResolver.javaToJvmName(type.getName());
		}

	}

	public void doNumCast(CtClass type) throws CompileError {
		if (this.arrayDim == 0 && !isRefType(this.exprType)) {
			if (!(type instanceof CtPrimitiveType)) {
				throw new CompileError("type mismatch");
			}

			CtPrimitiveType pt = (CtPrimitiveType) type;
			this.atNumCastExpr(this.exprType, MemberResolver.descToType(pt.getDescriptor()));
		}

	}
}
