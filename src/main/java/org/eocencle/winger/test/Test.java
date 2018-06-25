package org.eocencle.winger.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eocencle.winger.builder.ProjectBuilder;
import org.eocencle.winger.exceptions.WingerException;
import org.eocencle.winger.io.Resources;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.session.DefaultHttpSession;
import org.eocencle.winger.session.Session;
import org.eocencle.winger.test.api.UserEntity;
import org.eocencle.winger.util.JarLoaderUtil;
import org.eocencle.winger.util.StrictMap;

public class Test {

	/**
	 * main doc
	 * @param args	123
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		/*Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", 2);
		params.put("value", 55);
		Map<String, Object> test = new HashMap<String, Object>();
		test.put("address", "南京东路");
		params.put("test", test);
		params.put("result", true);
		Integer[] myList = new Integer[]{1, 2, 3, 4};
		params.put("idx", Arrays.asList(myList));
		
		System.out.println(params.hashCode());
		
		test.put("address", "南京西路");
		System.out.println(params.hashCode());*/
		System.out.println(Resources.getResourceAsFile("/"));
	}
	
	public void test1() {
		JarLoaderUtil.loadJarPath("C:\\Users\\dell\\Desktop\\test\\lib\\service.jar");
		try {
			Class<?> clazz = Class.forName("org.eocencle.service.UserService");
			Object obj = clazz.newInstance();
			Method method = clazz.getDeclaredMethod("toString");
			System.out.println(method.invoke(obj));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void test() throws IOException, WingerException {
		Configuration config = new Configuration();
		ProjectBuilder builder = new ProjectBuilder(config, new XPathParser(Resources.getResourceAsStream("config.xml")));
		Session session = new DefaultHttpSession(builder.parse());
		StrictMap<Object> params = new StrictMap<Object>("Params");
		params.put("id", 2);
		/*params.put("name", "张三");
		StrictMap<Object> user = new StrictMap<Object>("Params");
		user.put("id", 5);
		user.put("username", "李四");
		user.put("password", "123456");
		user.put("age", 18);
		user.put("sex", true);
		user.put("address", "上海市");
		params.put("user", user);*/
		params.put("value", 55);
		StrictMap<Object> test = new StrictMap<Object>("Test");
		test.put("address", "南京东路");
		params.put("test", test);
		params.put("result", true);
		Integer[] myList = new Integer[]{1, 2, 3, 4};
		params.put("idx", Arrays.asList(myList));
		System.out.println(session.request("/winger/item/addItem", params));
	}
}
