package model;

public class B2BBean {
	private String number;
	private String name;
	private double price;
	private double qty;
	private String wholeSaler;
	
	public B2BBean(String number, String name, double price, double qty, String wholeSaler) {
		super();
		this.number = number;
		this.name = name;
		this.price = price;
		this.qty = qty;
		this.wholeSaler = wholeSaler;
	}

	public String getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}
	
	public double getQty() {
		return qty;
	}

	public String getWholeSaler() {
		return wholeSaler;
	}
	
}
