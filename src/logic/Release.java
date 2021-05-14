package logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Release {
	
	//used for Proportion
	private int releaseIndex;
	//date from git
	private LocalDateTime releaseDate;
	//manage releases with same release date
	private String jiraName;
	private String gitName;
	private String releaseID;
	private String sha;
	private List<String> javaClasses;
	
	
	public Release(int releaseIndex, LocalDateTime releaseDate, String jiraName, String releaseID) {
		this.releaseIndex = releaseIndex;
		this.releaseDate = releaseDate;
		this.jiraName = jiraName;
		this.releaseID = releaseID;
		this.gitName = null;
		this.javaClasses = new ArrayList<>();
	}
	
	public Release(LocalDateTime releaseDate, String jiraName, String releaseID) {
		this.releaseDate = releaseDate;
		this.jiraName = jiraName;
		this.releaseID = releaseID;
		this.gitName = null;
		this.javaClasses = new ArrayList<>();
	}
	
	public Release(String jiraName, String releaseID) {
		this.releaseDate = null;
		this.jiraName = jiraName;
		this.releaseID = releaseID;
		this.gitName = null;
		this.javaClasses = new ArrayList<>();
	}
	
	public void setReleaseIndex(int index) {
		this.releaseIndex = index;
	}
	
	public int getReleaseIndex() {
		return this.releaseIndex;
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
	
	public void setSha(String sha) {
		this.sha = sha;
	}
	
	public String getSha() {
		return this.sha;
	}
	
	public void setClasses(List<String> classes) {
		this.javaClasses = classes;
	}
	
	public List<String> getClasses(){
		return this.javaClasses;
	}
	
	
	
}
