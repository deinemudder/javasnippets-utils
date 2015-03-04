package org.javasnippets.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Utils zur XML-Verarbeitung.
 * 
 * @author ckroeger
 */
public class XmlUtils {

	private static final Logger log = Logger.getLogger(XmlUtils.class);

	/**
	 * Liefert zu einem XPath-Ausdruck eine Liste der gefundenen Elemente.
	 * 
	 * @param doc
	 *            das geladene {@link Document} (!NULL)
	 * @param xPath
	 *            der XPath-Ausdruck (!EMPTY)
	 * @return {@link List Liste} der gefundenen Elemente
	 */
	public static List<Element> getXPathElements(Document doc, String xPath) {
		{ // sanity-checks
			Validate.notNull(doc);
			Validate.notEmpty(xPath);
		}
		XPathFactory instance = XPathFactory.instance();
		XPathExpression<Element> startDateExpression = instance.compile(xPath,
				Filters.element());
		List<Element> foundElements = startDateExpression.evaluate(doc);
		return foundElements;
	}

	/**
	 * Liefert zu einem XPath-Ausdruck ein gefundenes Element. Wenn nicht genau
	 * ein Element gefunden wurde, gibt es eine Fehlermeldung.
	 * 
	 * @param doc
	 *            das geladene {@link Document} (!NULL)
	 * @param xPath
	 *            der XPath-Ausdruck (!EMPTY)
	 * @return gefundenes Element
	 */
	public static Element getXPathElement(Document doc, String xPath) {
		List<Element> foundElements = getXPathElements(doc, xPath);
		Validate.notNull(foundElements, "No Element found under XPath = "
				+ xPath);
		Validate.isTrue(foundElements.size() == 1, "Found more than one hit ("
				+ foundElements.size() + ") for xpath = " + xPath);
		return foundElements.get(0);
	}

	/**
	 * Liefert ob den XPathElement in XPath-Ausdruck ein gefunden wird.
	 * 
	 * @param doc
	 *            das geladene {@link Document} (!NULL)
	 * @param xPath
	 *            der XPath-Ausdruck (!EMPTY)
	 * @return true wenn XPathElement gefunden, ansonst false
	 */
	public static boolean isXPathElementFound(Document doc, String xPath) {

		boolean isFound = false;

		List<Element> foundElements = getXPathElements(doc, xPath);
		if (CollectionUtils.isNotEmpty(foundElements)) {
			isFound = true;
		}
		return isFound;
	}

	/**
	 * Lädt aus dem übergebenen Classpath die XML-Datei und erzeugt ein
	 * {@link Document}-Objekt.
	 * 
	 * @param path
	 *            der Classpath-Pfad zur XML-Datei (!EMPTY)
	 * @return
	 */
	public static Document getDocumentFromXml(String xml) {
		Validate.notEmpty(xml);
		try {
			InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			Document document = new SAXBuilder().build(stream);

			if (log.isDebugEnabled()) {
				StringWriter sw = new StringWriter();
				XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
				out.output(document, sw);
				// log.debug("Read Xml = " + sw.toString());
			}
			return document;
		} catch (Exception e) {
			log.debug("Failures XML: \n" + xml);
			log.error(e);
			throw new RuntimeException("Failed to read XML, message = "
					+ e.getMessage());
		}

	}

	/**
	 * Lädt aus dem übergebenen Classpath die XML-Datei und erzeugt ein
	 * {@link Document}-Objekt.
	 * 
	 * @param path
	 *            der Classpath-Pfad zur XML-Datei (!EMPTY)
	 * @return
	 */
	public static Document getDocumentFromClaspath(String path) {
		Validate.notEmpty(path);
		String xml = readFully(path, XmlUtils.class);

		Document fromXml = getDocumentFromXml(xml);
		return fromXml;
	}

	/**
	 * Extrahiert das über den XPath ermittelte Element und wandelt es zu einem
	 * Java-Objekt.
	 * 
	 * @param xPath
	 *            der XPath (!EMPTY)
	 * @param document
	 *            das DOM-{@link Document} (!NULL)
	 * @return das extrahierte Java-Objekt (!NULL)
	 */
	public static Object extractXPathObject(String xPath, Document document) {
		{ // sanity-checks
			Validate.notEmpty(xPath);
			Validate.notNull(document);
		}
		Element element = XmlUtils.getXPathElement(document, xPath);
		// Zurück als XML
		String elementAsXml = getElementAsXmlString(element);
		// In Java-Object umwandeln und in Typ casten
		Object extr = materialize(elementAsXml);
		return extr;

	}

	/**
	 * Liefert die XML-Repräsentation eines {@link Element}s.
	 * 
	 * @param element
	 *            das {@link Element} (!NULL)
	 * @return XML-String (!NULL)
	 */
	public static String getElementAsXmlString(Element element) {
		{ // sanity-check
			Validate.notNull(element);
		}
		XMLOutputter outp = new XMLOutputter();
		String s = outp.outputString(element);
		return s;
	}

