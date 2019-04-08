package org.eocencle.winger.builder;

import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;

/**
 * xml抽象建构类
 * @author huan
 *
 */
public abstract class AbstractXmlBuilder extends AbstractBuilder {
	
	// 节点
	protected XNode node;
	
	public AbstractXmlBuilder(Configuration config, XNode node) {
		super(config);
		this.node = node;
	}

}
