package logic;

public class DataFile {
	
	String name;
	Integer added;
	Integer deleted;
	
	public DataFile() {
		this.name = null;
		this.added = null;
		this.deleted = null;
	}
	
	public DataFile(String name, Integer added, Integer deleted) {
		this.name = name;
		this.added = added;
		this.deleted = deleted;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setAdded(Integer added) {
		this.added = added;
	}
	
	public Integer getAdded() {
		return this.added;
	}
	
	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}
	
	public Integer getDeleted() {
		return this.deleted;
	}
	
	
}