	/**
	 * Deserialisiert ein XML-{@link Element} in ein Java-Objekt.
	 * 
	 * @param element
	 *            das {@link Element} (!NULL)
	 * @return ein Java-Objekt
	 */
	public static Object getElementObject(Element element) {
		{ // sanity-check
			Validate.notNull(element);
		}
		String xmlString = getElementAsXmlString(element);
		Object materializedObject = materialize(xmlString);
		return materializedObject;
	}

	private static final String CONTENT_REMOVED = "... content removed ...";
	private static final int[] NONEFOUND = new int[] { -1, -1 };

	/**
	 * Log auf Level DEBUG die XML-Struktur des übergebenen Objektes
	 * 
	 * @param log
	 *            der {@link Logger} (!NULL)
	 * @param object
	 *            das Objekt welches als XML ausgegeben werden soll (!NULL)
	 * @param msg
	 *            die Meldung die zusätzlich geloggt werden soll
	 */
	public static void logObject(final Logger log, Object object, String msg) {
		assert log != null;
		if (log.isDebugEnabled()) {
			String xml = getXMLStringOfObject(object);
			log.debug(msg);
			log.debug(xml);
		}
	}

	/**
	 * Erzeugt aus einem Objekt eine XML-Repräsentation.
	 * 
	 * @param object
	 *            das umzuwandelne Objekt (!NULL)
	 * @return XML-Repräsentation als String
	 */
	public static String getXMLStringOfObject(Object object) {
		String xml = new XStream(new DomDriver()).toXML(object);
		return xml;
	}

	/**
	 * Erzeugt aus einem XML-XStream-String wieder ein Java-Objekt.
	 * 
	 * @param objectAsXML
	 *            XML-String (!EMPTY)
	 * @return
	 */
	public static Object materialize(String objectAsXML) {
		Validate.notEmpty(objectAsXML);
		Object fromXML = new XStream(new DomDriver()).fromXML(objectAsXML);
		return fromXML;
	}

	public static String replaceXmlElement(String xml, String element,
			String replacement) {
		boolean emptyElement = StringUtils.isBlank(element);
		boolean emptyXML = StringUtils.isBlank(xml);
		if (emptyXML || emptyElement) {
			return xml;
		}
		replacement = replacement == null ? CONTENT_REMOVED : replacement;
		while (true) {
			int[] startEnd = findStartEnd(xml, element);
			if (startEnd == NONEFOUND) {
				break;
			}
			xml = xml.substring(0, startEnd[0]) + "<" + element + "_ "
					+ replacement + " " + xml.substring(startEnd[1]);
		}
		return xml;
	}

	private static int[] findStartEnd(String xml, String element) {
		boolean blank = StringUtils.isBlank(xml);
		if (blank) {
			return NONEFOUND;
		}
		int start = xml.indexOf("<" + element);
		if (start == -1) {
			return NONEFOUND;
		}
		int length = element.length();
		int pos = start + length + 1;
		if (xml.charAt(pos) == '_') {
			start = xml.indexOf("<" + element, pos + 1);
			if (start == -1) {
				return NONEFOUND;
			}
		}
		int end = xml.indexOf("</" + element + ">", start + length);
		if (end == -1) {
			end = xml.indexOf("/>", start + length);
			if (end == -1) {
				return NONEFOUND;
			}
		}
		return new int[] { start, end };
	}

	/**
	 * Liest aus einem Classpath eine Datei als String ein. Diese Datei muss
	 * UTF8 kodiert sein.
	 * 
	 * @param classPath
	 *            Classpath zur Resource (!EMPTY)
	 * @param clazzForLoader
	 *            Aufrufer-Klasse damit die gewünschte Resource geladen werden
	 *            kann. (!NULL)
	 * @return
	 */
	public static String readFully(String classPath, Class<?> clazzForLoader) {
		InputStream resourceAsStream = clazzForLoader
				.getResourceAsStream(classPath);
		String xml = readFully(resourceAsStream);
		if (xml == null) {
			throw new IllegalArgumentException("Can not find given resource = "
					+ classPath);
		}
		return xml;
	}

	/**
	 * Liest aus einem Stream in einen String. <b>Achtung:</b> Der Aufrufer muss
	 * sich um das schließen des InputStreams kümmern.
	 * 
	 * @param resourceAsStream
	 *            der {@link InputStream} (!NULL)
	 * @return eingelesener String
	 */
	public static String readFully(InputStream resourceAsStream) {
		try {
			String string = IOUtils.toString(resourceAsStream, "UTF8");
			return string.replaceAll("\r\n", "\n");
		} catch (NullPointerException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

}
