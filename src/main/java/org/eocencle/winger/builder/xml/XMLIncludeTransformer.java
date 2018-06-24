package org.eocencle.winger.builder.xml;

import org.eocencle.winger.builder.AbstractBuilder;
import org.eocencle.winger.builder.XmlBranchBuilder;
import org.eocencle.winger.exceptions.IncompleteElementException;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.type.NamespaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLIncludeTransformer extends AbstractBuilder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLIncludeTransformer.class);
	
	private final BuilderAssistant builderAssistant;

	public XMLIncludeTransformer(Configuration config, BuilderAssistant builderAssistant) {
		super(config);
		this.builderAssistant = builderAssistant;
	}

	public void applyIncludes(Node source) {
		if ("include".equals(source.getNodeName())) {
			Node toInclude = this.findJsonFragment(this.getStringAttribute(source, "refid"));
			this.applyIncludes(toInclude);
			if (toInclude.getOwnerDocument() != source.getOwnerDocument()) {
				toInclude = source.getOwnerDocument().importNode(toInclude, true);
			}
			source.getParentNode().replaceChild(toInclude, source);
			while (toInclude.hasChildNodes()) {
				toInclude.getParentNode().insertBefore(toInclude.getFirstChild(), toInclude);
			}
			toInclude.getParentNode().removeChild(toInclude);
		} else if (source.getNodeType() == Node.ELEMENT_NODE) {
			NodeList children = source.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				this.applyIncludes(children.item(i));
			}
		}
	}
	
	private Node findJsonFragment(String refid) {
		try {
			XNode nodeToInclude = this.config.getFragment(refid);
			Node result = nodeToInclude.getNode().cloneNode(true);
			return result;
		} catch (IllegalArgumentException e) {
			//throw new IncompleteElementException("Could not find json to include with refid '" + refid + "'", e);
			LOGGER.debug("Could not find json to include with refid '" + refid + "'");
		}
		return null;
	}

	private String getStringAttribute(Node node, String name) {
		return node.getAttributes().getNamedItem(name).getNodeValue();
	}

	@Override
	public Configuration parse() {
		// TODO Auto-generated method stub
		return null;
	}
}
