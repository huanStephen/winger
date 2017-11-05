package org.eocencle.winger.mapping;

public interface SqlSource {
	BoundSql getBoundSql(Object parameterObject);
}
