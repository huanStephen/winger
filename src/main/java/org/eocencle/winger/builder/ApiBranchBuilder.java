package org.eocencle.winger.builder;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eocencle.winger.builder.api.ApiObjectGenerate;
import org.eocencle.winger.mapping.ApiBranch;
import org.eocencle.winger.mapping.ApiNamespace;
import org.eocencle.winger.mapping.ApiResponseBranch;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.util.JarLoaderUtil;
import org.eocencle.winger.web.server.JettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Api分支建构类
 * @author huan
 *
 */
public class ApiBranchBuilder extends AbstractBuilder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiBranchBuilder.class);
	
	// 生成器
	private ApiObjectGenerate generate;
	
	public ApiBranchBuilder(Configuration config, ApiObjectGenerate generate) {
		super(config);
		this.generate = generate;
	}

	@Override
	public Configuration parse() {
		List<Class<?>> clazzs = null;
		Object obj = null;
		String namespace = null, uri = null;
		ApiBranch apiBranch = null;
		ApiResponseBranch branch = null;
		ApiNamespace apiNamespace = null;
		String userDir = System.getProperty("user.dir");
		String jarPath = null;
		
		try {
			for (Entry<String, String> jar : this.config.getJars().entrySet()) {
				jarPath = jar.getKey();
				if (-1 == jarPath.indexOf(":") && 0 != jarPath.indexOf("/")) {
					jarPath = userDir + "\\lib\\" + jarPath;
				}
				JarLoaderUtil.loadJarPath(jarPath);
				LOGGER.info("Jar file " + jarPath + " was depended");
				
				// 注意：这里只能扫描到打包时勾选Add directory entries的jar包的类
				clazzs = this.getClasses(jar.getValue());
				for (Class<?> clazz : clazzs) {
					obj = this.generate.generate(clazz);
					
					apiNamespace = clazz.getAnnotation(ApiNamespace.class);
					if (null != apiNamespace) {
						namespace = apiNamespace.value();
					} else {
						namespace = "";
					}
					
					for (Method m : clazz.getDeclaredMethods()) {
						apiBranch = m.getAnnotation(ApiBranch.class);
						if (null != apiBranch && null != obj) {
							try {
								uri = namespace + apiBranch.value();
								
								LOGGER.info("Api response [" + uri + "] onto " + clazz.getName() + "." + m.getName());
								
								branch = new ApiResponseBranch(this.config, namespace, uri, obj, m);
								branch.setType(apiBranch.type());
								this.config.pushBranch(uri, branch);
								
								this.config.addUri(uri);
							} catch (IllegalArgumentException e) {
								LOGGER.info(e.getMessage());
							}
						}
					}
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return this.config;
	}
	
	public List<Class<?>> getClasses(String packageName) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		
		boolean recursive = true;
		
		String packageDirName = packageName.replace('.', '/');
		
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			
			while (dirs.hasMoreElements()){
				
				URL url = dirs.nextElement();
				
				String protocol = url.getProtocol();
				
				if ("file".equals(protocol)) {
					
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					
					this.findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)){
					
					JarFile jar;
					try {
						
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						
						Enumeration<JarEntry> entries = jar.entries();
						
						while (entries.hasMoreElements()) {
							
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							
							if (name.charAt(0) == '/') {
								
								name = name.substring(1);
							}
							
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								
								if (idx != -1) {
									
									packageName = name.substring(0, idx).replace('/', '.');
								}
								
								if ((idx != -1) || recursive) {
									
									if (name.endsWith(".class") && !entry.isDirectory()) {
										
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										try {
											
											classes.add(Class.forName(packageName + '.' + className));
										} catch (ClassNotFoundException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		return classes;
	}
	
	private void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
		
		File dir = new File(packagePath);
		
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		
		File[] dirfiles = dir.listFiles(new FileFilter() {
		
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		
		for (File file : dirfiles) {
			
			if (file.isDirectory()) {
				this.findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
					file.getAbsolutePath(), recursive, classes);
			}
			else {
				
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					
					classes.add(Class.forName(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
