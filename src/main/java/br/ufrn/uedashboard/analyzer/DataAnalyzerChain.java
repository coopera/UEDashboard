package br.ufrn.uedashboard.analyzer;

public class DataAnalyzerChain {
	
	private UnusualEventsDataAnalyzer unusualEventsDataAnalyzer;
	
	public DataAnalyzerChain() {
		UnusualEventsDataAnalyzer numberOfChangesToFileAnalyzer = new NumberOfChangesToFileAnalyzer();
		UnusualEventsDataAnalyzer numberOfCodeModificationsAnalyzer = new NumberOfCodeModificationsAnalyzer();
		UnusualEventsDataAnalyzer numberOfDifferentDevelopersModificationAnalyzer = new NumberOfDifferentDevelopersModificationAnalyzer();
		UnusualEventsDataAnalyzer numberOfFileModificationsAnalyzer = new NumberOfFileModificationsAnalyzer();
		UnusualEventsDataAnalyzer timeBetweenCommitsAnalyzer = new TimeBetweenCommitsAnalyzer();
		UnusualEventsDataAnalyzer timeBetweenFileModification = new TimeBetweenFileModificationAnalyzer();
		
		this.unusualEventsDataAnalyzer = numberOfChangesToFileAnalyzer;
		numberOfChangesToFileAnalyzer.setSuccessor(numberOfCodeModificationsAnalyzer);
		numberOfCodeModificationsAnalyzer.setSuccessor(numberOfDifferentDevelopersModificationAnalyzer);
		numberOfDifferentDevelopersModificationAnalyzer.setSuccessor(numberOfFileModificationsAnalyzer);
		numberOfFileModificationsAnalyzer.setSuccessor(timeBetweenCommitsAnalyzer);
		timeBetweenCommitsAnalyzer.setSuccessor(timeBetweenFileModification);
	}
	
	public UnusualEventsDataAnalyzer getUnusualEventsDataAnalyzer() {
		return this.unusualEventsDataAnalyzer;
	}

}
