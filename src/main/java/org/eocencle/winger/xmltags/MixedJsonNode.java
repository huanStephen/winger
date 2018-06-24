package org.eocencle.winger.xmltags;

import java.util.List;

import org.eocencle.winger.exceptions.WingerException;

public class MixedJsonNode implements JsonNode {
	private List<JsonNode> contents;

	public MixedJsonNode(List<JsonNode> contents) {
		this.contents = contents;
	}

	public boolean apply(DynamicContext context) throws WingerException {
		for (JsonNode jsonNode : contents) {
			jsonNode.apply(context);
		}
		return true;
	}
}
