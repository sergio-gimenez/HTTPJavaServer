/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sergio.npawprueba;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author sqlitetutorial.net
 */
public class SelectApp {

    /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:/home/sergio/NetBeansProjects/NPAWprueba/NPAWprueba/src/sql/db.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void getResponseFromDB() {
        String sql = "SELECT pingTime, hostA, hostB FROM serviceConfig"
                + " WHERE accountCode = ? AND targetDevice = ? AND "
                + "pluginVersion = ? ";

        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value            
            pstmt.setString(1, "clienteA");
            pstmt.setString(2, "XBox");
            pstmt.setString(3, "3.3.1");

            //
            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("pingTime") + "\t"
                        + rs.getInt("hostA") + "\t"
                        + rs.getInt("hostB"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SelectApp app = new SelectApp();
        app.getResponseFromDB();
    }
}
