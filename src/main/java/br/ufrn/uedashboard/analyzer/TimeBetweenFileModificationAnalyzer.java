package br.ufrn.uedashboard.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.ufrn.uedashboard.csv.CSVFileWriter;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Event;
import br.ufrn.uedashboard.model.Modification;
import br.ufrn.uedashboard.services.EventService;
import br.ufrn.uedashboard.statistics.StatisticalOperations;

public class TimeBetweenFileModificationAnalyzer extends UnusualEventsDataAnalyzer {
	
	private final String FOLDER_PATH = "File Modification Date/";
	
	private Date lastModificationDate;
	
	private List<String> lines;

	@Override
	public void checkIfOutlier(Commit commit) {
		List<Modification> modifications = commit.getModifications();
		for (Modification modification : modifications) {
			if (isOutlier(modification.getPath(), commit.getDate())) {
				Event event = new Event();
				event.setId_commit(commit.getId());
				event.setDescription("File "+modification.getPath()+" had not been modified since "+lastModificationDate);
				event.setMessage("File "+modification.getPath()+" had not been modified since "+lastModificationDate);
				event.setType("Time Between File Modification");
				
				EventService.getEventService().saveEvent(event);
			}
		}
	}

	public boolean isOutlier(String path, Date currentDate) {
		lines = new LinkedList<String>();
		
		BufferedReader br = csvFileReader.getBufferedReader(FOLDER_PATH+"files.csv");
		
		String line = "";
		List<Date> lastDates = new ArrayList<Date>();
		
		int fileDaysSinceLastModification = 0;
		
		try {
			//ignore header
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				String dateStr = values[1];
				Date date = null;
				try {
					date = dateUtil.getDateFormatter().parse(dateStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				if (values[0].equals(path)) {
					lastModificationDate = date;
					line = path+","+dateUtil.getDateFormatter().format(new Date(lastModificationDate.getTime()));
					fileDaysSinceLastModification = dateUtil.daysBetween(date, currentDate);
				}
				
				lines.add(line);
				lastDates.add(date);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int[] daysPassed = new int[lastDates.size()];
		
		for (int i=0; i<daysPassed.length; i++) {
			daysPassed[i] = dateUtil.daysBetween(lastDates.get(i), currentDate);
		}
		
		double mean = StatisticalOperations.mean(daysPassed);
		double standardDeviation = StatisticalOperations.standardDeviation(daysPassed);
		
		if (fileDaysSinceLastModification > mean+2*standardDeviation) {
			return true;
		}
		return false;
		
	}
	
	@Override
	public void updateCSV() {
		this.csvWriter = new CSVFileWriter("File Modification Date/files");
		this.csvWriter.addLines(lines);
	}

}
