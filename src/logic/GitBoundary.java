package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

public class GitBoundary {
	
	private File workingCopy;
	private static final Logger LOGGER = Logger.getLogger(GitBoundary.class.getName());
	private static final String DATE_FORMAT = "--date=iso";
	private static final String DATE = "--pretty=format:%cd";
	private static final String COMMIT_FORMAT = "--pretty=format:%H---%s---%an---%cd---";
	private static final String ALL_OPT = "--all";
	private static final String NO_MERGE_OPT = "--no-merges";
	private String projectName;
	
	public GitBoundary(String gitUrl) throws GitAPIException, IOException {
		
		//Parse project name
		String[] splitted = gitUrl.split("/");
		this.projectName = splitted[splitted.length -1];
		
		LOGGER.log(Level.INFO, this.projectName);
		
		//check repo dir exists
		File localDir = new File ("repo");
		if( !localDir.isDirectory()) {
			if( !localDir.mkdir() )
				LOGGER.log(Level.WARNING, "Dir not created");
			else 
				LOGGER.log(Level.INFO, "Dir created");
		}		
		
		//clone if working copy doesn't exist or pull it
		this.workingCopy = new File("repo/"+ projectName);
		
		if(!this.workingCopy.exists()) {
			//clone
			LOGGER.log(Level.WARNING,"Cloning project please wait...");
			Git.cloneRepository().setURI(gitUrl).setDirectory(this.workingCopy).call();
			LOGGER.log(Level.INFO,"Project cloned");
		}else
			LOGGER.log(Level.INFO, "Project exists pulling");
			//pull
			this.pull();		
	}
	
	
	private void pull() throws IOException, GitAPIException {
		
		Repository repo = new FileRepository(this.workingCopy+ "/.git");
		Git git = new Git(repo);
		try {
			PullCommand pullCmd = git.pull();
			PullResult result = pullCmd.call();
		
			if(!result.isSuccessful())
				LOGGER.log(Level.WARNING,"Pull failed");
		
			LOGGER.log(Level.INFO, "Pull successful");
		} finally {
			git.close();
		}
	}
	
	public LocalDateTime getDate(String name, boolean isRelease ) throws IOException{
		
		Process process;
		
		if(isRelease)
			process = Runtime.getRuntime().exec(new String[] {"git", "log", name, "-1", DATE ,DATE_FORMAT }, null, this.workingCopy);
		else
			process = Runtime.getRuntime().exec(new String[] {"git", "log", "--diff-filter=A", DATE ,DATE_FORMAT, "--",name }, null, this.workingCopy);
		BufferedReader reader = new BufferedReader (new InputStreamReader (process.getInputStream()));
		String line;
		String date = null;
		LocalDateTime dateTime = null;
		while((line = reader.readLine()) != null) {
			date = line;
				
			//get Date from full line
			date = date.split(" ")[0];
				
			LocalDate ld = LocalDate.parse(date);
			dateTime = ld.atStartOfDay();
			
		}		
		
		return dateTime;
	}
	
	
	public List<String> getReleaseClasses(String gitName) throws IOException{
		List<String> classes = new ArrayList<>();
		
		Process process = Runtime.getRuntime().exec(new String[] {"git", "ls-tree", "-r", gitName, "--name-only"}, null, this.workingCopy);
		BufferedReader reader = new BufferedReader (new InputStreamReader (process.getInputStream()));
		String line;
		String className = null;
		
		while((line = reader.readLine()) != null) {
			className = line;
			
			//remove last \n
			className = className.split("\n")[0]; 
			if(className.endsWith(".java")) 
				classes.add(className);
		}
		Collections.sort(classes);
		return classes;
	}
	
