package logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Release {
	
	//used for Proportion
	private int index;
	//date from git
	private LocalDateTime releaseDate;
	private String jiraName;
	private String gitName;
	private String releaseID;
	private List<JavaFile> javaClasses;
	private List<Commit> commitList;
	
	
	public Release(int releaseIndex, LocalDateTime releaseDate, String jiraName, String releaseID) {
		this.index = releaseIndex;
		this.releaseDate = releaseDate;
		this.jiraName = jiraName;
		this.releaseID = releaseID;
		this.gitName = null;
		this.javaClasses = new ArrayList<>();
		this.commitList = new ArrayList<>();
	}
	
	public Release(LocalDateTime releaseDate, String jiraName, String releaseID) {
		this.releaseDate = releaseDate;
		this.jiraName = jiraName;
		this.releaseID = releaseID;
		this.gitName = null;
		this.javaClasses = new ArrayList<>();
		this.commitList = new ArrayList<>();
	}
	
	public Release(String jiraName, String releaseID) {
		this.releaseDate = null;
		this.jiraName = jiraName;
		this.releaseID = releaseID;
		this.gitName = null;
		this.javaClasses = new ArrayList<>();
		this.commitList = new ArrayList<>();
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void setReleaseDate(LocalDateTime releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	public LocalDateTime getReleaseDate() {
		return this.releaseDate;
	}
	
	public String getJiraName() {
		return this.jiraName;
	}
	
	public void setJiraName(String jiraName) {
		this.jiraName = jiraName;
	}
	
	public void setGitName(String gitName) {
		this.gitName = gitName;
	}
	
	public String getGitName() {
		return this.gitName;
	}
	
	public void setReleaseID(String releaseID) {
		this.releaseID = releaseID;
	}
	
	public String getReleaseID() {
		return this.releaseID;
	}	
	
	public void setJavaFiles(List<JavaFile> classes) {
		this.javaClasses = classes;
	}
	

	public void setClasses(List<String> classesNames) {
		List<JavaFile> classes = new ArrayList<>();
		JavaFile file;
		for(String name: classesNames) {
			file = new JavaFile(name);
			classes.add(file);
		}
		this.javaClasses = classes;
	}
	
	public List<JavaFile> getClasses(){
		return this.javaClasses;
	}
	
	public JavaFile getClassByName(String name) {
		JavaFile file;
		file = this.javaClasses.stream().filter(f -> name.contentEquals(f.getName())).findAny().orElse(null); 
		return file;
	}
	
	public void setCommits(List<Commit> commits) {
		this.commitList = commits;
	}
	
	public List<Commit> getCommits(){
		return this.commitList;
	}
	
	public static Release getMaxRelease(List<Release> list) {
		Release rel = null;
		
		if(!list.isEmpty())
			rel = Collections.max(list, Comparator.comparing(Release::getIndex));
		
		return rel;
	}
	
	public static Release getMinRelease(List<Release> list) {
		Release rel = null;
		
		if(!list.isEmpty())
			rel = Collections.min(list, Comparator.comparing(Release::getIndex));
		
		return rel;
	}
	
	
	
}
