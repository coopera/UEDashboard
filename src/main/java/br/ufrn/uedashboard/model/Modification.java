package br.ufrn.uedashboard.model;

public class Modification {
	
	private int id;
	private String path;
	private String type;
	private int idCommit;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getIdCommit() {
		return idCommit;
	}

	public void setIdCommit(int idCommit) {
		this.idCommit = idCommit;
	}

}
