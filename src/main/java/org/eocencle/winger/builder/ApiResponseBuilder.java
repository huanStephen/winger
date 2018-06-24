package org.eocencle.winger.builder;

import java.util.List;

import org.eocencle.winger.builder.api.ObjectGenerateFactory;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * api响应建构类
 * @author huan
 *
 */
public class ApiResponseBuilder extends AbstractXmlBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiResponseBuilder.class);
	
	public ApiResponseBuilder(Configuration container, XNode node) {
		super(container, node);
	}

	@Override
	public Configuration parse() {
		this.parseAttributes(this.node.evalNode("apis"));
		return this.config;
	}
	
	private void parseAttributes(XNode node) {
		if (null == node) {
			// 加警告，无API配置
			return ;
		}
		this.config.setGenMode(node.getStringAttribute("genMode", "invoke").toLowerCase());
		this.checkGenMode();
		this.parseProperties(node.getChildren());
		
		ApiBranchBuilder builder = new ApiBranchBuilder(this.config, ObjectGenerateFactory.getInstance(this.config));
		builder.parse();
	}
	
	/**
	 * 检测生成模式是否满足条件
	 */
	private void checkGenMode() {
		if (Configuration.GEN_MODE_SPRING.equals(this.config.getGenMode())) {
			if (null == this.config.getContext()) {
				LOGGER.info("Generate mode no Spring Application Context set, using defaults");
				this.config.setGenMode(Configuration.GEN_MODE_INVOKE);
			} else {
				LOGGER.info("Generate mode using Spring Application Context");
			}
		} else if (Configuration.GEN_MODE_INVOKE.equals(this.config.getGenMode())) {
			LOGGER.info("Generate mode using Invoke");
		}
	}

	private void parseProperties(List<XNode> nodes) {
		List<XNode> children = null;
		for (XNode node : nodes) {
			if ("property".equals(node.getName()) && "jars".equals(node.getStringAttribute("name"))) {
				children = node.getChildren();
				if ("map".equals(children.get(0).getName())) {
					children = children.get(0).getChildren();
					for (XNode n : children) {
						if ("entry".equals(n.getName())) {
							this.config.pushJar(n.getStringAttribute("key"), n.getStringAttribute("value"));
						}
					}
				}
			}
		}
	}
}
