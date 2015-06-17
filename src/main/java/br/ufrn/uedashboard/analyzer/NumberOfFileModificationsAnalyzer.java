package br.ufrn.uedashboard.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import br.ufrn.uedashboard.csv.CSVFileReader;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Event;
import br.ufrn.uedashboard.model.Modification;
import br.ufrn.uedashboard.services.EventService;

public class NumberOfFileModificationsAnalyzer extends UnusualEventsDataAnalyzer {
	
	private final String FOLDER_PATH = "File Modifications/";

	@Override
	public void checkIfOutlier(Commit commit) {
		int added = 0;
		int modified = 0;
		int deleted = 0;
		
		List<Modification> modifications = commit.getModifications();
		for (Modification modification : modifications) {
			if (modification.getType().equals("A")) {
				added++;
			} else if (modification.getType().equals("M")) {
				modified++;
			} else if (modification.getType().equals("D")) {
				deleted++;
			}
		}
		
		getStats(commit, added, modified, deleted);
	}
	
	private void getStats(Commit commit, int added, int modified, int deleted) {
		String developer = commit.getDeveloper();
		csvFileReader = new CSVFileReader();
		BufferedReader br = csvFileReader.getBufferedReader(FOLDER_PATH+"team_stats_commit.csv");
		
		String line = "";
		
		double meanAddedFiles = 0, stdAddedFiles = 0;
		double meanModifiedFiles = 0, stdModifiedFiles = 0;
		double meanDeletedFiles = 0, stdDeletedFiles = 0;
		
		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (values[0].equals(developer)) {
					meanAddedFiles = Double.valueOf(values[1]);
					stdAddedFiles = Double.valueOf(values[2]);
					
					meanModifiedFiles = Double.valueOf(values[3]);
					stdModifiedFiles = Double.valueOf(values[4]);
					
					meanDeletedFiles = Double.valueOf(values[5]);
					stdDeletedFiles = Double.valueOf(values[6]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (added > meanAddedFiles+2*stdAddedFiles) {
			Event event = new Event();
			event.setDescription("outlier - "+added+" added files in this commit");
			event.setType("Large Number of Added Files");
			event.setId_commit(commit.getId());
			event.setMessage("Large number of added files");
			
			EventService.getEventService().saveEvent(event);
		}
		if (modified > meanModifiedFiles+2*stdModifiedFiles) {
			Event event = new Event();
			event.setDescription("outlier - "+modified+" modified files in this commit");
			event.setType("Large Number of Modified Files");
			event.setId_commit(commit.getId());
			event.setMessage("Large number of modified files");
			
			EventService.getEventService().saveEvent(event);
		}
		if (deleted > meanDeletedFiles+2*stdDeletedFiles) {
			Event event = new Event();
			event.setDescription("outlier - "+deleted+" deleted files in this commit");
			event.setType("Large Number of Deleted Files");
			event.setId_commit(commit.getId());
			event.setMessage("Large number of deleted files");
			
			EventService.getEventService().saveEvent(event);
		}
	}
	
	@Override
	public void updateCSV() {
	}

}
