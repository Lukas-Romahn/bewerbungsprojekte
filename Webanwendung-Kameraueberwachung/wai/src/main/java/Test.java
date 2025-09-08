
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JNDIFactory;
import java.sql.*;



@WebServlet("/test")
public class Test extends HttpServlet{   

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection con;
        JNDIFactory factory= JNDIFactory.getInstance();
        try {
        	con=factory.getConnection("jdbc/waiDB");
        	Statement stmt=con.createStatement();
        	ResultSet rs=stmt.executeQuery("select * from Nutzer");
        	while(rs.next()) {
        		System.out.println(rs.getInt("userid"));
        		System.out.println(rs.getString("username"));
        		System.out.println(rs.getString("pwdhash"));
        		System.out.println(rs.getInt("rollenid"));
        	}
        }catch(Exception e) {
        	
        };
    }

    
}
