package xerxes.soaproject.modules;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import xerxes.soaproject.interfaces.Modules;

public class LuceneOOModule implements Modules {

	static final File INDEX_DIR = new File("index");

	/**
	 * Log4j logger
	 */
	static Logger log4j = Logger.getLogger("org.soarproject.xerxes");

	public LuceneOOModule() {

	}

	public boolean initializeModule() {
		if (INDEX_DIR.exists()) {
			log4j.debug("Cannot save index to '" + INDEX_DIR
					+ "' directory, please delete it first");
			return false;
		}
		return true;
	}

	public String getExtension() {
		return "oo";
	}

	public boolean parseFile(String pathName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseFile(String pathName, String[] tags) {
		Date start = new Date();
		boolean createNewIndex = true;
		try {

			if (INDEX_DIR.list().length > 1)
				createNewIndex = false;

			IndexWriter writer = new IndexWriter(INDEX_DIR,
					new StandardAnalyzer(), createNewIndex,
					IndexWriter.MaxFieldLength.LIMITED);
			log4j.debug("Indexing to directory '" + INDEX_DIR + "'...");
			indexDocs(writer, new File(pathName), tags);
			log4j.debug("Optimizing...");
			writer.optimize();
			writer.close();

			Date end = new Date();
			log4j
					.debug(end.getTime() - start.getTime()
							+ " total milliseconds");

		} catch (IOException e) {
			log4j.debug(" caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
			return false;
		} catch (JDOMException e) {
			log4j.debug(" caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
			return false;
		} catch (JaxenException e) {
			log4j.debug(" caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
			return false;
		}

		return true;
	}

	static void indexDocs(IndexWriter writer, File file, String[] tags)
			throws IOException, JaxenException, JDOMException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]), tags);
					}
				}
			} else {
				log4j.debug("adding " + file);
				try {
					Document doc = new Document();
					String title = file.getName();
					int startExt = title.lastIndexOf('.');

					doc.add(new Field("filename", title, Field.Store.YES,
							Field.Index.ANALYZED));
					for (int i = 0; i < tags.length; i++)
						doc.add(new Field("tag", tags[i], Field.Store.YES,
								Field.Index.ANALYZED));
					if (startExt > 0)
						doc.add(new Field("ext", title.substring(startExt),
								Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("path", file.getAbsolutePath(),
							Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("modified", DateTools.timeToString(file
							.lastModified(), DateTools.Resolution.MINUTE),
							Field.Store.YES, Field.Index.NOT_ANALYZED));
					// De aici incepe partea de parasare
					FileInputStream fis = new FileInputStream(file);
					byte[] bytes = new byte[fis.available()];
					fis.read(bytes);
					fis.close();
					ZipInputStream zis = new ZipInputStream(
							new ByteArrayInputStream(bytes));
					ZipEntry ze = null;
					String text;
					while ((ze = zis.getNextEntry()) != null) {
						if (ze.getName().equals("content.xml")) {
							text = parseContent(ze, zis);
							doc.add(new Field("contents", text, Field.Store.NO,
									Field.Index.ANALYZED));
						} else if (ze.getName().equals("meta.xml")) {
							parseMeta(ze, zis, doc);
						}
					}
					zis.close();
					writer.addDocument(doc);
				}
				// at least on windows, some temporary files raise this
				// exception with an "access denied" message
				// checking if the file can be read doesn't help
				catch (FileNotFoundException e) {
					log4j.debug(" caught a " + e.getClass()
							+ "\n with message: " + e.getMessage());
				}
			}
		}
	}

	// extract as much plain text as possible.
	private static String parseContent(ZipEntry ze, ZipInputStream zis)
			throws JaxenException, JDOMException, IOException {
		StringBuffer res = new StringBuffer();
		FilterInputStream fis = new FilterInputStream(zis) {
			public void close() {
			};
		};
		SAXBuilder builder = new SAXBuilder();
		org.jdom.Document doc = builder.build(fis);
		Element root = doc.getRootElement();
		// XXX this is expensive for very large documents. In those cases
		// another
		// XXX method (direct processing of SAX events, or XMLPull) should be
		// used.
		XPath path = new JDOMXPath(
				"//text:span | //text:p | //text:tab | //text:tab-stop | //text:a");
		path.addNamespace("text", root.getNamespace("text").getURI());
		
		@SuppressWarnings("unused")
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
	private static void parseMeta(ZipEntry ze, ZipInputStream zis,
			Document document) throws IOException, JaxenException,
			JDOMException {
		FilterInputStream fis = new FilterInputStream(zis) {
			public void close() {
			};
		};
		SAXBuilder builder = new SAXBuilder();
		org.jdom.Document doc = builder.build(fis);
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
			if (name.equals("title") && text != null)
				document.add(new Field("title", text, Field.Store.YES,
						Field.Index.ANALYZED));
			else if (name.equals("language") && text != null)
				document.add(new Field("language", text, Field.Store.YES,
						Field.Index.ANALYZED));
			else if (name.equals("creation-date") && text != null)
				document.add(new Field("creation-date", text, Field.Store.YES,
						Field.Index.ANALYZED));
			else if (name.equals("print-date") && text != null)
				document.add(new Field("print-date", text, Field.Store.YES,
						Field.Index.ANALYZED));
			else if (name.equals("generator") && text != null)
				document.add(new Field("generator", text, Field.Store.YES,
						Field.Index.ANALYZED));
			else if (name.equals("creator") && text != null)
				document.add(new Field("creator", text, Field.Store.YES,
						Field.Index.ANALYZED));
		}
	}
}
