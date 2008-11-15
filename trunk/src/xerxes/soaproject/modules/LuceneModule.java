package xerxes.soaproject.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.demo.FileDocument;
import org.apache.lucene.index.IndexWriter;

public class LuceneModule implements Modules {
	static final File INDEX_DIR = new File("index");

	public LuceneModule() {

	}

	public boolean initializeModule() {
		if (INDEX_DIR.exists()) {
			System.out.println("Cannot save index to '" + INDEX_DIR
					+ "' directory, please delete it first");
			return false;
		}
		return true;
	}

	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean parseFile(String pathName) {
		Date start = new Date();

		try {
			IndexWriter writer = new IndexWriter(INDEX_DIR,
					new StandardAnalyzer(), true,
					IndexWriter.MaxFieldLength.LIMITED);
			System.out.println("Indexing to directory '" + INDEX_DIR + "'...");
			indexDocs(writer, new File(pathName));
			System.out.println("Optimizing...");
			writer.optimize();
			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime()
					+ " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
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
				System.out.println("adding " + file);
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
