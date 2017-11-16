package org.eocencle.winger.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.eocencle.winger.builder.xml.XMLConfigBuilder;
import org.eocencle.winger.exceptions.ExceptionFactory;
import org.eocencle.winger.executor.ErrorContext;

public class JsonSessionFactory {
	private XMLConfigBuilder parser;
	
	public JsonSession build(Reader reader) {
		return build(reader, null, null);
	}

	public JsonSession build(Reader reader, String environment) {
		return build(reader, environment, null);
	}

	public JsonSession build(Reader reader, Properties properties) {
		return build(reader, null, properties);
	}

	public JsonSession build(Reader reader, String environment, Properties properties) {
		try {
			XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
			return build(parser.parse());
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error building SqlSession.", e);
		} finally {
			ErrorContext.instance().reset();
			try {
				reader.close();
			} catch (IOException e) {
				// Intentionally ignore. Prefer previous error.
			}
		}
	}

	public JsonSession build(InputStream inputStream) {
		return build(inputStream, null, null);
	}

	public JsonSession build(InputStream inputStream, String environment) {
		return build(inputStream, environment, null);
	}

	public JsonSession build(InputStream inputStream, Properties properties) {
		return build(inputStream, null, properties);
	}

	public JsonSession build(InputStream inputStream, String environment, Properties properties) {
		try {
			this.parser = new XMLConfigBuilder(inputStream, environment, properties);
			return build(parser.parse());
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error building SqlSession.", e);
		} finally {
			ErrorContext.instance().reset();
			try {
				inputStream.close();
			} catch (IOException e) {
				// Intentionally ignore. Prefer previous error.
			}
		}
	}
		
	public JsonSession build(Configuration config) {
		return new DefaultJsonSession(config);
	}
	
	public XMLConfigBuilder getXMLConfigBuilder() {
		return this.parser;
	}
}
