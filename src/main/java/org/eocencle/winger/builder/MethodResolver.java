package org.eocencle.winger.builder;

import java.lang.reflect.Method;

public class MethodResolver {
	private final MapperAnnotationBuilder annotationBuilder;
	private Method method;

	public MethodResolver(MapperAnnotationBuilder annotationBuilder, Method method) {
		this.annotationBuilder = annotationBuilder;
		this.method = method;
	}

	public void resolve() {
		annotationBuilder.parseStatement(method);
	}
}