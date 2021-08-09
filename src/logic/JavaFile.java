package logic;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaFile {
	
	private static final Logger LOGGER = Logger.getLogger(JavaFile.class.getName());
	private String name;
	private Boolean buggy;
	private Integer size;
	private LocalDateTime creationDate;
	private long age;
	
	
	public JavaFile(String name) {
		this.name = name;
		this.buggy = false;
		this.creationDate = null;
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
	
	public void setSize(Integer size) {
		this.size = size;
	}
	
	public Integer getSize() {
		return this.size;
	}
	
	public void setCreationDate(LocalDateTime ldt) {
		this.creationDate = ldt;
	}
	
	public LocalDateTime getCreationDate() {
		return this.creationDate;
	}
	
	public void execAge(LocalDateTime compareDate) {
		if(this.creationDate != null) {
			this.age = ChronoUnit.WEEKS.between(this.creationDate, compareDate);
		}else {
			LOGGER.log(Level.WARNING, "Null creation date");
		}
	}
	
	public long getAge() {
		return this.age;
	}
	
}
