package br.ufrn.uedashboard.analyzer;

import br.ufrn.uedashboard.csv.CSVFileReader;
import br.ufrn.uedashboard.csv.CSVFileWriter;
import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.util.DateUtil;

public abstract class UnusualEventsDataAnalyzer {
	
	protected UnusualEventsDataAnalyzer successor;
	
	protected CSVFileWriter csvWriter;
	
	protected CSVFileReader csvFileReader;
	
	protected DateUtil dateUtil;
	
	public void setSuccessor(UnusualEventsDataAnalyzer successor) {
		this.successor = successor;
	}
	
	public void analyze(Commit commit) {
		this.dateUtil = new DateUtil();
		this.csvFileReader = new CSVFileReader();
		
		checkIfOutlier(commit);
		updateCSV();
		
		if (this.successor != null) {
			successor.analyze(commit);
		}
	}
	
	public abstract void checkIfOutlier(Commit commit);

	public abstract void updateCSV();
}
