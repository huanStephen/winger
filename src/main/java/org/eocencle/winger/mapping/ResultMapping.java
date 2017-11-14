package org.eocencle.winger.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.type.JdbcType;
import org.eocencle.winger.type.TypeHandler;
import org.eocencle.winger.type.TypeHandlerRegistry;

public class ResultMapping {
	private Configuration configuration;
	private String property;
	private String column;
	private Class<?> javaType;
	private JdbcType jdbcType;
	private TypeHandler<?> typeHandler;
	private String nestedResultMapId;
	private String nestedQueryId;
	private Set<String> notNullColumns;
	private String columnPrefix;
	private List<ResultFlag> flags;
	private List<ResultMapping> composites;

	private ResultMapping() {
	}

	public static class Builder {
		private ResultMapping resultMapping = new ResultMapping();

		public Builder(Configuration configuration, String property, String column, TypeHandler<?> typeHandler) {
			resultMapping.configuration = configuration;
			resultMapping.property = property;
			resultMapping.column = column;
			resultMapping.typeHandler = typeHandler;
			resultMapping.flags = new ArrayList<ResultFlag>();
			resultMapping.composites = new ArrayList<ResultMapping>();
		}

		public Builder(Configuration configuration, String property, String column, Class<?> javaType) {
			resultMapping.configuration = configuration;
			resultMapping.property = property;
			resultMapping.column = column;
			resultMapping.javaType = javaType;
			resultMapping.flags = new ArrayList<ResultFlag>();
			resultMapping.composites = new ArrayList<ResultMapping>();
		}

		public Builder(Configuration configuration, String property) {
			resultMapping.configuration = configuration;
			resultMapping.property = property;
			resultMapping.flags = new ArrayList<ResultFlag>();
			resultMapping.composites = new ArrayList<ResultMapping>();
		}

		public Builder javaType(Class<?> javaType) {
			resultMapping.javaType = javaType;
			return this;
		}

		public Builder jdbcType(JdbcType jdbcType) {
			resultMapping.jdbcType = jdbcType;
			return this;
		}

		public Builder nestedResultMapId(String nestedResultMapId) {
			resultMapping.nestedResultMapId = nestedResultMapId;
			return this;
		}

		public Builder nestedQueryId(String nestedQueryId) {
			resultMapping.nestedQueryId = nestedQueryId;
			return this;
		}

		public Builder notNullColumns(Set<String> notNullColumns) {
			resultMapping.notNullColumns = notNullColumns;
			return this;
		}

		public Builder columnPrefix(String columnPrefix) {
			resultMapping.columnPrefix = columnPrefix;
			return this;
		}

		public Builder flags(List<ResultFlag> flags) {
			resultMapping.flags = flags;
			return this;
		}

		public Builder typeHandler(TypeHandler<?> typeHandler) {
			resultMapping.typeHandler = typeHandler;
			return this;
		}

		public Builder composites(List<ResultMapping> composites) {
			resultMapping.composites = composites;
			return this;
		}

		public ResultMapping build() {
			validate();
			// lock down collections
			resultMapping.flags = Collections.unmodifiableList(resultMapping.flags);
			resultMapping.composites = Collections.unmodifiableList(resultMapping.composites);
			resolveTypeHandler();
			return resultMapping;
		}

		private void validate() {
			// Issue 4: column is mandatory on nested queries
			if (resultMapping.nestedQueryId != null && resultMapping.column == null && resultMapping.composites.size() == 0) {
				throw new IllegalStateException("Missing column attribute for nested select in property " + resultMapping.property);
			}
			// Issue 697: cannot define both nestedQueryId and nestedResultMapId
			if (resultMapping.nestedQueryId != null && resultMapping.nestedResultMapId != null) {
				throw new IllegalStateException("Cannot define both nestedQueryId and nestedResultMapId in property " + resultMapping.property);
			}
		}

		private void resolveTypeHandler() {
			if (resultMapping.typeHandler == null) {
				if (resultMapping.javaType != null) {
					Configuration configuration = resultMapping.configuration;
					TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
					resultMapping.typeHandler = typeHandlerRegistry.getTypeHandler(resultMapping.javaType, resultMapping.jdbcType);
				}
			}
		}

		public Builder column(String column) {
			resultMapping.column = column;
			return this;
		}
	}

	public String getProperty() {
		return property;
	}

	public String getColumn() {
		return column;
	}

	public Class<?> getJavaType() {
		return javaType;
	}

	public JdbcType getJdbcType() {
		return jdbcType;
	}

	public TypeHandler<?> getTypeHandler() {
		return typeHandler;
	}

	public String getNestedResultMapId() {
		return nestedResultMapId;
	}

	public String getNestedQueryId() {
		return nestedQueryId;
	}

	public Set<String> getNotNullColumns() {
		return notNullColumns;
	}

	public String getColumnPrefix() {
		return columnPrefix;
	}

	public List<ResultFlag> getFlags() {
		return flags;
	}

	public List<ResultMapping> getComposites() {
		return composites;
	}

	public boolean isCompositeResult() {
		return this.composites != null && !this.composites.isEmpty();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ResultMapping that = (ResultMapping) o;

		if (property == null || !property.equals(that.property)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		if (property != null) {
			return property.hashCode();
		} else if (column != null) {
			return column.hashCode();
		} else {
			return 0;
		}
	}
}
