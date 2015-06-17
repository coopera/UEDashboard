package br.ufrn.uedashboard.collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufrn.uedashboard.csv.CSVFileWriter;
import br.ufrn.uedashboard.services.CommitService;

public class TimeBetweenFileModification extends UnusualEventsDataCollector {

	@Override
	public void collectData() {
		this.commitService = CommitService.getCommitService();
		getFilesLastModificationDate();
		
		if (this.successor != null) {
			this.successor.collectData();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void getFilesLastModificationDate() {
		HashMap<String,Date> fileLastModificationDate = this.commitService.getFileLastModificationDate();
		List<String> lines = new ArrayList<String>();
		
		Iterator<Entry<String, Date>> iterator = fileLastModificationDate.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry pairs = (Map.Entry) iterator.next();
			lines.add(pairs.getKey().toString()+","+pairs.getValue().toString());
		}
		
		writeToCSV(lines);
	}
	
	public void writeToCSV(List<String> lines) {
		this.csvWriter = new CSVFileWriter("File Modification Date/files");
		this.csvWriter.createHeader("File,Last Modification");
		this.csvWriter.addLines(lines);
	}

}
