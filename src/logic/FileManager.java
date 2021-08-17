package logic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class FileManager {
	
	private String fullName;
	
	private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());
	private static final String DIR_NAME = "output";
	private static final String[] COLUMNS = new String[] {"ReleaseNumber","JavaFile", "Size", "LOCtouched", "NR", "NAuth", "LOCadded","MAX_LOCadded", "AVG_LOCadded", "Churn", "MAX_Churn", "AVG_Churn", "ChgSetSize", "MAX_ChgSet", "AVG_ChgSet", "Age","WeightedAge" ,"Buggy"};
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
	
	public String getFullName() {
		return this.fullName;
	}
	
	public boolean fileExists() {
		File checkFile = new File(fullName);
		return checkFile.exists();
	}
	
	public String escapeSpecialCharacters(String data) {
	    String escapedData = data.replaceAll("\\R", " ");
	    if (data.contains(",") || data.contains("\"") || data.contains("'")) {
	        data = data.replace("\"", "\"\"");
	        escapedData = "\"" + data + "\"";
	    }
	    return escapedData;
	}
	
	public String convertToCSV(String[] data) {
		return Stream.of(data).map(this::escapeSpecialCharacters).collect(Collectors.joining(","));
	}
	
	public void toCsv(List<String[]> dataLines) throws IOException {
	    File csvOutputFile = new File(fullName);
	    if(!csvOutputFile.createNewFile()) 
	    	LOGGER.log(Level.WARNING,"File already exists");
		else {
			try (PrintWriter pw = new PrintWriter (csvOutputFile)){
				dataLines.stream().map(this::convertToCSV).forEach(pw::println);
			}
			LOGGER.log(Level.INFO, "File created");
		}
	}
	
	public void generateCsv(List<JavaFile> fileList) throws IOException{
		List<String[]> dataToConvert = new ArrayList<>();
		dataToConvert.add(COLUMNS);
		
		//file metrics
		Integer releaseIndex;
		String fileName;
		Integer size;
		Long touchedLOC;
		
		Integer commitCount;
		Integer authorCount;
		
		Integer addedLOC;
		Integer maxAdded;
		Double avgAdded;
		
		Integer churn;
		Integer maxChurn;
		Double avgChurn;
		
		Integer chgSetSize;
		Integer maxChgSet;
		Double avgChgSet;
		
		Long age; 
		Long weightedAge;
		
		String buggy;
		
		for(JavaFile file:fileList) {
			//release index
			releaseIndex = file.getReleaseIndex();
					
			//file name
			fileName = file.getName();

			//size
			size = file.getSize();
					
			//LOC touched
			touchedLOC = file.getTouchedLOC();
					
			//NR
			commitCount = file.getCommitCount();
					
			//NAuth
			authorCount = file.getAuthorCount();
					
			//LOC added
			addedLOC = file.getTotalAddedLOC();
					
			//MAX LOC added
			maxAdded = file.getMaxAddedLOC();
			
			//AVG LOC added
			avgAdded = file.getAvgAddedLOC();
			
			//churn
			churn = file.getTotalChurn();
						
			//MAX churn
			maxChurn = file.getMaxChurn();
							
			//AVG churn 
			avgChurn = file.getAvgChurn();
								
			//ChgSetSize
			chgSetSize = file.getTotalChgSetSize();
							
			//MAX chgSet
			maxChgSet = file.getMaxChgSetSize();
								
			//AVG chgSet
			avgChgSet = file.getAvgChgSetSize();
								
			//age
			age = file.getAge();
								
			//weighted age
			weightedAge = file.getWeightedAge();
								
			//buggy
			if(file.isBuggy().booleanValue()) {
				buggy = "Yes";
			}else {
				buggy = "No";
			}
			
			dataToConvert.add(new String[] {releaseIndex.toString(), fileName, size.toString(), touchedLOC.toString(), commitCount.toString(), authorCount.toString(), addedLOC.toString(), maxAdded.toString(), avgAdded.toString(), churn.toString(), maxChurn.toString(), avgChurn.toString(), chgSetSize.toString(), maxChgSet.toString(), avgChgSet.toString(), age.toString(), weightedAge.toString(), buggy});			
		
		}
		
		try {
			toCsv(dataToConvert);
	
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}	
	
}
