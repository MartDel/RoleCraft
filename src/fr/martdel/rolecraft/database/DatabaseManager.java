package fr.martdel.rolecraft.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
	
	private Connection connection;
	
	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection(
					DBCredentials.toURI(),
					DBCredentials.USERNAME,
					DBCredentials.PASSWORD);
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
