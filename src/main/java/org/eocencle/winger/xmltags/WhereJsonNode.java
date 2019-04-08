package org.eocencle.winger.xmltags;

import org.eocencle.winger.session.Configuration;

public class WhereJsonNode extends TrimJsonNode {
	public WhereJsonNode(Configuration config, JsonNode contents) {
		super(config, contents, "WHERE", "AND |OR |AND\n|OR\n|AND\r|OR\r", null, null);
	}
}
