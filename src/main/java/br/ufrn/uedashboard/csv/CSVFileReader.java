package br.ufrn.uedashboard.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class CSVFileReader {
	
	private static final String ABSOLUTE_PATH = "<INSERT HERE YOUR ABSOLUTE PATH>";
	
	private String csvFile;
	private BufferedReader br = null;
	
	public BufferedReader getBufferedReader(String path) {
		csvFile = ABSOLUTE_PATH+path;
		try {
			br = new BufferedReader(new FileReader(csvFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return br;
	}
}
