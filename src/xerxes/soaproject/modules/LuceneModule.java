package xerxes.soaproject.modules;

import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.demo.FileDocument;
import org.apache.lucene.index.IndexWriter;

public class LuceneModule implements Modules {
	static final File INDEX_DIR = new File("index");
	/**
	 * Log4j logger
	 */
	static Logger log4j = Logger.getLogger("org.soarproject.xerxes");

	public LuceneModule() {
		log4j.debug("WTF?");
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
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean parseFile(String pathName) {
		Date start = new Date();

		try {
			IndexWriter writer = new IndexWriter(INDEX_DIR,
					new StandardAnalyzer(), true,
					IndexWriter.MaxFieldLength.LIMITED);
			log4j.debug("Indexing to directory '" + INDEX_DIR + "'...");
			indexDocs(writer, new File(pathName));
			log4j.debug("Optimizing...");
			writer.optimize();
			writer.close();

			Date end = new Date();
			log4j.debug(end.getTime() - start.getTime()
					+ " total milliseconds");

		} catch (IOException e) {
			log4j.debug(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
			return false;
		}

		return true;
	}

	static void indexDocs(IndexWriter writer, File file) throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				log4j.debug("adding " + file);
				try {
					writer.addDocument(FileDocument.Document(file));
				}
				// at least on windows, some temporary files raise this
				// exception with an "access denied" message
				// checking if the file can be read doesn't help
				catch (FileNotFoundException fnfe) {
					;
				}
			}
		}
	}
}
