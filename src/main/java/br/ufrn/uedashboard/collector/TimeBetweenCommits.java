package br.ufrn.uedashboard.collector;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import br.ufrn.uedashboard.csv.CSVFileWriter;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.services.CommitService;
import br.ufrn.uedashboard.statistics.StatisticalOperations;
import br.ufrn.uedashboard.util.DateUtil;

public class TimeBetweenCommits extends UnusualEventsDataCollector {
	
	private List<String> teamStats;

	@Override
	public void collectData() {
		this.commitService = CommitService.getCommitService();
		this.teamStats = new LinkedList<String>();
		
		this.collectDateTimeInterval();
		
		if (this.successor != null) {
			this.successor.collectData();
		}
	}
	
	private void collectDateTimeInterval() {
		List<String> developers = this.commitService.getAllDevelopers();
		for (String developer : developers) {
			List<Commit> allCommitsFromDeveloper = this.commitService.getAllCommitsFromDeveloper(developer);
			this.collectDateTimeIntervalByDeveloper(developer, allCommitsFromDeveloper);
		}
		writeTeamCSVFile();
	}
	
	private void collectDateTimeIntervalByDeveloper(String developer, List<Commit> commits) {
		DateUtil dateUtil = new DateUtil();
		Commit previousCommit = null;
		
		this.csvWriter = new CSVFileWriter("Time Between Commits/"+developer);
		this.csvWriter.createHeader("Commit Date,Time Interval");

		List<String> lines = new LinkedList<String>();
		
		int[] timeIntervals = new int[commits.size()+1];
		int idx = 0;
		
		for (Commit commit : commits) {
			String line = "";
			if (previousCommit != null) {
				int daysBetween = dateUtil.daysBetween(previousCommit.getDate(), commit.getDate());
				timeIntervals[idx] = daysBetween;
				idx++;
				line = commit.getDate()+","+daysBetween;
			} else {
				line = commit.getDate()+",";
			}
			lines.add(line);
			previousCommit = commit;
		}
		this.csvWriter.addLines(lines);
		this.getStatsTimeBetweenCommits(developer, commits.size(), timeIntervals);
	}
	
	private void getStatsTimeBetweenCommits(String developer, int totalCommits, int[] timeIntervals) {
		DecimalFormat df = new DecimalFormat("#.####");
		
		double mean = StatisticalOperations.mean(timeIntervals);
		double standardDeviation = StatisticalOperations.standardDeviation(timeIntervals);
		int averageDays = (int) Math.ceil(mean);

		String line = developer+","+totalCommits+","+averageDays+","+df.format(mean)+","+df.format(standardDeviation);
		teamStats.add(line);
	}

	private void writeTeamCSVFile() {
		this.csvWriter = new CSVFileWriter("Time Between Commits/team");
		this.csvWriter.createHeader("Developer,Total Commits,Average Days Between Commits,Mean,Standard Deviation");
		this.csvWriter.addLines(teamStats);
	}
	
}
