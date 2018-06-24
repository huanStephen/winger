package org.eocencle.winger.builder;

import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.session.Configuration;

/**
 * 项目建构类
 * @author huan
 *
 */
public class ProjectBuilder extends AbstractXmlBuilder {
	
	public ProjectBuilder(Configuration container, XNode node) {
		super(container, node);
	}
	
	public ProjectBuilder(Configuration container, XPathParser parser) {
		this(container, parser.evalNode("/project"));
	}

	@Override
	public Configuration parse() {
		this.parseConfiguration();
		return this.config;
	}
	
	private void parseConfiguration() {
		this.config.setContextPath("/" + this.node.getStringAttribute("contextPath", Configuration.DEFAULT_CONTEXT_PATH));
		this.parseServer();
		this.parseIntercepters();
		this.parseApis();
		this.parseXmls();
		this.parseCaches();
	}

	private void parseServer() {
		ServerBuilder builder = new ServerBuilder(this.config, this.node);
		builder.parse();
	}
	
	private void parseIntercepters() {
		IntercepterBuilder builder = new IntercepterBuilder(this.config, this.node);
		builder.parse();
	}
	
	private void parseApis() {
		ApiResponseBuilder builder = new ApiResponseBuilder(this.config, this.node);
		builder.parse();
	}
	
	private void parseXmls() {
		XmlResponseBuilder builder = new XmlResponseBuilder(this.config, this.node);
		builder.parse();
	}
	
	private void parseCaches() {
		CacheBuilder builder = new CacheBuilder(this.config, this.node);
		builder.parse();
	}
}
