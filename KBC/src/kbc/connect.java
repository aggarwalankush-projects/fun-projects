/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kbc;

/**
 *
 * @author anku
 */
import java.sql.*;

public class connect {
   static Statement state()
    {
try{
    Class.forName("com.mysql.jdbc.Driver");
    Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/kbc", "root","sham");
    Statement st=con.createStatement();
    return st;
}
catch(Exception e)
{return null;}
    }

}
