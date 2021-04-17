package logic;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;


public class ReleaseRetriever {
	
	private String projectName;
	private List<Release> releases;
	private int numReleases;
	
	public ReleaseRetriever(String projectName) {
		this.projectName = projectName;
	}
	
	public ReleaseRetriever() {
		this.projectName = "";
	}
	
	public void retrieveReleases() throws IOException {
		releases = new ArrayList<>();
		Integer i;
		
		JSONArray versions = JiraBoundary.getReleases(this.projectName);
		for (i = 0; i < versions.length(); i++) {
			String name = "";
			String id = "";
			
			//Add only releases with date
			if(versions.getJSONObject(i).has("releaseDate")) {
				if (versions.getJSONObject(i).has("name"))
					name = versions.getJSONObject(i).getString("name");
				if (versions.getJSONObject(i).has("id"))
					id = versions.getJSONObject(i).getString("id");
				this.addRelease(versions.getJSONObject(i).getString("releaseDate"), name, id);
			}
		}
		
		// order releases 		
		Collections.sort(this.releases, ( Release r1, Release r2) -> r1.getReleaseDate().compareTo(r2.getReleaseDate()));
		
		//set size
		this.numReleases = this.releases.size();
		
		//set index
		for (i = 0; i < this.numReleases; i++ ) {
			this.releases.get(i).setReleaseIndex(i+1);
		}		
	}
	
	private void addRelease (String strDate, String name, String id) {
		LocalDate date = LocalDate.parse(strDate);
		LocalDateTime dateTime = date.atStartOfDay();
		
		//add only name to a release that already exists with same date
		if( this.releases.stream().anyMatch(rel -> rel.getReleaseDate().equals(dateTime)))
			this.releases.stream().filter(rel -> rel.getReleaseDate().equals(dateTime)).forEach(relResponse -> relResponse.setSingleReleaseName(name));
		else {
			List<String> nameList = new ArrayList<>();
			nameList.add(name);
			Release r = new Release(dateTime, nameList, id);
			this.releases.add(r);
		}
	}
	
	public int getSize() {
		return this.numReleases;
	}
	
	public List<Release> getReleases(){
		return this.releases;
	}
	

}