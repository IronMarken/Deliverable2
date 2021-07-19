package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class IssueManager {
	
	private String projectName;
	private List<Issue> issues;
	//I think i need it
	private ReleaseManager rm;
	
	public IssueManager(String projectName, ReleaseManager rm) {
		this.projectName = projectName;
		this.rm = rm;
		this.issues = new ArrayList<>();
	}
	
	
	public List<Issue> getIssues(){
		return this.issues;
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
		
		do {
			j = i + 1000;
			JSONObject json = JiraBoundary.getIssue(this.projectName, i);
			JSONArray issuesJson = json.getJSONArray("issues");
			total = json.getInt("total");
			
			for(; i < total && i < j; i++) {
				//i%1000 max per page
				id = issuesJson.getJSONObject(i%1000).getString("id");
				key = issuesJson.getJSONObject(i%1000).getString("key");
				
				injectedVersion = this.retrieveInjectedVersion(issuesJson.getJSONObject(i).getJSONObject("fields").getJSONArray("versions"));
				fixVersion = this.retrieveFixedVersion(issuesJson.getJSONObject(i).getJSONObject("fields").getJSONArray("fixVersions"));
				
				issue = new Issue(id, key, fixVersion, injectedVersion);
				this.issues.add(0, issue);
			}
			
		}while(i < total);		
	}

}
