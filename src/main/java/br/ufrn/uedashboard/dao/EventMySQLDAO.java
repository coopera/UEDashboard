package br.ufrn.uedashboard.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.ufrn.uedashboard.model.Event;

public class EventMySQLDAO implements EventDAO {

	@Override
	public void saveEvent(Event event) {
		PreparedStatement statement;
		
		String sql = "INSERT into Event (id, id_commit, description, type, message) VALUES(?, ?, ?, ?, ?)";

		try {
			statement = MySQLConnector.getConnection().prepareStatement(sql);
			statement.setInt(1, getNextId());
			statement.setInt(2, event.getId_commit());
			statement.setString(3, event.getDescription());
			statement.setString(4, event.getType());
			statement.setString(5, event.getMessage());
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getNextId() {
		String query = "SELECT row from Commit ORDER BY id DESC LIMIT 1";
		
		int id = -1;
		
		try {
			PreparedStatement statement = MySQLConnector.getConnection()
					.prepareStatement(query);
			
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next()) {
				id = rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return id+1;
	}
	
}
