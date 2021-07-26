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
	
	public LocalDateTime getReleaseDate(String gitName) throws IOException{
		
		Process process = Runtime.getRuntime().exec(new String[] {"git", "log", gitName, "-1", "--pretty=format:%cd" ,DATE_FORMAT }, null, this.workingCopy);
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
		return classes;
	}
	
	public List<Commit> getReleaseCommits(String gitName) throws IOException{
		
		List<Commit> commits = new ArrayList<>();
		
		Process process = Runtime.getRuntime().exec(new String[] {"git", "log", gitName ,"--pretty=format:%H---%s---%an---%cd---", DATE_FORMAT}, null, this.workingCopy);
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
	
	
	public List<Commit> getIssueCommit(Integer index) throws IOException{
		
		List<Commit> commits = new ArrayList<>();
		
		Process process = Runtime.getRuntime().exec(new String[] {"git", "log","--pretty=format:%H---%s---%an---%cd---", DATE_FORMAT}, null, this.workingCopy);
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
				pattern = Pattern.compile("(ISSUE|"+this.projectName.toUpperCase()+")(-| #)"+index+"(:|\\.)",Pattern.CASE_INSENSITIVE);
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

}
