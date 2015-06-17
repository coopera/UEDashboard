package br.ufrn.uedashboard.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import br.ufrn.uedashboard.csv.CSVFileReader;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Event;
import br.ufrn.uedashboard.model.Modification;
import br.ufrn.uedashboard.services.EventService;

public class NumberOfDifferentDevelopersModificationAnalyzer extends UnusualEventsDataAnalyzer {
	
	private final String FOLDER_PATH = "File Modification Developers/";
	
	private int totalDevelopers;

	@Override
	public void checkIfOutlier(Commit commit) {
		List<Modification> modifications = commit.getModifications();
		for (Modification modification : modifications) {
			if (isOutlier(commit.getDeveloper(), modification.getPath())) {
				Event event = new Event();
				event.setDescription("File "+modification.getPath()+" has been modified by "+totalDevelopers+" developers");
				event.setId_commit(commit.getId());
				event.setType("File Modified By Many Different Developers");
				event.setMessage("File "+modification.getPath()+" has been modified by "+totalDevelopers+" developers");
				
				EventService.getEventService().saveEvent(event);
			}
		}
	}

	private boolean isOutlier(String developer, String path) {
		BufferedReader br = csvFileReader.getBufferedReader(FOLDER_PATH+"files.csv");
		
		String line = "";
		totalDevelopers = 0;
		
		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (values[0].equals(path)) {
					
					String developers = values[2];
					totalDevelopers = Integer.parseInt(values[1]);
					if (!developers.contains(developer)) {
						updateCSV();
						totalDevelopers++;
					}
				}
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
		if (totalDevelopers > criteria) {
			return true;
		}
		return false;
	}
	
	@Override
	public void updateCSV() {
	}

}
