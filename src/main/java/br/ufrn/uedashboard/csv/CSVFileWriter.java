package br.ufrn.uedashboard.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVFileWriter {
	
	private static final String ABSOLUTE_PATH = "<INSERT HERE YOUR ABSOLUTE PATH>";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	private FileWriter fileWriter;
	
	public CSVFileWriter(String path) {
		this.createFile(path);
	}
	
	private void createFile(String path) {
		try {
			File file = new File(ABSOLUTE_PATH+path+".csv");
			
			final File parent_directory = file.getParentFile();

			if (null != parent_directory) {
			    parent_directory.mkdirs();
			}
			
			if (!file.exists()) {
                file.createNewFile();
            }
			fileWriter = new FileWriter(file.getAbsoluteFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createHeader(String headerContent) {
		try {
			fileWriter.append(headerContent);
			fileWriter.append(NEW_LINE_SEPARATOR);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addLines(List<String> lines) {
		try {
			for (String line : lines) {
				fileWriter.append(line);
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.closeFile();
	}
	
	public void closeFile() {
		try {
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
