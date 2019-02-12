package dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import common.DatabaseConnection;
import exception.DBConnectException;
import exception.DBConsultException;
import exception.DBCreateException;
import exception.DBDataNotFoundException;
import model.Plugin;

public class PluginDAO implements Serializable {
	
	private static final long serialVersionUID = -5098749675099318542L;

	public List<Plugin> getPlugins() throws DBConsultException, DBConnectException, DBDataNotFoundException {
		List<Plugin> plugins = new ArrayList<Plugin>();
		DatabaseConnection db = null;
		Statement statment = null;
		ResultSet result = null;
		try {
			db = new DatabaseConnection();
			statment = db.getConnection().createStatement();
			result = statment.executeQuery("SELECT * FROM PLUGIN");
			if(!result.isBeforeFirst()) {
				throw new DBDataNotFoundException();
			}
			while(result.next()) {
				plugins.add(new Plugin(
					result.getInt("id"),
					result.getString("nome"),
					result.getString("descricao"),
					result.getString("dataCriacao")
				));
			};
		} catch (DBConnectException e) {
			throw e;
		} catch (SQLException e) {
			throw new DBConsultException(e.getMessage(), e.getCause());
		} finally {
		    try { result.close(); } catch (Exception e) { /* ignorar */ }
		    db.closeConnection();
		}
		return plugins;
	}
	
	public void createPlugin(Plugin plugin) throws DBConnectException, DBCreateException {
		DatabaseConnection db = null;
		PreparedStatement statment = null;
		try {
			db = new DatabaseConnection();
			statment = db.getConnection().prepareStatement("INSERT INTO PLUGIN (nome, descricao) VALUES (?, ?)");
			statment.setString(1, plugin.getNome());
			statment.setString(2, plugin.getDescricao());
			statment.executeUpdate();
		} catch(SQLException e) {
			throw new DBCreateException(e.getMessage(), e.getCause());
		} catch(DBConnectException e) {
			throw e;
		} finally {
			try { statment.close(); } catch (Exception e) { /* ignorar */ }
		    db.closeConnection();
		}
	}
}
