package org.eocencle.winger.builder.xml;

import org.eocencle.winger.builder.IncompleteElementException;
import org.eocencle.winger.builder.MapperBuilderAssistant;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLIncludeTransformer {
	private final Configuration configuration;
	private final MapperBuilderAssistant builderAssistant;

	public XMLIncludeTransformer(Configuration configuration, MapperBuilderAssistant builderAssistant) {
		this.configuration = configuration;
		this.builderAssistant = builderAssistant;
	}

	public void applyIncludes(Node source) {
		if (source.getNodeName().equals("include")) {
			Node toInclude = findSqlFragment(getStringAttribute(source, "refid"));
			applyIncludes(toInclude);
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
		refid = builderAssistant.applyCurrentNamespace(refid, true);
		try {
			XNode nodeToInclude = configuration.getSqlFragments().get(refid);
			Node result = nodeToInclude.getNode().cloneNode(true);
			return result;
		} catch (IllegalArgumentException e) {
			throw new IncompleteElementException("Could not find SQL statement to include with refid '" + refid + "'", e);
		}
	}

	private String getStringAttribute(Node node, String name) {
		return node.getAttributes().getNamedItem(name).getNodeValue();
	}
}
