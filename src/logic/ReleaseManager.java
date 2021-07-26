package logic;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;


public class ReleaseManager {
	
	private static final Logger LOGGER = Logger.getLogger(ReleaseManager.class.getName());
	private GitBoundary gb;
	private ReleaseNameAdapter rna;
	private String projectName;
	private List<Release> releases;
	//considered only for references in the issue's versions
	private List<Release> unreleased;
	private List<Release> myReleases;
	
	public ReleaseManager(String projectName, GitBoundary gb, ReleaseNameAdapter rna) {
		this.projectName = projectName;
		this.gb = gb;
		this.rna = rna;
	}
	
	public ReleaseManager(String projectName, GitBoundary gb) {
		this.projectName = projectName;
		this.gb = gb;
	}
	
	public ReleaseManager(GitBoundary gb) {
		this.projectName = "";
		this.gb = gb;
	}
	
	public void setAdapter(ReleaseNameAdapter rna) {
		this.rna = rna;
	}

	public void setupReleases() throws IOException {
		retrieveReleases();
		retrieveClasses();
		retrieveCommit();
		String report = "STEUP COMPLETED\nTotal releases "+this.releases.size()+
				"\nTotal unreleased "+this.unreleased.size()+"\nTotal considered releases "+this.myReleases.size();
		LOGGER.log(Level.INFO, report);
	}
	
	
	private void retrieveReleases() throws IOException {
		LOGGER.log(Level.INFO, "Getting releases");
		this.releases = new ArrayList<>();
		this.unreleased = new ArrayList<>();
		this.myReleases = new ArrayList<>();
		Integer i;
		LocalDateTime date;
		
		JSONArray versions = JiraBoundary.getReleases(this.projectName);
		for (i = 0; i < versions.length(); i++) {
			String name = "";
			String id = ""; 
			
			//get params
			if(versions.getJSONObject(i).has("name"))
				name = versions.getJSONObject(i).getString("name");
			if(versions.getJSONObject(i).has("id"))
				id = versions.getJSONObject(i).getString("id");
			
			//add all realeases with a Date or released
			if(versions.getJSONObject(i).has("releaseDate") ) { 
				date = LocalDate.parse(versions.getJSONObject(i).getString("releaseDate")).atStartOfDay();
				this.addRelease(name, id, date);				
			}
			//check date on git or add to unreleased
			else { 
				date = this.gb.getReleaseDate(this.rna.deriveGitName(name));
				if(date == null)
					//date don't exists
					this.addUnreleased(name, id);
				else
					//date taken from git
					this.addRelease(name, id, date);
			}
		}
		
		// order releases 		
		Collections.sort(this.releases, ( Release r1, Release r2) -> r1.getReleaseDate().compareTo(r2.getReleaseDate()));
		
		//set index
		for (i = 0; i < this.releases.size(); i++ ) {
			this.releases.get(i).setIndex(i+1);
		}
		//for unreleased maxIndex + 1
		for(int j = 0; j< this.unreleased.size(); j++) {
			this.unreleased.get(j).setIndex(i+1);
		}
		
		//add releases considered for the anlysis
		this.myReleases = this.releases.subList(0, this.releases.size()/2);
	}
	
	private void addRelease (String name, String id, LocalDateTime date) {
		
		//derive gitName with Adapter
		String gitName = this.rna.deriveGitName(name);
		Release r = new Release(name, id);
		r.setGitName(gitName);
		r.setReleaseDate(date);
		this.releases.add(r);	
	}
	
	private void addUnreleased(String name, String id){
		
		//derive gitName with Adapter
		String gitName = this.rna.deriveGitName(name);
		
		//add only for a reference
		Release r = new Release(name, id);
		r.setGitName(gitName);
		r.setReleaseDate(null);
		
		this.unreleased.add(r);
	}
	
	private void retrieveClasses() throws IOException{
		LOGGER.log(Level.INFO, "Getting classes");
		Release rel;
		List<String> classes;
		for (int i = 0; i < this.myReleases.size(); i++) {
			rel = this.myReleases.get(i);
			classes = this.gb.getReleaseClasses(rel.getGitName());
			rel.setClasses(classes);
		}		
	}
	
	private void retrieveCommit() throws IOException{
		Release rel;
		List<Commit> commits;
		for(int i=0; i < this.myReleases.size(); i++) {
			rel = this.myReleases.get(i);
			commits = this.gb.getReleaseCommits(rel.getGitName());
			rel.setCommits(commits);
		}
	}
	
	
	public Release getReleaseByGitName(String gitName) {
		Release rel;
		//if unreleased exists only Jira Name
		rel = this.releases.stream().filter(release -> gitName.equals(release.getGitName())).findAny().orElse(null);
		return rel;
	}
	
	public Release getReleaseByJiraName(String jiraName) {
		Release rel;
		rel = this.releases.stream().filter(release -> jiraName.equals(release.getJiraName())).findAny().orElse(null);
		if(rel == null) 
			rel = this.unreleased.stream().filter(release -> jiraName.equals(release.getJiraName())).findAny().orElse(null);
		return rel;
	}
	
	
	public List<Release> getUnreleased(){
		return this.unreleased;
	}
	
	public List<Release> getReleases(){
		return this.releases;
	}
	
	public List<Release> getConsideredReleases(){
		return this.myReleases;
	}
	
	public Release getLastReleaseConsidered() {
		return this.myReleases.get(this.myReleases.size()-1);
	}

}
