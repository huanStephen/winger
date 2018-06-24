package org.eocencle.winger.xmltags;

import java.util.List;

import org.eocencle.winger.exceptions.WingerException;

public class ChooseJsonNode implements JsonNode {
	private JsonNode defaultJsonNode;
	private List<JsonNode> ifJsonNodes;

	public ChooseJsonNode(List<JsonNode> ifJsonNodes, JsonNode defaultJsonNode) {
		this.ifJsonNodes = ifJsonNodes;
		this.defaultJsonNode = defaultJsonNode;
	}

	public boolean apply(DynamicContext context) throws WingerException {
		for (JsonNode jsonNode : this.ifJsonNodes) {
		if (jsonNode.apply(context)) {
			return true;
		}
		}
		if (this.ifJsonNodes != null) {
			this.defaultJsonNode.apply(context);
		return true;
		}
		return false;
	}
}
