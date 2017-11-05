package org.eocencle.winger.ognl.enhance;

public class LocalReferenceImpl implements LocalReference {
	String _name;
	Class _type;
	String _expression;

	public LocalReferenceImpl(String name, String expression, Class type) {
		this._name = name;
		this._type = type;
		this._expression = expression;
	}

	public String getName() {
		return this._name;
	}

	public String getExpression() {
		return this._expression;
	}

	public Class getType() {
		return this._type;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			LocalReferenceImpl that;
			label45: {
				that = (LocalReferenceImpl) o;
				if (this._expression != null) {
					if (this._expression.equals(that._expression)) {
						break label45;
					}
				} else if (that._expression == null) {
					break label45;
				}

				return false;
			}

			label38: {
				if (this._name != null) {
					if (this._name.equals(that._name)) {
						break label38;
					}
				} else if (that._name == null) {
					break label38;
				}

				return false;
			}

			if (this._type != null) {
				if (!this._type.equals(that._type)) {
					return false;
				}
			} else if (that._type != null) {
				return false;
			}

			return true;
		} else {
			return false;
		}
	}

	public int hashCode() {
		int result = this._name != null ? this._name.hashCode() : 0;
		result = 31 * result + (this._type != null ? this._type.hashCode() : 0);
		result = 31 * result + (this._expression != null ? this._expression.hashCode() : 0);
		return result;
	}

	public String toString() {
		return "LocalReferenceImpl[_name=\'" + this._name + '\'' + '\n' + ", _type=" + this._type + '\n'
				+ ", _expression=\'" + this._expression + '\'' + '\n' + ']';
	}
}
