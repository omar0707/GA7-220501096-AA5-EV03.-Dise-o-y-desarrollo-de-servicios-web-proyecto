package com.canstyling.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestor Singleton de Conexión a Base de Datos MySQL con JDBC y PreparedStatement.
 */
public class DatabaseConnection {

    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            String host = EnvConfig.get("DB_HOST", "localhost");
            String port = EnvConfig.get("DB_PORT", "3306");
            String dbName = EnvConfig.get("DB_NAME", "peluqueria_canina_db");
            String user = EnvConfig.get("DB_USER", "root");
            String password = EnvConfig.get("DB_PASSWORD", "");
            String timezone = EnvConfig.get("DB_SERVER_TIMEZONE", "America/Bogota");

            String url = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=%s&characterEncoding=UTF-8",
                host, port, dbName, timezone
            );

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ignored) {}

            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a MySQL: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {}
        }
    }
}
