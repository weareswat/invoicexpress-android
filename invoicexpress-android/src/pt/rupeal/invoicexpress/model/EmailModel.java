package pt.rupeal.invoicexpress.model;

import java.io.Serializable;

public class EmailModel implements Serializable {

	private static final long serialVersionUID = 4769792458741428728L;

	public static final String EMAIL = "email";
	
	private String to;
	private String subject;
	private String body;
	
	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public static String getEmail() {
		return EMAIL;
	}

}
