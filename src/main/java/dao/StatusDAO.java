package dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import common.DatabaseConnection;
import exception.DBConnectException;
import exception.DBConsultException;
import exception.DBDataNotFoundException;
import model.Status;

public class StatusDAO implements Serializable {
	
	private static final long serialVersionUID = -1503189464001292373L;

	private String DBUrl;
	private String DBUser;
	private String DBPass;
	
	public StatusDAO(String url, String user, String pass) {
		this.DBUrl = url;
		this.DBUser = user;
		this.DBPass = pass;
	}
	
	public List<Status> getStatus() throws DBConsultException, DBConnectException, DBDataNotFoundException {
		List<Status> stats = new ArrayList<Status>();
		DatabaseConnection db = null;
		Statement statment = null;
		ResultSet result = null;
		try {
			db = new DatabaseConnection(DBUrl, DBUser, DBPass);
			statment = db.getConnection().createStatement();
			result = statment.executeQuery("SELECT * FROM STATUS");
			if(result.isBeforeFirst()) {
				while(result.next()) {
					Status stat = new Status();
					stat.setId(result.getInt("id"));
					stat.setNome(result.getString("nome"));
					stats.add(stat);
				};
			}
		} catch (DBConnectException e) {
			throw e;
		} catch (SQLException e) {
			throw new DBConsultException(e.getMessage(), e.getCause());
		} finally {
		    try { result.close(); } catch (Exception e) { /* ignorar */ }
		    try { db.closeConnection(); } catch (Exception e) { /* ignorar */ } 
		}
		return stats;
	}
}
    