package br.ufrn.uedashboard.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufrn.uedashboard.csv.CSVFileWriter;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Modification;
import br.ufrn.uedashboard.services.CommitService;
import br.ufrn.uedashboard.statistics.StatisticalOperations;

public class DevelopersFileModification extends UnusualEventsDataCollector {

	@Override
	public void collectData() {
		this.commitService = CommitService.getCommitService();
		
		List<Commit> allCommits = this.commitService.getAllCommits();
		findDevelopersThatChangedEachFile(allCommits);
		
		if (this.successor != null) {
			this.successor.collectData();
		}
	}
	
	public void findDevelopersThatChangedEachFile(List<Commit> commits) {
		Map<String, List<String>> fileDevelopers = new HashMap<String, List<String>>();

		for (Commit commit : commits) {
			for (Modification modification : commit.getModifications()) {
				if (fileDevelopers.containsKey(modification.getPath())) {
					List<String> developers = fileDevelopers.get(modification.getPath());
					if (!developers.contains(commit.getDeveloper())) {
						developers.add(commit.getDeveloper());
						fileDevelopers.put(modification.getPath(), developers);
					}
				} else {
					List<String> developers = new ArrayList<String>();
					developers.add(commit.getDeveloper());
					fileDevelopers.put(modification.getPath(), developers);
				}
			}
		}
		
		List<String> lines = prepareDataToCSV(fileDevelopers);
		writeToCSV(lines);	
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private List<String> prepareDataToCSV(Map<String, List<String>> fileDevelopers) {
		List<String> lines = new ArrayList<String>();
		
		int[] diffDevelopers = new int[fileDevelopers.size()];
		
		int idx = 0;
		
		Iterator<Entry<String, List<String>>> iterator = fileDevelopers.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry pairs = (Map.Entry) iterator.next();
			String line = pairs.getKey().toString();
			List<String> developers = (List<String>) pairs.getValue();
			line += ","+developers.size()+",";
			for (String developer : developers) {
				line += developer+"-";
			}
			lines.add(line);
			diffDevelopers[idx] = developers.size();
			idx++;
		}
		getStats(diffDevelopers);
		return lines;
	}

	public void writeToCSV(List<String> lines) {
		this.csvWriter = new CSVFileWriter("File Modification Developers/files");
		this.csvWriter.createHeader("File,Number of Developers,Developers");
		this.csvWriter.addLines(lines);
	}
	
	private void getStats(int[] diffDevelopers) {
		List<String> lines = new ArrayList<String>();
		
		double mean = StatisticalOperations.mean(diffDevelopers);
		double std = StatisticalOperations.standardDeviation(diffDevelopers);
		
		lines.add(mean+","+std);
		writeToCSVStats(lines);
	}
	
	public void writeToCSVStats(List<String> lines) {
		this.csvWriter = new CSVFileWriter("File Modification Developers/stats");
		this.csvWriter.createHeader("Mean,Standard Deviation");
		this.csvWriter.addLines(lines);
	}

}
