package xerxes.db;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

public class DBManager {
	String url_db;

	String user;

	String pass;

	Connection con = null;

	// initializare
	public void init() {
		try {

			url_db = "jdbc:mysql://localhost:3306/xerxes";
			user = "root";
			pass = "rootpass";

		} catch (Exception e) {
			System.err.println("Problema la citirea datelor din fisier.");
			System.exit(0);
		}

	}

	public DBManager() {
		init();

		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(url_db, user, pass);
			Statement stmt = con.createStatement();
			// creare DB

			stmt.executeUpdate("CREATE TABLE if not exists `changemasks` ("
					+ "  `CMId` int(11) NOT NULL AUTO_INCREMENT,"
					+ "  `CMName` varchar(255) NOT NULL,"
					+ "  `CMPath` varchar(255) NOT NULL,"
					+ "  `CMImage1Id` int(11) NOT NULL,"
					+ "  `CMImage2Id` int(11) NOT NULL," + "  `CMResult` blob,"
					+ "  `Time` datetime NOT NULL," + "  PRIMARY KEY  (`CMId`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			stmt.executeUpdate("CREATE TABLE if not exists `images` ("
					+ "  `ImageId` int(11) NOT NULL AUTO_INCREMENT,"
					+ "  `ImageName` varchar(255) NOT NULL,"
					+ "  `ImagePath` varchar(255) NOT NULL,"
					+ "  `Processed` int(11) NOT NULL," + "  `Image` blob,"
					+ "  `Time` datetime NOT NULL,"
					+ "  PRIMARY KEY  (`ImageId`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			stmt
					.executeUpdate("CREATE TABLE if not exists`resultmembers` ("
							+ "  `ResultId` int(11) DEFAULT NULL,"
							+ "  `CMId` int(11) DEFAULT NULL,"
							+ "  KEY `ChangeMask_constraint` (`CMId`),"
							+ "  CONSTRAINT `ChangeMask_constraint` FOREIGN KEY (`CMId`) "
							+ "REFERENCES `changemasks` (`CMId`) ON DELETE NO ACTION ON UPDATE NO ACTION"
							+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			stmt.executeUpdate("CREATE TABLE if not exists`results` ("
					+ "  `ResultId` int(11) NOT NULL AUTO_INCREMENT,"
					+ "  `ResultPath` varchar(255) NOT NULL,"
					+ "  `ResultyType` varchar(255) NOT NULL,"
					+ "  PRIMARY KEY  (`ResultId`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
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

	// adauga/updateaza un item
	public int addImage(String imageName, String imagePath, boolean data) {
		try {

			Statement stmt = con.createStatement();
			Date d = new Date(Calendar.getInstance().getTimeInMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			imagePath = imagePath.replace(File.separatorChar, '/');

			if (!imageExists(imagePath))
				stmt.executeUpdate("insert  into images values (0,\'"
						+ imageName + "\',\'" + imagePath + "\',\'" + 0
						+ "\',NULL,\'" + sdf.format(d).toString() + "\');");
			else {
				stmt.executeUpdate("update images set images.time=\'"
						+ (d.toString()) + "\' where " + "ImagePath=\'"
						+ imagePath + "\'");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean imageExists(String path) {
		boolean found = false;
		try {
			Statement stmt = con.createStatement();
			stmt
					.executeQuery("select count(*) as count from images where imagePath=\'"
							+ path + "\';");
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

}
