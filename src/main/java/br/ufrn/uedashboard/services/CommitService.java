package br.ufrn.uedashboard.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.uedashboard.dao.CommitDAO;
import br.ufrn.uedashboard.dao.CommitMySQLDAO;
import br.ufrn.uedashboard.model.Commit;

public class CommitService {
	
	private static CommitService instance;
	
	private CommitService() { }
	
	public static CommitService getCommitService() {
		if (instance == null) {
			instance = new CommitService();
		}
		return instance;
	}
	
	public List<Commit> getAllCommits() {
		CommitDAO commitDAO = new CommitMySQLDAO();
		List<Commit> commits = commitDAO.getCommits();
		return commits;
	}
	
	public List<Commit> getAllCommitsFromDeveloper(String developer) {
		CommitDAO commitDAO = new CommitMySQLDAO();
		List<Commit> developerCommits = commitDAO.getCommitsByDeveloper(developer);
		return developerCommits;
	}

	public List<String> getAllDevelopers() {
		CommitDAO commitDAO = new CommitMySQLDAO();
		return commitDAO.getAuthors();
	}
	
	public HashMap<String, Date> getFileLastModificationDate() {
		CommitDAO commitDAO = new CommitMySQLDAO();
		return commitDAO.getFilesLastModificationDate();
	}
	
	public Map<String, Integer> getFilesChangeFrequency() {
		CommitDAO commitDAO = new CommitMySQLDAO();
		return commitDAO.getFrequencyChangedFile();
	}
}
