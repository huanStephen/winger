package org.eocencle.winger.builder;

import org.eocencle.winger.cache.Cache;
import org.eocencle.winger.executor.ErrorContext;
import org.eocencle.winger.mapping.AbstractResponseBranch;
import org.eocencle.winger.mapping.AbstractResponseBranch.RequestType;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.mapping.XmlResponseBranch;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.type.NamespaceType;

public class ResponseBuilderAssistant extends BaseBuilder {
	private String currentNamespace;
	private String resource;
	private Cache currentCache;
	private boolean unresolvedCacheRef;

	public ResponseBuilderAssistant(Configuration configuration, String resource) {
		super(configuration);
		ErrorContext.instance().resource(resource);
		this.resource = resource;
	}

	public String getCurrentContextPath() {
		return currentNamespace;
	}

	public void setCurrentNamespace(String currentNamespace) {
		if (currentNamespace == null) {
			throw new BuilderException("The response element requires a contextpath attribute to be specified.");
		}

		if (this.currentNamespace != null && !this.currentNamespace.equals(currentNamespace)) {
			throw new BuilderException("Wrong namespace. Expected '" + this.currentNamespace + "' but found '" + currentNamespace + "'.");
		}

		this.currentNamespace = currentNamespace;
	}

	public String applyCurrentNamespace(String base, boolean isReference, NamespaceType type) {
		if (base == null || base.isEmpty()) return null;
		// branch不能被索引
		if (isReference && NamespaceType.BRANCH == type) {
			throw new BuilderException("Branches cannot be referenced");
		}
		if (NamespaceType.BRANCH == type) {
			// 第一个字符不是/则补全；仅有/的抛无效action异常
			if (0 != base.indexOf("/")) {
				base = "/" + base;
			} else {
				if (1 == base.length()) {
					throw new BuilderException("Invalid action from " + base);
				}
			}
		} else if (NamespaceType.JSON == type) {
			// 索引情况下必须包含/和#或者什么也不包含，否则抛无效reference异常
			if (isReference) {
				if (base.contains("/") && base.contains("#")) {
					return base;
				} else {
					if (base.contains("/") || base.contains("#")) {
						throw new BuilderException("Invalid reference from " + base);
					}
				}
			}
			// 非索引情况下不能包含/或#
			else {
				if (base.contains("/") || base.contains("#")) {
					throw new BuilderException("Slashes are not allowed in element id of json, please remove it from " + base);
				}
			}
			base = "#" + base;
		}
		return this.currentNamespace + base;
	}

	public AbstractResponseBranch addResponseBranch(String name, RequestType type, JsonSource jsonSource) {
		
		if (unresolvedCacheRef) throw new IncompleteElementException("Cache-ref not yet resolved");
		
		name = this.applyCurrentNamespace(name, false, NamespaceType.BRANCH);

		XmlResponseBranch branch = new XmlResponseBranch(name, type, configuration, jsonSource);
		this.configuration.addResponseBranch(branch);
		return branch;
	}
}
