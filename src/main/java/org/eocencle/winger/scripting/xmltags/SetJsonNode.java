package org.eocencle.winger.scripting.xmltags;

import org.eocencle.winger.session.Configuration;

public class SetJsonNode extends TrimJsonNode {
	public SetJsonNode(Configuration configuration,JsonNode contents) {
		super(configuration, contents, "SET", null, null, ",");
	}
}
