package org.eocencle.winger.builder.annotation;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.eocencle.winger.builder.BaseBuilder;
import org.eocencle.winger.mapping.ApiBranch;
import org.eocencle.winger.mapping.ApiResponseBranch;
import org.eocencle.winger.scripting.java.JavaJsonSource;
import org.eocencle.winger.session.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ResponseAnnotationBuilder extends BaseBuilder {

	private String packet;
	
	private String container;
	
	private ApplicationContext applicationContext;
	
	public ResponseAnnotationBuilder(Configuration configuration, String packet, String container) {
		super(configuration);
		this.packet = packet;
		this.container = container;
		this.applicationContext = new ClassPathXmlApplicationContext(this.container);
	}
	
	public void parse() {
		if (StringUtils.isNotBlank(this.packet)) {
			List<Class<?>> cls = this.getClasses(this.packet);
			Object bean = null;
			for (Class<?> clazz : cls) {
				try {
					bean = this.applicationContext.getBean(clazz);
				} catch (BeansException e) {
					// object not found
					continue;
				}
				for (Method m : clazz.getDeclaredMethods()) {
					ApiBranch apiBranch = m.getAnnotation(ApiBranch.class);
					if (null != apiBranch && null != bean) {
						ApiResponseBranch apiResponseBranch = new ApiResponseBranch(apiBranch.value(), bean, m, configuration, new JavaJsonSource(bean, m, this.configuration));
						this.configuration.addResponseBranch(apiResponseBranch);
					}
				}
			}
		}
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
