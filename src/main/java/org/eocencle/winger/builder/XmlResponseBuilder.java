package org.eocencle.winger.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xml响应建构类
 * @author huan
 *
 */
public class XmlResponseBuilder extends AbstractXmlBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(XmlResponseBuilder.class);
	
	public XmlResponseBuilder(Configuration config, XNode node) {
		super(config, node);
	}

	@Override
	public Configuration parse() {
		this.parseAttributes(this.node);
		this.parseElements(this.node.evalNode("xmls"));
		return this.config;
	}
	
	private void parseAttributes(XNode node) {
		if (null == node) {
			// 加警告，无xml配置
			return ;
		}
		this.config.setUpdateMode(node.getStringAttribute("updateMode", "none").toLowerCase());
	}

	private void parseElements(XNode node) {
		if (null == node) {
			// 加警告，无xml配置
			return ;
		}
		List<XNode> nodes = node.getChildren();
		String resource = null;
		XmlResponseFileBuilder builder = null;
		for (XNode n : nodes) {
			resource = n.getStringAttribute("resource");
			if (null != resource) {
				try {
					resource = this.config.getRoot() + "/" + resource;
					this.config.addXmlRespFile(resource);
					builder = new XmlResponseFileBuilder(this.config, new XPathParser(new FileInputStream(new File(resource))), resource);
					builder.parse();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
