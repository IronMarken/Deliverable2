package logic;

import java.time.LocalDateTime;


public class Release {
	
	private int releaseIndex;
	private LocalDateTime releaseDate;
	private String releaseName;
	private String releaseID;
	
	
	public Release(int releaseIndex, LocalDateTime releaseDate, String releaseName, String releaseID) {
		this.releaseIndex = releaseIndex;
		this.releaseDate = releaseDate;
		this.releaseName = releaseName;
		this.releaseID = releaseID;
	}
	
	public Release(LocalDateTime releaseDate, String releaseName, String releaseID) {
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
	
	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}
	
	public String getReleaseName() {
		return this.releaseName;
	}
	
	public void setReleaseID(String releaseID) {
		this.releaseID = releaseID;
	}
	
	public String getReleaseID() {
		return this.releaseID;
	}		
	
}
