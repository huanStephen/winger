package org.eocencle.winger.scripting.xmltags;

import java.util.List;

public class MixedJsonNode implements JsonNode {
	private List<JsonNode> contents;

	public MixedJsonNode(List<JsonNode> contents) {
		this.contents = contents;
	}

	public boolean apply(DynamicContext context) {
		for (JsonNode sqlNode : contents) {
			sqlNode.apply(context);
		}
		return true;
	}
}
