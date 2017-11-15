package org.eocencle.winger.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eocencle.winger.cache.Cache;
import org.eocencle.winger.executor.ErrorContext;
import org.eocencle.winger.mapping.Discriminator;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.mapping.ResponseBranch;
import org.eocencle.winger.mapping.ResultFlag;
import org.eocencle.winger.mapping.ResultMap;
import org.eocencle.winger.mapping.ResultMapping;
import org.eocencle.winger.scripting.LanguageDriver;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.type.ContextPathType;

public class ResponseBuilderAssistant extends BaseBuilder {
	private String currentContextPath;
	private String resource;
	private Cache currentCache;
	private boolean unresolvedCacheRef;

	public ResponseBuilderAssistant(Configuration configuration, String resource) {
		super(configuration);
		ErrorContext.instance().resource(resource);
		this.resource = resource;
	}

	public String getCurrentContextPath() {
		return currentContextPath;
	}

	public void setCurrentContextPath(String currentContextPath) {
		if (currentContextPath == null) {
			throw new BuilderException("The response element requires a contextpath attribute to be specified.");
		}

		if (this.currentContextPath != null && !this.currentContextPath.equals(currentContextPath)) {
			throw new BuilderException("Wrong namespace. Expected '" + this.currentContextPath + "' but found '" + currentContextPath + "'.");
		}

		this.currentContextPath = currentContextPath;
	}

	public String applyCurrentContextPath(String base, boolean isReference, ContextPathType type) {
		if (base == null || base.isEmpty()) return null;
		// branch不能被索引
		if (isReference && ContextPathType.BRANCH == type) {
			throw new BuilderException("Branches cannot be referenced");
		}
		if (ContextPathType.BRANCH == type) {
			// 第一个字符不是/则补全；仅有/的抛无效action异常
			if (0 != base.indexOf("/")) {
				base = "/" + base;
			} else {
				if (1 == base.length()) {
					throw new BuilderException("Invalid action from " + base);
				}
			}
		} else if (ContextPathType.JSON == type) {
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
		return this.currentContextPath + base;
	}

	public Cache useCacheRef(String contextPath) {
		if (contextPath == null) {
			throw new BuilderException("cache-ref element requires a namespace attribute.");
		}
		try {
			unresolvedCacheRef = true;
			Cache cache = configuration.getCache(contextPath);
			if (cache == null) {
				throw new IncompleteElementException("No cache for contextpath '" + contextPath + "' could be found.");
			}
			currentCache = cache;
			unresolvedCacheRef = false;
			return cache;
		} catch (IllegalArgumentException e) {
			throw new IncompleteElementException("No cache for contextpath '" + contextPath + "' could be found.", e);
		}
	}

	public ResultMap addResultMap(
		String id,
		Class<?> type,
		String extend,
		Discriminator discriminator,
		List<ResultMapping> resultMappings,
		Boolean autoMapping) {
		id = this.applyCurrentContextPath(id, false, ContextPathType.BRANCH);
		extend = this.applyCurrentContextPath(extend, true, ContextPathType.BRANCH);

		ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, id, type, resultMappings, autoMapping);
		if (extend != null) {
			if (!configuration.hasResultMap(extend)) {
				throw new IncompleteElementException("Could not find a parent resultmap with id '" + extend + "'");
			}
			ResultMap resultMap = configuration.getResultMap(extend);
			List<ResultMapping> extendedResultMappings = new ArrayList<ResultMapping>(resultMap.getResultMappings());
			extendedResultMappings.removeAll(resultMappings);
			// Remove parent constructor if this resultMap declares a constructor.
			boolean declaresConstructor = false;
			for (ResultMapping resultMapping : resultMappings) {
				if (resultMapping.getFlags().contains(ResultFlag.CONSTRUCTOR)) {
					declaresConstructor = true;
					break;
				}
			}
			if (declaresConstructor) {
				Iterator<ResultMapping> extendedResultMappingsIter = extendedResultMappings.iterator();
				while (extendedResultMappingsIter.hasNext()) {
					if (extendedResultMappingsIter.next().getFlags().contains(ResultFlag.CONSTRUCTOR)) {
						extendedResultMappingsIter.remove();
					}
				}
			}
			resultMappings.addAll(extendedResultMappings);
		}
		resultMapBuilder.discriminator(discriminator);
		ResultMap resultMap = resultMapBuilder.build();
		configuration.addResultMap(resultMap);
		return resultMap;
	}

	public ResponseBranch addResponseBranch(String action, String method, JsonSource jsonSource, Class<?> parameterType, LanguageDriver lang) {
		
		if (unresolvedCacheRef) throw new IncompleteElementException("Cache-ref not yet resolved");
		
		action = this.applyCurrentContextPath(action, false, ContextPathType.BRANCH);

		ResponseBranch.Builder branchBuilder = new ResponseBranch.Builder(configuration, action, method, jsonSource);
		branchBuilder.resource(resource);
		branchBuilder.lang(lang);

		ResponseBranch branch = branchBuilder.build();
		this.configuration.addResponseBranch(branch);
		return branch;
	}
}
