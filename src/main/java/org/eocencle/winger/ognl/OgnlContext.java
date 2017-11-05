package org.eocencle.winger.ognl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eocencle.winger.ognl.enhance.LocalReference;

public class OgnlContext implements Map {
	public static final String CONTEXT_CONTEXT_KEY = "context";
	public static final String ROOT_CONTEXT_KEY = "root";
	public static final String THIS_CONTEXT_KEY = "this";
	public static final String TRACE_EVALUATIONS_CONTEXT_KEY = "_traceEvaluations";
	public static final String LAST_EVALUATION_CONTEXT_KEY = "_lastEvaluation";
	public static final String KEEP_LAST_EVALUATION_CONTEXT_KEY = "_keepLastEvaluation";
	public static final String CLASS_RESOLVER_CONTEXT_KEY = "_classResolver";
	public static final String TYPE_CONVERTER_CONTEXT_KEY = "_typeConverter";
	public static final String MEMBER_ACCESS_CONTEXT_KEY = "_memberAccess";
	private static final String PROPERTY_KEY_PREFIX = "org.eocencle.winger.ognl";
	private static boolean DEFAULT_TRACE_EVALUATIONS = false;
	private static boolean DEFAULT_KEEP_LAST_EVALUATION = false;
	public static final ClassResolver DEFAULT_CLASS_RESOLVER = new DefaultClassResolver();
	public static final TypeConverter DEFAULT_TYPE_CONVERTER = new DefaultTypeConverter();
	public static final MemberAccess DEFAULT_MEMBER_ACCESS = new DefaultMemberAccess(false);
	private static Map RESERVED_KEYS = new HashMap(11);
	private Object _root;
	private Object _currentObject;
	private Node _currentNode;
	private boolean _traceEvaluations;
	private Evaluation _rootEvaluation;
	private Evaluation _currentEvaluation;
	private Evaluation _lastEvaluation;
	private boolean _keepLastEvaluation;
	private final Map _values;
	private ClassResolver _classResolver;
	private TypeConverter _typeConverter;
	private MemberAccess _memberAccess;
	private final List _typeStack;
	private final List _accessorStack;
	private int _localReferenceCounter;
	private Map _localReferenceMap;

	public OgnlContext() {
		this((ClassResolver) null, (TypeConverter) null, (MemberAccess) null);
	}

	public OgnlContext(ClassResolver classResolver, TypeConverter typeConverter, MemberAccess memberAccess) {
		this(classResolver, typeConverter, memberAccess, new HashMap(23));
	}

	public OgnlContext(Map values) {
		this((ClassResolver) null, (TypeConverter) null, (MemberAccess) null, values);
	}

	public OgnlContext(ClassResolver classResolver, TypeConverter typeConverter, MemberAccess memberAccess,
			Map values) {
		this._traceEvaluations = DEFAULT_TRACE_EVALUATIONS;
		this._keepLastEvaluation = DEFAULT_KEEP_LAST_EVALUATION;
		this._classResolver = DEFAULT_CLASS_RESOLVER;
		this._typeConverter = DEFAULT_TYPE_CONVERTER;
		this._memberAccess = DEFAULT_MEMBER_ACCESS;
		this._typeStack = new ArrayList(3);
		this._accessorStack = new ArrayList(3);
		this._localReferenceCounter = 0;
		this._localReferenceMap = null;
		this._values = values;
		if (classResolver != null) {
			this._classResolver = classResolver;
		}

		if (typeConverter != null) {
			this._typeConverter = typeConverter;
		}

		if (memberAccess != null) {
			this._memberAccess = memberAccess;
		}

	}

	public void setValues(Map value) {
		Iterator it = value.keySet().iterator();

		while (it.hasNext()) {
			Object k = it.next();
			this._values.put(k, value.get(k));
		}

	}

	public Map getValues() {
		return this._values;
	}

	public void setClassResolver(ClassResolver value) {
		if (value == null) {
			throw new IllegalArgumentException("cannot set ClassResolver to null");
		} else {
			this._classResolver = value;
		}
	}

