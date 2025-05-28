package poly.cafe.util;

import java.sql.*;

public class XJdbc {
    public static Connection openConnection() {
        try {
            var driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            var dburl = "jdbc:sqlserver://localhost;database=PolyCafe;encrypt=true;trustServerCertificate=true;";
            var username = "sa";
            var password = "123"; // Đổi thành pass thật của bạn

            Class.forName(driver);
            return DriverManager.getConnection(dburl, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeUpdate(String sql, Object... args) {
        try (
            Connection con = openConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet executeQuery(String sql, Object... args) {
        try {
            Connection con = openConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            return stmt.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
