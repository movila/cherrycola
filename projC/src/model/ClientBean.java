package model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ClientBean {
	
	private int number;
	private String name;
	
	public ClientBean() {}
	
	public ClientBean(int number, String name) {
		this.number = number;
		this.name = name;
	}
	
	@XmlAttribute(name="account")
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	@XmlElement
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