	public ClassResolver getClassResolver() {
		return this._classResolver;
	}

	public void setTypeConverter(TypeConverter value) {
		if (value == null) {
			throw new IllegalArgumentException("cannot set TypeConverter to null");
		} else {
			this._typeConverter = value;
		}
	}

	public TypeConverter getTypeConverter() {
		return this._typeConverter;
	}

	public void setMemberAccess(MemberAccess value) {
		if (value == null) {
			throw new IllegalArgumentException("cannot set MemberAccess to null");
		} else {
			this._memberAccess = value;
		}
	}

	public MemberAccess getMemberAccess() {
		return this._memberAccess;
	}

	public void setRoot(Object value) {
		this._root = value;
		this._accessorStack.clear();
		this._typeStack.clear();
		this._currentObject = value;
		if (this._currentObject != null) {
			this.setCurrentType(this._currentObject.getClass());
		}

	}

	public Object getRoot() {
		return this._root;
	}

	public boolean getTraceEvaluations() {
		return this._traceEvaluations;
	}

	public void setTraceEvaluations(boolean value) {
		this._traceEvaluations = value;
	}

	public Evaluation getLastEvaluation() {
		return this._lastEvaluation;
	}

	public void setLastEvaluation(Evaluation value) {
		this._lastEvaluation = value;
	}

	public void recycleLastEvaluation() {
		OgnlRuntime.getEvaluationPool().recycleAll(this._lastEvaluation);
		this._lastEvaluation = null;
	}

	public boolean getKeepLastEvaluation() {
		return this._keepLastEvaluation;
	}

	public void setKeepLastEvaluation(boolean value) {
		this._keepLastEvaluation = value;
	}

	public void setCurrentObject(Object value) {
		this._currentObject = value;
	}

	public Object getCurrentObject() {
		return this._currentObject;
	}

	public void setCurrentAccessor(Class type) {
		this._accessorStack.add(type);
	}

	public Class getCurrentAccessor() {
		return this._accessorStack.isEmpty() ? null : (Class) this._accessorStack.get(this._accessorStack.size() - 1);
	}

	public Class getPreviousAccessor() {
		return this._accessorStack.isEmpty() ? null
				: (this._accessorStack.size() > 1 ? (Class) this._accessorStack.get(this._accessorStack.size() - 2)
						: null);
	}

	public Class getFirstAccessor() {
		return this._accessorStack.isEmpty() ? null : (Class) this._accessorStack.get(0);
	}

	public Class getCurrentType() {
		return this._typeStack.isEmpty() ? null : (Class) this._typeStack.get(this._typeStack.size() - 1);
	}

	public void setCurrentType(Class type) {
		this._typeStack.add(type);
	}

	public Class getPreviousType() {
		return this._typeStack.isEmpty() ? null
				: (this._typeStack.size() > 1 ? (Class) this._typeStack.get(this._typeStack.size() - 2) : null);
	}

	public void setPreviousType(Class type) {
		if (!this._typeStack.isEmpty() && this._typeStack.size() >= 2) {
			this._typeStack.set(this._typeStack.size() - 2, type);
		}
	}

	public Class getFirstType() {
		return this._typeStack.isEmpty() ? null : (Class) this._typeStack.get(0);
	}

	public void setCurrentNode(Node value) {
		this._currentNode = value;
	}

	public Node getCurrentNode() {
		return this._currentNode;
	}

	public Evaluation getCurrentEvaluation() {
		return this._currentEvaluation;
	}

	public void setCurrentEvaluation(Evaluation value) {
		this._currentEvaluation = value;
	}

	public Evaluation getRootEvaluation() {
		return this._rootEvaluation;
	}

	public void setRootEvaluation(Evaluation value) {
		this._rootEvaluation = value;
	}

	public Evaluation getEvaluation(int relativeIndex) {
		Evaluation result = null;
		if (relativeIndex <= 0) {
			result = this._currentEvaluation;

			while (true) {
				++relativeIndex;
				if (relativeIndex >= 0 || result == null) {
					break;
				}

				result = result.getParent();
			}
		}

		return result;
	}

