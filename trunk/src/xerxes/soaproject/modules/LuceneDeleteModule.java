package xerxes.soaproject.modules;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import xerxes.soaproject.interfaces.Modules;

public class LuceneDeleteModule implements Modules {

	static final File INDEX_DIR = new File("index");

	static Logger log4j = Logger.getLogger("org.soarproject.xerxes");

	public boolean initializeModule() {
		if (INDEX_DIR.exists()) {
			log4j.debug("Cannot save index to '" + INDEX_DIR
					+ "' directory, please delete it first");
			return false;
		}
		return true;
	}

	public String getExtension() {

		return "deleteFile";
	}

	public boolean deleteFile(String pathName) {
		boolean result = false;
		try {
			log4j.debug("Index Reader open");
			IndexReader ir = IndexReader.open(INDEX_DIR);
			// TODO vezi ca nu ii pasez complet calea de aia nu sterge
			// SMF 
			ir.deleteDocuments(new Term("path", pathName));
			log4j.debug("Document "+ pathName + " marked for deletion");
			ir.close();
			log4j.debug("Index Reader closed");
			

		}

		catch (CorruptIndexException e) {
			log4j.debug(" caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
			return false;
		} catch (IOException e) {
			log4j.debug(" caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
			return false;
		}

		return result;

	}

	public boolean parseFile(String pathName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseFile(String pathName, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

}
