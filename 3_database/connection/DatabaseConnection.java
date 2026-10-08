package com.studyplanner.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manages JDBC Database Connectivity with MySQL as primary DBMS
 * and automatic fallback to embedded SQLite for offline/standalone execution.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private final Properties properties = new Properties();

    private boolean usingMySQL = false;
    private String currentDatabaseType = "Unknown";
    private String connectionStatusMessage = "Not connected";

    private DatabaseConnection() {
        loadProperties();
        initConnection();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (Exception e) {
            System.err.println("Could not load db.properties, using defaults: " + e.getMessage());
        }
    }

    private void initConnection() {
        // Try MySQL first
        String host = properties.getProperty("db.host", "localhost");
        String port = properties.getProperty("db.port", "3306");
        String dbName = properties.getProperty("db.name", "study_planner");
        String user = properties.getProperty("db.username", "root");
        String pass = properties.getProperty("db.password", "root");

        String mysqlUrl = String.format(
                "jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, dbName
        );

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(mysqlUrl, user, pass)) {
                this.usingMySQL = true;
                this.currentDatabaseType = "MySQL (" + host + ":" + port + "/" + dbName + ")";
                this.connectionStatusMessage = "Connected to MySQL Server successfully";
                System.out.println("[DatabaseConnection] Successfully connected to MySQL at " + host + ":" + port);
                executeSchemaInit(conn, true);
                return;
            }
        } catch (Exception mysqlEx) {
            System.out.println("[DatabaseConnection] MySQL connection failed (" + mysqlEx.getMessage() + "). Initializing local SQLite fallback...");
        }

        // Fallback to SQLite
        try {
            Class.forName("org.sqlite.JDBC");
            String sqliteUrl = properties.getProperty("sqlite.url", "jdbc:sqlite:study_planner.db");
            try (Connection conn = DriverManager.getConnection(sqliteUrl)) {
                this.usingMySQL = false;
                this.currentDatabaseType = "SQLite (Local Embedded)";
                this.connectionStatusMessage = "Connected to local SQLite database (MySQL unavailable)";
                System.out.println("[DatabaseConnection] Successfully initialized local SQLite storage at " + sqliteUrl);
                executeSchemaInit(conn, false);
            }
        } catch (Exception sqliteEx) {
            this.connectionStatusMessage = "Fatal: Unable to initialize database: " + sqliteEx.getMessage();
            System.err.println("[DatabaseConnection] SQLite initialization failed: " + sqliteEx.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (usingMySQL) {
            String host = properties.getProperty("db.host", "localhost");
            String port = properties.getProperty("db.port", "3306");
            String dbName = properties.getProperty("db.name", "study_planner");
            String user = properties.getProperty("db.username", "root");
            String pass = properties.getProperty("db.password", "root");
            String mysqlUrl = String.format(
                    "jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    host, port, dbName
            );
            return DriverManager.getConnection(mysqlUrl, user, pass);
        } else {
            String sqliteUrl = properties.getProperty("sqlite.url", "jdbc:sqlite:study_planner.db");
            return DriverManager.getConnection(sqliteUrl);
        }
    }

    /**
     * Executes the SQL initialization schema on the newly created connection.
     */
    private void executeSchemaInit(Connection conn, boolean isMySQL) {
        try (Statement stmt = conn.createStatement()) {
            if (!isMySQL) {
                // Enable foreign keys in SQLite
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            InputStream in = getClass().getClassLoader().getResourceAsStream("sql/schema.sql");
            if (in == null) return;

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.isEmpty()) continue;
                sb.append(line).append("\n");
            }

            String fullSql = sb.toString();
            if (!isMySQL) {
                // Adapt MySQL syntax for SQLite
                fullSql = fullSql.replace("INT AUTO_INCREMENT PRIMARY KEY", "INTEGER PRIMARY KEY AUTOINCREMENT");
            }

            String[] queries = fullSql.split(";");
            for (String query : queries) {
                String q = query.trim();
                if (!q.isEmpty()) {
                    try {
                        stmt.execute(q);
                    } catch (SQLException ex) {
                        // Ignore already exists error
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DatabaseConnection] Error initializing schema: " + e.getMessage());
        }
    }

    /**
     * Attempts to switch credentials and test MySQL connection.
     */
    public boolean switchAndTestMySQL(String host, String port, String dbName, String user, String pass) {
        String url = String.format(
                "jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, dbName
        );
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                properties.setProperty("db.host", host);
                properties.setProperty("db.port", port);
                properties.setProperty("db.name", dbName);
                properties.setProperty("db.username", user);
                properties.setProperty("db.password", pass);
                this.usingMySQL = true;
                this.currentDatabaseType = "MySQL (" + host + ":" + port + "/" + dbName + ")";
                this.connectionStatusMessage = "Successfully connected to MySQL Server";
                executeSchemaInit(conn, true);
                return true;
            }
        } catch (Exception e) {
            this.connectionStatusMessage = "MySQL Connection failed: " + e.getMessage();
            return false;
        }
    }

    public boolean isUsingMySQL() {
        return usingMySQL;
    }

    public String getCurrentDatabaseType() {
        return currentDatabaseType;
    }

    public String getConnectionStatusMessage() {
        return connectionStatusMessage;
    }
}
