package br.ufrn.uedashboard.collector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufrn.uedashboard.csv.CSVFileWriter;
import br.ufrn.uedashboard.main.Main;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Modification;
import br.ufrn.uedashboard.services.CommitService;
import br.ufrn.uedashboard.statistics.StatisticalOperations;
import br.ufrn.uedashboard.wrapper.ClassWrapper;
import br.ufrn.uedashboard.wrapper.MethodWrapper;
import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
import difflib.Patch;

public class CodeModifications extends UnusualEventsDataCollector {
	
	private List<String> teamStats;
	
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
	public void collectData() {
		this.commitService = CommitService.getCommitService();
		teamStats = new LinkedList<String>();
		
		List<String> developers = this.commitService.getAllDevelopers();
		for (String developer : developers) {
			List<Commit> allCommitsFromDeveloper = this.commitService.getAllCommitsFromDeveloper(developer);
			calculateCodeModifications(developer, allCommitsFromDeveloper);
		}
		
		writeToCSV(teamStats);
		
		if (this.successor != null) {
			this.successor.collectData();
		}
	}
	
	private void calculateCodeModifications(String developer, List<Commit> commits) {
		List<String> developerModifications = new LinkedList<String>();
		
		int[] addedLOCArray = new int[commits.size()];
		int[] avgAddedLOCArray = new int[commits.size()];
		int[] maxAddedLOCArray = new int[commits.size()];
		
		int[] changedLOCArray = new int[commits.size()];
		int[] avgChangedLOCArray = new int[commits.size()];
		int[] maxChangedLOCArray = new int[commits.size()];
		
		int[] deletedLOCArray = new int[commits.size()];
		int[] avgDeletedLOCArray = new int[commits.size()];
		int[] maxDeletedLOCArray = new int[commits.size()];
		
		int[] addedComplexityArray = new int[commits.size()];
		int[] avgAddedComplexityArray = new int[commits.size()];
		int[] maxAddedComplexityArray = new int[commits.size()];
		
		int[] addedMethodsArray = new int[commits.size()];
		int[] avgAddedMethodsArray = new int[commits.size()];
		int[] maxAddedMethodsArray = new int[commits.size()];
		
		int[] changedMethodsArray = new int[commits.size()];
		int[] avgChangedMethodsArray = new int[commits.size()];
		int[] maxChangedMethodsArray = new int[commits.size()];
		
		int idx=0;
		
		for (Commit commit : commits) {
			String commitData = commit.getDate()+",";
			
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
					System.out.println(modification.getPath());
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
			
			commitData += addedLOC + "," + avgAddedLOC + "," + maxAddedLOC + "," + changedLOC + "," + avgChangedLOC + "," + maxChangedLOC  + "," + deletedLOC + "," + avgDeletedLOC + "," + maxDeletedLOC + "," +
					addedComplexity + "," + avgAddedComplexity + "," + maxAddedComplexity + "," + addedMethods + "," + avgAddedMethods + "," + maxAddedMethods + "," + changedMethods + "," + avgChangedMethods + "," + maxChangedMethods;
			
			addedLOCArray[idx] = addedLOC;
			avgAddedLOCArray[idx] = avgAddedLOC;
			maxAddedLOCArray[idx] = maxAddedLOC;
			
			changedLOCArray[idx] = changedLOC;
			avgChangedLOCArray[idx] = avgChangedLOC;
			maxChangedLOCArray[idx] = maxChangedLOC;
			
			deletedLOCArray[idx] = deletedLOC;
			avgDeletedLOCArray[idx] = avgDeletedLOC;
			maxDeletedLOCArray[idx] = maxDeletedLOC;
			
			addedComplexityArray[idx] = addedComplexity;
			avgAddedComplexityArray[idx] = avgAddedComplexity;
			maxAddedComplexityArray[idx] = maxAddedComplexity;
			
			addedMethodsArray[idx] = addedMethods;
			avgAddedMethodsArray[idx] = avgAddedMethods;
			maxAddedMethodsArray[idx] = maxAddedMethods;
			
			changedMethodsArray[idx] = changedMethods;
			avgChangedMethodsArray[idx] = avgChangedMethods;
			maxChangedMethodsArray[idx] = maxChangedMethods;
			
			idx++;		
					
			developerModifications.add(commitData);
		}
		writeToCSV(developer, developerModifications);
		getStatsCodeModification(developer, commits.size(), addedLOCArray, avgAddedLOCArray, maxAddedLOCArray,
				changedLOCArray, avgChangedLOCArray, maxChangedLOCArray, 
				deletedLOCArray, avgDeletedLOCArray, maxDeletedLOCArray, 
				addedComplexityArray, avgAddedComplexityArray, maxAddedComplexityArray,
				addedMethodsArray, avgAddedMethodsArray, maxAddedMethodsArray,
				changedMethodsArray, avgChangedMethodsArray, maxChangedMethodsArray);
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
	
	private void getStatsCodeModification(String developer, int totalCommits, int[] addedLOC, int[] avgAddedLOC, int[] maxAddedLOC, 
			int[] changedLOC, int[] avgChangedLOC, int[] maxChangedLOC, int[] deletedLOC, int[] avgDeletedLOC, int[] maxDeletedLOC, 
			int[] addedComplexity, int[] avgAddedComplexity, int[] maxAddedComplexity, 
			int[] addedMethods, int[] avgAddedMethods, int[] maxAddedMethods, int[] changedMethods, int[] avgChangedMethods, int[] maxChangedMethods) {
		
		double meanAddedLOC = StatisticalOperations.mean(addedLOC);
		double stdAddedLOC = StatisticalOperations.standardDeviation(addedLOC);
		double meanAvgAddedLOC = StatisticalOperations.mean(avgAddedLOC);
		double stdAvgAddedLOC = StatisticalOperations.standardDeviation(avgAddedLOC);
		double meanMaxAddedLOC = StatisticalOperations.mean(maxAddedLOC);
		double stdMaxAddedLOC = StatisticalOperations.standardDeviation(maxAddedLOC);
		
		double meanChangedLOC = StatisticalOperations.mean(changedLOC);
		double stdChangedLOC = StatisticalOperations.standardDeviation(changedLOC);
		double meanAvgChangedLOC = StatisticalOperations.mean(avgChangedLOC);
		double stdAvgChangedLOC = StatisticalOperations.standardDeviation(avgChangedLOC);
		double meanMaxChangedLOC = StatisticalOperations.mean(maxChangedLOC);
		double stdMaxChangedLOC = StatisticalOperations.standardDeviation(maxChangedLOC);
		
		double meanDeletedLOC = StatisticalOperations.mean(deletedLOC);
		double stdDeletedLOC = StatisticalOperations.standardDeviation(deletedLOC);
		double meanAvgDeletedLOC = StatisticalOperations.mean(avgDeletedLOC);
		double stdAvgDeletedLOC = StatisticalOperations.standardDeviation(avgDeletedLOC);
		double meanMaxDeletedLOC = StatisticalOperations.mean(maxDeletedLOC);
		double stdMaxDeletedLOC = StatisticalOperations.standardDeviation(maxDeletedLOC);
		
		double meanAddedComplexity = StatisticalOperations.mean(addedComplexity);
		double stdAddedComplexity = StatisticalOperations.standardDeviation(addedComplexity);
		double meanAvgAddedComplexity = StatisticalOperations.mean(avgAddedComplexity);
		double stdAvgAddedComplexity = StatisticalOperations.standardDeviation(avgAddedComplexity);
		double meanMaxAddedComplexity = StatisticalOperations.mean(maxAddedComplexity);
		double stdMaxAddedComplexity = StatisticalOperations.standardDeviation(maxAddedComplexity);
		
		double meanAddedMethods = StatisticalOperations.mean(addedMethods);
		double stdAddedMethods = StatisticalOperations.standardDeviation(addedMethods);
		double meanAvgAddedMethods = StatisticalOperations.mean(avgAddedMethods);
		double stdAvgAddedMethods = StatisticalOperations.standardDeviation(avgAddedMethods);
		double meanMaxAddedMethods = StatisticalOperations.mean(maxAddedMethods);
		double stdMaxAddedMethods = StatisticalOperations.standardDeviation(maxAddedMethods);
		
		double meanChangedMethods = StatisticalOperations.mean(changedMethods);
		double stdChangedMethods = StatisticalOperations.standardDeviation(changedMethods);
		double meanAvgChangedMethods = StatisticalOperations.mean(avgChangedMethods);
		double stdAvgChangedMethods = StatisticalOperations.standardDeviation(avgChangedMethods);
		double meanMaxChangedMethods = StatisticalOperations.mean(maxChangedMethods);
		double stdMaxChangedMethods = StatisticalOperations.standardDeviation(maxChangedMethods);
		
		String line = developer+","+totalCommits+","+meanAddedLOC+","+meanAvgAddedLOC+","+meanMaxAddedLOC+","+stdAddedLOC+","+stdAvgAddedLOC+","+stdMaxAddedLOC+","
				+meanChangedLOC+","+meanAvgChangedLOC+","+meanMaxChangedLOC+","+stdChangedLOC+","+stdAvgChangedLOC+","+stdMaxChangedLOC+","
				+meanDeletedLOC+","+meanAvgDeletedLOC+","+meanMaxDeletedLOC+","+stdDeletedLOC+","+stdAvgDeletedLOC+","+stdMaxDeletedLOC+","
				+meanAddedComplexity+","+meanAvgAddedComplexity+","+meanMaxAddedComplexity+","+stdAddedComplexity+","+stdAvgAddedComplexity+","+stdMaxAddedComplexity+","
				+meanAddedMethods+","+meanAvgAddedMethods+","+meanMaxAddedMethods+","+stdAddedMethods+","+stdAvgAddedMethods+","+stdMaxAddedMethods+","
				+meanChangedMethods+","+meanAvgChangedMethods+","+meanMaxChangedMethods+","+stdChangedMethods+","+stdAvgChangedMethods+","+stdMaxChangedMethods;
		
		teamStats.add(line);
		
	}
	
	private void writeToCSV(String developer, List<String> lines) {
		this.csvWriter = new CSVFileWriter("Code Modification/"+developer);
		this.csvWriter.createHeader("Commit,LOC Added,Avg LOC Added,Max LOC Added,LOC Changed,Avg LOC Changed,Max LOC Changed,"
				+ "LOC Deleted,Avg LOC Deleted,Max LOC Deleted,Added Code Complexity,Avg Added Complexity,Max Added Complexity,"
				+ "Added Methods,Avg Added Methods,Max Added Methods,Changed Methods,Avg Changed Methods,Max Changed Methods");
		this.csvWriter.addLines(lines);
	}
	
	private void writeToCSV(List<String> lines) {
		this.csvWriter = new CSVFileWriter("Code Modification/team");
		this.csvWriter.createHeader("Developer,Total Commits,Added LOC Mean,Avg Added LOC Mean,Max Added LOC Mean,Changed LOC Mean,Avg Changed LOC Mean,Max Changed LOC Mean,Deleted LOC Mean,Avg Deleted LOC Mean,Max Deleted LOC Mean,"
				+ "Added LOC Standard Deviation,Avg Added LOC Standard Deviation,Max Added LOC Standard Deviation,Changed LOC Standard Deviation,Avg Changed LOC Standard Deviation,Max Changed LOC Standard Deviation,Deleted LOC Standard Deviation,Avg Deleted LOC Standard Deviation,Max Deleted LOC Standard Deviation,"
				+ "Code Complexity Mean,Avg Code Complexity Mean,Max Code Complexity Mean,Code Complexity Standard Deviation,Avg Code Complexity Standard Deviation,Max Code Complexity Standard Deviation,"
				+ "Added Methods Mean,Avg Added Methods Mean,Max Added Methods Mean,Added Methods Standard Deviation,Avg Added Methods Standard Deviation,Max Added Methods Standard Deviation,"
				+ "Changed Methods Mean,Avg Changed Methods Mean,Max Changed Methods Mean,Changed Methods Standard Deviation,Avg Changed Methods Standard Deviation,Max Changed Methods Standard Deviation");
		this.csvWriter.addLines(lines);
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

}