	public List<Commit> getReleaseCommits(LocalDateTime afterDate, LocalDateTime beforeDate) throws IOException{

		List<Commit> commits = new ArrayList<>();
		//managing commits with same date of the release 
		
		LocalDateTime before = beforeDate.plusDays(1);
		
		String beforeString = "--before="+before.getYear()+"-"+before.getMonthValue()+"-"+before.getDayOfMonth();
		System.out.println("before "+beforeString);
		Process process;
		//after = null for first release
		if(afterDate != null) {
			LocalDateTime after = afterDate.plusDays(1);
			
			String afterString = "--after="+after.getYear()+"-"+after.getMonthValue()+"-"+after.getDayOfMonth();
			System.out.println("after "+afterString);
			process = Runtime.getRuntime().exec(new String[] {"git", "log", ALL_OPT,NO_MERGE_OPT,beforeString, afterString ,COMMIT_FORMAT, DATE_FORMAT}, null, this.workingCopy); 
		}else {
			process = Runtime.getRuntime().exec(new String[] {"git", "log", ALL_OPT,NO_MERGE_OPT, beforeString, COMMIT_FORMAT, DATE_FORMAT}, null, this.workingCopy); 
		}
		
		BufferedReader reader = new BufferedReader (new InputStreamReader (process.getInputStream()));
		String line;
		String[] splitted;
		
		String sha;
		String message;
		String author;
		String date;
		Commit commit;
		
		while((line = reader.readLine()) != null) {
			if(!line.isEmpty()) {
				splitted = line.split("---");
				sha = splitted[0];
				message = splitted[1];
				author = splitted[2];
				//get only date 
				date = splitted[3].split(" ")[0];
				
				commit = new Commit(date, sha, author, message);
				commits.add(commit);
			
			}
		}
		//order by date
		Collections.sort(commits, (Commit c1, Commit c2) -> c1.getDate().compareTo(c2.getDate()));
		return commits;
	}
	
	
	public List<Commit> getIssueCommit(Issue issue) throws IOException{
		
		List<Commit> commits = new ArrayList<>();
		
		Process process = Runtime.getRuntime().exec(new String[] {"git", "log",COMMIT_FORMAT,NO_MERGE_OPT, ALL_OPT,DATE_FORMAT}, null, this.workingCopy);
		BufferedReader reader = new BufferedReader (new InputStreamReader (process.getInputStream()));
		String line;
		String[] splitted;
		
		String sha;
		String message;
		String author;
		String date;
		Commit commit;
		
		Pattern pattern;
		Matcher matcher;
		
		while((line = reader.readLine()) != null) {
			if(!line.isEmpty()) {
				splitted = line.split("---");
				sha = splitted[0];
				message = splitted[1];
				author = splitted[2];
				//get only date 
				date = splitted[3].split(" ")[0];
				
				//regular expression for matching
				pattern = Pattern.compile("(ISSUE|"+this.projectName.toUpperCase()+")(-| #)"+issue.getIndex()+"(:|\\.)",Pattern.CASE_INSENSITIVE);
				matcher = pattern.matcher(message);
				
				if(matcher.find()) {		
					commit = new Commit(date, sha, author, message);
					commits.add(commit);
				}
			}
		}
		//order by date
		Collections.sort(commits, (Commit c1, Commit c2) -> c1.getDate().compareTo(c2.getDate()));
		return commits;
		
	}
	
	public List<String> getTouchedFile(String sha) throws IOException{
		List<String> fileList = new ArrayList<>();
		
		Process process = Runtime.getRuntime().exec(new String[] {"git", "log",sha ,"--pretty=format:","--name-only","-1"}, null, this.workingCopy);
		BufferedReader reader = new BufferedReader (new InputStreamReader (process.getInputStream()));
		String line;
		
		while((line = reader.readLine()) != null) {
			if(!line.isEmpty() && line.endsWith(".java")) {
				fileList.add(line);
			}
		}
		Collections.sort(fileList);
		return fileList;
		
	}
	
	public Integer getFileSize(String releaseName, String fileName) throws IOException{
		Process process = Runtime.getRuntime().exec(new String[] {"git","show",releaseName+":"+fileName}, null, this.workingCopy);
	    BufferedReader reader = new BufferedReader (new InputStreamReader (process.getInputStream()));
		String line;
		Integer size = 0;
		
		while((line = reader.readLine()) != null) {
			if(!line.isEmpty()) {
				size ++;
			}
		}
		return size;
	}

}
