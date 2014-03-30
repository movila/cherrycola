package model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="order")
public class OrderWrapper
{
	@XmlAttribute
	private int id;
	@XmlAttribute
	private String submitted;
	@XmlElement
	private ClientBean customer;
	@XmlElement
	private ItemBeanList items;
	@XmlElement
	private double total;
	@XmlElement
	private double shipping;
	@XmlElement
	private double HST;
	@XmlElement
	private double grandTotal;
	
	public OrderWrapper() {
	}

	public OrderWrapper(int id, String submitted, ClientBean customer,
			ItemBeanList items, double total, double shipping, double hST,
			double grandTotal) {
		super();
		this.id = id;
		this.submitted = submitted;
		this.customer = customer;
		this.items = items;
		this.total = total;
		this.shipping = shipping;
		HST = hST;
		this.grandTotal = grandTotal;
	}

	public int getId() {
		return id;
	}

	public String getSubmitted() {
		return submitted;
	}

	public ClientBean getCustomer() {
		return customer;
	}

	public ItemBeanList getItems() {
		return items;
	}

	public double getTotal() {
		return total;
	}

	public double getShipping() {
		return shipping;
	}

	public double getHST() {
		return HST;
	}

	public double getGrandTotal() {
		return grandTotal;
	}

	

}
