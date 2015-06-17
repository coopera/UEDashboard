package br.ufrn.uedashboard.collector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufrn.uedashboard.csv.CSVFileWriter;
import br.ufrn.uedashboard.services.CommitService;
import br.ufrn.uedashboard.statistics.StatisticalOperations;

public class FileChanges extends UnusualEventsDataCollector {

	@Override
	@SuppressWarnings("rawtypes")
	public void collectData() {
		this.commitService = CommitService.getCommitService();
		
		Map<String, Integer> filesChangeFrequency = this.commitService.getFilesChangeFrequency();
		
		List<String> lines = new LinkedList<String>();
		
		int[] changes = new int[filesChangeFrequency.size()];
		
		int idx = 0;
		
		Iterator<Entry<String, Integer>> iterator = filesChangeFrequency.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry pairs = (Map.Entry) iterator.next();
			lines.add(pairs.getKey().toString()+","+pairs.getValue().toString());
			changes[idx] = Integer.parseInt(pairs.getValue().toString());
			idx++;
		}
		
		writeToCSV(lines);
		getStats(changes);
	}
	
	public void writeToCSV(List<String> lines) {
		this.csvWriter = new CSVFileWriter("File Modifications/files");
		this.csvWriter.createHeader("File,Changes");
		this.csvWriter.addLines(lines);
	}
	
	private void getStats(int[] changes) {
		List<String> lines = new ArrayList<String>();
		
		double mean = StatisticalOperations.mean(changes);
		double std = StatisticalOperations.standardDeviation(changes);
		
		lines.add(mean+","+std);
		writeToCSVStats(lines);
	}
	
	public void writeToCSVStats(List<String> lines) {
		this.csvWriter = new CSVFileWriter("File Modifications/stats");
		this.csvWriter.createHeader("Mean,Standard Deviation");
		this.csvWriter.addLines(lines);
	}

}
