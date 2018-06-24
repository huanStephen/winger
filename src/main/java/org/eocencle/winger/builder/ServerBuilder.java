package org.eocencle.winger.builder;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;

/**
 * 服务器建构类
 * @author huan
 *
 */
public class ServerBuilder extends AbstractXmlBuilder {

	public ServerBuilder(Configuration container, XNode node) {
		super(container, node);
	}

	@Override
	public Configuration parse() {
		this.parseAttribute(this.node.evalNode("server"));
		return this.config;
	}
	
	private void parseAttribute(XNode node) {
		if (null == node) {
			// 加警告，无服务器配置
			return ;
		}
		if (StringUtils.isBlank(this.config.getContextPath())) {
			this.config.setContextPath("/" + this.node.getStringAttribute("contextPath", Configuration.DEFAULT_CONTEXT_PATH));
		}
		this.config.setServerMode(node.getStringAttribute("mode", Configuration.SERVER_MODE_OWN));
		this.config.setPort(node.getIntAttribute("port", Configuration.DEFAULT_PORT));
		this.config.setResourceBase(node.getStringAttribute("resourceBase"));
		this.parseProperties(node.getChildren());
	}

	private void parseProperties(List<XNode> nodes) {
		List<XNode> children = null;
		for (XNode node : nodes) {
			if ("property".equals(node.getName()) && "resources".equals(node.getStringAttribute("name"))) {
				children = node.getChildren();
				if ("list".equals(children.get(0).getName()) || "array".equals(children.get(0).getName())) {
					children = children.get(0).getChildren();
					for (XNode n : children) {
						if ("value".equals(n.getName())) {
							this.config.addResourceSuffix(n.getStringBody());
						}
					}
				}
			}
		}
	}
}
