package logic;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Commit {
	
	private LocalDateTime date;
	private String sha;
	private String author;
	private String message;
	
	public Commit() {}
	
	public Commit(String date, String sha, String author, String message) {
		this.date = LocalDate.parse(date).atStartOfDay();
		this.sha = sha;
		this.author = author;
		this.message = message;
	}
	
	public Commit(LocalDateTime date, String sha, String author, String message) {
		this.date = date;
		this.sha = sha;
		this.author = author;
		this.message = message;
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
