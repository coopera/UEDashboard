package br.ufrn.uedashboard.collector;

import java.util.LinkedList;
import java.util.List;

import br.ufrn.uedashboard.csv.CSVFileWriter;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Modification;
import br.ufrn.uedashboard.services.CommitService;
import br.ufrn.uedashboard.statistics.StatisticalOperations;

public class FilesTouchedCommit extends UnusualEventsDataCollector {

	protected final String CSV_FOLDER = "File Modifications";
	
	private List<String> teamStats;
	
	@Override
	public void collectData() {
		this.commitService = CommitService.getCommitService();
		this.teamStats = new LinkedList<String>();
		
		List<String> allDevelopers = this.commitService.getAllDevelopers();
		for (String developer : allDevelopers) {
			List<Commit> allCommitsFromDeveloper = this.commitService.getAllCommitsFromDeveloper(developer);
			getNumberOfModificationsByCommit(developer, allCommitsFromDeveloper);
		}
		
		writeToCSVStats(teamStats);
	}
	
	private void getNumberOfModificationsByCommit(String developer, List<Commit> commits) {
		List<String> lines = new LinkedList<String>();
		
		int[] addedFiles = new int[commits.size()];
		int[] modifiedFiles = new int[commits.size()];
		int[] deletedFiles = new int[commits.size()];
		
		int idx = 0;
		
		for (Commit commit : commits) {
			
			int added = 0;
			int modified = 0;
			int deleted = 0;
			
			for (Modification modification : commit.getModifications()) {
				if (modification.getType().equals("A")) {
					added++;
				} else if (modification.getType().equals("M")) {
					modified++;
				} else if (modification.getType().equals("D")) {
					deleted++;
				} 
			}
			
			String line = commit.getDate()+","+added+","+modified+","+deleted;
			lines.add(line);
			
			addedFiles[idx] = added;
			modifiedFiles[idx] = modified;
			deletedFiles[idx] = deleted;
			
			idx++;
		}
		
		writeToCSV(developer, lines);
		getDeveloperStats(developer, addedFiles, modifiedFiles, deletedFiles);
	}
	
	private void getDeveloperStats(String developer, int[] addedFiles, int[] modifiedFiles, int[] deletedFiles) {
		double meanAddedFiles = StatisticalOperations.mean(addedFiles);
		double stdAddedFiles = StatisticalOperations.standardDeviation(addedFiles);
		
		double meanModifiedFiles = StatisticalOperations.mean(modifiedFiles);
		double stdModifiedFiles = StatisticalOperations.standardDeviation(modifiedFiles);
		
		double meanDeletedFiles = StatisticalOperations.mean(deletedFiles);
		double stdDeletedFiles = StatisticalOperations.standardDeviation(deletedFiles);
		
		String line = developer+","+meanAddedFiles+","+stdAddedFiles+","+meanModifiedFiles+","+stdModifiedFiles+","+meanDeletedFiles+","+stdDeletedFiles;
		
		teamStats.add(line);
	}

	private void writeToCSV(String developer, List<String> lines) {
		this.csvWriter = new CSVFileWriter(CSV_FOLDER+"/"+developer+"/commits");
		this.csvWriter.createHeader("Date,Added,Modified,Deleted");
		this.csvWriter.addLines(lines);
	}
	
	private void writeToCSVStats(List<String> lines) {
		this.csvWriter = new CSVFileWriter(CSV_FOLDER+"/team_stats_commit");
		this.csvWriter.createHeader("Developer,Added Mean,Added Standard Deviation,Modified Mean,Modified Standard Deviation,Deleted Mean,Deleted Standard Deviation");
		this.csvWriter.addLines(lines);
	}

}
