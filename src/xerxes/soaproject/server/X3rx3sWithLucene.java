package xerxes.soaproject.server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import xerxes.soaproject.db.DBManager;
import xerxes.soaproject.interfaces.Constants;
import xerxes.soaproject.interfaces.Modules;

public class X3rx3sWithLucene implements Constants {

	private static X3rx3sWithLucene server;

	private static DBManager dbM;

	private HashMap<String, Modules> loadedModules;

	public X3rx3sWithLucene(String[] args) {
		loadedModules = new HashMap<String, Modules>();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		server = new X3rx3sWithLucene(args);
		// get list of existing modules in Modules dir and load
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
						System.out.println("loading " + modules[i]);
						cls = loader.loadClass(cModulesPath
								+ modules[i].substring(0, modules[i]
										.lastIndexOf('.')));

						loadedModule = (Modules) (cls.newInstance());
						server.loadedModules.put(loadedModule.getExtension(),
								loadedModule);
						System.out.println("Added "
								+ loadedModule.getExtension());

					}

			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
		// create and test connection to DB
		// dbM = new DBManager();
		// load data
		// ce-ar trebui sa load-uiasca?
		File f = new File("tests");
		Modules module;
		for (String file : f.list())
			if (file.charAt(0) != '.') {
				if (file.substring(file.lastIndexOf('.') + 1).equals("odt")
						|| file.substring(file.lastIndexOf('.') + 1).equals(
								"odp")
						|| file.substring(file.lastIndexOf('.') + 1).equals(
								"ods"))
					module = server.loadedModules.get("oo");
				else
					module = server.loadedModules.get(file.substring(file
							.lastIndexOf('.') + 1));
				System.out.println("Pentru " + file + " module =" + module);

				if (module == null)
					module = server.loadedModules.get("txt");

				module.parseFile("tests/" + file, new String[] {});
			}

		// server.loadedModules.get("txt").parseFile("tests/Razvan.txt",new
		// String[]{"mama","are","mere"});

		// server.loadedModules.get("mp3").parseFile("tests/test.mp3");
		// server.loadedModules.get("oo").parseFile("tests/ootest.odp");

		// spawn thread and prepare to accept commands

	}

	// entry points for application

	// entry point for C module
	// TODO remind CLa to append / at the end of the fileName to mark dir files.

	public void addMonitoredFile(String fileName, String filePath, String tags) {
		int fileType;
		int fileFlag = 0;
		String[] arrayOfTags = tags.split(",");
		HashSet<String> tagsForFile;
		if (fileName.charAt(fileName.length() - 1) == File.pathSeparatorChar)
			fileType = Constants.CDirectory;
		else {
			fileType = Constants.CFIle;

		}

		tagsForFile = dbM.getTagsForFile(filePath);

	}

}
