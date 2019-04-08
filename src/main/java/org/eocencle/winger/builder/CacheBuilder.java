package org.eocencle.winger.builder;

import java.util.List;

import org.eocencle.winger.cache.ResponseCache;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存建构类
 * @author huan
 *
 */
public class CacheBuilder extends AbstractXmlBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheBuilder.class);
	
	public CacheBuilder(Configuration config, XNode node) {
		super(config, node);
	}

	@Override
	public Configuration parse() {
		this.parseElements(this.node.evalNode("caches"));
		return this.config;
	}
	
	private void parseElements(XNode node) {
		ResponseCache openCache = new ResponseCache();
		ResponseCache closeCache = new ResponseCache(true);
		if (null == node) {
			// 加警告，无拦截器配置
			LOGGER.info("No Cache set");
			this.config.setResponseCache(closeCache);
			return ;
		}
		List<XNode> nodes = node.getChildren();
		XNode n = null;
		if (null != nodes && 0 != nodes.size()) {
			n = nodes.get(0);
			if ("cache".equals(n.getName()) && "response".equals(n.getStringAttribute("name"))) {
				if (n.getBooleanAttribute("open", false)) {
					nodes = n.getChildren();
					if (null != nodes && 0 != nodes.size()) {
						n = nodes.get(0);
						if ("property".equals(n.getName()) && "lazy".equals(n.getStringAttribute("lazy"))) {
							if (n.getBooleanBody()) {
								this.config.setResponseCache(openCache);
							} else {
								this.config.setResponseCache(new ResponseCache(this.config.getUriList()));
							}
						} else {
							this.config.setResponseCache(openCache);
						}
					} else {
						this.config.setResponseCache(openCache);
					}
				} else {
					this.config.setResponseCache(closeCache);
				}
			} else {
				this.config.setResponseCache(closeCache);
			}
		} else {
			this.config.setResponseCache(closeCache);
		}
	}

}
