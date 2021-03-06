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
import org.json.JSONObject;


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
		calculateData();
		filterFirstRelease();
		LOGGER.log(Level.INFO, "All data calculated");
	}
	
	private void parseRelease(JSONObject obj) throws IOException {
		String name = "";
		String id = ""; 
		LocalDateTime date;
		
		//get parameters
		if(obj.has("name"))
			name = obj.getString("name");
		if(obj.has("id"))
			id = obj.getString("id");
		
		//add all releases released
		boolean isReleased = obj.getBoolean("released");
		boolean isDated = obj.has("releaseDate");
		
		//Released with JiraDate
		if(isReleased && isDated) {
			date = LocalDate.parse(obj.getString("releaseDate")).atStartOfDay();
			this.addRelease(name, id, date);
		}
		
		//Released without JiraDate
		if(isReleased && !isDated) {
			date = this.gb.getDate(this.rna.deriveGitName(name), true);
			if(date == null)
				//date don't exists
				this.addUnreleased(name, id);
			else
				//date taken from git
				this.addRelease(name, id, date);
		}
		
		//unreleased version
		if(!isReleased)
			this.addUnreleased(name, id);
	}
	
	
	private void retrieveReleases() throws IOException {
		LOGGER.log(Level.INFO, "Getting releases");
		this.releases = new ArrayList<>();
		this.unreleased = new ArrayList<>();
		this.myReleases = new ArrayList<>();
		Integer i;
		
		JSONArray versions = JiraBoundary.getReleases(this.projectName);
		for (i = 0; i < versions.length(); i++) {
			parseRelease(versions.getJSONObject(i));
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
		
		//add releases considered for the analysis
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
	
	//get releases classes their size and age
	private void retrieveClasses() throws IOException{
		LOGGER.log(Level.INFO, "Getting classes");
		Release rel;
		JavaFile file;
		List<String> classes;
		List<JavaFile> fileList;
		for (int i = 0; i < this.myReleases.size(); i++) {
			fileList = new ArrayList<>();
			rel = this.myReleases.get(i);
			classes = this.gb.getReleaseClasses(rel.getGitName());
			for(String name: classes) {
				file = new JavaFile(name);
				file.setReleaseIndex(rel.getIndex());
				file.setSize(this.gb.getFileSize(rel.getGitName(), name));
				file.setCreationDate(this.gb.getDate(name, false));
				file.execAge(rel.getReleaseDate());
				fileList.add(file);
			}
			rel.setJavaFiles(fileList);
		}		
	}
	
	private void retrieveCommit() throws IOException{
		Release rel;
		List<Commit> commits;
		LocalDateTime after;
		LocalDateTime before;
		int i;
		for(i=0; i < this.myReleases.size(); i++) {
			
			if(i == 0) {
				after = null;
			}else 
				after = this.myReleases.get(i-1).getReleaseDate();
			rel = this.myReleases.get(i);
			before = rel.getReleaseDate();
			commits = this.gb.getReleaseCommits(after, before);
			commits = setupCommit(commits);
			rel.setCommits(commits);
		}
	}
	
	private List<Commit> setupCommit(List<Commit> commitList) throws IOException{
		
		List<Commit> setuppedList = new ArrayList<>();
		List<DataFile> dataFiles;
		
		for(Commit commit: commitList) {
			//get touched files
			dataFiles = this.gb.getTouchedFileWithData(commit.getSha());
			//filter 
			if(!dataFiles.isEmpty()) {
				commit.setDataFile(dataFiles);
				setuppedList.add(commit);
			}	
		}
		return setuppedList;
	}
	
	
	public List<JavaFile> getFinalFileList(){
		
		List<JavaFile> finalList = new ArrayList<>();
		List<JavaFile> relList;
		
		
		for(Release rel:this.myReleases) {
			relList = rel.getClasses();
			Collections.sort(relList, ( JavaFile jf1, JavaFile jf2) -> jf1.getName().compareTo(jf2.getName()));
			finalList.addAll(relList);
		}
		
		return finalList;
	}
	
	private void calculateData() {
		LOGGER.log(Level.INFO, "Calculating file data");
		JavaFile javaFile;
		String fileName;
		String author;
		Integer added;
		Integer changed;
		Integer deleted;
		Integer chgSetSize;
		
		
		for(Release release: this.myReleases) {

			for(Commit commit: release.getCommits()) {

				author = commit.getAuthor();
				for(DataFile dataFile: commit.getDataList()) {
					
					fileName = dataFile.getName();
					added = dataFile.getAdded();
					changed = dataFile.getChanged();
					deleted = dataFile.getDeleted();
					chgSetSize = dataFile.getChgSetSize();
					
					javaFile = release.getClassByName(fileName);
					if(javaFile != null) {
						//set needed parameters
						javaFile.increaseCommitCount();
						javaFile.increaseTouchedLOC((long)added + deleted + changed);
						javaFile.addAuthor(author);
						javaFile.addAddedCount(added);
						javaFile.addChurnCount(added+deleted);
						javaFile.addChgSetSize(chgSetSize);
					}
					
				}
			}
		}
		
		LOGGER.log(Level.INFO, "Data calculated");
	}
	
	private void filterFirstRelease() {
		Release first = this.myReleases.get(0);
		List<JavaFile> filteredList = new ArrayList<>();
		
		for(JavaFile file:first.getClasses()) {
			if(file.getCommitCount()!=0)
				filteredList.add(file);
		}
		
		first.setJavaFiles(filteredList);
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
	
	public Release getReleaseFromDate(String date) {
		Release rel;
		Release actual;
		rel = null;
		
		LocalDateTime ldt = LocalDate.parse(date).atStartOfDay();
		
		//return the first release with first date after given date
		for(int i=0; i<this.releases.size(); i++) {
			actual = this.releases.get(i);
			if(ldt.isBefore(actual.getReleaseDate())) {
				return actual;
			}
		}
		
		//if no release match get the first unreleased or null
		if(!this.unreleased.isEmpty())	
			rel = this.unreleased.get(0);
		
		return rel;
	}

}
