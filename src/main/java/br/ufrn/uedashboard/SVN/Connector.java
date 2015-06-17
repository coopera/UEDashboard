package br.ufrn.uedashboard.SVN;

public class Connector<T> {
	
	private String url;
	private String user;
	private String password;
	private T encapsulation;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public T getEncapsulation() {
		return encapsulation;
	}

	public void setEncapsulation(T encapsulation) {
		this.encapsulation = encapsulation;
	}

}
