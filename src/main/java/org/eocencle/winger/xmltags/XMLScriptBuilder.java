package org.eocencle.winger.xmltags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eocencle.winger.builder.xml.XMLMapperEntityResolver;
import org.eocencle.winger.exceptions.BuilderException;
import org.eocencle.winger.exceptions.WingerException;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.builder.AbstractBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLScriptBuilder extends AbstractBuilder {
	private XNode context;

	public XMLScriptBuilder(Configuration config, XNode context) {
		super(config);
		this.context = context;
	}

	public XMLScriptBuilder(Configuration configuration, String context) {
		super(configuration);
		//XPathParser parser = new XPathParser(context, false, configuration.getVariables(), new XMLMapperEntityResolver());
		//this.context = parser.evalNode("/script");
	}

	public JsonSource parseScriptNode() throws WingerException {
		List<JsonNode> contents = parseDynamicTags(context);
		MixedJsonNode rootJsonNode = new MixedJsonNode(contents);
		JsonSource jsonSource = new DynamicJsonSource(this.config, rootJsonNode);
		return jsonSource;
	}

	private List<JsonNode> parseDynamicTags(XNode node) throws WingerException {
		List<JsonNode> contents = new ArrayList<JsonNode>();
		NodeList children = node.getNode().getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			XNode child = node.newXNode(children.item(i));
			String nodeName = child.getNode().getNodeName();
			if (child.getNode().getNodeType() == Node.CDATA_SECTION_NODE
				|| child.getNode().getNodeType() == Node.TEXT_NODE) {
				String data = child.getStringBody("");
				contents.add(new TextJsonNode(data));
			} else if (child.getNode().getNodeType() == Node.ELEMENT_NODE && !"selectKey".equals(nodeName)) {
				NodeHandler handler = this.nodeHandlers.get(nodeName);
				if (handler == null) {
					throw new BuilderException("Unknown element <" + nodeName + "> in Json.");
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
		void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) throws WingerException ;
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
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) throws WingerException {
			List<JsonNode> contents = parseDynamicTags(nodeToHandle);
			MixedJsonNode mixedSqlNode = new MixedJsonNode(contents);
			String prefix = nodeToHandle.getStringAttribute("prefix");
			String prefixOverrides = nodeToHandle.getStringAttribute("prefixOverrides");
			String suffix = nodeToHandle.getStringAttribute("suffix");
			String suffixOverrides = nodeToHandle.getStringAttribute("suffixOverrides");
			TrimJsonNode trim = new TrimJsonNode(config, mixedSqlNode, prefix, prefixOverrides, suffix, suffixOverrides);
			targetContents.add(trim);
		}
	}

	private class WhereHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) throws WingerException {
			List<JsonNode> contents = parseDynamicTags(nodeToHandle);
			MixedJsonNode mixedJsonNode = new MixedJsonNode(contents);
			WhereJsonNode where = new WhereJsonNode(config, mixedJsonNode);
			targetContents.add(where);
		}
	}

	private class SetHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) throws WingerException {
			List<JsonNode> contents = parseDynamicTags(nodeToHandle);
			MixedJsonNode mixedJsonNode = new MixedJsonNode(contents);
			SetJsonNode set = new SetJsonNode(config, mixedJsonNode);
			targetContents.add(set);
		}
	}

	private class ForEachHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) throws WingerException {
			List<JsonNode> contents = parseDynamicTags(nodeToHandle);
			MixedJsonNode mixedJsonNode = new MixedJsonNode(contents);
			String collection = nodeToHandle.getStringAttribute("collection");
			String item = nodeToHandle.getStringAttribute("item");
			String index = nodeToHandle.getStringAttribute("index");
			String open = nodeToHandle.getStringAttribute("open");
			String close = nodeToHandle.getStringAttribute("close");
			String separator = nodeToHandle.getStringAttribute("separator");
			ForEachJsonNode forEachJsonNode = new ForEachJsonNode(config, mixedJsonNode, collection, index, item, open, close, separator);
			targetContents.add(forEachJsonNode);
		}
	}

	private class IfHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) throws WingerException {
			List<JsonNode> contents = parseDynamicTags(nodeToHandle);
			MixedJsonNode mixedJsonNode = new MixedJsonNode(contents);
			String test = nodeToHandle.getStringAttribute("test");
			IfJsonNode ifJsonNode = new IfJsonNode(mixedJsonNode, test);
			targetContents.add(ifJsonNode);
		}
	}

	private class OtherwiseHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) throws WingerException {
			List<JsonNode> contents = parseDynamicTags(nodeToHandle);
			MixedJsonNode mixedJsonNode = new MixedJsonNode(contents);
			targetContents.add(mixedJsonNode);
		}
	}

	private class ChooseHandler implements NodeHandler {
		public void handleNode(XNode nodeToHandle, List<JsonNode> targetContents) throws WingerException {
			List<JsonNode> whenJsonNodes = new ArrayList<JsonNode>();
			List<JsonNode> otherwiseJsonNodes = new ArrayList<JsonNode>();
			handleWhenOtherwiseNodes(nodeToHandle, whenJsonNodes, otherwiseJsonNodes);
			JsonNode defaultJsonNode = getDefaultSqlNode(otherwiseJsonNodes);
			ChooseJsonNode chooseJsonNode = new ChooseJsonNode(whenJsonNodes, defaultJsonNode);
			targetContents.add(chooseJsonNode);
		}

		private void handleWhenOtherwiseNodes(XNode chooseSqlNode, List<JsonNode> ifJsonNodes, List<JsonNode> defaultJsonNodes) throws WingerException {
			List<XNode> children = chooseSqlNode.getChildren();
			for (XNode child : children) {
				String nodeName = child.getNode().getNodeName();
				NodeHandler handler = nodeHandlers.get(nodeName);
				if (handler instanceof IfHandler) {
					handler.handleNode(child, ifJsonNodes);
				} else if (handler instanceof OtherwiseHandler) {
					handler.handleNode(child, defaultJsonNodes);
				}
			}
		}

		private JsonNode getDefaultSqlNode(List<JsonNode> defaultJsonNodes) throws WingerException {
			JsonNode defaultJsonNode = null;
			if (defaultJsonNodes.size() == 1) {
				defaultJsonNode = defaultJsonNodes.get(0);
			} else if (defaultJsonNodes.size() > 1) {
				throw new BuilderException("Too many default (otherwise) elements in choose statement.");
			}
			return defaultJsonNode;
		}
	}

	@Override
	public Configuration parse() {
		// TODO Auto-generated method stub
		return null;
	}
}
