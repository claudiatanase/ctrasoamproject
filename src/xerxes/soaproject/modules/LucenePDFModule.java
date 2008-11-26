package xerxes.soaproject.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.demo.FileDocument;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexWriter;
import org.pdfbox.encryption.DocumentEncryption;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;

import xerxes.soaproject.interfaces.Modules;

public class LucenePDFModule implements Modules {

	static final File INDEX_DIR = new File("index");

	/**
	 * Log4j logger
	 */
	static Logger log4j = Logger.getLogger("org.soarproject.xerxes");

	public LucenePDFModule() {

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
		return "pdf";
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
		} catch (CryptographyException e) {
			log4j.debug(" caughy a " + e.getClass() + "\n with message: "
					+ e.getMessage());
		} catch (InvalidPasswordException e) {
			log4j.debug(" caughy a " + e.getClass() + "\n with message: "
					+ e.getMessage());
		}

		return true;
	}

	static void indexDocs(IndexWriter writer, File file, String[] tags)
			throws IOException, CryptographyException, InvalidPasswordException {
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
							Field.Store.YES, Field.Index.ANALYZED));

					doc.add(new Field("modified", DateTools.timeToString(file
							.lastModified(), DateTools.Resolution.MINUTE),
							Field.Store.YES, Field.Index.NOT_ANALYZED));

					PDFParser pdParser = new PDFParser(
							new FileInputStream(file));
					pdParser.parse();
					PDDocument pdf = null;
					pdf = pdParser.getPDDocument();
					if (pdf.isEncrypted()) {
						DocumentEncryption decryptor = new DocumentEncryption(
								pdf);
						// Just try using the default password and move on
						decryptor.decryptDocument("");
					}

					// collect text
					PDFTextStripper stripper = new PDFTextStripper();
					String text = stripper.getText(pdf);

					// TODO paseaza textul
					// System.out.println(text);
					// collect title
					PDDocumentInformation info = pdf.getDocumentInformation();
					doc.add(new Field("title", info.getTitle(),
							Field.Store.YES, Field.Index.ANALYZED));

					// System.out.println(String.valueOf(pdf.getPageCount()));
					if (info.getAuthor() != null)
						doc.add(new Field("author", info.getAuthor(),
								Field.Store.YES, Field.Index.ANALYZED));
					if (info.getSubject() != null)
						doc.add(new Field("subject", info.getSubject(),
								Field.Store.YES, Field.Index.ANALYZED));
					if (info.getKeywords() != null)
						doc.add(new Field("keywords", info.getKeywords(),
								Field.Store.YES, Field.Index.ANALYZED));
					if (info.getCreator() != null)
						doc.add(new Field("creator", info.getCreator(),
								Field.Store.YES, Field.Index.ANALYZED));
					if (info.getProducer() != null)
						doc.add(new Field("produces", info.getProducer(),
								Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("contents", text, Field.Store.NO,
							Field.Index.ANALYZED));

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
}
