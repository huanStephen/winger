package org.eocencle.winger.mapping;

import java.util.ArrayList;
import java.util.List;

import org.eocencle.winger.exceptions.WingerException;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.util.StrictMap;

/**
 * xml响应分支
 * @author huan
 *
 */
public class XmlResponseBranch extends AbstractResponseBranch implements FragmentJson {

	// 分支节点
	private XNode node;
	
	// 碎片名
	private List<String> fragmentNames = new ArrayList<String>();
	
	// json碎片
	private StrictMap<XNode> jsonFragments = new StrictMap<XNode>("Response Branch Fragment Collection");
	
	// json原
	private JsonSource jsonSource;
	
	public XmlResponseBranch(Configuration config, String namespace, String url, XNode node) {
		super(config, namespace, url);
		this.node = node;
	}
	
	@Override
	public String getCompleteJson(StrictMap<Object> params) throws WingerException {
		return this.format(this.jsonSource.getBoundJson(params).getJson());
	}
	
	private String format(String json) {
		return json.replaceAll("\n", "").replaceAll("\t", "").replaceAll(" : ", ":");
	}

	public void findFragment() {
		XNode node = null;
		for (String name : fragmentNames) {
			node = this.config.getFragment(name);
			if (null == node) {
				throw new RuntimeException("未找到名字为" + name + "的json碎片！");
			} else {
				this.jsonFragments.put(name, node);
			}
		}
	}
	
	public void addFragmentName(String name) {
		this.fragmentNames.add(name);
	}

	public JsonSource getJsonSource() {
		return jsonSource;
	}

	public void setJsonSource(JsonSource jsonSource) {
		this.jsonSource = jsonSource;
	}
}
