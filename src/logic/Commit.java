package logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Commit {
	
	private LocalDateTime date;
	private String sha;
	private String author;
	private String message;
	private List<String> touchedFiles;
	
	public Commit() {
		this.touchedFiles = new ArrayList<>();
	}
	
	public Commit(String date, String sha, String author, String message) {
		this.date = LocalDate.parse(date).atStartOfDay();
		this.sha = sha;
		this.author = author;
		this.message = message;
		this.touchedFiles = new ArrayList<>();
	}
	
	public Commit(LocalDateTime date, String sha, String author, String message) {
		this.date = date;
		this.sha = sha;
		this.author = author;
		this.message = message;
		this.touchedFiles = new ArrayList<>();
	}
	
	public void setTouchedFiles(List<String> touchedFiles) {
		this.touchedFiles = touchedFiles;
	}
	
	public List<String> getTouchedFiles(){
		return this.touchedFiles;
	}
	
	public void setDate(String date) {
		this.date = LocalDate.parse(date).atStartOfDay();
	}
	
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	public LocalDateTime getDate() {
		return this.date;
	}
	
	public String getSha() {
		return this.sha;
	}
	public String getAuthor() {
		return this.author;
	}
	
	public String getMessage() {
		return this.message;
	}
	
}
