/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sergioprova;

import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author sqlitetutorial.net
 */
public class DBHandler {

    /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string

 
        String url = "jdbc:sqlite:" + Paths.get("").toAbsolutePath().toString() + "/sql/db.db";        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public String getResponseFromDB(String accountCode, String targetDevice, String pluginVersion) {
        String sql = "SELECT pingTime, hostA, hostB FROM serviceConfig"
                + " WHERE accountCode = ? AND targetDevice = ? AND "
                + "pluginVersion = ? ";

        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value            
            pstmt.setString(1, accountCode);
            pstmt.setString(2, targetDevice);
            pstmt.setString(3, pluginVersion);

            //
            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            String dbResponse = null;
            while (rs.next()) {
                dbResponse = (rs.getInt("pingTime") + ","
                        + rs.getInt("hostA") + ","
                        + rs.getInt("hostB"));
            }

            return dbResponse;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
