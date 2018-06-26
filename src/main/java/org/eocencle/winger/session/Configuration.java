package org.eocencle.winger.session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eocencle.winger.cache.ResponseCache;
import org.eocencle.winger.intercepter.IntercepterEntity;
import org.eocencle.winger.mapping.AbstractResponseBranch;
import org.eocencle.winger.mapping.FragmentJson;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.util.StrictMap;
import org.springframework.context.ApplicationContext;

public class Configuration {

	// 配置路径
	private String configPath;
	
	// root目录
	private String root;
	
	// 项目名称
	private String contextPath;
	
	// 默认项目名称
	public static final String DEFAULT_CONTEXT_PATH = "winger";
	
	// 服务器模式，默认自带服务器
	private String serverMode = "own";
	
	public static final String SERVER_MODE_OWN = "own";
	public static final String SERVER_MODE_OTHER = "other";
	
	// 端口号，默认8088
	private int port = 8088;
	
	// 资源目录
	private String resourceBase;
	
	// 资源后缀
	private List<String> resourceSuffixes = new ArrayList<String>();
	
	// 默认端口
	public static int DEFAULT_PORT = 8088;
	
	// 系统拦截器
	private List<IntercepterEntity> sysIntercepters = new ArrayList<IntercepterEntity>();
	
	// 其他拦截器
	private List<IntercepterEntity> otherIntercepters = new ArrayList<IntercepterEntity>();
	
	// 扫描包路径
	private StrictMap<String> jars = new StrictMap<String>("Jar Collection");
	
	// 响应分支集合
	private StrictMap<AbstractResponseBranch> branches = new StrictMap<AbstractResponseBranch>("Response Branch Collection");
	
	// json碎片
	private StrictMap<XNode> jsonFragments = new StrictMap<XNode>("Json Fragment Collection");
	
	// 实例生成模式，默认反射
	private String genMode = "invoke";
	
	public static final String GEN_MODE_INVOKE = "invoke";
	public static final String GEN_MODE_SPRING = "spring";
	
	// 命名空间
	private Set<String> namespace = new HashSet<String>();
	
	// 全局空间
	public static final String NAMESPACE_GLOBAL = "_global";
	
	// SpringBean容器
	private ApplicationContext context;
	
	// 更新模式
	private String updateMode = "none";
	
	public static final String UPDATE_MODE_NONE = "none";
	public static final String UPDATE_MODE_AUTO = "auto";
	public static final String UPDATE_MODE_HAND = "hand";
	
	// xml响应文件
	private Set<String> xmlRespFiles = new HashSet<String>();
	
	// uris
	private Set<String> uris = new HashSet<String>();
	
	// 响应缓存
	private ResponseCache responseCache;
	
	public void findJsonFragment() {
		AbstractResponseBranch branch = null;
		for (Map.Entry<String, AbstractResponseBranch> entry : this.branches.entrySet()) {
			branch = entry.getValue();
			if (branch instanceof FragmentJson) {
				((FragmentJson) branch).findFragment();
			}
		}
	}
	
	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getServerMode() {
		return serverMode;
	}

	public void setServerMode(String serverMode) {
		this.serverMode = serverMode;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getResourceBase() {
		return resourceBase;
	}

	public void setResourceBase(String resourceBase) {
		this.resourceBase = resourceBase;
	}
	
	public List<String> getResourceSuffixes() {
		return this.resourceSuffixes;
	}
	
	public void addResourceSuffix(String resourceSuffix) {
		this.resourceSuffixes.add(resourceSuffix);
	}

	public void addSysIntercepter(IntercepterEntity intercepter) {
		this.sysIntercepters.add(intercepter);
	}
	
	public List<IntercepterEntity> getSysIntercepters() {
		return this.sysIntercepters;
	}
	
	public void addOtherIntercepter(IntercepterEntity intercepter) {
		this.otherIntercepters.add(intercepter);
	}
	
	public List<IntercepterEntity> getOtherIntercepters() {
		return this.otherIntercepters;
	}
	
	public void pushJar(String jar, String scan) {
		this.jars.put(jar, scan);
	}

	public StrictMap<String> getJars() {
		return jars;
	}

	public boolean pushBranch(String uri, AbstractResponseBranch branch) {
		return this.branches.put(uri, branch, true);
	}
	
	public StrictMap<AbstractResponseBranch> getBranches() {
		return this.branches;
	}
	
	public AbstractResponseBranch getBranch(String uri) {
		return this.branches.get(uri);
	}
	
	public void pushFragment(String name, XNode node) {
		this.jsonFragments.put(name, node);
	}
	
	public XNode getFragment(String name) {
		return this.jsonFragments.get(name);
	}

	public String getGenMode() {
		return genMode;
	}

	public void setGenMode(String genMode) {
		this.genMode = genMode;
	}
	
	public boolean hasNamespace(String val) {
		return this.namespace.contains(val);
	}
	
	public void addNamespace(String val) {
		this.namespace.add(val);
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public String getUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(String updateMode) {
		this.updateMode = updateMode;
	}

	public Set<String> getXmlRespFiles() {
		return xmlRespFiles;
	}
	
	public void addXmlRespFile(String file) {
		this.xmlRespFiles.add(file);
	}
	
	public boolean xmlRespFileContains(String file) {
		return this.xmlRespFiles.contains(file);
	}

	public Set<String> getUris() {
		return uris;
	}
	
	public List<String> getUriList() {
		List<String> list = new ArrayList<String>();
		for (String uri : this.uris) {
			list.add(uri);
		}
		return list;
	}
	
	public void addUri(String uri) {
		this.uris.add(uri);
	}
	
	public boolean contain(String uri) {
		return this.uris.contains(uri);
	}

	public ResponseCache getResponseCache() {
		return responseCache;
	}

	public void setResponseCache(ResponseCache responseCache) {
		this.responseCache = responseCache;
	}
}
