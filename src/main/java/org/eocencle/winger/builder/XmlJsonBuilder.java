package org.eocencle.winger.builder;

import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;

/**
 * xmlJson碎片建构
 * @author huan
 *
 */
public class XmlJsonBuilder extends AbstractBuilder {

	// 节点
	private XNode node;
	
	public XmlJsonBuilder(Configuration config, XNode node) {
		super(config);
		this.node = node;
	}

	@Override
	public Configuration parse() {
		String id = this.node.getStringAttribute("id");
		this.config.pushFragment(id, this.node);
		return this.config;
	}
	
	

}
