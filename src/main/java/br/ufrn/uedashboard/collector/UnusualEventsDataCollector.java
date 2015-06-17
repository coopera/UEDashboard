package br.ufrn.uedashboard.collector;

import br.ufrn.uedashboard.csv.CSVFileWriter;
import br.ufrn.uedashboard.services.CommitService;
import br.ufrn.uedashboard.util.DateUtil;

public abstract class UnusualEventsDataCollector {
	
	protected UnusualEventsDataCollector successor;
	
	protected CommitService commitService;
	
	protected CSVFileWriter csvWriter;
	
	protected DateUtil dateUtil;
	
	public void setSuccessor(UnusualEventsDataCollector successor) {
		this.successor = successor;
	}
	
	public abstract void collectData();

}
