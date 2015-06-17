package br.ufrn.uedashboard.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Modification;

public interface CommitDAO {
	
	public void saveCommit(Commit commit, int id);
	
	public List<String> getAuthors();
		
	public List<Commit> getCommits();
	
	public List<Modification> getModifications(int id_commit);
	
	public List<Commit> getCommitsByDeveloper(String author);
	
	public HashMap<String, Date> getFilesLastModificationDate();
	
	public Map<String, Integer> getFrequencyChangedFile();
	
	public Commit getLastStoredCommit();
	
}
