package org.eocencle.winger.builder.xml;

import org.eocencle.winger.builder.IncompleteElementException;
import org.eocencle.winger.builder.ResponseBuilderAssistant;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.type.ContextPathType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLIncludeTransformer {
	private final Configuration configuration;
	private final ResponseBuilderAssistant builderAssistant;

	public XMLIncludeTransformer(Configuration configuration, ResponseBuilderAssistant builderAssistant) {
		this.configuration = configuration;
		this.builderAssistant = builderAssistant;
	}

	public void applyIncludes(Node source) {
		if (source.getNodeName().equals("include")) {
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
			for (int i=0; i<children.getLength(); i++) {
				applyIncludes(children.item(i));
			}
		}
	}

	private Node findSqlFragment(String refid) {
		refid = builderAssistant.applyCurrentContextPath(refid, true);
		try {
			XNode nodeToInclude = configuration.getSqlFragments().get(refid);
			Node result = nodeToInclude.getNode().cloneNode(true);
			return result;
		} catch (IllegalArgumentException e) {
			throw new IncompleteElementException("Could not find SQL statement to include with refid '" + refid + "'", e);
		}
	}
	
	private Node findJsonFragment(String refid) {
		refid = this.builderAssistant.applyCurrentContextPath(refid, true, ContextPathType.JSON);
		try {
			XNode nodeToInclude = this.configuration.getJsonFragments().get(refid);
			Node result = nodeToInclude.getNode().cloneNode(true);
			return result;
		} catch (IllegalArgumentException e) {
			throw new IncompleteElementException("Could not find json to include with refid '" + refid + "'", e);
		}
	}

	private String getStringAttribute(Node node, String name) {
		return node.getAttributes().getNamedItem(name).getNodeValue();
	}
}
