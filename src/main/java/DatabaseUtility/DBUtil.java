package DatabaseUtility;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
public class DBUtil {
	private SQLServerDataSource AHPPLDbDataSource = null;
	public DBUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public DBUtil(String ServerName, int Port, String DatabaseName, String UserName, String Password)
	{
		AHPPLDbDataSource = new SQLServerDataSource();
		AHPPLDbDataSource.setServerName(ServerName);
		AHPPLDbDataSource.setPortNumber(Port);
		AHPPLDbDataSource.setDatabaseName(DatabaseName);
		AHPPLDbDataSource.setUser(UserName);
		AHPPLDbDataSource.setPassword(Password);
	}
	
	public ResultSet getRecordsFromDB(String QueryText) throws SQLException
	{
		Connection DBConnection = AHPPLDbDataSource.getConnection();
		java.sql.Statement DBQuery = DBConnection.createStatement();
		
		return DBQuery.executeQuery(QueryText);
		
	}

}
