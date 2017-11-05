package org.eocencle.winger.scripting.xmltags;

import org.eocencle.winger.session.Configuration;

public class WhereJsonNode extends TrimJsonNode {
	public WhereJsonNode(Configuration configuration, JsonNode contents) {
		super(configuration, contents, "WHERE", "AND |OR |AND\n|OR\n|AND\r|OR\r", null, null);
	}
}
