package com.gym.util;
import java.sql.*;

public class Db {
    public static Connection getConnection() {
        String url  = "jdbc:postgresql://localhost:5432/gymdb";
        String user = "postgres";
        String pass = "password";
        try { return DriverManager.getConnection(url, user, pass); }
        catch (SQLException e) { throw new RuntimeException("DB connect failed", e); }
    }
}