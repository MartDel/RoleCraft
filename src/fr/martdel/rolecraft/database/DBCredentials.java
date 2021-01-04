package fr.martdel.rolecraft.database;

public abstract class DBCredentials {
	
	public static final String HOST = "localhost";
	public static final String USERNAME = "debian-sys-maint";
	public static final String PASSWORD = "glTsY8j6p0olqFFD";
	public static final String DBNAME = "rolecraft";
	public static final int PORT = 3306;
	
	public static String toURI() {
		return "jdbc:mysql://"
			+ HOST + ":"
			+ PORT + "/"
			+ DBNAME;
	}

}
