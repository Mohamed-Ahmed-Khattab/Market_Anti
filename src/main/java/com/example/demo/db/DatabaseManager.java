package com.example.demo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class DatabaseManager {
    private static String dbUrl;
    private static String dbUserName;
    private static String dbPassword;

    private static volatile DatabaseManager instance = null;
    private static Connection connection = null;

    // THE THREAD POOL
    private final ExecutorService threadPool;

    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());

    private DatabaseManager() {
        loadDatabaseProperties();
        registerDriver();

        /*
         * OPTION A: I/O INTENSIVE (Recommended for DB)
         * Creates a pool that can grow. Good because DB threads spend time waiting.
         */
        this.threadPool = Executors.newCachedThreadPool();

        /*
         * OPTION B: CPU INTENSIVE (Use if doing heavy calculations)
         * int cores = Runtime.getRuntime().availableProcessors();
         * this.threadPool = Executors.newFixedThreadPool(cores + 1);
         */
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    // This method allows you to run database tasks in the background
    public void executeTask(Runnable task) {
        threadPool.execute(task);
    }

    public Connection getConnection() throws SQLException {
        if (dbUrl == null) {
            loadDatabaseProperties();
        }
        // Return a fresh connection Every Time to avoid session/telemetry corruption in
        // threads.
        // DAOs are already using try-with-resources to close them.
        return DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
    }

    private static void registerDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.severe("Driver missing: " + e.getMessage());
        }
    }

    private void loadDatabaseProperties() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                logger.severe("config.properties not found in classpath");
                return;
            }
            Properties props = new Properties();
            props.load(in);
            dbUrl = props.getProperty("db.url");
            dbUserName = props.getProperty("db.username");
            dbPassword = props.getProperty("db.password");
        } catch (IOException e) {
            logger.severe("Config error: " + e.getMessage());
        }
    }

    public void shutdown() {
        threadPool.shutdown();
    }
}
