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
	
	public static void main(String[] args) throws WekaException, IOException, GitAPIException {
		
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
			wm = new WekaManager(fm.getFullName(), projName);
			if(!wm.fileExists())
				wm.csvToArff();
			else {
				report = "Already exists a arff file for "+projName;
				LOGGER.log(Level.INFO, report);
			}
			
			//setClassifiers
			List<String> classifierList = new ArrayList<>();
			classifierList.add("RandomForest");
			classifierList.add("NaiveBayes");
			classifierList.add("IBk");
			
			//setFeatureSelection
			List<String> featureSelectionList = new ArrayList<>();
			featureSelectionList.add("no_feature_selection");
			featureSelectionList.add("BestFirst");
			
			//setSampling
			List<String> samplingList = new ArrayList<>();
			samplingList.add("no_sampling");
			samplingList.add("Oversampling");
			samplingList.add("Undersampling");
			samplingList.add("SMOTE");
			
			//setCostSensitive
			List<String> costList = new ArrayList<>();
			costList.add("no_cost");
			costList.add("Sensitive_Threshold");
			costList.add("Sensitive_Learning");
			
			wm.walkForward(classifierList, featureSelectionList, samplingList, costList);
		}
		
		
	}

}