	public void pushEvaluation(Evaluation value) {
		if (this._currentEvaluation != null) {
			this._currentEvaluation.addChild(value);
		} else {
			this.setRootEvaluation(value);
		}

		this.setCurrentEvaluation(value);
	}

	public Evaluation popEvaluation() {
		Evaluation result = this._currentEvaluation;
		this.setCurrentEvaluation(result.getParent());
		if (this._currentEvaluation == null) {
			this.setLastEvaluation(this.getKeepLastEvaluation() ? result : null);
			this.setRootEvaluation((Evaluation) null);
			this.setCurrentNode((Node) null);
		}

		return result;
	}

	public int incrementLocalReferenceCounter() {
		return ++this._localReferenceCounter;
	}

	public void addLocalReference(String key, LocalReference reference) {
		if (this._localReferenceMap == null) {
			this._localReferenceMap = new LinkedHashMap();
		}

		this._localReferenceMap.put(key, reference);
	}

	public Map getLocalReferences() {
		return this._localReferenceMap;
	}

	public int size() {
		return this._values.size();
	}

	public boolean isEmpty() {
		return this._values.isEmpty();
	}

	public boolean containsKey(Object key) {
		return this._values.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return this._values.containsValue(value);
	}

	public Object get(Object key) {
		Object result;
		if (RESERVED_KEYS.containsKey(key)) {
			if (key.equals("this")) {
				result = this.getCurrentObject();
			} else if (key.equals("root")) {
				result = this.getRoot();
			} else if (key.equals("context")) {
				result = this;
			} else if (key.equals("_traceEvaluations")) {
				result = this.getTraceEvaluations() ? Boolean.TRUE : Boolean.FALSE;
			} else if (key.equals("_lastEvaluation")) {
				result = this.getLastEvaluation();
			} else if (key.equals("_keepLastEvaluation")) {
				result = this.getKeepLastEvaluation() ? Boolean.TRUE : Boolean.FALSE;
			} else if (key.equals("_classResolver")) {
				result = this.getClassResolver();
			} else if (key.equals("_typeConverter")) {
				result = this.getTypeConverter();
			} else {
				if (!key.equals("_memberAccess")) {
					throw new IllegalArgumentException("unknown reserved key \'" + key + "\'");
				}

				result = this.getMemberAccess();
			}
		} else {
			result = this._values.get(key);
		}

		return result;
	}

	public Object put(Object key, Object value) {
		Object result;
		if (RESERVED_KEYS.containsKey(key)) {
			if (key.equals("this")) {
				result = this.getCurrentObject();
				this.setCurrentObject(value);
			} else if (key.equals("root")) {
				result = this.getRoot();
				this.setRoot(value);
			} else {
				if (key.equals("context")) {
					throw new IllegalArgumentException("can\'t change context in context");
				}

				if (key.equals("_traceEvaluations")) {
					result = this.getTraceEvaluations() ? Boolean.TRUE : Boolean.FALSE;
					this.setTraceEvaluations(OgnlOps.booleanValue(value));
				} else if (key.equals("_lastEvaluation")) {
					result = this.getLastEvaluation();
					this._lastEvaluation = (Evaluation) value;
				} else if (key.equals("_keepLastEvaluation")) {
					result = this.getKeepLastEvaluation() ? Boolean.TRUE : Boolean.FALSE;
					this.setKeepLastEvaluation(OgnlOps.booleanValue(value));
				} else if (key.equals("_classResolver")) {
					result = this.getClassResolver();
					this.setClassResolver((ClassResolver) value);
				} else if (key.equals("_typeConverter")) {
					result = this.getTypeConverter();
					this.setTypeConverter((TypeConverter) value);
				} else {
					if (!key.equals("_memberAccess")) {
						throw new IllegalArgumentException("unknown reserved key \'" + key + "\'");
					}

					result = this.getMemberAccess();
					this.setMemberAccess((MemberAccess) value);
				}
			}
		} else {
			result = this._values.put(key, value);
		}

		return result;
	}

