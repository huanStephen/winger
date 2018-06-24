package org.eocencle.winger.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

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
	
	public Session build(InputStream inputStream, ApplicationContext context) throws SAXException, IOException, ParserConfigurationException {
		Configuration config = new Configuration();
		config.setContext(context);
		ProjectBuilder builder = new ProjectBuilder(config, new XPathParser(inputStream));
		return this.build(builder.parse());
	}
	
	public Session build(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
		ProjectBuilder builder = new ProjectBuilder(new Configuration(), new XPathParser(inputStream));
		return this.build(builder.parse());
	}
	
	public Session build(Reader reader) {
		return null;
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
