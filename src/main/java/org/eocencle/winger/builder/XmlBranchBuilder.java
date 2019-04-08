package org.eocencle.winger.builder;

import org.apache.commons.lang3.StringUtils;
import org.eocencle.winger.builder.xml.BuilderAssistant;
import org.eocencle.winger.builder.xml.XMLIncludeTransformer;
import org.eocencle.winger.exceptions.BuilderException;
import org.eocencle.winger.exceptions.WingerException;
import org.eocencle.winger.mapping.AbstractResponseBranch;
import org.eocencle.winger.mapping.XmlResponseBranch;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.xmltags.XMLScriptBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Xml响应分支建构
 * @author huan
 *
 */
public class XmlBranchBuilder extends AbstractBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(XmlBranchBuilder.class);
	
	// 节点
	private XNode node;
	
	// 命名空间
	private BuilderAssistant builderAssistant;
	
	public XmlBranchBuilder(Configuration config, XNode node, BuilderAssistant builderAssistant) {
		super(config);
		this.node = node;
		this.builderAssistant = builderAssistant;
	}

	@Override
	public Configuration parse() {
		String uri = this.node.getStringAttribute("name");
		if (StringUtils.isNotBlank(uri)) {
			try {
				uri = this.builderAssistant.getCurrentNamespace() + uri;
				
				LOGGER.info("Xml response [" + uri + "] onto " + this.builderAssistant.getCurrentFileName());
				
				XmlResponseBranch branch = new XmlResponseBranch(this.config, this.builderAssistant.getCurrentNamespace(), uri, this.node);
				String type = this.node.getStringAttribute("type", AbstractResponseBranch.TYPE_GET).toUpperCase();
				branch.setType(type);
				
				XMLIncludeTransformer includeParser = new XMLIncludeTransformer(this.config, this.builderAssistant);
				includeParser.applyIncludes(this.node.getNode());
				
				try {
					XMLScriptBuilder builder = new XMLScriptBuilder(this.config, this.node);
					branch.setJsonSource(builder.parseScriptNode());
				} catch (BuilderException e) {
					LOGGER.info(e.getMessage());
					LOGGER.debug(e.getMessage());
				} catch (WingerException e) {
					LOGGER.debug(e.getMessage());
				}
				
				if (this.config.pushBranch(branch.getUri(), branch)) {
					LOGGER.info(branch.getUri() + " is repetitive");
				}
				
				this.config.addUri(uri);
			} catch (IllegalArgumentException e) {
				LOGGER.info(e.getMessage());
			}
		}
		return this.config;
	}

}
