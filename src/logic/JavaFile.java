package logic;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaFile {
	
	private static final Logger LOGGER = Logger.getLogger(JavaFile.class.getName());
	
	private int releaseIndex;
	private String name;
	private Boolean buggy;
	private Integer size;
	private LocalDateTime creationDate;
	private long age;
	//LOC added+deleted+changed
	private long touchedLOC;
	//commit count
	private int commitCount;
	//authorList for author count
	//list needed to count only new names
	private List<String> authorList;
	//added count
	//list in order to calculate avg, max ad sum easily
	private List<Integer> addedList;
	//added+deleted
	private List<Integer> churnList;	
	//amount of files committed with
	private List<Integer> chgSetSizeList;
	
	public JavaFile(String name) {
		this.name = name;
		this.buggy = false;
		this.creationDate = null;
		this.touchedLOC = 0;
		this.commitCount = 0;
		this.authorList = new ArrayList<>();
		this.addedList = new ArrayList<>();
		this.churnList = new ArrayList<>();
		this.chgSetSizeList = new ArrayList<>();
	}
	
	
	public void setReleaseIndex(int releaseIndex) {
		this.releaseIndex = releaseIndex;
	}
	
	public int getReleaseIndex() {
		return this.releaseIndex;
	}
	
	public List<String> getAuthorList(){
		return this.authorList;
	}
	
	public List<Integer> getAddedList(){
		return this.addedList;
	}
	
	public List<Integer> getChurnList(){
		return this.churnList;
	}
	
	public List<Integer> getChgSetSizeList(){
		return this.chgSetSizeList;
	}
	
	public void addChgSetSize(Integer count) {
		this.chgSetSizeList.add(count);
	}
	
	public Integer getMaxChgSetSize() {
		if(this.chgSetSizeList.isEmpty())
			return 0;
		else
		   return Collections.max(this.chgSetSizeList);
	}
	
	public double getAvgChgSetSize() {
		if(this.chgSetSizeList.isEmpty())
			return 0;
		else
		   return this.chgSetSizeList.stream().mapToDouble(a->a).average().getAsDouble();
	}
	
	public Integer getTotalChgSetSize() {
		if(this.chgSetSizeList.isEmpty())
			return 0;
		else
		   return this.chgSetSizeList.stream().mapToInt(Integer::intValue).sum();
	}
	
	
	public long getWeightedAge() {
		return this.age * this.touchedLOC;
	}
	
	
	public void addChurnCount(Integer count) {
		this.churnList.add(count);
	}
	
	public Integer getMaxChurn() {
		if(this.churnList.isEmpty())
			return 0;
		else
		   return Collections.max(this.churnList);
	}
	
	public double getAvgChurn() {
		if(this.churnList.isEmpty())
			return 0;
		else
		   return this.churnList.stream().mapToDouble(a->a).average().getAsDouble();
	}
	
	public Integer getTotalChurn() {
		if(this.churnList.isEmpty())
			return 0;
		else
		   return this.churnList.stream().mapToInt(Integer::intValue).sum();
	}
	
	public void addAddedCount(Integer count) {
		this.addedList.add(count);
	}
	
	public Integer getMaxAddedLOC() {
		if(this.addedList.isEmpty())
			return 0;
		else
		   return Collections.max(this.addedList);
	}
	
	public double getAvgAddedLOC() {
		if(this.addedList.isEmpty())
			return 0;
		else
		   return this.addedList.stream().mapToDouble(a->a).average().getAsDouble();
	}
	
	public Integer getTotalAddedLOC() {
		if(this.addedList.isEmpty())
			return 0;
		else
		   return this.addedList.stream().mapToInt(Integer::intValue).sum();
	}
	
	public void addAuthor(String authorName) {
		//add only new names
		if(!this.authorList.contains(authorName))
			this.authorList.add(authorName);
	}
	
	public int getAuthorCount() {
		return this.authorList.size();
	}
	
	public void increaseCommitCount() {
		this.commitCount++;
	}
	
	public int getCommitCount() {
		return this.commitCount;
	}
	
	public void increaseTouchedLOC(long newTouchedLOC) {
		this.touchedLOC = this.touchedLOC + newTouchedLOC;
	}
	
	public long getTouchedLOC() {
		return this.touchedLOC;
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
