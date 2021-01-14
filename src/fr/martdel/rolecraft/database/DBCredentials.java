package fr.martdel.rolecraft.database;

import fr.martdel.rolecraft.Credentials;

public abstract class DBCredentials {
	
	public static final String HOST = "localhost";
	public static final String USERNAME = "rolecraft";
	public static final String PASSWORD = Credentials.DB_PASSWORD;
	public static final String DBNAME = "rolecraft";
	public static final int PORT = 3306;
	
	public static String toURI() {
		return "jdbc:mysql://"
			+ HOST + ":"
			+ PORT + "/"
			+ DBNAME;
	}

}
