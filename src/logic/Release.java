package logic;

import java.time.LocalDateTime;
import java.util.List;


public class Release {
	
	private int releaseIndex;
	private LocalDateTime releaseDate;
	//manage releases with same release date
	private List<String> releaseName;
	private String releaseID;
	
	
	public Release(int releaseIndex, LocalDateTime releaseDate, List<String> releaseName, String releaseID) {
		this.releaseIndex = releaseIndex;
		this.releaseDate = releaseDate;
		this.releaseName = releaseName;
		this.releaseID = releaseID;
	}
	
	public Release(LocalDateTime releaseDate, List<String> releaseName, String releaseID) {
		this.releaseDate = releaseDate;
		this.releaseName = releaseName;
		this.releaseID = releaseID;
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
	
	public void setAllReleaseName(List<String> releaseName) {
		this.releaseName = releaseName;
	}
	
	public List<String> getAllReleaseName() {
		return this.releaseName;
	}
	
	public void setSingleReleaseName(String name) {
		this.releaseName.add(name);
	}
	
	public String getSingleReleaseName(int index) {
		return this.releaseName.get(index);
	}
	
	public void setReleaseID(String releaseID) {
		this.releaseID = releaseID;
	}
	
	public String getReleaseID() {
		return this.releaseID;
	}		
	
}
