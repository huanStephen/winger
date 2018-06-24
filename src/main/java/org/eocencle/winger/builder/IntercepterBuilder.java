package org.eocencle.winger.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eocencle.winger.intercepter.IntercepterEntity;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.util.StrictMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 拦截器建构类
 * @author huan
 *
 */
public class IntercepterBuilder extends AbstractXmlBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(IntercepterBuilder.class);
	
	public IntercepterBuilder(Configuration container, XNode node) {
		super(container, node);
	}

	@Override
	public Configuration parse() {
		this.parseElements(this.node.evalNode("intercepters"));
		return this.config;
	}
	
	private void parseElements(XNode node) {
		if (null == node) {
			// 加警告，无拦截器配置
			LOGGER.info("No Intercepter set");
			return ;
		}
		List<XNode> nodes = node.getChildren();
		IntercepterEntity entity = null;
		for (XNode n : nodes) {
			entity = this.parseIntercepter(n);
			if (null != entity) {
				this.config.addOtherIntercepter(entity);
			}
		}
	}
	
	private IntercepterEntity parseIntercepter(XNode node) {
		IntercepterEntity entity = new IntercepterEntity();
		entity.setClazz(node.getStringAttribute("class"));
		try {
			entity.setCls(Class.forName(entity.getClazz()));
		} catch (ClassNotFoundException e) {
			LOGGER.warn("Intercepter " + entity.getClazz() + " not found！");
			return null;
		}
		
		List<XNode> nodes = node.getChildren();
		List<XNode> props = null;
		String name = null, na = null;
		String type = null;
		XNode prop = null;
		String[] arrVal = null;
		List<String> listVal = new ArrayList<String>();
		StrictMap<String> mapVal = new StrictMap<String>("Map property");
		for (XNode n : nodes) {
			name = n.getStringAttribute("name");
			if (StringUtils.isBlank(name)) {
				LOGGER.warn("Intercepter name is null!");
				continue;
			}
			type = n.getStringAttribute("type");
			props = n.getChildren();
			if (0 == props.size()) {
				if (StringUtils.isBlank(type) || "string".equals(type.toLowerCase())) {
					entity.push(name, n.getStringBody());
				} else if ("boolean".equals(type.toLowerCase())) {
					entity.push(name, n.getBooleanBody());
				} else if ("int".equals(type.toLowerCase())) {
					entity.push(name, n.getIntBody());
				} else if ("long".equals(type.toLowerCase())) {
					entity.push(name, n.getLongBody());
				} else if ("float".equals(type.toLowerCase())) {
					entity.push(name, n.getFloatBody());
				} else if ("double".equals(type.toLowerCase())) {
					entity.push(name, n.getDoubleBody());
				}
			} else {
				prop = props.get(0);
				if ("array".equals(prop.getName())) {
					props = prop.getChildren();
					arrVal = new String[props.size()];
					for (int i = 0; i < props.size(); i ++) {
						if ("value".equals(props.get(i).getName())) {
							arrVal[i] = props.get(i).getStringBody();
						}
					}
					entity.push(name, arrVal);
				} else if ("list".equals(prop.getName())) {
					props = prop.getChildren();
					listVal.clear();
					for (XNode valNode : props) {
						if ("value".equals(valNode.getName())) {
							listVal.add(valNode.getStringBody());
						}
					}
					entity.push(name, listVal);
				} else if ("map".equals(prop.getName())) {
					props = prop.getChildren();
					mapVal.clear();
					for (XNode valNode : props) {
						if ("entry".equals(valNode.getName())) {
							na = valNode.getStringAttribute(name);
							if (StringUtils.isNoneBlank(na)) {
								mapVal.put(na, valNode.getStringBody());
							}
						}
					}
					entity.push(name, mapVal);
				} else {
					LOGGER.warn("未知属性！");
					continue;
				}
			}
		}
	
		return entity;
	}

}
