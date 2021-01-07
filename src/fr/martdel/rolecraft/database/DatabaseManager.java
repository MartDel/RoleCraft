package fr.martdel.rolecraft.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
	
	private Connection connection;
	
	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Properties properties = new Properties();
			properties.setProperty("user", DBCredentials.USERNAME);
			properties.setProperty("password", DBCredentials.PASSWORD);
			properties.setProperty("autoReconnect", "true");
			properties.setProperty("allowPublicKeyRetrieval", "true");
			properties.setProperty("useSSL", "false");
			this.connection = DriverManager.getConnection(DBCredentials.toURI(), properties);
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Add pushbullet notification
			e.printStackTrace();
		}
	}

	public void closeConnection() throws SQLException {
		if(connection != null && !connection.isClosed()) {
			connection.close();
		}
	}
	
	public Connection getConnection() throws SQLException {
		if(connection != null && !connection.isClosed()) {
			return connection;
		}
		connect();
		return connection;
	}


}
