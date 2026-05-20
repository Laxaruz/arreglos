package com.ezcerts.app;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
public class CheckDb {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:33065/ezcerts?useSSL=false&serverTimezone=UTC", "root", "");
        ResultSet rs = conn.createStatement().executeQuery("SHOW CREATE TABLE certificados");
        while (rs.next()) {
            System.out.println(rs.getString(2));
        }
        conn.close();
    }
}