package logic;

public class DataFile {
	
	private String name;
	private Integer added;
	private Integer deleted;
	private Integer chgSetSize;
	
	public DataFile() {
		this.name = null;
		this.added = null;
		this.deleted = null;
		this.chgSetSize = null;
	}
	
	public DataFile(String name, Integer added, Integer deleted) {
		this.name = name;
		this.added = added;
		this.deleted = deleted;
		this.chgSetSize = null;
	}
	
	public void setChgSetSize(Integer size) {
		this.chgSetSize = size;
	}
	
	public Integer getChgSetSize() {
		if(this.chgSetSize == null)
			return 0;
		else
			return this.chgSetSize;
	}
	
	public Integer getChanged() {
		if(this.added == null || this.deleted == null)
			return 0;
		else
			return this.added+this.deleted;
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
		if(this.added == null)
			return 0;
		else
			return this.added;
	}
	
	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}
	
	public Integer getDeleted() {
		if(this.deleted == null)
			return 0;
		else
			return this.deleted;
	}
	
	
}
