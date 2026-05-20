 
package com.fashionstore.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
	private static DBConnection instance;
	private final Properties properties = new Properties();

	private DBConnection() {
		try (InputStream input = DBConnection.class.getClassLoader()
				.getResourceAsStream("db.properties")) {
			if (input == null) {
				System.err.println("db.properties not found.");
				return;
			}
			properties.load(input);
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (Exception ex) {
			System.err.println("Failed to load DB config.");
		}
	}

	public static DBConnection getInstance() {
		if (instance == null) {
			instance = new DBConnection();
		}
		return instance;
	}

	public Connection getConnection() throws SQLException {
		String url = properties.getProperty("db.url");
		String username = properties.getProperty("db.username");
		String password = properties.getProperty("db.password");
		return DriverManager.getConnection(url, username, password);
	}
}

