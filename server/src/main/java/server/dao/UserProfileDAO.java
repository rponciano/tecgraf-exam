package server.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.exceptions.DBConnectException;
import common.model.Perfil;
import server.exceptions.DBConsultException;
import server.exceptions.DBCreateException;
import server.exceptions.DBDeleteException;

public class UserProfileDAO extends GenericDAO {

	private static final long serialVersionUID = -4624205244267762954L;

	public void deleteUserProfilesByUserId(Long userId) throws DBConnectException, DBDeleteException {
		String sql = "DELETE FROM USER_PROFILE WHERE USUARIOID = ?";
		try {
			runCreateUpdateDeleteSQL(sql, userId);
		} catch (SQLException e) {
			throw new DBDeleteException(e.getMessage(), e.getCause());
		}
	}

	public void deleteUserProfilesByProfileId(Long perfilId) throws DBConnectException, DBDeleteException {
		String sql = "DELETE FROM USER_PROFILE WHERE PERFILID = ?";
		try {
			runCreateUpdateDeleteSQL(sql, perfilId);
		} catch (SQLException e) {
			throw new DBDeleteException(e.getMessage(), e.getCause());
		}
	}

	public void createUserProfile(Long userId, Long perfilId) throws DBCreateException, DBConnectException {
		String sql = "INSERT INTO USER_PROFILE(USUARIOID, PERFILID) VALUES (?, ?)";
		try {
			runCreateUpdateDeleteSQL(sql, userId, perfilId);
		} catch (SQLException e) {
			throw new DBCreateException(e.getMessage(), e.getCause());
		}
	}

	public List<Perfil> getUserProfilesByUserId(Long userId) throws DBConsultException, DBConnectException {
		String sql = "SELECT * FROM PERFIL p INNER JOIN USER_PROFILE up ON p.ID = up.PERFILID WHERE up.USUARIOID = ?";
		List<Object> objs = runSelectSQL(sql, userId);
		return objectListToPerfilList(objs);
	}

	@Override
	public List<Object> populateListOfSelectSQL(ResultSet result) throws SQLException {
		List<Perfil> perfis = new ArrayList<Perfil>();
		while (result.next()) {
			Long id = result.getLong("id");
			String name = result.getString("nome");
			String desc = result.getString("descricao");
			Date dataCriacao = result.getDate("dataCriacao");
			perfis.add(new Perfil(id, name, desc, dataCriacao));
		}
		;
		return new ArrayList<Object>(perfis);
	}

	private List<Perfil> objectListToPerfilList(List<Object> objs) {
		List<Perfil> perfis = new ArrayList<Perfil>();
		for (Object obj : objs) {
			perfis.add((Perfil) obj);
		}
		return perfis;
	}
}
