package model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"name", "price", "quantity", "extended"})
public class ItemBean {
	
	private String number;
	private String name;
	private double price;
	private int quantity;
	private double extended;
	
	public ItemBean() {}
	
	public ItemBean(String number, String name, double price, int quantity,
			double extended) {
		this.number = number;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.extended = extended;
	}
	
	@XmlAttribute
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getExtended() {
		return extended;
	}

	public void setExtended(double extended) {
		this.extended = extended;
	}

}
