package logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class FileManager {
	
	private String fullName;
	
	private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());
	private static final String DIR_NAME = "output";
	private static final List<String> COLUMNS = new ArrayList<>(Arrays.asList("Release number","Java file", "Size", "LOC touched", "NR", "NAuth", "LOC added","MAX LOC added", "AVG LOC added", "Churn", "MAX Churn", "AVG Churn", "ChgSetSize", "MAX ChgSet", "AVG ChgSet", "Age","WeightedAge" ,"Buggy"));
	private static final String SEPARATOR = ", ";
	private static final char NEW_LINE = '\n';
	private static final String FILE_EXT = ".csv";
	
	public FileManager(String fileName) {
		//check if output directory exists
		File dir = new File(DIR_NAME);
		if(!dir.isDirectory()) {
			dir.mkdir();
			LOGGER.log(Level.INFO, "Generating output directory");
		}else 
			LOGGER.log(Level.INFO, "Output directory already exists");
			
		this.fullName = DIR_NAME+"/"+fileName+FILE_EXT;
		
	}
	
	public boolean fileExists() {
		File checkFile = new File(fullName);
		return checkFile.exists();
	}
	
	public void generateCsv(List<JavaFile> fileList) throws IOException{
		
		File csvFile = new File(fullName);
		String report;
		if(!csvFile.exists()) {
			report = "Generating "+this.fullName;
			LOGGER.log(Level.INFO, report);
			
			StringBuilder sb = new StringBuilder();
			
			//Add columns' names
			for(String column: COLUMNS) {
				sb.append(column);
				sb.append(SEPARATOR);
			}
			sb.append(NEW_LINE);
			
			//Add java file data
			for(JavaFile file:fileList) {
				//release index
				sb.append(file.getReleaseIndex());
				sb.append(SEPARATOR);
				
				//file name
				sb.append(file.getName());
				sb.append(SEPARATOR);
				
				//size
				sb.append(file.getSize());
				sb.append(SEPARATOR);
				
				//LOC touched
				sb.append(file.getTouchedLOC());
				sb.append(SEPARATOR);
				
				//NR
				sb.append(file.getCommitCount());
				sb.append(SEPARATOR);
				
				//NAuth
				sb.append(file.getAuthorCount());
				sb.append(SEPARATOR);
				
				//LOC added
				sb.append(file.getTotalAddedLOC());
				sb.append(SEPARATOR);
				
				//MAX LOC added
				sb.append(file.getMaxAddedLOC());
				sb.append(SEPARATOR);
				
				//AVG LOC added
				sb.append(file.getAvgAddedLOC());
				sb.append(SEPARATOR);
				
				//churn
				sb.append(file.getTotalChurn());
				sb.append(SEPARATOR);
				
				//MAX churn
				sb.append(file.getMaxChurn());
				sb.append(SEPARATOR);
				
				//AVG churn 
				sb.append(file.getAvgChurn());
				sb.append(SEPARATOR);
				
				//ChgSetSize
				sb.append(file.getTotalChgSetSize());
				sb.append(SEPARATOR);
				
				//MAX chgSet
				sb.append(file.getMaxChgSetSize());
				sb.append(SEPARATOR);
				
				//AVG chgSet
				sb.append(file.getAvgChgSetSize());
				sb.append(SEPARATOR);
				
				//age
				sb.append(file.getAge());
				sb.append(SEPARATOR);
				
				//weigthed age
				sb.append(file.getWeightedAge());
				sb.append(SEPARATOR);
				
				//buggy
				if(file.isBuggy().booleanValue()) {
					sb.append("Yes");
				}else {
					sb.append("No");
				}
				sb.append(SEPARATOR);
				
				//new line
				sb.append(NEW_LINE);
				
			}
			
			
			//generate csv
			try(PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(csvFile)));){
				writer.write(sb.toString());
			}
			LOGGER.log(Level.INFO, "Csv file generated");
			
			
			
		}else {
			report = "File already exists";
			LOGGER.log(Level.INFO, report);
		}
		
	}
	
	
}
