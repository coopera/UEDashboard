package br.ufrn.uedashboard.main;

import java.util.List;

import br.ufrn.uedashboard.SVN.SVNConnector;
import br.ufrn.uedashboard.SVN.SVNMiner;
import br.ufrn.uedashboard.SVN.SubversionConnectorFactory;
import br.ufrn.uedashboard.analyzer.DataAnalyzerChain;
import br.ufrn.uedashboard.collector.DataCollectorChain;
import br.ufrn.uedashboard.dao.CommitDAO;
import br.ufrn.uedashboard.dao.CommitMySQLDAO;
import br.ufrn.uedashboard.dao.ModificationDAO;
import br.ufrn.uedashboard.dao.ModificationMySQLDAO;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Modification;

public class Main {

	public static void main(String[] args) {
		/* Retrieve commits from SVN */
		retrieveCommits();

		/* Start the collection process */
		DataCollectorChain dataCollectorChain = new DataCollectorChain();
		dataCollectorChain.getUnusualEventsDataCollector().collectData();
		
		/* Start the analysis process */
		runAnalysis();
	}

	public static SVNMiner getSVNMiner() {
		SubversionConnectorFactory svnConnectorFactory = new SubversionConnectorFactory();
		SVNConnector svnConnector = svnConnectorFactory.createConnector("<SVN USERNAME>", "<SVN PASSWORD>", "<SVN URL>");
		
		SVNMiner svnMiner = new SVNMiner(svnConnector);
		return svnMiner;
	}
	
	public static void retrieveCommits() {
		SVNMiner svnMiner = getSVNMiner();
		List<Commit> commits = svnMiner.getCommits(java.sql.Date.valueOf("<ADD START DATE>"), java.sql.Date.valueOf("<ADD END DATE>"));
		
		saveCommits(commits);
	}
	
	public static void saveCommits(List<Commit> commits) {
		CommitDAO commitDAO = new CommitMySQLDAO();
		ModificationDAO modificationDAO = new ModificationMySQLDAO();
		
		int id_commit = 1;
		int id_modifications = 1;
		
		for (Commit commit : commits) {
			commitDAO.saveCommit(commit, id_commit);
			for (Modification modification : commit.getModifications()) {
				modificationDAO.saveModification(modification, id_commit, id_modifications);
				id_modifications++;
			}
			id_commit++;
		}
	}
	
	private static void runAnalysis() {
		DataAnalyzerChain dataAnalyzerChain = new DataAnalyzerChain();
		
		long lastRevision = getLastStoredCommit().getRevision();
		
		while (true) {
			List<Commit> commits = getCommitsSinceLastRevision(lastRevision);
			if (commits.size() > 0) {
				for (Commit commit : commits) {
					dataAnalyzerChain.getUnusualEventsDataAnalyzer().analyze(commit);
				}
				saveCommits(commits);
				lastRevision = getLastStoredCommit().getRevision();
			}
		}
	}

	private static List<Commit> getCommitsSinceLastRevision(long revision) {
		SVNMiner svnMiner = getSVNMiner();
		return svnMiner.getCommitsAfterRevision(revision);
	}

	private static Commit getLastStoredCommit() {
		CommitDAO commitDAO = new CommitMySQLDAO();
		return commitDAO.getLastStoredCommit();
	}
}
