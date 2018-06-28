package org.eocencle.winger.builder;

import org.eocencle.winger.session.Configuration;

/**
 * xml响应重构
 * @author huan
 *
 */
public class XmlResponseRebuilder extends AbstractBuilder {

	private XmlResponseBuilder builder;
	
	private String updateMode;
	
	public XmlResponseRebuilder(Configuration config, XmlResponseBuilder builder, String updateMode) {
		super(config);
		this.builder = builder;
		this.updateMode = updateMode;
	}

	@Override
	public Configuration parse() {
		
		return null;
	}

}
