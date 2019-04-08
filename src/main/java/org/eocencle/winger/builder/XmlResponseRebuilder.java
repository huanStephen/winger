package org.eocencle.winger.builder;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eocencle.winger.session.Configuration;

/**
 * xml响应重构
 * @author huan
 *
 */
public class XmlResponseRebuilder extends AbstractBuilder {

	private XmlResponseBuilder builder;
	
	private String updateMode;
	
	private Set<String> filePathes = new HashSet<String>();
	
	private Integer delay = 0;
	
	public XmlResponseRebuilder(Configuration config, XmlResponseBuilder builder, String updateMode) {
		super(config);
		this.builder = builder;
		this.updateMode = updateMode;
	}

	@Override
	public Configuration parse() {
		for (String resource : this.filePathes) {
			if (this.config.xmlRespFileContains(resource)) {
				this.builder.loadResponse(resource);
			}
		}
		
		this.config.getResponseCache().clear();
		this.filePathes.clear();
		return this.config;
	}
	
	public void addFilePath(String filePath) {
		if (StringUtils.isNoneBlank(filePath)) {
			this.filePathes.add(filePath);
		}
	}
	
	public void addFilePath(Set<String> filePathes) {
		this.filePathes.addAll(filePathes);
	}

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}

}
