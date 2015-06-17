package br.ufrn.uedashboard.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufrn.uedashboard.csv.CSVFileReader;
import br.ufrn.uedashboard.csv.CSVFileWriter;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Event;
import br.ufrn.uedashboard.model.Modification;
import br.ufrn.uedashboard.services.EventService;
import br.ufrn.uedashboard.statistics.StatisticalOperations;

public class NumberOfChangesToFileAnalyzer extends UnusualEventsDataAnalyzer {
	
	private final String FOLDER_PATH = "File Modifications/";
	
	private HashMap<String, Integer> filesChangeFrequency;
	
	private int totalModifications;

	@Override
	public void checkIfOutlier(Commit commit) {
		filesChangeFrequency = new HashMap<String, Integer>();
		
		List<Modification> modifications = commit.getModifications();
		for (Modification modification : modifications) {
			if (isOutlier(modification.getPath())) {
				Event event = new Event();
				event.setDescription("File "+modification.getPath()+" modified in this commit has already had a lot of modifications");
				event.setType("File with Large Number of Modifications");
				event.setMessage("File "+modification.getPath()+" has already been modified many times");
				event.setId_commit(commit.getId());
				
				EventService.getEventService().saveEvent(event);
			}
		}
	}

	private boolean isOutlier(String path) {
		csvFileReader = new CSVFileReader();
		BufferedReader br = csvFileReader.getBufferedReader(FOLDER_PATH+"files.csv");
		
		String line = "";
		totalModifications = 0;
		
		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				totalModifications = Integer.parseInt(values[1]);
				if (values[0].equals(path)) {
					totalModifications++;
				}
				filesChangeFrequency.put(values[0], totalModifications);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		csvFileReader = new CSVFileReader();
		br = csvFileReader.getBufferedReader(FOLDER_PATH+"stats.csv");
		
		double mean = 0, standardDeviation = 0;
		
		try {
			//ignore header
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				mean = Double.valueOf(values[0]);
				standardDeviation = Double.valueOf(values[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		double criteria = mean+2*standardDeviation;
		if (totalModifications > criteria) {
			return true;
		}
		return false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void updateCSV() {
		List<String> lines = new ArrayList<String>();

		this.csvWriter = new CSVFileWriter(FOLDER_PATH+"files.csv");
		int[] changes = new int[filesChangeFrequency.size()];
		int idx = 0;
		
		Iterator<Entry<String, Integer>> iterator = filesChangeFrequency.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry pairs = (Map.Entry) iterator.next();
			lines.add(pairs.getKey().toString()+","+pairs.getValue().toString());
			changes[idx] = Integer.parseInt(pairs.getValue().toString());
			idx++;
		}
		
		this.csvWriter.addLines(lines);
		
		lines = new ArrayList<String>();
		
		double mean = StatisticalOperations.mean(changes);
		double std = StatisticalOperations.standardDeviation(changes);
		
		lines.add(mean+","+std);
	
		this.csvWriter = new CSVFileWriter(FOLDER_PATH+"/stats");
		this.csvWriter.createHeader("Mean,Standard Deviation");
		this.csvWriter.addLines(lines);
	}

}
