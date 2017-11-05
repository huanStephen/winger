package org.eocencle.winger.scripting.xmltags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eocencle.winger.builder.BaseBuilder;
import org.eocencle.winger.builder.BuilderException;
import org.eocencle.winger.builder.xml.XMLMapperEntityResolver;
import org.eocencle.winger.mapping.SqlSource;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.session.Configuration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLScriptBuilder extends BaseBuilder {
	private XNode context;

	public XMLScriptBuilder(Configuration configuration, XNode context) {
		super(configuration);
		this.context = context;
	}

	public XMLScriptBuilder(Configuration configuration, String context) {
		super(configuration);
		XPathParser parser = new XPathParser(context, false, configuration.getVariables(), new XMLMapperEntityResolver());
		this.context = parser.evalNode("/script");
	}

	public SqlSource parseScriptNode() {
		List<JsonNode> contents = parseDynamicTags(context);
		MixedJsonNode rootSqlNode = new MixedJsonNode(contents);
		SqlSource sqlSource = new DynamicSqlSource(configuration, rootSqlNode);
		return sqlSource;
	}

	private List<JsonNode> parseDynamicTags(XNode node) {
		List<JsonNode> contents = new ArrayList<JsonNode>();
		NodeList children = node.getNode().getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
		XNode child = node.newXNode(children.item(i));
		String nodeName = child.getNode().getNodeName();
		if (child.getNode().getNodeType() == Node.CDATA_SECTION_NODE
			|| child.getNode().getNodeType() == Node.TEXT_NODE) {
			String data = child.getStringBody("");
			contents.add(new TextJsonNode(data));
		} else if (child.getNode().getNodeType() == Node.ELEMENT_NODE && !"selectKey".equals(nodeName)) { // issue #628
			NodeHandler handler = nodeHandlers.get(nodeName);
			if (handler == null) {
			throw new BuilderException("Unknown element <" + nodeName + "> in SQL statement.");
			}
			handler.handleNode(child, contents);
		}
		}
		return contents;
	}

	private Map<String, NodeHandler> nodeHandlers = new HashMap<String, NodeHandler>() {
		private static final long serialVersionUID = 7123056019193266281L;

		{
			put("trim", new TrimHandler());
			put("where", new WhereHandler());
			put("set", new SetHandler());
			put("foreach", new ForEachHandler());
			put("if", new IfHandler());
			put("choose", new ChooseHandler());
			put("when", new IfHandler());
			put("otherwise", new OtherwiseHandler());
			put("bind", new BindHandler());
		}
	};

	private interface NodeHandler {
		void handleNode(XNode nodeToHandle, List<JsonNode> targetContents);
	}

	private class BindHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) {
		final String name = nodeToHandle.getStringAttribute("name");
		final String expression = nodeToHandle.getStringAttribute("value");
		final VarDeclJsonNode node = new VarDeclJsonNode(name, expression);
		targetContents.add(node);
		}
	}

	private class TrimHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) {
		List<JsonNode> contents = parseDynamicTags(nodeToHandle);
		MixedJsonNode mixedSqlNode = new MixedJsonNode(contents);
		String prefix = nodeToHandle.getStringAttribute("prefix");
		String prefixOverrides = nodeToHandle.getStringAttribute("prefixOverrides");
		String suffix = nodeToHandle.getStringAttribute("suffix");
		String suffixOverrides = nodeToHandle.getStringAttribute("suffixOverrides");
		TrimJsonNode trim = new TrimJsonNode(configuration, mixedSqlNode, prefix, prefixOverrides, suffix, suffixOverrides);
		targetContents.add(trim);
		}
	}

	private class WhereHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) {
			List<JsonNode> contents = parseDynamicTags(nodeToHandle);
			MixedJsonNode mixedSqlNode = new MixedJsonNode(contents);
			WhereJsonNode where = new WhereJsonNode(configuration, mixedSqlNode);
			targetContents.add(where);
		}
	}

	private class SetHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) {
			List<JsonNode> contents = parseDynamicTags(nodeToHandle);
			MixedJsonNode mixedSqlNode = new MixedJsonNode(contents);
			SetJsonNode set = new SetJsonNode(configuration, mixedSqlNode);
			targetContents.add(set);
		}
	}

	private class ForEachHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) {
			List<JsonNode> contents = parseDynamicTags(nodeToHandle);
			MixedJsonNode mixedSqlNode = new MixedJsonNode(contents);
			String collection = nodeToHandle.getStringAttribute("collection");
			String item = nodeToHandle.getStringAttribute("item");
			String index = nodeToHandle.getStringAttribute("index");
			String open = nodeToHandle.getStringAttribute("open");
			String close = nodeToHandle.getStringAttribute("close");
			String separator = nodeToHandle.getStringAttribute("separator");
			ForEachJsonNode forEachSqlNode = new ForEachJsonNode(configuration, mixedSqlNode, collection, index, item, open, close, separator);
			targetContents.add(forEachSqlNode);
		}
	}

	private class IfHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) {
			List<JsonNode> contents = parseDynamicTags(nodeToHandle);
			MixedJsonNode mixedSqlNode = new MixedJsonNode(contents);
			String test = nodeToHandle.getStringAttribute("test");
			IfJsonNode ifSqlNode = new IfJsonNode(mixedSqlNode, test);
			targetContents.add(ifSqlNode);
		}
	}

	private class OtherwiseHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) {
			List<JsonNode> contents = parseDynamicTags(nodeToHandle);
			MixedJsonNode mixedSqlNode = new MixedJsonNode(contents);
			targetContents.add(mixedSqlNode);
		}
	}

	private class ChooseHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) {
			List<JsonNode> whenSqlNodes = new ArrayList<JsonNode>();
			List<JsonNode> otherwiseSqlNodes = new ArrayList<JsonNode>();
			handleWhenOtherwiseNodes(nodeToHandle, whenSqlNodes, otherwiseSqlNodes);
			JsonNode defaultSqlNode = getDefaultSqlNode(otherwiseSqlNodes);
			ChooseJsonNode chooseSqlNode = new ChooseJsonNode(whenSqlNodes, defaultSqlNode);
			targetContents.add(chooseSqlNode);
		}

		private void handleWhenOtherwiseNodes(XNode chooseSqlNode, List<JsonNode> ifSqlNodes, List<JsonNode> defaultSqlNodes) {
			List<XNode> children = chooseSqlNode.getChildren();
			for (XNode child : children) {
				String nodeName = child.getNode().getNodeName();
				NodeHandler handler = nodeHandlers.get(nodeName);
				if (handler instanceof IfHandler) {
					handler.handleNode(child, ifSqlNodes);
				} else if (handler instanceof OtherwiseHandler) {
					handler.handleNode(child, defaultSqlNodes);
				}
			}
		}

		private JsonNode getDefaultSqlNode(List<JsonNode> defaultJsonNodes) {
			JsonNode defaultJsonNode = null;
			if (defaultJsonNodes.size() == 1) {
				defaultJsonNode = defaultJsonNodes.get(0);
			} else if (defaultJsonNodes.size() > 1) {
				throw new BuilderException("Too many default (otherwise) elements in choose statement.");
			}
			return defaultJsonNode;
		}
	}
}
