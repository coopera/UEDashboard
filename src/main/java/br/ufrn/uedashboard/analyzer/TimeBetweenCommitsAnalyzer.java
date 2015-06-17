package br.ufrn.uedashboard.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.ufrn.uedashboard.csv.CSVFileReader;
import br.ufrn.uedashboard.csv.CSVFileWriter;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Event;
import br.ufrn.uedashboard.services.EventService;

public class TimeBetweenCommitsAnalyzer extends UnusualEventsDataAnalyzer {
	
	private final String FOLDER_PATH = "Time Between Commits/";
	
	private List<String> lines;
	
	private String developer;
	
	@Override
	public void checkIfOutlier(Commit commit) {
		this.lines = new LinkedList<String>();
		
		Date lastCommitDate = getLastCommitDate(commit.getDeveloper());
		int daysBetween = dateUtil.daysBetween(lastCommitDate, commit.getDate());
		double stats = getStats(commit.getDeveloper());
		
		//line to be appended
		this.lines.add(dateUtil.getDateFormatter().format(new Date(commit.getDate().getTime()))+","+daysBetween);
		this.developer = commit.getDeveloper();
		
		if (daysBetween > stats) {
			Event event = new Event();
			event.setDescription("outlier - "+daysBetween+" days since last commit by "+commit.getDeveloper());
			event.setType("Time Between Commits");
			event.setId_commit(commit.getId());
			event.setMessage(commit.getDeveloper()+" hasn't commited anything for "+daysBetween+" days");
			
			EventService.getEventService().saveEvent(event);
		}
	}

	private Date getLastCommitDate(String developer) {
		BufferedReader br = csvFileReader.getBufferedReader(FOLDER_PATH+developer+".csv");
		
		String line = "";
		String prevLine = "";
		
		try {
			while ((line = br.readLine()) != null) {
				lines.add(line);
				prevLine = line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String[] prevLineArray = prevLine.split(",");
		String lastCommitDate = prevLineArray[0];
		
		Date lastCommit = null;
		try {
			lastCommit = dateUtil.getDateFormatter().parse(lastCommitDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return lastCommit;
	}
	
	private double getStats(String developer) {
		CSVFileReader csvReader = new CSVFileReader();
		BufferedReader br = csvReader.getBufferedReader(FOLDER_PATH+"team.csv");
		
		String line = "";
		double mean = 0, standardDeviation = 0;
		
		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (values[0].equals(developer)) {
					mean = Double.valueOf(values[3]);
					standardDeviation = Double.valueOf(values[4]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mean+2*standardDeviation;
	}

	@Override
	public void updateCSV() {
		this.csvWriter = new CSVFileWriter("Time Between Commits/"+developer);
		this.csvWriter.addLines(lines);
	}

}
