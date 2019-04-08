package org.eocencle.winger.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eocencle.winger.builder.ProjectBuilder;
import org.eocencle.winger.intercepter.Intercepter;
import org.eocencle.winger.intercepter.IntercepterChain;
import org.eocencle.winger.intercepter.IntercepterEntity;
import org.eocencle.winger.parsing.XPathParser;
import org.springframework.context.ApplicationContext;
import org.xml.sax.SAXException;

/**
 * session工厂类
 * @author huan
 *
 */
public class SessionFactory {
	
	public Session build(File file, ApplicationContext context) throws SAXException, IOException, ParserConfigurationException {
		Configuration config = new Configuration();
		config.setContext(context);
		String path = file.getPath();
		config.setConfigPath(path);
		if (-1 == path.indexOf("/")) {
			config.setRoot(path.substring(0, path.lastIndexOf("\\")));
		} else {
			config.setRoot(path.substring(0, path.lastIndexOf("/")));
		}
		ProjectBuilder builder = new ProjectBuilder(config, new XPathParser(new FileInputStream(file)));
		return this.build(builder.parse());
	}
	
	public Session build(String file, ApplicationContext context) throws SAXException, IOException, ParserConfigurationException {
		return this.build(new File(file), context);
	}
	
	public Session build(File file) throws SAXException, IOException, ParserConfigurationException {
		return this.build(file, null);
	}
	
	public Session build(String file) throws SAXException, IOException, ParserConfigurationException {
		return this.build(file, null);
	}
	
	public Session build(Configuration config) {
		config.findJsonFragment();
		
		IntercepterChain chain = new IntercepterChain();
		for (IntercepterEntity entity : config.getOtherIntercepters()) {
			chain.addIntercepter((Intercepter) entity.getInstance());
		}
		DefaultHttpSession session = new DefaultHttpSession(config);
		chain.addIntercepter(session);
		
		session.setChain(chain);
		
		return session;
	}
}
