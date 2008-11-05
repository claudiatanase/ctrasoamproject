package xerses.soaproject.server;

import java.io.File;
import java.util.ArrayList;

import xerses.soaproject.Constants;

public class X3rx3s implements Constants {

	private static X3rx3s server;
	public X3rx3s(String[] args){
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		server = new X3rx3s(args);
		// get list of existing modules in Modules dir
		File directory= new File(cNodDirectory);
		if (directory.isDirectory()==true){
			String[] modules=directory.list();
			for (int i=0;i<modules.length;i++)
				System.out.println(modules[i]);
		}
		// create and test connection to DB
		
		// load data 
		
		// spawn thread and prepare to accept commands
		
		
	}

}
