package logic;

import java.util.ArrayList;
import java.util.List;

public class Issue {
	
	private String id;
	private Integer index;
	private String key;
	private Release fixVersion;
	//AV where exists is considered as injected version
	private Release injectedVersion;
	private Release openingVersion;
	private List<Commit> commits;
	private List<String> touchedFiles;
	
	
	public Issue() {
		this.commits = new ArrayList<>();
		this.touchedFiles = new ArrayList<>();
	}
	
	public Issue(String id, String key, Release fixVersion, Release injectedVersion) {
		this.id = id;
		this.key = key;
		this.fixVersion = fixVersion;
		this.injectedVersion = injectedVersion;
		this.index = Integer.parseInt(this.key.split("-")[1]);
		this.commits = new ArrayList<>();
		this.touchedFiles = new ArrayList<>();
	}
	
	public void calculateTouchedFiles() {
		for(Commit commit : this.commits) {
			for(String file : commit.getTouchedFiles()) {
				if(!this.touchedFiles.contains(file))
					this.touchedFiles.add(file);
			}
		}
	}
	
	public List<String> getTouchedFiles(){
		return this.touchedFiles;
	}
	
	public Integer getIndex() {
		return this.index;
	}
	
	public void setCommits(List<Commit> commits) {
		this.commits = commits;
	}
	
	public List<Commit> getCommits(){
		return this.commits;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public void setFixVersion(Release fixVersion) {
		this.fixVersion = fixVersion;
	}
	
	public Release getFixVersion() {
		return this.fixVersion;
	}
	
	public void setInjectedVersion(Release injectedVersion) {
		this.injectedVersion = injectedVersion;
	}
	
	public Release getInjectedVersion() {
		return this.injectedVersion;
	}
	
	public void setOpeningVersion(Release openingVersion) {
		this.openingVersion = openingVersion;
	}
	
	public Release getOpeningVersion() {
		return this.openingVersion;
	}
}
