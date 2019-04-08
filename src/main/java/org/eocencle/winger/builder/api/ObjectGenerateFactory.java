package org.eocencle.winger.builder.api;

import org.eocencle.winger.session.Configuration;

/**
 * 生成类工厂
 * @author huan
 *
 */
public class ObjectGenerateFactory {

	public static ApiObjectGenerate getInstance(Configuration config) {
		String genMode = config.getGenMode();
		if ("invoke".equals(genMode)) {
			return new InvokeObject();
		} else if ("spring".equals(genMode)) {
			return new SpringConatinerObject(config.getContext());
		}
		return new InvokeObject();
	}
	
}
