package org.eocencle.winger.builder;

import org.eocencle.winger.session.Configuration;

/**
 * 抽象建构类
 * @author huan
 *
 */
public abstract class AbstractBuilder {
	
	protected Configuration config;
	
	public AbstractBuilder(Configuration config) {
		this.config = config;
	}
	
	public abstract Configuration parse();
}
