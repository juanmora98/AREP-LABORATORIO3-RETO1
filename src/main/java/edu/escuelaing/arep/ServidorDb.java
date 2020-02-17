package edu.escuelaing.arep;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class ServidorDb 
{
    public static void main( String[] args )
    {
        try {  
            Class.forName("org.postgresql.Driver");
            String host = "ec2-184-72-235-159.compute-1.amazonaws.com";
            String database = "d7lq925j3m6b5u";
            String port = "5432";
            String user = "qgxofteshbgryg";
            String passwd = "b25c5f64a60de8fb771e36b36b9d501c811e42c737736efa5ea989031884f06f";
            Connection con = DriverManager.getConnection( "jdbc:postgresql://"+ host + ":" + port + "/" + database, user, passwd);
            Statement stmt=con.createStatement();  
            ResultSet rs=stmt.executeQuery("select * from editores");
            while(rs.next())  
                System.out.println(rs.getInt(1)+"  "+rs.getString(2));  
            con.close();  
        } catch(Exception e) { 
            System.out.println(e);
        }
    }
}
