package org.eocencle.winger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eocencle.winger.builder.xml.XMLMapperEntityResolver;
import org.eocencle.winger.scripting.xmltags.OgnlCache;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Test {

	public static void main(String[] args) {
		try {
			testSax();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testOgnl() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("param", 2);
		System.out.println(OgnlCache.getValue("1 == 1 && 5 > param", params));
	}
	
	public static void testSax() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);

		factory.setNamespaceAware(false);
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(false);
		factory.setCoalescing(false);
		factory.setExpandEntityReferences(true);

		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setEntityResolver(new XMLMapperEntityResolver());
		builder.setErrorHandler(new ErrorHandler() {
			public void error(SAXParseException exception) throws SAXException {
				throw exception;
			}

			public void fatalError(SAXParseException exception) throws SAXException {
				throw exception;
			}

			public void warning(SAXParseException exception) throws SAXException {
			}
		});
		InputStream is = Test.class.getClassLoader().getResourceAsStream("conf.xml");
		
		Document doc = builder.parse(new InputSource(is));
		
		XPathFactory xfactory = XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		Node node = (Node) xpath.evaluate("/configuration", doc, XPathConstants.NODE);
		Node environments = (Node) xpath.evaluate("environments", node, XPathConstants.NODE);
		NamedNodeMap attributeNodes = environments.getAttributes();
		if (attributeNodes != null) {
			for (int i = 0; i < attributeNodes.getLength(); i++) {
				Node attribute = attributeNodes.item(i);
				System.out.println(attribute.getNodeName() + "," + attribute.getNodeValue());
			}
		}
	}

}
