package logic;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.errors.GitAPIException;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

public class DebugMain {
	
	public static void main(String[] args) throws Exception, IOException, GitAPIException {
		
		//String gitUrl = "https://github.com/apache/avro";
		/*String gitUrl = "https://github.com/apache/bookkeeper";
		
		//Parse project name
		String[] splitted = gitUrl.split("/");
		String projName = splitted[splitted.length -1];
		
		GitBoundary gb = new GitBoundary(gitUrl);
		
		List<Commit> commits; 
		Commit commit;
		
		FileManager fm = new FileManager(projName);
		
		ReleaseNameAdapter rna = new ReleaseNameAdapter(0, "release-");
		ReleaseManager rr = new ReleaseManager(projName, gb, rna);
		rr.setupReleases();
		
		List<Release> considered = rr.getConsideredReleases();
		
		
		List<Release> releases = rr.getReleases();
		
		IssueManager im = new IssueManager(projName, rr, gb);
		im.setupIssues();
		
		
		fm.generateCsv(rr.getFinalFileList());
		WekaManager wm = new WekaManager(fm.getFullName()); 
		
		
		List<Issue> issues; 
		
		issues = im.getIssues();
		
		Release rel;
		Issue issue;
		List<String> classes;
		List<Commit> issueCommits;
		int i,j;
		List<String> touchedFiles;
		
		long touchedLOC;
		int commitCount;
		List<Integer> addedList;
		List<Integer> churnList;
		List<Integer> chgSetSize;
		
		List<JavaFile> finalList = rr.getFinalFileList();
		fm.generateCsv(finalList);
		
		rel = considered.get(1);
		JavaFile jf = rel.getClassByName("bookkeeper-server/src/main/java/org/apache/bookkeeper/bookie/GarbageCollectorThread.java");
		
		System.out.println(jf.getAvgAddedLOC());
		
		/*System.out.println("Starting");
		int total = 0;
		int count = 0;
		
		for(Release release: considered) {
			total = 0;
			count = 0;
			for(JavaFile file: release.getClasses()) {
				total ++;
				touchedLOC = file.getTouchedLOC();
				/*if(touchedLOC == 0) {
					System.out.println(release.getGitName()+" file "+file.getName()+" with no touched LOC");
				}*/
				
				//commitCount = file.getCommitCount();
				/*if(commitCount == 0) {
					System.out.println(release.getGitName()+" file "+file.getName()+" with no commit count");
				}*/
				
				/*if(authorList.isEmpty()) {
					System.out.println(release.getGitName()+" file "+file.getName()+" with no commit authors");
				}*/
				
				//addedList = file.getAddedList();
				/*if(addedList.isEmpty()) {
					System.out.println(release.getGitName()+" file "+file.getName()+" with no added list");
				}*/
				
				//churnList = file.getChurnList();
				/*if(churnList.isEmpty()) {
					System.out.println(release.getGitName()+" file "+file.getName()+" with no churn");
				}*/
				
				//chgSetSize = file.getChgSetSizeList();
				/*if(chgSetSize.isEmpty()) {
					System.out.println(release.getGitName()+" file "+file.getName()+" with no chgSetSize");
				}*/
				/*if(commitCount == 0 && touchedLOC == 0)
					count ++;
				if(commitCount != 0 && touchedLOC == 0) {
					System.out.println("Problema "+release.getGitName()+" file "+file.getName());
				}
				
			}
			System.out.println(release.getGitName()+" count "+count+" Total "+total);
		}
		
		/*JavaFile jf = new JavaFile("test");
		jf.addAddedCount(2);
		jf.addAddedCount(5);
		jf.addAddedCount(5);
		jf.addAddedCount(1);
		
		jf.addAuthor("ciao");
		jf.addAuthor("mi");
		jf.addAuthor("chiamo");
		jf.addAuthor("ciao");
		
		System.out.println("sum "+jf.getTotalAddedLOC()+" max "+jf.getMaxAddedLOC()+" avg "+jf.getAvgAddedLOC());
		System.out.println("author count "+jf.getAuthorCount());*/
		
		
		/*List<DataFile> dfList = gb.getTouchedFileWithData("8dd55dbac12103c4e81be199a7d819e8e8fb5b56");
		for(DataFile df : dfList) {
			System.out.println("name "+df.getName()+" added "+df.getAdded()+" deleted "+df.getDeleted()+" chgSetSize "+df.getChgSetSize());
		}*/
		
		/*System.out.println("Issue commits");
		for(i=0; i< issues.size(); i++) {
			issue = issues.get(i);
			System.out.println("Issue "+issue.getIndex()+" key "+issue.getKey());
			issueCommits = issue.getCommits();
			touchedFiles = issue.getTouchedFiles();
			for(j=0; j<touchedFiles.size(); j++) {
				System.out.println(touchedFiles.get(j));
			}
			
			
			for(j=0; j< issueCommits.size(); j++) {
				commit = issueCommits.get(j);
				System.out.println("\tsha "+commit.getSha()+" date "+commit.getDate()+" author "+commit.getDate()+" message "+commit.getMessage());
			}
		}*/
		//System.out.println("Total "+i);
		
		
		/*System.out.println("Commits");
		for(i=0; i<considered.size(); i++){
			rel = considered.get(i);
			System.out.println(rel.getGitName());
			//System.out.println("Sha \t author \t date \t message");
			commits = rel.getCommits();
			if(commits.isEmpty())
				System.out.println("rel "+rel.getGitName()+" senza commit");
			else
				System.out.println("rel "+rel.getGitName()+" "+commits.size());
			/*for(j=0; j< commits.size(); j++) {
				commit = commits.get(j);
				System.out.printf("%s \t %s \t %s \t %s\n",commit.getSha(), commit.getAuthor(), commit.getDate(), commit.getMessage());
			}*/
		//}
		
		/*System.out.println("Issues");
		for(i=0 ; i < issues.size(); i++) {
			issue = issues.get(i);
			System.out.printf("%s \t %s \t %d \t %s\t %d \t\n", issue.getId(), issue.getKey(), issue.getIndex(), issue.getOpeningVersion().getJiraName(), issue.getOpeningVersion().getIndex());
		}*/
		
		/*rel = considered.get(0);
		System.out.println(rel.getGitName());
		for(JavaFile file: rel.getClasses()) {
			System.out.println(file.getName()+" "+file.isBuggy().toString()+" "+file.getSize().toString());
		}*/
		
		/*for(Release release:considered) {
			System.out.println("\n"+release.getGitName()+" "+release.getCommits().size()+" rel date "+release.getReleaseDate().toString()+" first commit "+release.getCommits().get(0).getDate()+ " last commit "+release.getCommits().get(release.getCommits().size()-1).getDate().toString()+"\n");
		}*/
		
		
		/*System.out.println("considered releases");
		for(i=0 ; i < considered.size(); i++) {
			rel = releases.get(i);
			System.out.printf("%d \t %s \t %s \t %s  \t\n", rel.getIndex(), rel.getReleaseID(), rel.getJiraName(), rel.getGitName());
		}
		
		System.out.println("SIZE RELEASED "+releases.size());
		System.out.println("RELEASED\n\n");
		System.out.printf("Index \t VersionID \t Version Name \t Git Name \t Data \t\n");
		for (i = 0; i< releases.size(); i++){
			rel = releases.get(i);
			System.out.printf("%d \t %s \t %s \t %s \t%s  \t\n", rel.getIndex(), rel.getReleaseID(), rel.getJiraName(), rel.getGitName(), rel.getReleaseDate());
		}
		
		
		int size = releases.size();
		
		System.out.println("\n\nUNRELEASED\n\n");
		System.out.printf("Index \t VersionID \t Version Name \t Git Name \t\n");
		releases = rr.getUnreleased();
		for ( i = 0; i< releases.size(); i++){
			rel = releases.get(i);
			System.out.printf("%d \t %s \t %s \t\n", rel.getIndex(), rel.getReleaseID(), rel.getJiraName());
		}
		
		System.out.println("SIZE UNRELEASED "+ releases.size());

		size += releases.size();
		System.out.println("\n\nTotal size "+size);*/
		
		/*Scanner sc = new Scanner(new File("output/bookkeeper.csv"));
		
		sc.useDelimiter("\n");
		int lineC = 0;
		String string;
		while (sc.hasNext()){  
			lineC++;
			string = sc.next();
			if(lineC == 255 )
				System.out.println(string);  
		}   
		sc.close();   */
		
		DataSource source = new DataSource("output/bookkeeper.arff");
		Instances data = source.getDataSet();
		System.out.println(data);
		//int numReleases = data.attribute(0).numValues();
		int numReleases = data.numAttributes();
		System.out.println("bookkeeper "+numReleases);
		
		
		System.out.println("END");
	}
}
