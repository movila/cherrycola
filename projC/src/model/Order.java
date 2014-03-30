package model;

import java.text.DecimalFormat;
import java.util.List;

public class Order {
	
	private int id;
	private String smbmitted;
	private ClientBean customer;
	private List<ItemBean> items;
	private double total;
	private double shipping;
	private double HST;
	private double grandTotal;
	
	public Order(int id, String smbmitted, ClientBean customer,
			List<ItemBean> items, double total, double shipping, double hST,
			double grandTotal) {
		super();
		this.id = id;
		this.smbmitted = smbmitted;
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

	public void setId(int id) {
		this.id = id;
	}

	public String getSmbmitted() {
		return smbmitted;
	}

	public void setSmbmitted(String smbmitted) {
		this.smbmitted = smbmitted;
	}

	public ClientBean getCustomer() {
		return customer;
	}

	public void setCustomer(ClientBean customer) {
		this.customer = customer;
	}

	public List<ItemBean> getItems() {
		return items;
	}

	public void setItems(List<ItemBean> items) {
		this.items = items;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		DecimalFormat f = new DecimalFormat("#.00");
		this.total = Double.parseDouble(f.format(total));
	}

	public double getShipping() {
		return shipping;
	}

	public void setShipping(double shipping) {
		this.shipping = shipping;
	}

	public double getHST() {
		return HST;
	}

	public void setHST(double hST) {
		DecimalFormat f = new DecimalFormat("#.00");
		HST = Double.parseDouble(f.format(hST));
	}

	public double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(double grandTotal) {
		DecimalFormat f = new DecimalFormat("#.00");
		this.grandTotal = Double.parseDouble(f.format(grandTotal));
	}

	
}
