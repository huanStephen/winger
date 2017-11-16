package org.eocencle.winger.builder.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.eocencle.winger.builder.BaseBuilder;
import org.eocencle.winger.builder.IncompleteElementException;
import org.eocencle.winger.builder.ResponseBuilderAssistant;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.type.ContextPathType;

public class XMLResponseBuilder extends BaseBuilder {
	private XPathParser parser;
	private ResponseBuilderAssistant builderAssistant;
	private Map<String, XNode> jsonFragments;
	protected String resource;

	@Deprecated
	public XMLResponseBuilder(Reader reader, Configuration configuration, String resource, Map<String, XNode> jsonFragments, String contextPath) {
		this(reader, configuration, resource, jsonFragments);
		this.builderAssistant.setCurrentContextPath(contextPath);
	}

	@Deprecated
	public XMLResponseBuilder(Reader reader, Configuration configuration, String resource, Map<String, XNode> jsonFragments) {
		this(new XPathParser(reader, true, configuration.getVariables(), new XMLMapperEntityResolver()), configuration, resource, jsonFragments);
	}

	public XMLResponseBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> jsonFragments, String contextPath) {
		this(inputStream, configuration, resource, jsonFragments);
		this.builderAssistant.setCurrentContextPath(contextPath);
	}

	public XMLResponseBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> jsonFragments) {
		this(new XPathParser(inputStream, true, configuration.getVariables(), new XMLMapperEntityResolver()),
			configuration, resource, jsonFragments);
	}

	private XMLResponseBuilder(XPathParser parser, Configuration configuration, String resource, Map<String, XNode> jsonFragments) {
		super(configuration);
		this.builderAssistant = new ResponseBuilderAssistant(configuration, resource);
		this.parser = parser;
		this.jsonFragments = jsonFragments;
		this.resource = resource;
	}

	public void parse() {
		if (!configuration.isResourceLoaded(resource)) {
			this.configurationElement(parser.evalNode("/response"));
			configuration.addLoadedResource(resource);
		}
	}

	private void configurationElement(XNode context) {
		try {
			String contextpath = context.getStringAttribute("contextpath");
			if (0 != contextpath.indexOf("/")) {
				contextpath = "/" + contextpath;
			}
			if (contextpath.lastIndexOf("/") == contextpath.length() - 1) {
				contextpath = contextpath.substring(0, contextpath.length() - 1);
			}
			this.builderAssistant.setCurrentContextPath(contextpath);
			this.jsonElement(context.evalNodes("json"));
			this.buildBranchFormContext(context.evalNodes("branch"));
		} catch (Exception e) {
			throw new RuntimeException("Error parsing Response XML. Cause: " + e, e);
		}
	}
	
	private void buildBranchFormContext(List<XNode> list) {
		for (XNode context : list) {
			final XMLBranchBuilder branchParser = new XMLBranchBuilder(this.configuration, this.builderAssistant, context);
			try {
				branchParser.parseBranchNode();
			} catch (IncompleteElementException e) {
				this.configuration.addIncompleteBranch(branchParser);
			}
		}
	}
	
	private void jsonElement(List<XNode> list) throws Exception {
		for (XNode context : list) {
			String id = context.getStringAttribute("id");
			id = this.builderAssistant.applyCurrentContextPath(id, false, ContextPathType.JSON);
			
			this.jsonFragments.put(id, context);
		}
	}
}
