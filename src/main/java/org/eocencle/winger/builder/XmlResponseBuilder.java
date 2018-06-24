package org.eocencle.winger.builder;

import java.io.IOException;
import java.util.List;

import org.eocencle.winger.io.Resources;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.session.Configuration;

/**
 * xml响应建构类
 * @author huan
 *
 */
public class XmlResponseBuilder extends AbstractXmlBuilder {

	public XmlResponseBuilder(Configuration config, XNode node) {
		super(config, node);
	}

	@Override
	public Configuration parse() {
		this.parseElements(this.node.evalNode("xmls"));
		return this.config;
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
					this.config.addXmlRespFile(resource);
					builder = new XmlResponseFileBuilder(this.config, new XPathParser(Resources.getResourceAsStream(resource)), resource);
					builder.parse();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
