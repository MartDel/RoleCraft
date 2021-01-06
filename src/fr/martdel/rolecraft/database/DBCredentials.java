package fr.martdel.rolecraft.database;

public abstract class DBCredentials {
	
	public static final String HOST = "localhost";
	public static final String USERNAME = "rolecraft";
	public static final String PASSWORD = "Rxx^4i49!h#By5";
	public static final String DBNAME = "rolecraft";
	public static final int PORT = 3306;
	
	public static String toURI() {
		return "jdbc:mysql://"
			+ HOST + ":"
			+ PORT + "/"
			+ DBNAME;
	}

}
