package org.eocencle.winger.builder;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eocencle.winger.builder.xml.BuilderAssistant;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.session.Configuration;

/**
 * xml响应文件建构类
 * @author huan
 *
 */
public class XmlResponseFileBuilder extends AbstractXmlBuilder {

	private String xmlRespFileName;
	
	public XmlResponseFileBuilder(Configuration config, XNode node, String xmlRespFile) {
		super(config, node);
		this.xmlRespFileName = this.getXmlRespFileName(xmlRespFile);
	}
	
	public XmlResponseFileBuilder(Configuration config, XPathParser parser, String xmlRespFile) {
		this(config, parser.evalNode("/response"), xmlRespFile);
	}
	
	private String getXmlRespFileName(String file) {
		if (-1 != file.indexOf("/")) {
			return file.substring(file.lastIndexOf("/") + 1, file.length());
		}
		return file;
	}

	@Override
	public Configuration parse() {
		this.parseConfiguration();
		return this.config;
	}
	
	private void parseConfiguration() {
		if (null == this.node) {
			// 加警告，无响应配置
			return ;
		}
		
		String namespace = this.node.getStringAttribute("namespace");
		if (0 != namespace.indexOf("/")) {
			namespace = "/" + namespace;
		}
		
		if (namespace.lastIndexOf("/") == namespace.length() - 1) {
			namespace = namespace.substring(0, namespace.length() - 1);
		}
		
		if (StringUtils.isNotBlank(namespace)) {
			this.config.addNamespace(namespace);
		} else {
			namespace = "";
			this.config.addNamespace(Configuration.NAMESPACE_GLOBAL);
		}
		
		this.parseJsonFragment();
		this.parseBranch(namespace);
	}
	
	private void parseJsonFragment() {
		List<XNode> nodes = this.node.evalNodes("json");
		XmlJsonBuilder builder = null;
		for (XNode n : nodes) {
			builder = new XmlJsonBuilder(this.config, n);
			builder.parse();
		}
	}
	
	private void parseBranch(String namespace) {
		List<XNode> nodes = this.node.evalNodes("branch");
		XmlBranchBuilder builder = null;
		for (XNode n : nodes) {
			builder = new XmlBranchBuilder(this.config, n, new BuilderAssistant(this.xmlRespFileName, namespace));
			builder.parse();
		}
	}

}
