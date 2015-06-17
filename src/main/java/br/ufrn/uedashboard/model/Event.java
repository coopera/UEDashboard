package br.ufrn.uedashboard.model;

public class Event {
	
	private int id;
	private int id_commit;
	private String description;
	private String type;
	private String message;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId_commit() {
		return id_commit;
	}
	
	public void setId_commit(int id_commit) {
		this.id_commit = id_commit;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
}
