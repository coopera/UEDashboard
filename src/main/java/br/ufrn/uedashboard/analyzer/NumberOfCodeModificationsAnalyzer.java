package br.ufrn.uedashboard.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.Delta.TYPE;
import br.ufrn.uedashboard.csv.CSVFileReader;
import br.ufrn.uedashboard.main.Main;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Event;
import br.ufrn.uedashboard.model.Modification;
import br.ufrn.uedashboard.services.EventService;
import br.ufrn.uedashboard.wrapper.ClassWrapper;
import br.ufrn.uedashboard.wrapper.MethodWrapper;

public class NumberOfCodeModificationsAnalyzer extends UnusualEventsDataAnalyzer {
	
	private final String FOLDER_PATH = "Code Modification/";
	
	private int addedMethods;
	private int maxAddedMethods;
	private int avgAddedMethods;

	private int changedMethods;
	private int maxChangedMethods;
	private int avgChangedMethods;
	
	private int addedLOC;
	private int maxAddedLOC;
	private int avgAddedLOC;
	
	private int changedLOC;
	private int maxChangedLOC;
	private int avgChangedLOC;
	
	private int deletedLOC;
	private int maxDeletedLOC;
	private int avgDeletedLOC;
	
	private int addedComplexity;
	private int maxAddedComplexity;
	private int avgAddedComplexity;

	@Override
	public void checkIfOutlier(Commit commit) {
		this.addedMethods = 0;
		this.maxAddedMethods = 0;
		this.avgAddedMethods = 0;
		
		this.changedMethods = 0;
		this.maxChangedMethods = 0;
		this.avgChangedMethods = 0;

		this.addedLOC = 0;
		this.maxAddedLOC = 0;
		this.avgAddedLOC = 0;
		
		this.changedLOC = 0;
		this.maxChangedLOC = 0;
		this.avgChangedLOC = 0;
		
		this.deletedLOC = 0;
		this.maxDeletedLOC = 0;
		this.avgDeletedLOC = 0;
		
		this.addedComplexity = 0;
		this.maxAddedComplexity = 0;
		this.avgAddedComplexity = 0;
		
		List<Modification> modifications = commit.getModifications();
		for (Modification modification : modifications) {
			List<String> revisions = getRevisions(modification.getPath(), commit.getRevision());
			if (revisions != null && revisions.get(0) != null && revisions.get(1) != null) {
				calculateLOCChanged(revisions);
				calculateMethodsChangedAndCiclomaticComplexity(revisions);
			}
		}
		int modificationsSize = modifications.size();
		this.avgAddedComplexity = addedComplexity/modificationsSize;
		this.avgAddedLOC = addedLOC/modificationsSize;
		this.avgAddedMethods = addedMethods/modificationsSize;
		this.avgChangedLOC = changedLOC/modificationsSize;
		this.avgChangedMethods = changedMethods/modificationsSize;
		this.avgDeletedLOC = deletedLOC/modificationsSize;
		
		getStats(commit);
	}
	
