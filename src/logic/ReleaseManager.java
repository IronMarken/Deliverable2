package logic;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;


public class ReleaseManager {
	
	
	private GitBoundary gb;
	private ReleaseNameAdapter rna;
	private String projectName;
	private List<Release> releases;
	//considered only for references in the issues versions
	private List<Release> unreleased;
	
	private int numReleases;
	
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

	
	public void retrieveReleases() throws IOException {
		releases = new ArrayList<>();
		unreleased = new ArrayList<>();
		Integer i;
		
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
			if(versions.getJSONObject(i).has("releaseDate") || versions.getJSONObject(i).getBoolean("released") ) 
				this.addRelease(name, id);				
			//add to not released
			else 
				this.addUnreleased(name, id);
		}
		
		// order releases 		
		Collections.sort(this.releases, ( Release r1, Release r2) -> r1.getReleaseDate().compareTo(r2.getReleaseDate()));
		
		//set size
		this.numReleases = this.releases.size();
		
		//set index
		for (i = 0; i < this.numReleases; i++ ) {
			this.releases.get(i).setReleaseIndex(i+1);
		}
		//for unreleased maxIndex + 1
		for(int j = 0; j< this.unreleased.size(); j++) {
			this.unreleased.get(j).setReleaseIndex(i+1);
		}
	}
	
	private void addRelease (String name, String id) throws IOException {

		//getDate and SHA from Git
		String sha;
		LocalDateTime ldt;
		
		//derive gitName with Adapter
		String gitName = this.rna.deriveGitName(name);
		
		ldt = this.gb.getReleaseDate(gitName);
		sha = this.gb.getReleaseSha(gitName);
		if(ldt != null && sha != null) {
			Release r = new Release(name, id);
			r.setGitName(gitName);
			r.setReleaseDate(ldt);
			r.setSha(sha);
			this.releases.add(r);	
		}
	}
	
	private void addUnreleased(String name, String id){
		
		//derive gitName with Adapter
		String gitName = this.rna.deriveGitName(name);
		
		//add only for a reference
		Release r = new Release(name, id);
		r.setGitName(gitName);
		r.setReleaseDate(null);
		r.setSha(null);
		
		this.unreleased.add(r);
	}
	
	public void retrieveClasses() throws IOException{
		Release rel;
		List<String> classes;
		for (int i = 0; i < this.releases.size(); i++) {
			rel = this.releases.get(i);
			classes = this.gb.getReleaseClasses(rel.getGitName());
			rel.setClasses(classes);
		}
	}
	
	public int getSize() {
		return this.numReleases;
	}
	
	public List<Release> getUnreleased(){
		return this.unreleased;
	}
	
	public List<Release> getReleases(){
		return this.releases;
	}
	

}
