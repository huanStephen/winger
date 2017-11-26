package org.eocencle.winger.builder.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.eocencle.winger.builder.BaseBuilder;
import org.eocencle.winger.builder.BuilderException;
import org.eocencle.winger.builder.annotation.ResponseAnnotationBuilder;
import org.eocencle.winger.executor.ErrorContext;
import org.eocencle.winger.io.Resources;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.session.Configuration;
import org.springframework.context.ApplicationContext;

public class XMLConfigBuilder extends BaseBuilder {
	private boolean parsed;
	private XPathParser parser;
	private XMLResponseRebuilder responseRebuilder;
	private ApplicationContext applicationContext;

	public XMLConfigBuilder(Reader reader) {
		this(reader, null, null);
	}

	public XMLConfigBuilder(Reader reader, ApplicationContext applicationContext) {
		this(reader, applicationContext, null);
	}

	public XMLConfigBuilder(Reader reader, ApplicationContext applicationContext, Properties props) {
		this(new XPathParser(reader, true, props, new XMLMapperEntityResolver()), applicationContext, props);
	}

	public XMLConfigBuilder(InputStream inputStream) {
		this(inputStream, null, null);
	}

	public XMLConfigBuilder(InputStream inputStream, ApplicationContext applicationContext) {
		this(inputStream, applicationContext, null);
	}

	public XMLConfigBuilder(InputStream inputStream, ApplicationContext applicationContext, Properties props) {
		this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), applicationContext, props);
	}

	private XMLConfigBuilder(XPathParser parser, ApplicationContext applicationContext, Properties props) {
		super(new Configuration());
		ErrorContext.instance().resource("Response Configuration");
		this.configuration.setVariables(props);
		this.parsed = false;
		this.parser = parser;
		this.responseRebuilder = new XMLResponseRebuilder(this.configuration);
		this.applicationContext = applicationContext;
	}

	public Configuration parse() {
		if (parsed) {
			throw new BuilderException("Each ResponseConfigParser can only be used once.");
		}
		parsed = true;
		this.parseConfiguration(parser.evalNode("/configuration"));
		return configuration;
	}

	private void parseConfiguration(XNode root) {
		try {
			this.apiResponseElement(root.evalNode("apis"));
			this.xmlResponseElement(root.evalNode("xmls"));
		} catch (Exception e) {
			throw new BuilderException("Error parsing Response Configuration. Cause: " + e, e);
		}
	}
	
	private void apiResponseElement(XNode node) throws Exception {
		if (null == this.applicationContext) {
			throw new BuilderException("ApplicationContext is null.");
		}
		if (node != null) {
			String packet = node.getStringAttribute("resource");
			if (packet != null) {
				ResponseAnnotationBuilder responseAnnotationBuilder = new ResponseAnnotationBuilder(this.configuration, packet, this.applicationContext);
				responseAnnotationBuilder.parse();
			} else {
				throw new BuilderException("A apis element may only specify a scan.");
			}
		}
	}

	private void xmlResponseElement(XNode parent) throws Exception {
		if (parent != null) {
			for (XNode child : parent.getChildren()) {
				String resource = child.getStringAttribute("resource");
				if (resource != null) {
					ErrorContext.instance().resource(resource);
					InputStream inputStream = Resources.getResourceAsStream(resource);
					XMLResponseBuilder responseParser = new XMLResponseBuilder(inputStream, configuration, resource, configuration.getJsonFragments());
					responseParser.parse();
				} else {
					throw new BuilderException("A response element may only specify a resource.");
				}
			}
		}
	}
	
	public XMLResponseRebuilder getXMLResponseRebuilder() {
		return this.responseRebuilder;
	}
	
	public static class XMLResponseRebuilder {
		
		private Configuration configuration;
		
		private XMLResponseRebuilder(Configuration configuration) {
			this.configuration = configuration;
		}
		
		public void parse(String xmlPath) throws IOException {
			if (null == xmlPath || xmlPath.isEmpty()) {
				throw new BuilderException("XmlPath is empty.");
			}
			if (this.configuration.isResourceLoaded(xmlPath)) {
				this.configuration.removeResourceLoaded(xmlPath);
				InputStream is = Resources.getResourceAsStream(xmlPath);
				XMLResponseBuilder responseParser = new XMLResponseBuilder(is, this.configuration, xmlPath, this.configuration.getJsonFragments());
				responseParser.parse();
			}
		}
	}
}