	private void getStats(Commit commit) {
		String developer = commit.getDeveloper();
		csvFileReader = new CSVFileReader();
		BufferedReader br = csvFileReader.getBufferedReader(FOLDER_PATH+"team.csv");
		
		String line = "";
		
		double meanAddedLOC = 0, stdAddedLOC = 0, meanAvgAddedLOC = 0, stdAvgAddedLOC = 0, meanMaxAddedLOC = 0, stdMaxAddedLOC = 0;
		double meanChangedLOC = 0, stdChangedLOC = 0, meanAvgChangedLOC = 0, stdAvgChangedLOC = 0, meanMaxChangedLOC = 0, stdMaxChangedLOC = 0;
		double meanDeletedLOC = 0, stdDeletedLOC = 0, meanAvgDeletedLOC = 0, stdAvgDeletedLOC = 0, meanMaxDeletedLOC = 0, stdMaxDeletedLOC = 0;
		double meanAddedMethods = 0, stdAddedMethods = 0, meanAvgAddedMethods = 0, stdAvgAddedMethods = 0, meanMaxAddedMethods = 0, stdMaxAddedMethods = 0;
		double meanAddedComplexity = 0, stdAddedComplexity = 0, meanAvgAddedComplexity = 0, stdAvgAddedComplexity = 0, meanMaxAddedComplexity = 0, stdMaxAddedComplexity = 0;
		double meanChangedMethods = 0, stdChangedMethods = 0, meanAvgChangedMethods = 0, stdAvgChangedMethods = 0, meanMaxChangedMethods = 0, stdMaxChangedMethods = 0;
		
		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (values[0].equals(developer)) {
					meanAddedLOC = Double.valueOf(values[2]);
					meanAvgAddedLOC = Double.valueOf(values[3]);
					meanMaxAddedLOC = Double.valueOf(values[4]);
					stdAddedLOC = Double.valueOf(values[5]);
					stdAvgAddedLOC = Double.valueOf(values[6]); 
					stdMaxAddedLOC = Double.valueOf(values[7]);
					
					meanChangedLOC = Double.valueOf(values[8]);
					meanAvgChangedLOC = Double.valueOf(values[9]);
					meanMaxChangedLOC = Double.valueOf(values[10]); 
					stdChangedLOC = Double.valueOf(values[11]);
					stdAvgChangedLOC = Double.valueOf(values[12]);
					stdMaxChangedLOC = Double.valueOf(values[13]);
					
					meanDeletedLOC = Double.valueOf(values[14]);
					meanAvgDeletedLOC = Double.valueOf(values[15]);
					meanMaxDeletedLOC = Double.valueOf(values[16]);
					stdDeletedLOC = Double.valueOf(values[17]); 
					stdAvgDeletedLOC = Double.valueOf(values[18]);
					stdMaxDeletedLOC = Double.valueOf(values[19]);
					
					meanAddedComplexity = Double.valueOf(values[20]);
					meanAvgAddedComplexity = Double.valueOf(values[21]);
					meanMaxAddedComplexity = Double.valueOf(values[22]);
					stdAddedComplexity = Double.valueOf(values[23]);
					stdAvgAddedComplexity = Double.valueOf(values[24]);
					stdMaxAddedComplexity = Double.valueOf(values[25]);
					
					meanAddedMethods = Double.valueOf(values[26]);
					meanAvgAddedMethods = Double.valueOf(values[27]);
					meanMaxAddedMethods = Double.valueOf(values[28]);
					stdAddedMethods = Double.valueOf(values[29]);
					stdAvgAddedMethods = Double.valueOf(values[30]);
					stdMaxAddedMethods = Double.valueOf(values[31]);
					
					meanChangedMethods = Double.valueOf(values[32]);
					meanAvgChangedMethods = Double.valueOf(values[33]);
					meanMaxChangedMethods = Double.valueOf(values[34]);
					stdChangedMethods = Double.valueOf(values[35]);
					stdAvgChangedMethods = Double.valueOf(values[36]);
					stdMaxChangedMethods = Double.valueOf(values[37]);
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (this.addedLOC > meanAddedLOC+2*stdAddedLOC) {
			String description = "Total number of LOC added";
			if (this.avgAddedLOC > meanAvgAddedLOC+2*stdAvgAddedLOC) {
				description += ", average number of LOC added";
			}
			description += " higher than usual";
			if (this.maxAddedLOC > meanMaxAddedLOC+2*stdMaxAddedLOC) {
				description += " and higher then the average maximum LOC added in a commit";
			}
			
			Event event = new Event();
			event.setId_commit(commit.getId());
			event.setMessage("Large number of LOC added");
			event.setDescription(description);
			event.setType("Large Number of LOC Added");
			
			EventService.getEventService().saveEvent(event);
		}
		if (this.changedLOC > meanChangedLOC+2*stdChangedLOC) {
			String description = "Total number of LOC changed";
			if (this.avgChangedLOC > meanAvgChangedLOC+2*stdAvgChangedLOC) {
				description += ", average number of LOC changed";
			}
			description += " higher than usual";
			if (this.maxChangedLOC > meanMaxChangedLOC+2*stdMaxChangedLOC) {
				description += " and higher then the average maximum LOC changed in a commit";
			}
			
			Event event = new Event();
			event.setId_commit(commit.getId());
			event.setMessage("Large number of LOC changed");
			event.setDescription(description);
			event.setType("Large Number of LOC Changed");
			
			EventService.getEventService().saveEvent(event);
		}
		if (this.deletedLOC > meanDeletedLOC+2*stdDeletedLOC) {
			String description = "Total number of LOC deleted";
			if (this.avgDeletedLOC > meanAvgDeletedLOC+2*stdAvgDeletedLOC) {
				description += ", average number of LOC deleted";
			}
			description += " higher than usual";
			if (this.maxDeletedLOC > meanMaxDeletedLOC+2*stdMaxDeletedLOC) {
				description += " and higher then the average maximum LOC deleted in a commit";
			}
			
			Event event = new Event();
			event.setId_commit(commit.getId());
			event.setMessage("Large number of LOC deleted");
			event.setDescription(description);
			event.setType("Large Number of LOC Deleted");
			
			EventService.getEventService().saveEvent(event);
		}
		if (this.addedComplexity > meanAddedComplexity+2*stdAddedComplexity) {
			String description = "Complexity added";
			if (this.avgAddedComplexity > meanAvgAddedComplexity+2*stdAvgAddedComplexity) {
				description += ", average complexity added";
			}
			description += " higher than usual";
			if (this.maxAddedComplexity > meanMaxAddedComplexity+2*stdMaxAddedComplexity) {
				description += " and higher then the average maximum complexity in a commit";
			}
			
			Event event = new Event();
			event.setId_commit(commit.getId());
			event.setMessage("Higher complexity added");
			event.setDescription(description);
			event.setType("Higher Complexity Added");
			
			EventService.getEventService().saveEvent(event);
		} else if (this.addedComplexity < meanAddedComplexity-2*stdAddedComplexity) {
			String description = "Complexity added";
			if (this.avgAddedComplexity < meanAvgAddedComplexity-2*stdAvgAddedComplexity) {
				description += ", and average complexity added";
			}
			description += " lower than usual";
			
			Event event = new Event();
			event.setId_commit(commit.getId());
			event.setMessage("Lower complexity added");
			event.setDescription(description);
			event.setType("Lower Complexity Added");
			
			EventService.getEventService().saveEvent(event);
		}
		if (this.addedMethods > meanAddedMethods+2*stdAddedMethods) {
			String description = "Methods added";
			if (this.avgAddedMethods > meanAvgAddedMethods+2*stdAvgAddedMethods) {
				description += ", average number of methods added";
			}
			description += " higher than usual";
			if (this.maxAddedMethods > meanMaxAddedMethods+2*stdMaxAddedMethods) {
				description += " and higher then the average maximum methods added in a commit";
			}
			
			Event event = new Event();
			event.setId_commit(commit.getId());
			event.setMessage("Large number of methods added");
			event.setDescription(description);
			event.setType("Large Number of Methods Added");
			
			EventService.getEventService().saveEvent(event);
		}
		if (this.changedMethods > meanChangedMethods+2*stdChangedMethods) {
			String description = "Methods changed";
			if (this.avgChangedMethods > meanAvgChangedMethods+2*stdAvgChangedMethods) {
				description += ", average number of methods changed";
			}
			description += " higher than usual";
			if (this.maxChangedMethods > meanMaxChangedMethods+2*stdMaxChangedMethods) {
				description += " and higher then the average maximum methods changed in a commit";
			}
			Event event = new Event();
			event.setId_commit(commit.getId());
			event.setMessage("Large number of methods changed");
			event.setDescription(description);
			event.setType("Large Number of Methods Changed");
			
			EventService.getEventService().saveEvent(event);
		}
	}

	private List<String> getRevisions(String path, long revision) {
		return Main.getSVNMiner().getRevision(path, revision);
	}
	
	@SuppressWarnings("unchecked")
	private void calculateLOCChanged(List<String> revisions) {
		List<String> revisionStrList = new ArrayList<String>();
		List<String> prevRevisionStrList = new ArrayList<String>();
		
		String revisionSplit[] = revisions.get(0).split("\\r?\\n");
		String prevRevisionSplit[] = revisions.get(1).split("\\r?\\n");

		revisionStrList = Arrays.asList(revisionSplit);
		prevRevisionStrList = Arrays.asList(prevRevisionSplit);
		
		Patch patch = DiffUtils.diff(revisionStrList, prevRevisionStrList);
		
		int addedLOCCurrentModification = 0;
		int changedLOCCurrentModification = 0;
		int deletedLOCCurrentModification = 0;
		
		for (Delta delta : patch.getDeltas()) {
			if (delta.getType() == TYPE.INSERT) {
				List<String> addedLines = (List<String>) delta.getRevised().getLines();
				for (String line : addedLines) {
					if(isComment(line)) continue;
					addedLOCCurrentModification++;
					this.addedLOC++;
				}
			} else if (delta.getType() == TYPE.CHANGE) {
				List<String> changedLines = (List<String>) delta.getRevised().getLines();
				for (String line : changedLines) {
					if(isComment(line))	continue;
					changedLOCCurrentModification++;
					this.changedLOC++;
				}
			} else if (delta.getType() == TYPE.DELETE) {
				List<String> deletedLines = (List<String>) delta.getOriginal().getLines();
				for (String line : deletedLines) {
					if(isComment(line)) continue;
					deletedLOCCurrentModification++;
					this.deletedLOC++;
				}
			}
		}
		
		if (this.maxAddedLOC < addedLOCCurrentModification) {
			this.maxAddedLOC = addedLOCCurrentModification;
		}
		if (this.maxChangedLOC < changedLOCCurrentModification) {
			this.maxChangedLOC = changedLOCCurrentModification;
		}
		if (this.maxDeletedLOC < deletedLOCCurrentModification) {
			this.maxDeletedLOC = deletedLOCCurrentModification;
		}
	}
	
	private void calculateMethodsChangedAndCiclomaticComplexity(List<String> revisions) {
		ClassWrapper currentClass = new ClassWrapper(revisions.get(0));
		ClassWrapper previousClass = new ClassWrapper(revisions.get(1));
		
		ArrayList<MethodWrapper> previousMethods = (ArrayList<MethodWrapper>) previousClass.getMethods();
		ArrayList<MethodWrapper> currentMethods = (ArrayList<MethodWrapper>) currentClass.getMethods();
		
		int addedMethodsCurrentModification = 0;
		int changedMethodsCurrentModification = 0;
		int addedComplexityCurrentModification = 0;

		for (MethodWrapper currentMethod : currentMethods) {
			if(!previousMethods.contains(currentMethod)) {
				addedMethodsCurrentModification++;
				this.addedMethods++;
			}
		}
		
		for(MethodWrapper currentMethod : currentMethods) {
			MethodWrapper correspondingPreviousMethod = null;
			for(MethodWrapper previousMethod : previousMethods) {
				if(previousMethod.equals(currentMethod)) {
					correspondingPreviousMethod = previousMethod;
					if(correspondingPreviousMethod.getCyclomaticComplexity() != currentMethod.getCyclomaticComplexity()) {
						int complexity = currentMethod.getCyclomaticComplexity() - correspondingPreviousMethod.getCyclomaticComplexity();
						addedComplexityCurrentModification = complexity;
						this.addedComplexity += complexity;
					} 
				}
			}
			if(correspondingPreviousMethod == null) {
				continue;
			}
			
			List<String> previousMethodBody = Arrays.asList(correspondingPreviousMethod.toString().split("[\r\n]+"));
			List<String> currentMethodBody = Arrays.asList(currentMethod.toString().split("[\r\n]+"));
			Patch patch = DiffUtils.diff(previousMethodBody, currentMethodBody);
			List<Delta> deltas = patch.getDeltas();
			if(deltas.size() > 0) {
				changedMethodsCurrentModification++;
				this.changedMethods++;
			}
		}
		if (this.maxAddedMethods < addedMethodsCurrentModification) {
			this.maxAddedMethods = addedMethodsCurrentModification;
		}
		if (this.maxChangedMethods < changedMethodsCurrentModification) {
			this.maxChangedMethods = changedMethodsCurrentModification;
		}
		if (this.maxAddedComplexity < addedComplexityCurrentModification) {
			this.maxAddedComplexity = addedComplexityCurrentModification;
		}
	}
	
	private boolean isComment(String line) {
		if(line.trim().length() == 0) return true; 

		boolean result;
		Pattern pattern = Pattern.compile("(?<!.+)^//.+$");
		Matcher matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if(result) return true;

		pattern = Pattern.compile("(?<!.+)//(?!.+)");
		matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if(result) return true;

		pattern = Pattern.compile("(?<!.+)\\*(?!.+)");
		matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if(result) return true;

		pattern = Pattern.compile("(?<!.+)^/\\*.+$");
		matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if(result) return true;

		pattern = Pattern.compile("(?<!.+)^\\*.+$");
		matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if(result) return true;

		pattern = Pattern.compile("(?<!.+)^\\s+\\*.+$");
		matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if(result) return true;

		pattern = Pattern.compile("(?<!.+)^\\*+/.+$");
		matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if(result) return true;

		pattern = Pattern.compile("^C:");
		matcher = pattern.matcher(line.trim());

		result = matcher.find();
		if(result) return true;

		return false;
	}

	@Override
	public void updateCSV() {
	}

}
