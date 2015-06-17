package br.ufrn.uedashboard.model;

import java.util.Date;
import java.util.List;

public class Commit {
	
	private int id;
	private String developer;
	private String comment;
	private Date date;
	private long revision;
	private List<Modification> modifications;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeveloper() {
		return developer;
	}
	
	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public List<Modification> getModifications() {
		return modifications;
	}

	public void setModifications(List<Modification> modifications) {
		this.modifications = modifications;
	}

}
