package xerxes.soaproject.server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

import org.apache.log4j.Logger;

import xerxes.soaproject.bridge.ControlDetail2;
import xerxes.soaproject.db.DBManager;
import xerxes.soaproject.interfaces.Constants;
import xerxes.soaproject.interfaces.Modules;
import xerxes.soaproject.modules.LuceneDeleteModule;
import xerxes.soaproject.modules.LuceneMP3Module;
import xerxes.soaproject.modules.LuceneOOModule;
import xerxes.soaproject.modules.LucenePDFModule;
import xerxes.soaproject.modules.LuceneTagsModule;
import xerxes.soaproject.modules.LuceneTextModule;
import xerxes.soaproject.tests.modules.SearchFiles;

public class X3rx3sWithLucene implements Constants {

	private static X3rx3sWithLucene server;

	@SuppressWarnings("unused")
	private static DBManager dbM;

	static Logger log4j = Logger.getLogger("org.soarproject.xerxes");

	private HashMap<String, Modules> loadedModules;

	public X3rx3sWithLucene() {
		this(new String[] {});
	}

	public X3rx3sWithLucene(String[] args) {
		loadedModules = new HashMap<String, Modules>();
		Class cls;
		File directory = new File(cNodDirectory);
		if (directory.isDirectory() == true) {
			String[] modules = directory.list();

			try {

				// Convert File to a URL
				URL url = directory.toURI().toURL();
				URL[] urls = new URL[] { url };

				// Create a new class loader with the directory

				ClassLoader loader = new URLClassLoader(urls);
				// Load in the class; Class.childclass should be located in
				// the directory file:/c:/class/user/information
				Modules loadedModule = null;
				for (int i = 0; i < modules.length; i++)
					if (modules[i].endsWith(".class")
							&& modules[i].indexOf('$') < 0) {
						log4j.error("loading " + modules[i]);
						cls = loader.loadClass(cModulesPath
								+ modules[i].substring(0, modules[i]
										.lastIndexOf('.')));

						loadedModule = (Modules) (cls.newInstance());
						this.loadedModules.put(loadedModule.getExtension(),
								loadedModule);
						log4j.error("Added " + loadedModule.getExtension());

					}

			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				log4j
						.error("Nu am putut incarca modulele dinamic.Le incarcam static.");
				LuceneDeleteModule ldm = new LuceneDeleteModule();
				LuceneMP3Module lmp3 = new LuceneMP3Module();
				LuceneOOModule loo = new LuceneOOModule();
				LucenePDFModule lpdf = new LucenePDFModule();
				LuceneTagsModule ltag = new LuceneTagsModule();
				LuceneTextModule ltext = new LuceneTextModule();

				this.loadedModules.put(ldm.getExtension(), ldm);
				this.loadedModules.put(lmp3.getExtension(), lmp3);
				this.loadedModules.put(loo.getExtension(), loo);
				this.loadedModules.put(lpdf.getExtension(), lpdf);
				this.loadedModules.put(ltag.getExtension(), ltag);
				this.loadedModules.put(ltext.getExtension(), ltext);
			}

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		server = new X3rx3sWithLucene(args);

		File f = new File("tests");

		for (File file : f.listFiles())
			if (file.getName().charAt(0) != '.') {
				server.addMonitoredFile(file.getName(), file.getAbsolutePath(),
						"");
			}

		// server.removeFile("/home/csrazvan/workspace/SOAProj/tests/test.mp3");

		// server.editTagsForFile("test2.mp3",
		// "/home/csrazvan/workspace/SOAProj/tests/test2.mp3",
		// "mama razvan textFile shit");

	}

	// entry points for application

	// entry point for C module
	// TODO remind CLa to append / at the end of the fileName to mark dir files.

	public void addMonitoredFile(String fileName, String filePath, String tags) {
		File f;
		Modules module = null;
		f = new File(filePath);
		if (f.isDirectory())
			for (File fx : f.listFiles()) {
				addMonitoredFile(fx.getName(),fx.getAbsolutePath(), tags);
			}
		else 
		if (fileName.charAt(0) != '.') {
			if (fileName.substring(fileName.lastIndexOf('.') + 1).equals("odt")
					|| fileName.substring(fileName.lastIndexOf('.') + 1)
							.equals("odp")
					|| fileName.substring(fileName.lastIndexOf('.') + 1)
							.equals("ods"))
				module = server.loadedModules.get("oo");
			else
				module = server.loadedModules.get(fileName.substring(fileName
						.lastIndexOf('.') + 1));
			System.out.println("Pentru " + fileName + " module =" + module);

			if (module == null)
				module = server.loadedModules.get("txt");

			module.parseFile(filePath, tags.split(" "));

		}
	}

	public boolean removeFile(String filePath) {
		boolean result = false;
		// ((LuceneDeleteModule) (server.loadedModules.get("deleteFile")))
		// .deleteFile(filePath);
		LuceneDeleteModule ldm = new LuceneDeleteModule();
		ldm.deleteFile(filePath);
		return result;
	}

	public void editTagsForFile(String fileName, String filePath, String tags) {

		@SuppressWarnings("unused")
		boolean result = ((LuceneDeleteModule) (server.loadedModules
				.get("deleteFile"))).deleteFile(filePath);
		addMonitoredFile(fileName, filePath, tags);
	}

	public void testCLI() {
		System.out.println("CLI WORKING");
	}

	public static String msgReceivedString = "id:%d,cmd=%d,path=%s,tag=%s";

	public static int testCLI2(ControlDetail2 ctrlDetail) {
		log4j.info(String.format(msgReceivedString, ctrlDetail.ID,
				ctrlDetail.cmd, ctrlDetail.path, ctrlDetail.tag));
		File f;
		if (ctrlDetail.cmd == ADD || ctrlDetail.cmd==TADD) {
			f = new File(ctrlDetail.path);
			if (server == null)
				server = new X3rx3sWithLucene();

			server.addMonitoredFile(f.getName(), f.getAbsolutePath(),
					ctrlDetail.tag);

		} else if (ctrlDetail.cmd ==SEARCH){
			try {
				SearchFiles.simpleSearch(ctrlDetail.path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (ctrlDetail.cmd == REM){
			server.removeFile(ctrlDetail.path);
		}
		return 1;
	}

}
