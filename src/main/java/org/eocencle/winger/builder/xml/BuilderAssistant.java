package org.eocencle.winger.builder.xml;

import org.eocencle.winger.exceptions.BuilderException;
import org.eocencle.winger.type.NamespaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuilderAssistant {

	private static final Logger LOGGER = LoggerFactory.getLogger(BuilderAssistant.class);
	
	// 当前文件名称
	private String currentFileName;
	
	// 当前命名空间
	private String currentNamespace;
	
	public BuilderAssistant(String currentFileName, String currentNamespace) {
		this.currentFileName = currentFileName;
		this.currentNamespace = currentNamespace;
	}
	
	public void setCurrentNamespace(String currentNamespace) {
		if (currentNamespace == null) {
			//throw new BuilderException("The response element requires a contextpath attribute to be specified.");
			LOGGER.debug("The response element requires a contextpath attribute to be specified.");
		}

		if (this.currentNamespace != null && !this.currentNamespace.equals(currentNamespace)) {
			//throw new BuilderException("Wrong namespace. Expected '" + this.currentNamespace + "' but found '" + currentNamespace + "'.");
			LOGGER.debug("Wrong namespace. Expected '" + this.currentNamespace + "' but found '" + currentNamespace + "'.");
		}

		this.currentNamespace = currentNamespace;
	}

	public String applyCurrentNamespace(String base, boolean isReference, NamespaceType type) {
		if (base == null || base.isEmpty()) return null;
		// branch不能被索引
		if (isReference && NamespaceType.BRANCH == type) {
			//throw new BuilderException("Branches cannot be referenced");
			LOGGER.debug("Branches cannot be referenced");
		}
		if (NamespaceType.BRANCH == type) {
			// 第一个字符不是/则补全；仅有/的抛无效action异常
			if (0 != base.indexOf("/")) {
				base = "/" + base;
			} else {
				if (1 == base.length()) {
					//throw new BuilderException("Invalid action from " + base);
					LOGGER.debug("Invalid action from " + base);
				}
			}
		} else if (NamespaceType.JSON == type) {
			// 索引情况下必须包含/和#或者什么也不包含，否则抛无效reference异常
			if (isReference) {
				if (base.contains("/") && base.contains("#")) {
					return base;
				} else {
					if (base.contains("/") || base.contains("#")) {
						//throw new BuilderException("Invalid reference from " + base);
						LOGGER.debug("Invalid reference from " + base);
					}
				}
			}
			// 非索引情况下不能包含/或#
			else {
				if (base.contains("/") || base.contains("#")) {
					//throw new BuilderException("Slashes are not allowed in element id of json, please remove it from " + base);
					LOGGER.debug("Slashes are not allowed in element id of json, please remove it from " + base);
				}
			}
			base = "#" + base;
		}
		return this.currentNamespace + base;
	}

	public String getCurrentNamespace() {
		return currentNamespace;
	}

	public String getCurrentFileName() {
		return currentFileName;
	}

	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
	}
}
