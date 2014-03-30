package model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ItemBeanList {
	@XmlElement(name="item")
	private List<ItemBean> items;

	public ItemBeanList() {}
	
	public ItemBeanList(List<ItemBean> items) {
		super();
		this.items = items;
	}

	public List<ItemBean> getItems() {
		return items;
	}
	
	
}
