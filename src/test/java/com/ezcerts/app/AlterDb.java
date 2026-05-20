package com.ezcerts.app;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
public class AlterDb {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:33065/ezcerts?useSSL=false&serverTimezone=UTC", "root", "");
        conn.createStatement().execute("ALTER TABLE certificados MODIFY COLUMN tipo VARCHAR(30) NOT NULL;");
        System.out.println("Columna tipo modificada a VARCHAR(30).");
        conn.close();
    }
}