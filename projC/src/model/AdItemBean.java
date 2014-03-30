package model;

public class AdItemBean {
	
	private String number;	
	private String name;
	private int hit;

	public AdItemBean(String number, String name) {
		super();
		this.number = number;
		this.name = name;
		this.hit = 0;
	}

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
	
	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof AdItemBean)) return false;
		
		AdItemBean aib = (AdItemBean) obj;
		return this.getNumber().equals(aib.getNumber()) &&
				this.getName().equals(aib.getName());
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() + this.number.hashCode() + 1234567890;
	}

}
