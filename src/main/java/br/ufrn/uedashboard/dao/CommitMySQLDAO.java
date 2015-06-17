package br.ufrn.uedashboard.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Modification;

public class CommitMySQLDAO implements CommitDAO {

	@Override
	public void saveCommit(Commit commit, int id) {
		PreparedStatement statement;
		
		String sql = "INSERT into Commit (id, developer, comment, date, revision) VALUES(?, ?, ?, ?, ?)";

		try {
			statement = MySQLConnector.getConnection().prepareStatement(sql);
			statement.setInt(1, id);
			statement.setString(2, commit.getDeveloper());
			statement.setString(3, commit.getComment());
			statement.setDate(4, new Date(commit.getDate().getTime()));
			statement.setLong(5, commit.getRevision());
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getAuthors() {
		String query = "SELECT DISTINCT author FROM Commit";
		List<String> authors = new ArrayList<String>();
		try {
			PreparedStatement statement = MySQLConnector.getConnection()
					.prepareStatement(query);
			
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next()) {
				authors.add(rs.getString("author"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return authors;
	}

	@Override
	public List<Commit> getCommits() {
		String query = "SELECT * FROM Commit";
		List<Commit> commits = new ArrayList<Commit>();
		
		try {
			PreparedStatement statement = MySQLConnector.getConnection()
					.prepareStatement(query);
			
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next()) {
				Commit commit = new Commit();
				commit.setId(rs.getInt("id"));
				commit.setDeveloper(rs.getString("developer"));
				commit.setComment(rs.getString("comment"));
				commit.setDate(rs.getDate("date"));
				commit.setModifications(getModifications(rs.getInt("id")));
				commit.setRevision(rs.getLong("revision"));
				
				commits.add(commit);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return commits;
	}

	@Override
	public List<Modification> getModifications(int id_commit) {
		String query = "SELECT * FROM Modification WHERE id_commit = "+id_commit;
		List<Modification> modifications = new ArrayList<Modification>();
		
		try {
			PreparedStatement statement = MySQLConnector.getConnection()
					.prepareStatement(query);
			
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next()) {
				Modification modification = new Modification();
				modification.setId(rs.getInt("id"));
				modification.setIdCommit(id_commit);
				modification.setPath(rs.getString("path"));
				modification.setType(rs.getString("type"));
				
				modifications.add(modification);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return modifications;
	}

	@Override
	public List<Commit> getCommitsByDeveloper(String developer) {
		String query = "SELECT * FROM Commit WHERE developer='"+developer+"' order by date;";
		List<Commit> commits = new ArrayList<Commit>();
		
		try {
			PreparedStatement statement = MySQLConnector.getConnection()
					.prepareStatement(query);
			
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next()) {
				Commit commit = new Commit();
				commit.setId(rs.getInt("id"));
				commit.setDeveloper(rs.getString("developer"));
				commit.setComment(rs.getString("comment"));
				commit.setDate(rs.getDate("date"));
				commit.setModifications(getModifications(rs.getInt("id")));
				commit.setRevision(rs.getLong("revision"));
				
				commits.add(commit);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return commits;
	}

	@Override
	public HashMap<String, java.util.Date> getFilesLastModificationDate() {
		String query = "SELECT date, path from commit inner join modification on commit.id = modification.id_commit order by date";
		
		HashMap<String, java.util.Date> filesLastModificationDate = new HashMap<String, java.util.Date>();
		
		try {
			PreparedStatement statement = MySQLConnector.getConnection()
					.prepareStatement(query);
			
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next()) {
				Date date = rs.getDate("date");
				String path = rs.getString("path");
				filesLastModificationDate.put(path, date);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return filesLastModificationDate;
	}

	@Override
	public Map<String, Integer> getFrequencyChangedFile() {
		String query = "SELECT path, count(*) from modification m inner join commit c on m.id_commit = c.id "
				+ "group by path order by count(*) desc;";
		
		Map<String, Integer> pathFrequency = new LinkedHashMap<String, Integer>();
		
		try {
			PreparedStatement statement = MySQLConnector.getConnection()
					.prepareStatement(query);
			
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next()) {
				String path = rs.getString("path");
				int frequency = rs.getInt("count(*)");
				pathFrequency.put(path, frequency);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pathFrequency;
	}

	@Override
	public Commit getLastStoredCommit() {
		String query = "SELECT row from Commit ORDER BY id DESC LIMIT 1";
		Commit commit = new Commit();
		
		try {
			PreparedStatement statement = MySQLConnector.getConnection()
					.prepareStatement(query);
			
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next()) {
				commit.setId(rs.getInt("id"));
				commit.setDeveloper(rs.getString("developer"));
				commit.setComment(rs.getString("comment"));
				commit.setDate(rs.getDate("date"));
				commit.setModifications(getModifications(rs.getInt("id")));
				commit.setRevision(rs.getLong("revision"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return commit;
	}
	
}
