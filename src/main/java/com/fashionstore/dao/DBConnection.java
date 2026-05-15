 
package com.fashionstore.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
	private static DBConnection instance;
	private final Properties properties = new Properties();
	private boolean mockMode;

	private DBConnection() {
		try (InputStream input = DBConnection.class.getClassLoader()
				.getResourceAsStream("db.properties")) {
			if (input == null) {
				mockMode = true;
				System.err.println("db.properties not found. Running in mock mode.");
				return;
			}
			properties.load(input);
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (Exception ex) {
			mockMode = true;
			System.err.println("Failed to load DB config. Running in mock mode.");
		}
	}

	public static DBConnection getInstance() {
		if (instance == null) {
			instance = new DBConnection();
		}
		return instance;
	}

	public Connection getConnection() throws SQLException {
		if (mockMode) {
			throw new SQLException("Mock mode: database connection not available.");
		}
		String url = properties.getProperty("db.url");
		String username = properties.getProperty("db.username");
		String password = properties.getProperty("db.password");
		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException ex) {
			mockMode = true;
			System.err.println("Database connection failed. Running in mock mode.");
			throw ex;
		}
	}

	public boolean isMockMode() {
		return mockMode;
	}
}

