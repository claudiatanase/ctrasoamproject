/**
 * Class containing methods for reading/writing data in the DB 
 */
package xerxes.soaproject.db;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Vector;

public class DBManager {
	String url_db;

	String user;

	String pass;

	Connection con = null;

	/**
	 * Initialization function. Sets database information.
	 */
	public void init() {
		try {

			url_db = "jdbc:mysql://localhost:3306/soaproject";
			user = "root";
			pass = "rootpass";

		} catch (Exception e) {
			System.err.println("Problema la citirea datelor din fisier.");
			System.exit(0);
		}

	}

	/**
	 * Constructor for the DBManager. Creates a connection to the DB and tests
	 * if all tables exist. If not necessary tables are created.
	 */
	public DBManager() {
		init();

		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(url_db, user, pass);
			@SuppressWarnings("unused")
			Statement stmt = con.createStatement();
			// creare DB
			stmt.executeUpdate("CREATE TABLE if not exists `MFileList` ("
					+ " `MFileId` int(11) NOT NULL AUTO_INCREMENT,"
					+ " `MFileName` varchar(255) NOT NULL,"
					+ " `MFilePath` varchar(255) NOT NULL,"
					+ " `MFileType` int(11) NOT NULL,"
					+ " `MFileFlag`  int(11) NOT NULL,"
					+ " `Tainted` int(11) NOT NULL,"
					+ " `Time` datetime NOT NULL,"
					+ " PRIMARY KEY (`MFileId`,`MFilePath`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			/*
			 * stmt.executeUpdate("CREATE TABLE if not exists `tags` (" + "
			 * `TagId` int(11) NOT NULL AUTO_INCREMENT," + " `FileName`
			 * varchar(255) NOT NULL," + " `FilePath` varchar(255) NOT NULL," + "
			 * `Tag` varchar(255) NOT NULL," + " `Time` datetime NOT NULL," + "
			 * PRIMARY KEY (`TagId`,`FilePath`)" + ") ENGINE=InnoDB DEFAULT
			 * CHARSET=latin1;"); stmt.executeUpdate("CREATE TABLE if not exists
			 * `MFileList` (" + " `MFileId` int(11) NOT NULL AUTO_INCREMENT," + "
			 * `MFileName` varchar(255) NOT NULL," + " `MFilePath` varchar(255)
			 * NOT NULL," + " `MFileType` int(11) NOT NULL," + " `MFileFlag`
			 * int(11) NOT NULL," + " `Tainted` int(11) NOT NULL," + " `Time`
			 * datetime NOT NULL," + " PRIMARY KEY (`MFileId`,`MFilePath`)" + ")
			 * ENGINE=InnoDB DEFAULT CHARSET=latin1;"); stmt
			 * .executeUpdate("CREATE TABLE `answerid` (" + " `AId` int(11) NOT
			 * NULL AUTO_INCREMENT," + " `SearchID` int(11) NOT NULL," + "
			 * `MFId` int(11) NOT NULL, " + " PRIMARY KEY (`AId`,`SearchID`)," + "
			 * KEY `MFConstraint` (`MFId`), CONSTRAINT `MFConstraint` " + "
			 * FOREIGN KEY (`MFId`) REFERENCES `mfilelist` (`MFileId`) " + " ON
			 * DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			 * stmt.executeUpdate("CREATE TABLE `searchcache` (" + "`SCId`
			 * int(11) NOT NULL AUTO_INCREMENT," + "`SearchId` int(11) NOT
			 * NULL," + "`SearchTerm` varchar(255) NOT NULL," + "PRIMARY KEY
			 * (`SCId`,`SearchId`)" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;" +
			 * ")");
			 */
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a monitored file, specified by fileName,filePath,fileType,fileFlag
	 * to the database. Initally the file is not tainted ( the latest version of
	 * the file has been parsed )
	 * 
	 * @param fileName
	 * @param filePath
	 * @param fileType
	 * @param fileFlag
	 * @param tainted
	 * @return
	 */
	public int addMFile(String fileName, String filePath, int fileType,
			int fileFlag, boolean tainted) {
		try {

			PreparedStatement stmtInsert = con
					.prepareStatement("insert into mfilelist values(0,?,?,?,0,?,?)");
			PreparedStatement stmtUpdate = con
					.prepareStatement("update mfilelist set mfilelist.time=?,mfilelist.tainted=? where MFilePath=?");

			Date d = new Date(Calendar.getInstance().getTimeInMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			filePath = filePath.replace(File.separatorChar, '/');

			if (!fileExists(filePath)) {
				stmtInsert.setString(1, fileName);
				stmtInsert.setString(2, filePath);
				stmtInsert.setInt(3, fileType);
				stmtInsert.setString(4, sdf.format(d).toString());
				stmtInsert.setInt(3, (tainted ? 1 : 0));
				stmtInsert.execute();

			} else {

				stmtUpdate.setString(1, d.toString());
				stmtUpdate.setInt(2, (tainted ? 1 : 0));
				stmtUpdate.setString(3, filePath);
				stmtUpdate.execute();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		return getFileIdByPath(filePath);
	}

	public int getFileIdByPath(String path) {
		int id = -1;
		try {
			PreparedStatement stmt = con
					.prepareStatement("select (*) from mfilelist where MFilePath=?");
			path = path.replace(File.separatorChar, '/');
			stmt.setString(1, path);
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				id = (rs.getInt("MFileId"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Removes a file from the database.
	 * 
	 * @param path
	 */
	public void removeFile(String path) {
		try {
			PreparedStatement stmt = con
					.prepareStatement("delete from mfilelist where MFilePath = ? ");
			stmt.setString(1, path);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests if file exists
	 * 
	 * @param path
	 * @return
	 */
	public boolean fileExists(String path) {
		boolean found = false;
		try {
			PreparedStatement stmt = con
					.prepareStatement("select count(*) as count from mfilelist where MFilePath=?");
			path = path.replace(File.separatorChar, '/');
			stmt.setString(1, path);
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String numar = (rs.getString("count"));
				if (Integer.parseInt(numar) != 0)
					found = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return found;
	}

	public String[] getTaintedFiles(int number) {
		Vector<String> taintedFiles = new Vector<String>();
		String path = null;
		try {
			PreparedStatement stmt = con
					.prepareStatement("select * from mfilelist where tainted =1 order by MFileId Limit ?");
			stmt.setInt(1, number);
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				path = (rs.getString("MFilePath"));
				taintedFiles.add(path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (String[]) (taintedFiles.toArray());
	}

	public void markFile(boolean tainted, String path) {
		try {
			PreparedStatement stmt = con
					.prepareStatement("update * from mfilelist where MFilePath =? set tainted = ?,mfilelist.time=?");
			Date d = new Date(Calendar.getInstance().getTimeInMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			stmt.setString(1, path);
			stmt.setInt(2, (tainted ? 1 : 0));
			stmt.setString(3, sdf.format(d).toString());
			stmt.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void markFiles(boolean tainted, String[] pathList) {
		try {
			PreparedStatement stmt = con
					.prepareStatement("update * from mfilelist where MFilePath =? set tainted = ?,mfilelist.time=?");
			Date d = new Date(Calendar.getInstance().getTimeInMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			for (String path : pathList) {
				stmt.setString(1, path);
				stmt.setInt(2, (tainted ? 1 : 0));
				stmt.setString(3, sdf.format(d).toString());
				stmt.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
