package br.ufrn.uedashboard.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.ufrn.uedashboard.model.Modification;

public class ModificationMySQLDAO implements ModificationDAO {

	@Override
	public void saveModification(Modification modification, int id_commit, int id) {
		
		PreparedStatement statement;
		
		String sql = "INSERT into Modification (id, path, type, id_commit) VALUES(?, ?, ?, ?)";

		try {
			statement = MySQLConnector.getConnection().prepareStatement(sql);
			statement.setInt(1, id);
			statement.setString(2, modification.getPath());
			statement.setString(3, modification.getType());
			statement.setInt(4, id_commit);
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
