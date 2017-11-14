package org.eocencle.winger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eocencle.winger.builder.xml.XMLConfigBuilder;
import org.eocencle.winger.builder.xml.XMLMapperEntityResolver;
import org.eocencle.winger.scripting.xmltags.OgnlCache;
import org.eocencle.winger.session.JsonSession;
import org.eocencle.winger.session.JsonSessionFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class Test {

	public static void main(String[] args) {
		testWinger();
	}
	
	public static void testWinger() {
		InputStream is = Test.class.getClassLoader().getResourceAsStream("config.xml");
		JsonSession session = new JsonSessionFactory().build(is);
		Map<String, Object> params = new HashMap<>();
		params.put("id", 2);
		params.put("value", "张三");
		System.out.println(session.request("/winger/item/addItem", params));
	}
	
	public static void testOgnl() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("param", 2);
		System.out.println(OgnlCache.getValue("1 == 1 && 5 > param", params));
	}
	
	public static void testSax() {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			SaxParseXml parseXml = new SaxParseXml();
			InputStream is = Test.class.getClassLoader().getResourceAsStream("conf.xml");
			parser.parse(is, parseXml);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class SaxParseXml extends DefaultHandler {

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			System.out.println(qName);
			for (int i = 0; i < attributes.getLength(); i ++) {
				System.out.println(attributes.getQName(i) + "," + attributes.getValue(i));
				
			}
			super.startElement(uri, localName, qName, attributes);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			System.out.println(qName);
			super.endElement(uri, localName, qName);
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			super.characters(ch, start, length);
		}
		
	}
	
	public static void testXPath() {
		try {
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
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
