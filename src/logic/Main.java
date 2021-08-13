package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jgit.api.errors.GitAPIException;


public class Main {
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public static void main(String[] args) throws IOException, GitAPIException {
		
		List<String> urlList = new ArrayList<>(Arrays.asList("https://github.com/apache/avro", "https://github.com/apache/bookkeeper"));
		List<JavaFile> finalList; 
		String[] splitted;
		String projName;
		FileManager fm;
		GitBoundary gb;
		ReleaseNameAdapter rna;
		ReleaseManager rm;
		IssueManager im;
		WekaManager wm;
		
		String report;
		
		for(String gitUrl:urlList) {
			//Parse project name
			splitted = gitUrl.split("/");
			projName = splitted[splitted.length -1];
		
			//FileManager setup
			fm = new FileManager(projName);
			
			if(!fm.fileExists()) {
				
				//Generate classes
				gb = new GitBoundary(gitUrl);
				rna = new ReleaseNameAdapter(0, "release-");
				rm = new ReleaseManager(projName, gb, rna);
				im = new IssueManager(projName, rm, gb);
				
				//Setup all
				rm.setupReleases();
				im.setupIssues();
				
				//Generate csv
				finalList = rm.getFinalFileList();
				fm.generateCsv(finalList);
				
			}else {
				report = "Skipping data retrieve of "+projName+". Csv file already exists";
				LOGGER.log(Level.INFO, report);
			}
			
			//setup weka manager and generate arff
			wm = new WekaManager(fm.getFullName());
			if(!wm.fileExists())
				wm.csvToArff();
			else {
				report = "Already exists a arff file for "+projName;
				LOGGER.log(Level.INFO, report);
			}
		}
		
		
	}

}
