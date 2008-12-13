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

public class X3rx3s implements Constants {

	private static X3rx3s server;
	private static DBManager dbM;
	private HashMap<String, Modules> loadedModules;

	public X3rx3s(String[] args) {
		loadedModules = new HashMap<String, Modules>();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		server = new X3rx3s(args);
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

				for (int i = 0; i < modules.length; i++)
					if (modules[i].endsWith(".class")) {
						cls = loader.loadClass(cModulesPath
								+ modules[i].substring(0, modules[i]
										.lastIndexOf('.')));

						server.loadedModules.put(modules[i], (Modules) (cls
								.newInstance()));

					}

			} catch (IllegalAccessException e) {
			} catch (InstantiationException e) {
			} catch (MalformedURLException e) {
			} catch (ClassNotFoundException e) {
			}

		}
		// create and test connection to DB
		dbM = new DBManager();
		// load data
		// ce-ar trebui sa load-uiasca?

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

		int fileId = dbM
				.addMFile(fileName, filePath, fileType, fileFlag, false);

		tagsForFile = dbM.getTagsForFile(filePath);
		for (int i = 0; i < arrayOfTags.length; i++) {
			dbM.addTag(arrayOfTags[i]);
			tagsForFile.add(arrayOfTags[i]);
		}
		arrayOfTags = (String[]) tagsForFile.toArray();
		dbM.markFile(fileId, arrayOfTags);

	}
	
	public String[] searchByTags(String tags){
		String[] tagsList=tags.split(",");
		Vector<String> results;
		results=dbM.getFilesWithTag(tagsList[0]);
		int i=1;
		while(i<tagsList.length)
			results.retainAll(dbM.getFilesWithTag(tagsList[i++]));
		return (String[])results.toArray();
	}
}
