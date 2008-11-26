package xerxes.soaproject.tests.modules;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

public class TestOOParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		FileInputStream fis = new FileInputStream(new File("ootest.ods"));
		byte[] bytes = new byte[fis.available()];
		fis.read(bytes);
		fis.close();
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes));
		ZipEntry ze = null;
		String text;
		while ((ze = zis.getNextEntry()) != null) {
			if (ze.getName().equals("content.xml")) {
				text = parseContent(ze, zis);
				System.out.println(text);
			} else if (ze.getName().equals("meta.xml")) {
				parseMeta(ze, zis);
			}
		}
		zis.close();
	}

	// extract as much plain text as possible.
	private static String parseContent(ZipEntry ze, ZipInputStream zis)
			throws Exception {
		StringBuffer res = new StringBuffer();
		FilterInputStream fis = new FilterInputStream(zis) {
			public void close() {
			};
		};
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(fis);
		Element root = doc.getRootElement();
		// XXX this is expensive for very large documents. In those cases
		// another
		// XXX method (direct processing of SAX events, or XMLPull) should be
		// used.
		XPath path = new JDOMXPath(
				"//text:span | //text:p | //text:tab | //text:tab-stop | //text:a");
		path.addNamespace("text", root.getNamespace("text").getURI());
		Namespace xlink = Namespace.getNamespace("xlink",
				"http://www.w3.org/1999/xlink");
		List list = path.selectNodes(doc);
		boolean lastp = true;
		for (int i = 0; i < list.size(); i++) {
			Element el = (Element) list.get(i);
			String text = el.getText();
			if (el.getName().equals("p")) {
				// skip empty paragraphs
				if (!text.equals("")) {
					if (!lastp)
						res.append("\n");
					res.append(text + "\n");
					lastp = true;
				}
			} else if (el.getName().startsWith("tab")) {
				res.append("\t");
				lastp = false;
			} else if (el.getName().equals("a")) {
				List nl = el.getChildren();
				String a = null;
				for (int k = 0; k < nl.size(); k++) {
					Element anchor = (Element) nl.get(k);
					String nsName = anchor.getNamespacePrefix() + ":"
							+ anchor.getName();
					if (!nsName.equals("text:span"))
						continue;
					a = anchor.getText();
					break;
				}

				if (a != null && !a.equals("")) {
					if (!lastp)
						res.append(' ');
					res.append(a);
					lastp = false;
				}
			} else {
				if (!text.equals("")) {
					if (!lastp)
						res.append(' ');
					res.append(text);
				}
				lastp = false;
			}
		}
		return res.toString();
	}

	// extract metadata and convert them to Nutch format
	private static void parseMeta(ZipEntry ze, ZipInputStream zis)
			throws Exception {
		FilterInputStream fis = new FilterInputStream(zis) {
			public void close() {
			};
		};
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(fis);
		XPath path = new JDOMXPath("/office:document-meta/office:meta/*");
		Element root = doc.getRootElement();
		path.addNamespace("office", root.getNamespace("office").getURI());
		List list = path.selectNodes(doc);
		for (int i = 0; i < list.size(); i++) {
			Element n = (Element) list.get(i);
			String text = n.getText();
			if (text.trim().equals(""))
				continue;
			String name = n.getName();
			if (name.equals("title"))
				System.out.println("TITLE" + " " + text);
			else if (name.equals("language"))
				System.out.println("LANGUAGE" + " " + text);
			else if (name.equals("creation-date"))
				System.out.println("DATE" + " " + text);
			else if (name.equals("print-date"))
				System.out.println("LAST_PRINTED" + " " + text);
			else if (name.equals("generator"))
				System.out.println("APPLICATION_NAME" + " " + text);
			else if (name.equals("creator"))
				System.out.println("CREATOR" + " " + text);
		}
	}
}
