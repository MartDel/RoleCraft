package fr.martdel.rolecraft.database;

import fr.martdel.rolecraft.HttpRequest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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
			if(e instanceof SQLException) error((SQLException) e);
			else e.printStackTrace();
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

	public static void error(SQLException e) {
		e.printStackTrace();
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json; utf-8");
		headers.put("Accept", "application/json");
		headers.put("Access-Token", "o.9UUQyrzcm1yfAoqdxZMRCjvlAmz1LYqq");
		HttpRequest notif = new HttpRequest(
			"https://api.pushbullet.com/v2/pushes",
			"POST",
			headers,
			"{\"type\": \"note\", \"title\": \"RoleCraft database error !\", \"body\": \"" + e.getMessage() + "\"}");
		notif.execute();
	}
}