	public Object remove(Object key) {
		Object result;
		if (RESERVED_KEYS.containsKey(key)) {
			if (key.equals("this")) {
				result = this.getCurrentObject();
				this.setCurrentObject((Object) null);
			} else if (key.equals("root")) {
				result = this.getRoot();
				this.setRoot((Object) null);
			} else {
				if (key.equals("context")) {
					throw new IllegalArgumentException("can\'t remove context from context");
				}

				if (key.equals("_traceEvaluations")) {
					throw new IllegalArgumentException("can\'t remove _traceEvaluations from context");
				}

				if (key.equals("_lastEvaluation")) {
					result = this._lastEvaluation;
					this.setLastEvaluation((Evaluation) null);
				} else {
					if (key.equals("_keepLastEvaluation")) {
						throw new IllegalArgumentException("can\'t remove _keepLastEvaluation from context");
					}

					if (key.equals("_classResolver")) {
						result = this.getClassResolver();
						this.setClassResolver((ClassResolver) null);
					} else if (key.equals("_typeConverter")) {
						result = this.getTypeConverter();
						this.setTypeConverter((TypeConverter) null);
					} else {
						if (!key.equals("_memberAccess")) {
							throw new IllegalArgumentException("unknown reserved key \'" + key + "\'");
						}

						result = this.getMemberAccess();
						this.setMemberAccess((MemberAccess) null);
					}
				}
			}
		} else {
			result = this._values.remove(key);
		}

		return result;
	}

	public void putAll(Map t) {
		Iterator it = t.keySet().iterator();

		while (it.hasNext()) {
			Object k = it.next();
			this.put(k, t.get(k));
		}

	}

	public void clear() {
		this._values.clear();
		this._typeStack.clear();
		this._accessorStack.clear();
		this._localReferenceCounter = 0;
		if (this._localReferenceMap != null) {
			this._localReferenceMap.clear();
		}

		this.setRoot((Object) null);
		this.setCurrentObject((Object) null);
		this.setRootEvaluation((Evaluation) null);
		this.setCurrentEvaluation((Evaluation) null);
		this.setLastEvaluation((Evaluation) null);
		this.setCurrentNode((Node) null);
		this.setClassResolver(DEFAULT_CLASS_RESOLVER);
		this.setTypeConverter(DEFAULT_TYPE_CONVERTER);
		this.setMemberAccess(DEFAULT_MEMBER_ACCESS);
	}

	public Set keySet() {
		return this._values.keySet();
	}

	public Collection values() {
		return this._values.values();
	}

	public Set entrySet() {
		return this._values.entrySet();
	}

	public boolean equals(Object o) {
		return this._values.equals(o);
	}

	public int hashCode() {
		return this._values.hashCode();
	}

	static {
		RESERVED_KEYS.put("context", (Object) null);
		RESERVED_KEYS.put("root", (Object) null);
		RESERVED_KEYS.put("this", (Object) null);
		RESERVED_KEYS.put("_traceEvaluations", (Object) null);
		RESERVED_KEYS.put("_lastEvaluation", (Object) null);
		RESERVED_KEYS.put("_keepLastEvaluation", (Object) null);
		RESERVED_KEYS.put("_classResolver", (Object) null);
		RESERVED_KEYS.put("_typeConverter", (Object) null);
		RESERVED_KEYS.put("_memberAccess", (Object) null);

		try {
			String s;
			if ((s = System.getProperty("org.eocencle.winger.ognl.traceEvaluations")) != null) {
				DEFAULT_TRACE_EVALUATIONS = Boolean.valueOf(s.trim()).booleanValue();
			}

			if ((s = System.getProperty("org.eocencle.winger.ognl.keepLastEvaluation")) != null) {
				DEFAULT_KEEP_LAST_EVALUATION = Boolean.valueOf(s.trim()).booleanValue();
			}
		} catch (SecurityException arg1) {
			;
		}

	}
}
