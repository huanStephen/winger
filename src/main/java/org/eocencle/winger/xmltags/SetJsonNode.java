package org.eocencle.winger.xmltags;

import org.eocencle.winger.session.Configuration;

public class SetJsonNode extends TrimJsonNode {
	public SetJsonNode(Configuration config,JsonNode contents) {
		super(config, contents, "SET", null, null, ",");
	}
}
