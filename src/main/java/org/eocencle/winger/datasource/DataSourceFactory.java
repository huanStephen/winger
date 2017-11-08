package org.eocencle.winger.datasource;

import java.util.Properties;

import javax.sql.DataSource;

public interface DataSourceFactory {
	void setProperties(Properties props);

	DataSource getDataSource();
}
