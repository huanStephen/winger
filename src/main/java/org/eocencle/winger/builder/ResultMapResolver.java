package org.eocencle.winger.builder;

import java.util.List;

import org.eocencle.winger.mapping.Discriminator;
import org.eocencle.winger.mapping.ResultMap;
import org.eocencle.winger.mapping.ResultMapping;

public class ResultMapResolver {
	private final ResponseBuilderAssistant assistant;
	private String id;
	private Class<?> type;
	private String extend;
	private Discriminator discriminator;
	private List<ResultMapping> resultMappings;
	private Boolean autoMapping;

	public ResultMapResolver(ResponseBuilderAssistant assistant, String id, Class<?> type, String extend, Discriminator discriminator, List<ResultMapping> resultMappings, Boolean autoMapping) {
		this.assistant = assistant;
		this.id = id;
		this.type = type;
		this.extend = extend;
		this.discriminator = discriminator;
		this.resultMappings = resultMappings;
		this.autoMapping = autoMapping;
	}

	public ResultMap resolve() {
		return assistant.addResultMap(this.id, this.type, this.extend, this.discriminator, this.resultMappings, this.autoMapping);
	}
}
