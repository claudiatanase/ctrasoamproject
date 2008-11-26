package xerxes.soaproject.tests.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import org.pdfbox.encryption.DocumentEncryption;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;

public class TestPDFParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
			PDFParser pdParser=new PDFParser(new FileInputStream(new File("test.pdf")));	
			pdParser.parse();
			PDDocument pdf = null;
			pdf=pdParser.getPDDocument();
			 if (pdf.isEncrypted()) {
			        DocumentEncryption decryptor = new DocumentEncryption(pdf);
			        //Just try using the default password and move on
			        decryptor.decryptDocument("");
			      }

		      // collect text
		      PDFTextStripper stripper = new PDFTextStripper();
		      String text = stripper.getText(pdf);
		      System.out.println("\n\n\n\n\n\n\n");
		      System.out.println(text);
		      // collect title
		      PDDocumentInformation info = pdf.getDocumentInformation();
		      System.out.println(info.getTitle());
		      // more useful info, currently not used. please keep them for future use.
		     System.out.println(String.valueOf(pdf.getPageCount()));
		     System.out.println(info.getAuthor());
		     System.out.println(info.getSubject());
		     System.out.println(info.getKeywords());
		     System.out.println(info.getCreator());
		     System.out.println(info.getProducer());
		      
	}

}
