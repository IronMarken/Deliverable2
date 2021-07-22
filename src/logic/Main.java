package logic;

import java.io.IOException;
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
		ReleaseManager rr = new ReleaseManager(projName, gb);
		rr.setAdapter(rna);
		rr.retrieveReleases();
		List<Release> considered = rr.getConsideredReleases();
		
		
		IssueManager im = new IssueManager(projName, rr);
		im.retrieveIssues();
		List<Issue> issues = im.getIssues();
		
		/*System.out.println("RELEASED\n\n");
		System.out.printf("Index \t VersionID \t Version Name \t Git Name \t SHA \t Data \t\n");
		List<Release> releases = rr.getReleases();*/
		rr.retrieveClasses();
		rr.retrieveCommit();
		
		Release rel;
		Issue issue;
		List<String> classes;
		int i,j;
		
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
		
		//get release by name
		/*rel = rr.getReleaseByGitName("release-1.11.0");
		if(rel == null) {
			System.out.println("Is null");
		}*/
		
		
		/*System.out.println("Issues");
		for(i=0 ; i < issues.size(); i++) {
			issue = issues.get(i);
			System.out.printf("%s \t %s \t %d\t\n", issue.getId(), issue.getKey(), issue.getIndex());
		}
		
		System.out.println("END");*/
		
		/*System.out.println("considered releases");
		for(i=0 ; i < considered.size(); i++) {
			rel = releases.get(i);
			System.out.printf("%d \t %s \t %s \t %s \t %s \t\n", rel.getIndex(), rel.getReleaseID(), rel.getJiraName(), rel.getGitName(), rel.getSha());
		}
		
		System.out.println("SIZE RELEASED"+releases.size());
		for (i = 0; i< releases.size(); i++){
			rel = releases.get(i);
			System.out.printf("%d \t %s \t %s \t %s \t%s \t %s \t\n", rel.getIndex(), rel.getReleaseID(), rel.getJiraName(), rel.getGitName(), rel.getSha(), rel.getReleaseDate());
		}*/
		
		/*System.out.println("Release git name \t Class name\n");
		for(i = 0; i < releases.size(); i++) {
			rel = releases.get(i);
			classes = rel.getClasses();
			/*if(rel.getGitName().equals( "release-1.10.0"))*/
				/*for(j = 0; j < classes.size(); j++ ) {
					System.out.printf("%s \t %s\n", rel.getGitName(),classes.get(j));
				}
			//System.out.printf("%s \t %d\n", rel.getGitName(), classes.size());
		}*/
		
		
		
		/*int size = releases.size();
		
		System.out.println("\n\nUNRELEASED\n\n");
		System.out.printf("Index \t VersionID \t Version Name \t Git Name \t SHA \t\n");
		releases = rr.getUnreleased();
		for ( i = 0; i< releases.size(); i++){
			rel = releases.get(i);
			System.out.printf("%d \t %s \t %s \t\n", rel.getIndex(), rel.getReleaseID(), rel.getJiraName(), rel.getGitName(), rel.getSha());
		}
		
		System.out.println("SIZE UNRELEASED "+ releases.size());

		size += releases.size();
		System.out.println("\n\nTotal size "+size);*/
		/*String key = "2640";
		String projectName = "bookkeeper";
		String value = "dsad BOOKKEEPER #2640: dfkjshfsfhskfhfksfhsd";
		Pattern pattern = Pattern.compile("(ISSUE|"+projectName.toUpperCase()+")(-| #)"+key+":",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(value);
		if(matcher.find())
			System.out.println("Found");
		else 
			System.out.println("Not found");*/
		
	}
}
