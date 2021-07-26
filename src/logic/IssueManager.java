package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class IssueManager {
	
	private static final String ISSUE_FIELDS = "fields";
	private static final Logger LOGGER = Logger.getLogger(IssueManager.class.getName());
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
	
	public void retrieveIssues() throws IOException{
		
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
					commitList = this.gb.getIssueCommit(issue.getIndex());
					
					
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
			fixVersion = issue.getInjectedVersion();	
			
			//filter injected > fix or injected > last Release considered
			if(!(injectedVersion != null && (injectedVersion.getIndex() > fixVersion.getIndex() || injectedVersion.getIndex() > lastRelease.getIndex()))) 
				filteredList.add(issue);				
				
		}
		return filteredList;
	}
	
}
