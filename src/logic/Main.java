package logic;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.errors.GitAPIException;

public class Main {
	
	public static void main(String[] args) throws IOException, GitAPIException {
		
		String gitUrl = "https://github.com/apache/avro";
		//String gitUrl = "https://github.com/apache/bookkeeper";
		
		//Parse project name
		String[] splitted = gitUrl.split("/");
		String projName = splitted[splitted.length -1];
		
		GitBoundary gb = new GitBoundary(gitUrl);
		
		List<Commit> commits; 
		Commit commit;
		
		
		ReleaseNameAdapter rna = new ReleaseNameAdapter(0, "release-");
		ReleaseManager rr = new ReleaseManager(projName, gb, rna);
		rr.setupReleases();
		
		List<Release> considered = rr.getConsideredReleases();
		
		
		List<Release> releases = rr.getReleases();
		
		IssueManager im = new IssueManager(projName, rr, gb);
		im.setupIssues();
		
		List<Issue> issues; 
		
		issues = im.getIssues();
		
		Release rel;
		Issue issue;
		List<String> classes;
		List<Commit> issueCommits;
		int i,j;
		List<String> touchedFiles;
		
		/*System.out.println("Issue commits");
		for(i=0; i< issues.size(); i++) {
			issue = issues.get(i);
			System.out.println("Issue "+issue.getIndex()+" key "+issue.getKey());
			/*issueCommits = issue.getCommits();
			touchedFiles = issue.getTouchedFiles();
			for(j=0; j<touchedFiles.size(); j++) {
				System.out.println(touchedFiles.get(j));
			}*/
			
			
			/*for(j=0; j< issueCommits.size(); j++) {
				commit = issueCommits.get(j);
				System.out.println("\tsha "+commit.getSha()+" date "+commit.getDate()+" author "+commit.getDate()+" message "+commit.getMessage());
			}*/
		//}
		//System.out.println("Total "+i);
		
		
		/*System.out.println("Commits");
		for(i=0; i<considered.size(); i++){
			rel = considered.get(i);
			System.out.println(rel.getGitName());
			System.out.println("Sha \t author \t date \t message");
			commits = rel.getCommits();
			for(j=0; j< commits.size(); j++) {
				commit = commits.get(j);
				System.out.printf("%s \t %s \t %s \t %s\n",commit.getSha(), commit.getAuthor(), commit.getDate(), commit.getMessage());
			}
		}*/
		
		/*System.out.println("Issues");
		for(i=0 ; i < issues.size(); i++) {
			issue = issues.get(i);
			System.out.printf("%s \t %s \t %d \t %s\t %d \t\n", issue.getId(), issue.getKey(), issue.getIndex(), issue.getOpeningVersion().getJiraName(), issue.getOpeningVersion().getIndex());
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
		System.out.println("\n\nTotal size "+size);
		
		/*for(i=1; i<=200; i++) {
			System.out.println((int)Math.ceil(i*0.01));
		}*/
		
		System.out.println("END");
	}
}
