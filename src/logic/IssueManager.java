package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class IssueManager {
	
	private static final String ISSUE_FIELDS = "fields";
	private static final Logger LOGGER = Logger.getLogger(IssueManager.class.getName());
	private static final double PERC = 0.01;
	private String projectName;
	private List<Issue> issues;
	private ReleaseManager rm;
	private GitBoundary gb;
	
	public IssueManager(String projectName, ReleaseManager rm) {
		this.projectName = projectName;
		this.rm = rm;
		this.issues = new ArrayList<>();
	}
	
	public IssueManager(String projectName, ReleaseManager rm, GitBoundary gb) {
		this.projectName = projectName;
		this.rm = rm;
		this.gb = gb;
		this.issues = new ArrayList<>();
	}
	
	
	public List<Issue> getIssues(){
		return this.issues;
	}
	
	public void setGitBoundary(GitBoundary gb) {
		this.gb = gb;
	}
	
	private Release retrieveFixedVersion(JSONArray ja) {
		int size;
		int k;
		Release fixVersion;
		Release rel;
		List<Release> releaseList;
		String relName;
		
		size = ja.length();
		
		
		if(size == 0) {
			fixVersion = null;
		}else {
			releaseList = new ArrayList<>();
			for(k=0; k < size; k++) {
				relName = ja.getJSONObject(k).getString("name");
				rel = rm.getReleaseByJiraName(relName);
				if(rel!= null)
					releaseList.add(rel);						
			}
			fixVersion = Release.getMaxRelease(releaseList);
		}
		return fixVersion;
	}
	
	private Release retrieveInjectedVersion(JSONArray ja) {
		int size;
		int k;
		Release injectedVersion;
		Release rel;
		List<Release> releaseList;
		String relName;
		size = ja.length();
		
		
		if(size == 0) {
			injectedVersion = null;
		}else {
			releaseList = new ArrayList<>();
			for(k=0; k < size; k++) {
				relName = ja.getJSONObject(k).getString("name");
				rel = rm.getReleaseByJiraName(relName);
				if(rel != null)
					releaseList.add(rel);						
			}
			injectedVersion = Release.getMinRelease(releaseList);					
		}
		return injectedVersion;
	}
	
	public void setupIssues() throws IOException{
		String report;
		retrieveIssues();
		getTouchedFiles();
		report = "Issues size after java touched files "+this.issues.size()+"\nExecuting proportion";
		LOGGER.log(Level.INFO, report);		
		proportion();
        LOGGER.log(Level.INFO, "Filter issues after proportion");
        filterAfterProportion();
        report = "After proportion final issues size "+this.issues.size()+"\nEvalueting buggy classes";
        LOGGER.log(Level.INFO, report);
        setBuggyness();
        LOGGER.log(Level.INFO, "Buggyness completed");
        

    }
    
    private void filterAfterProportion() {
    	
    	List<Issue> filteredList;
    	Release lastConsidered;
    	Release injectedVersion;
    	Release fixVersion;
    	
    	filteredList = new ArrayList<>();
    	lastConsidered = this.rm.getLastReleaseConsidered();
    	Issue issue;
    	for(int i=0; i< this.issues.size(); i++) {
    		issue = this.issues.get(i);
    		injectedVersion = issue.getInjectedVersion();
    		fixVersion = issue.getFixVersion();
    		
    		//reject issue if: 
    		//injected > fix -> invalid issue
    		//injected = fix -> no effects on buggy
    		//injected > last considered release -> don't care
    		//java touched file is empty -> no effects on buggy 
    		if(!(injectedVersion.getIndex() >= fixVersion.getIndex() || injectedVersion.getIndex() > lastConsidered.getIndex() || issue.getTouchedFiles().isEmpty()))
    			filteredList.add(issue);
    	}
    	
    	this.issues = filteredList;
	}
	
	private void retrieveIssues() throws IOException{
		
		Integer i = 0;
		Integer j = 0;
		Integer total = 1;
		
		Issue issue;
		String key;
		String id;
		Release injectedVersion;
		Release fixVersion;
		Release openingVersion;
		String openingDate;
		List<Commit> commitList;
		
		int noFixedCount = 0;
		int count = 0;
		int totalCount = 0;
		
		LOGGER.log(Level.INFO, "Getting issues and related commits");
		
		do {
			j = i + 1000;
			JSONObject json = JiraBoundary.getIssue(this.projectName, i);
			JSONArray issuesJson = json.getJSONArray("issues");
			total = json.getInt("total");
			
			for(; i < total && i < j; i++) {
				//i%1000 max per page
				id = issuesJson.getJSONObject(i%1000).getString("id");
				key = issuesJson.getJSONObject(i%1000).getString("key");
				
				openingDate = issuesJson.getJSONObject(i%1000).getJSONObject(ISSUE_FIELDS).getString("created").split("T")[0];

				
				injectedVersion = this.retrieveInjectedVersion(issuesJson.getJSONObject(i).getJSONObject(ISSUE_FIELDS).getJSONArray("versions"));
				fixVersion = this.retrieveFixedVersion(issuesJson.getJSONObject(i).getJSONObject(ISSUE_FIELDS).getJSONArray("fixVersions"));
				openingVersion = this.rm.getReleaseFromDate(openingDate);
				
				totalCount ++;				
				
				//filtering null fixVersions
				if(fixVersion != null && openingVersion != null) {
					noFixedCount ++;
					issue = new Issue(id, key, fixVersion, injectedVersion);
					issue.setOpeningVersion(openingVersion);
					commitList = this.gb.getIssueCommit(issue);
				
					
					if(!(injectedVersion == null && commitList.isEmpty())) {
						//add also issues with no commits but with injected not null just to help proportion with more data
						issue.setCommits(commitList);
						this.issues.add(issue);
						count ++;					
					}					
				}

			}
			
		}while(i < total);
		//reorder issues
		Collections.sort(this.issues, (Issue i1, Issue i2) -> i1.getIndex().compareTo(i2.getIndex()));
		//first filter before proportion
		LOGGER.log(Level.INFO, "First filtering for issues");
		this.issues = filterIssue(this.issues);
		String report = "REPORT\n"+"Total issues "+totalCount+".\nIssues with not null Fixed "
						+noFixedCount+".\nIssues with commits or no commits but not null injected  "
						+count+".\nAfter first filter "+this.issues.size();
		
		LOGGER.log(Level.INFO, report);
	}
	
	private List<Issue> filterIssue(List<Issue> issueList){
		List<Issue> filteredList = new ArrayList<>();
		Issue issue;
		Release lastRelease;
		Release injectedVersion;
		Release fixVersion;
		
		lastRelease = this.rm.getLastReleaseConsidered();
		
		for(int i=0; i < issueList.size(); i++) {
			issue = issueList.get(i);
			injectedVersion = issue.getInjectedVersion();
			fixVersion = issue.getFixVersion();	
			
			//filter injected > fix or injected > last Release considered
			if(!(injectedVersion != null && (injectedVersion.getIndex() > fixVersion.getIndex() || injectedVersion.getIndex() > lastRelease.getIndex()))) 
				filteredList.add(issue);
	
		}
		return filteredList;
	} 
	
	private void getTouchedFiles() throws IOException {
		
		List<Commit> commitList;
		List<String> touchedFiles;
		List<Issue> issueList = new ArrayList<>();
		
		for(Issue issue : this.issues) {
			commitList = issue.getCommits();
			for(Commit commit: commitList) {
				touchedFiles = this.gb.getTouchedFile(commit.getSha());
				commit.setTouchedFiles(touchedFiles);
			}
			issue.calculateTouchedFiles();
			//keep issues with no java touched files but with a injected version used for proportion
			if(!(issue.getTouchedFiles().isEmpty() && issue.getInjectedVersion() == null)) {
				issueList.add(issue);
			}
			
		}
		
		this.issues = issueList;
		
	}
	
	private void proportion() {
		
		double p;
		Release iv;
		Release ov;
		Release fv;
		Integer ivIndex;
		
		List<Release> releaseList;
		List<Release> unreleasedList;
		List<Issue> issueList = new ArrayList<>();
		Issue issue;
		
		releaseList = this.rm.getReleases();
		unreleasedList = this.rm.getUnreleased();
		
		for(int i=0; i < this.issues.size(); i++) {
			issue = this.issues.get(i);
			iv = issue.getInjectedVersion();
			ov = issue.getOpeningVersion();
			fv = issue.getFixVersion();
			
			if(iv == null) {
				
				p = movingWindow(issueList);
				ivIndex = (int)Math.round(fv.getIndex() - (fv.getIndex()-ov.getIndex())*p);
				
				//proportion formula can return negative value if 
				//ov is low and distance between ov and fv is big
				if(ivIndex <= 0)
					ivIndex = 1 ;  
				
				//check if in unreleased
				if(ivIndex > releaseList.size()) 
					iv = unreleasedList.get(0);
				else	
					//indexes starts from 1
					iv = releaseList.get(ivIndex - 1);
				issue.setInjectedVersion(iv);
			}
			issueList.add(issue);
		}
		
		this.issues = issueList;
	}
	
	private double movingWindow(List<Issue> list) {
		double p = 0;
		Issue issue;
		Release fv;
		Release ov;
		Release iv;
		double sum = 0;
		
		int count = 0;
		
		List<Issue> subList;
		
		
		if(list.isEmpty()) {
			//p for first issue if IV is null 
			//average of all issues with IV not null	
			subList = this.issues.stream().filter(i -> i.getInjectedVersion() != null).collect(Collectors.toList());
		}else {
			//calculated as moving window
			int size;
			size = (int)Math.ceil(PERC * list.size());
			int fromIndex = list.size()-size;
			//calculate p on last PERC (1%) issues
			subList = list.subList(fromIndex, list.size());

		}
		
		//calculate p and its average
		for(int i = 0; i < subList.size(); i++) {
			issue = subList.get(i);
			fv = issue.getFixVersion();
			ov = issue.getOpeningVersion();
			iv = issue.getInjectedVersion();
			
			count ++;
			//add 0 if fv = ov
			if(fv.getIndex() != ov.getIndex()) 
				sum = sum  + ((double)(fv.getIndex()-iv.getIndex())/(double)(fv.getIndex()-ov.getIndex())) ;
				
	
		}
		//control division by 0
		if( count != 0)
			p = sum/count;
		
		return p;
	}
	
	private void setBuggyness() {
		
		List<Release> considered;
		Release injectedVersion;
		Release fixVersion;
		Release lastRelease;
		Release rel;
		JavaFile fileTouched;
		int i;
		
		considered = this.rm.getConsideredReleases();
		lastRelease = this.rm.getLastReleaseConsidered();
		
		for(Issue issue:this.issues) {
			injectedVersion = issue.getInjectedVersion();
			fixVersion = issue.getFixVersion();
			//fromInjected included to fixVersion excluded
			//Warning to size of considered releases
			for(i=injectedVersion.getIndex(); i < fixVersion.getIndex() && i <= lastRelease.getIndex(); i++) {
				//release indexes start from 1
				rel = considered.get(i-1);
				//set buggy all files  touched by issues' commits
				for(String fileName:issue.getTouchedFiles()) {
					fileTouched = rel.getClassByName(fileName);
					if(fileTouched != null)
						fileTouched.setBuggy();
				}
			}	
		}
	
	}
}
