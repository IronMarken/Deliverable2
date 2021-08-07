package logic;

public class JavaFile {
	
	private String name;
	private Boolean buggy;
	
	
	public JavaFile(String name) {
		this.name = name;
		this.buggy = false;
	}
	
	public void setBuggy() {
		this.buggy = true;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Boolean isBuggy() {
		return this.buggy;
	}
	
	
	
}
