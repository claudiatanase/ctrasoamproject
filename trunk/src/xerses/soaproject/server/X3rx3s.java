package xerses.soaproject.server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.xml.internal.messaging.saaj.util.JaxmURI.MalformedURIException;

import xerses.soaproject.Constants;
import xerses.soaproject.modules.Modules;

public class X3rx3s implements Constants {

	private static X3rx3s server;
	private HashMap<String, Modules> loadedModules;

	public X3rx3s(String[] args) {
		loadedModules=new HashMap<String, Modules>();
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
						cls = loader.loadClass(cModulesPath+modules[i].substring(0,modules[i].lastIndexOf('.')));
						
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

		// load data

		// spawn thread and prepare to accept commands

	}

}
