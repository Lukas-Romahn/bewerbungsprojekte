package utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JNDIFactory {

    private static JNDIFactory instance = null;
    protected JNDIFactory() {
       // Exists only to defeat instantiation.
    }
    
    public static synchronized JNDIFactory getInstance() {
       if(instance == null) {
          instance = new JNDIFactory();
       }
       return instance;
    }
    

    /**
     * Holt sich eine neue Connection aus Connectionpool
     * 
     * @return Connection
     * @throws NamingException
     * @throws SQLException
     */
    public Connection getConnection(String Datasource) throws NamingException, SQLException {
      Context initContext = new InitialContext();
  
      Context envContext = (Context) initContext.lookup("java:/comp/env");
  
      if (envContext == null)
        throw new NamingException("InitialContext lookup wrong");
  
      DataSource ds = (DataSource) envContext.lookup(Datasource);

		if (ds == null)
			throw new NamingException("No Datasource");

		Connection conn = ds.getConnection();

		if (conn == null)
			throw new SQLException("No Connection found");

		return conn;

    }    

    /** Holt aus der context.xml eine Variable als String
     * @param envName
     * @return String
     * @throws NamingException
     */
    public String getEnvironmentAsString(String envName) throws NamingException {
    	String env = null;
        InitialContext ictx = new InitialContext();
        Context myenv = (Context) ictx.lookup("java:comp/env");
        try {
        	env = (String) myenv.lookup(envName);
        } catch (NamingException n) {
        	System.out.println ("String Environment '" + envName + "' is missing.");
        }
        return env;
    }

    /** Holt aus der context.xml eine Variable als Integer
     * @param envName
     * @return Integer
     * @throws NamingException
     */
    public Integer getEnvirontmenAsInteger(String envName) throws NamingException {
    	Integer env = null;
        InitialContext ictx = new InitialContext();
        Context myenv = (Context) ictx.lookup("java:comp/env");
        try {
        	env = (Integer) myenv.lookup(envName);
        } catch (NamingException n) {
        	System.out.println ("Integer Environment '" + envName + "' is missing.");
        }
        return env;
    }
    
    /** Holt aus der context.xml eine Variable als Boolean
     * @param envName
     * @return Boolean
     * @throws NamingException
     */
    public Boolean getEnvironmentAsBoolean(String envName) throws NamingException {
    	Boolean env = null;
        InitialContext ictx = new InitialContext();
        Context myenv = (Context) ictx.lookup("java:comp/env");
        try {
        	env = (Boolean) myenv.lookup(envName);
        } catch (NamingException n) {
        	System.out.println ("Boolean Environment '" + envName + "' is missing.");
        }
        return env;
    }
    

}
