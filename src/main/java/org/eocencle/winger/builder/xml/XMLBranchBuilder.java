package org.eocencle.winger.builder.xml;

import org.eocencle.winger.builder.BaseBuilder;
import org.eocencle.winger.builder.ResponseBuilderAssistant;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.scripting.LanguageDriver;
import org.eocencle.winger.session.Configuration;

import com.sun.javafx.collections.MappingChange.Map;

public class XMLBranchBuilder extends BaseBuilder {
	private ResponseBuilderAssistant builderAssistant;
	private XNode context;

	public XMLBranchBuilder(Configuration configuration, ResponseBuilderAssistant builderAssistant, XNode context) {
		super(configuration);
		this.builderAssistant = builderAssistant;
		this.context = context;
	}

	public void parseBranchNode() {
		String action = this.context.getStringAttribute("action");
		String method = this.context.getStringAttribute("method");

		XMLIncludeTransformer includeParser = new XMLIncludeTransformer(this.configuration, this.builderAssistant);
		includeParser.applyIncludes(this.context.getNode());

		LanguageDriver langDriver = getLanguageDriver(null);
		JsonSource jsonSource = langDriver.createJsonSource(this.configuration, this.context, null);
		
		this.builderAssistant.addResponseBranch(action, method, jsonSource, Map.class, langDriver);
	}

	private LanguageDriver getLanguageDriver(String lang) {
		Class<?> langClass;
		if (lang == null) {
			langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
		} else {
			langClass = resolveClass(lang);
			configuration.getLanguageRegistry().register(langClass);
		}
		if (langClass == null) {
			langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
		}
		return configuration.getLanguageRegistry().getDriver(langClass);
	}
}
