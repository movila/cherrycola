package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamResult;

public class FoodSYS {
	
	// Attributes ---------------------------------------------------------------------
	private static FoodDAO fDao;
	
	// Constructor ---------------------------------------------------------------------
	public FoodSYS() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		fDao = new FoodDAO();
	}
	
	/**
	 * Export xml file based on current order (shopping cart)
	 * 
	 * @param order			The order (shopping cart) to export
	 * @param orderID		Sum of all the order placed
	 * @param filename		The file and its path that about to write to
	 * @param contextPath	Context path of current application
	 * @throws Exception	Marshaling error
	 */
	public void export(Order order, int orderID, String filename, String contextPath) throws Exception {
	
		OrderWrapper ow = new OrderWrapper(orderID,(new SimpleDateFormat("yyyy-M-dd")).format(new Date()), order.getCustomer(),
				new ItemBeanList(order.getItems()), order.getTotal(), order.getShipping(), order.getHST(),
				order.getGrandTotal());
		
		JAXBContext jc = JAXBContext.newInstance(ow.getClass());
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		
		StringWriter sw = new StringWriter();
		sw.write("\n<?xml-stylesheet type=\"text/xsl\" href=\""+contextPath+"/Food.xsl\"?>\n");
		marshaller.marshal(ow, new StreamResult(sw));
		
		FileWriter fw = new FileWriter(filename);
		fw.write(sw.toString());
		fw.close();
	}
	
	/**
	 * Given clientID and 'export' folder path, find the related PO number with current client;
	 * if clientID is -1, return sum of all the files in 'export' folder.
	 * 
	 * @param clientID	Client number
	 * @param folder	Real path to the 'export' folder
	 * @return			Client P/O number; otherwise, total files in folder
	 */
	public int getMaxOrMaxPO(int clientID, final File folder) {
		int i = 1;
	    for (final File fileEntry : folder.listFiles()) 
	        if (!fileEntry.isDirectory()) 
	        	if (clientID == -1 || clientID == Integer.parseInt(fileEntry.getName().split("_")[0].substring(2))) 
	        		i++;
	    return i;
	}
	
	/**
	 * Given the 'export' folder real path, unmarshal all the xml files back to POJO,
	 * and consolidates all the P/O orders to a <item-number, total-quantity> map
	 * 
	 * @param filepath	'export' folder path
	 * @return			aggregated <item-number, total-quantity> map
	 */
	public Map<String, Integer> unmarshalToMap(String filepath, String type) {
		
		Map<String, Integer> itemMap = new HashMap<String, Integer>();
		File folder = new File(filepath);
		
		//++++++++++++++++++++++++++++++++++++++
		Calendar currentCalendar = Calendar.getInstance();
		long diffDate = 0;
		//++++++++++++++++++++++++++++++++++++++
		
		for (final File fileEntry : folder.listFiles()) {
			 if (!fileEntry.isDirectory()) {
				 try {
					 JAXBContext jc = JAXBContext.newInstance(OrderWrapper.class);
					 Unmarshaller unmarshaller = jc.createUnmarshaller();
					 OrderWrapper ow = (OrderWrapper) unmarshaller.unmarshal(new File(filepath + "/" + fileEntry.getName()));
					 
					 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					 Calendar pastCalendar = Calendar.getInstance();
					 DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					 pastCalendar.setTime(df.parse(ow.getSubmitted()));
					 
					 switch(type) {
					 	case "NIGHTLY":
					 		diffDate = (currentCalendar.getTimeInMillis() - pastCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000);
					 		break;
					 	case "WEEKLY":
					 		diffDate = (currentCalendar.getTimeInMillis() - pastCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000 * 7) ;
					 		break;
					 }
					 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					 
					 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					 if(diffDate <= 0){
						 List<ItemBean> items = ow.getItems().getItems();
						 for (ItemBean item: items) {
							 String number = item.getNumber();
							 int qty = item.getQuantity();
							 
							 if (itemMap.containsKey(number))
								 qty += itemMap.get(number);
							 
							 itemMap.put(number, qty);
						 }
					 }
					 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 } catch (JAXBException e) {
					 e.printStackTrace();
				 } catch (ParseException e){				
					e.printStackTrace();
				}
			 }
		}
	       
		return itemMap;
	}
	
	/**
	 * With given <itemNumber, quantity> map, connect to YHZ, YYZ, YVR servers
	 * and retrieve the minimum price provider. Then place the order.
	 * 
	 * @param map	<itemNumber, quantity> maps
	 * return		List of B2B bean with min-price, name of wholesaler
	 */
	public List<B2BBean> requestB2B(Map<String, Integer> map) throws Exception {
		List<B2BBean> b2b = new LinkedList<B2BBean>();
		
		
		String[] tnsArr = {"YHZ", "YYZ", "YVR"};

		SOAPMessage msg = MessageFactory.newInstance().createMessage();
		
		MimeHeaders header = msg.getMimeHeaders();
		header.addHeader("SOAPAction", "");
		
		SOAPPart soap = msg.getSOAPPart();
		SOAPEnvelope envelope = soap.getEnvelope();
		SOAPBody body = envelope.getBody();
		
		
		SOAPConnection sc = null;
		SOAPMessage resp = null;	
		
		for (Map.Entry<String, Integer> entry: map.entrySet()) {
			
			
			// GET Minimum price and provider ------------------------------------
			body.addChildElement("quote").addChildElement("itemNumber").setTextContent(entry.getKey());
			double minPrice = Double.MAX_VALUE;
			String minProvider = "";
			String itemName = "";
			
			for (String tns: tnsArr) {
				sc = SOAPConnectionFactory.newInstance().createConnection();
				resp = sc.call(msg, new URL("http://red.cse.yorku.ca:4413/axis/" + tns + ".jws"));
				sc.close();
				
				double resValue = Double.parseDouble(resp.getSOAPBody().getElementsByTagName("quoteReturn").item(0).getTextContent());
				
				if (resValue >= 0) {
					minProvider = Math.min(minPrice, resValue) == resValue ? tns : minProvider;
					minPrice = Math.min(minPrice, resValue);
				}
				
				msg.writeTo(System.out);
				resp.writeTo(System.out);
				
			}
			
			System.out.println("\n==================================================================\nFor item: " + entry.getKey() 
								+ " Min price is " + minPrice + " provided by " + minProvider 
								+ ".\n==================================================================\n");
			body.removeContents();

			
			// GET name ------------------------------------
			body.addChildElement("getName").addChildElement("itemNumber").setTextContent(entry.getKey());
			
			sc = SOAPConnectionFactory.newInstance().createConnection();
			resp = sc.call(msg, new URL("http://red.cse.yorku.ca:4413/axis/" + minProvider + ".jws"));
			sc.close();
			itemName = resp.getSOAPBody().getElementsByTagName("getNameReturn").item(0).getTextContent();
			System.out.println("NEW CONNECTION -----------------------------");
			msg.writeTo(System.out);
			resp.writeTo(System.out);
			System.out.println("\nNEW CONNECTION /-----------------------------\n\n\n\n");
			body.removeContents();

			
			// Place Order ------------------------------------
			SOAPElement seOrder = body.addChildElement("order");
			seOrder.addChildElement("itemNumber").setTextContent(entry.getKey());
			seOrder.addChildElement("quantity").setTextContent(""+entry.getValue());
			seOrder.addChildElement("key").setTextContent("cse83111p7");
			
			sc = SOAPConnectionFactory.newInstance().createConnection();
			resp = sc.call(msg, new URL("http://red.cse.yorku.ca:4413/axis/" + minProvider + ".jws"));
			sc.close();
			
			System.out.println("NEW CONNECTION -----------------------------");
			msg.writeTo(System.out);
			resp.writeTo(System.out);
			System.out.println("\nNEW CONNECTION /-----------------------------\n\n\n\n");
			body.removeContents();
			
			switch (minProvider) {
				case "YYZ":
					minProvider = "Toronto";
					break;
				case "YVR":
					minProvider = "Vancouver";
					break;
				case "YHZ":
					minProvider = "Halifax";
					break;
			}
			
			b2b.add(new B2BBean(entry.getKey(), itemName, minPrice, entry.getValue(), minProvider));
		}
		
		
		return b2b;
	}
	
	
	/**
	 * Validate item attributes, syntactical checking
	 * 
	 * @param number	item number
	 * @param name		item name
	 * @param price		item price
	 * @param qty		item quantity
	 * @return			true; otherwise, false
	 */
	public boolean itemValidation(String number, String name, String price, String qty) {
		if (number == null || name == null || price == null || qty == null) 
			return false;
		
		if (!number.matches("[\\w&&[^_]]{8,8}")) 
			return false;
		
		try {
			double d = Double.parseDouble(price);
			double i = Integer.parseInt(qty);
			if (d < 0 || i < 0)
				return false;
			
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Add new item to current shopping cart (order), if item number exists, add up its quantity
	 * 
	 * @param client		current log-in client; otherwise null
	 * @param newItemBean	new item to add to cart
	 * @param order			current order
	 * @return				new order with updated item
	 */
	public Order addToOrder(ClientBean client, ItemBean newItemBean, Order order) {
		// TODO orderID and submitted-date only set when P/O file generated
		//String currentDate = (new SimpleDateFormat("yyyy-M-dd")).format(new Date());
		// create a new order for current session, then return
		if (order == null) {
			List<ItemBean> list = new LinkedList<ItemBean>();
			list.add(newItemBean);
			
			double total = newItemBean.getExtended();
			double shipCost = newItemBean.getExtended() >= 100 ? 0.0 : 5.0;
			double HST = (total + shipCost) * 0.13;
			double grandTotal = total + shipCost + HST;
			
			return new Order(0, null, client, list, total, shipCost, HST, grandTotal);
		}
		// if client log-in-ed, ADD ORDER 
		if (order.getCustomer() == null && client != null)
			order.setCustomer(client);
		List<ItemBean> itemList = order.getItems();
		boolean performedUpdate = false;
		// UPDATE item to existing ORDER item-list
		for (ItemBean item: itemList) {
			if (item.getNumber().equals(newItemBean.getNumber())) { 
				// UPDATE ITEM BEAN
				int newQty = item.getQuantity() + newItemBean.getQuantity();
				double newExtended = item.getPrice() * newQty;
				item.setQuantity(newQty);
				item.setExtended(newExtended);
				// UPDATE ORDER
				order.setTotal(order.getTotal() + newItemBean.getExtended());
				updateTotalPrice(order);
				
				performedUpdate = true;
				break;
			}
		}
		// if no existing match, then ADD item to existing ORDER
		if (!performedUpdate) { 
			itemList.add(newItemBean);
			// UPDATE ORDER
			order.setTotal(order.getTotal() + newItemBean.getExtended());
			updateTotalPrice(order);
		}
		return order;
	}
	
	/**
	 * Delete the given array of items from current order
	 * 
	 * @param delList	List of items to delete
	 * @param order		Current shopping cart
	 * @return			Updated order with items removed
	 */
	public Order delFromCart(String[] delList, Order order) {
		if (delList == null || order == null) return order;
		
		for (String delNumber: delList) {
			int delIndex = 0;
			for (ItemBean item: order.getItems()) {
				if (item.getNumber().equals(delNumber)) { 
					break;
				}
				delIndex++;
			}
			
			if (delNumber == null) break;
			double delEx = order.getItems().get(delIndex).getExtended();
			order.setTotal(order.getTotal() - delEx);
			updateTotalPrice(order);
			order.getItems().remove(delIndex);
			
		}
		
		return order;
	}
	
	/**
	 * Override items with new quantity in the shopping cart
	 * 
	 * @param itemID	item number
	 * @param newQty	new quantity
	 * @param order		updated order with new item quantity
	 */
	public void updateCart(String itemID, int newQty, Order order) {
		List<ItemBean> list = order.getItems();

		for (ItemBean item: list) {
			if (itemID.equals(item.getNumber())) {
				int diffQty = newQty - item.getQuantity();
				
				item.setQuantity(newQty);
				item.setExtended(item.getPrice() * newQty);
				
				order.setTotal(order.getTotal() + item.getPrice()*diffQty);
				updateTotalPrice(order);
			}
		}
	}
	
	/**
	 * Add an advertisement relation map, with given promoted product ID and
	 * target product ID
	 * 
	 * @param map			Many-to-many (one-to-many with backwards searchability) relationship map
	 * @param promoteID		Promoted product ID
	 * @param relatedID		Target product ID
	 * @throws Exception
	 */
	public void addAdRelation(Map<AdItemBean, List<AdItemBean>> map, String promoteID, String relatedID) throws Exception {
		// Validation
		if (map == null) 
			throw new Exception("RUN FRONT CONTROLLER FIRST, to initialize adMap");

		if (promoteID == null || relatedID == null)
			throw new Exception("Invalide input, not item found");
		
		if (!promoteID.matches("\\d{4,4}[a-zA-Z]\\d{3,3}") || !relatedID.matches("\\d{4,4}[a-zA-Z]\\d{3,3}"))
			throw new Exception("Invalide input, not item found");
		
		
		AdItemBean promote = null, related = null;
		// Retrieve 'promote' Advertisement Bean
		List<ItemBean> retrieveItem = retrieveItem(promoteID, "search");
		if (retrieveItem.size() > 0)
			promote = new AdItemBean(promoteID, retrieveItem.get(0).getName());
		else
			throw new Exception("Invalide input, not item found");
		// Retrieve 'related' Advertisement Bean
		retrieveItem = retrieveItem(relatedID, "search");
		if (retrieveItem.size() > 0)
			related = new AdItemBean(relatedID, retrieveItem.get(0).getName());
		else
			throw new Exception("Invalide input, not item found");
		
		// Add related product promotion
		if (map.containsKey(promote)) {
			if (!map.get(promote).contains(related))
				map.get(promote).add(related);
		} else {
			List<AdItemBean> relatedList = new LinkedList<AdItemBean>();
			relatedList.add(related);
			map.put(promote, relatedList);
		}
		
	}
	
	/**
	 * Given current relate list, return the mapped advertisement in a list,
	 * when maximum is given, the top matched max-number-of items will returned.
	 * 
	 * @param adMap			Advertise relationship map
	 * @param order			Current order
	 * @param maxReturned	The maximum number of items returned
	 * @return				List of mapped advertisement
	 */
	public List<AdItemBean> getAdItems(Map<AdItemBean, List<AdItemBean>> adMap, Object reList, int maxReturned) {
		List<AdItemBean> promotionList = new LinkedList<AdItemBean>();
		
		

		if (reList instanceof Map) {
			
			
			Map<String, Integer> rmap = (Map<String, Integer>) reList;
			
	        ValueComparator bvc =  new ValueComparator(rmap);
	        TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
	        sorted_map.putAll(rmap);
	        System.out.println("results: "+sorted_map);
	        
	        int i = 0;
	        for (Map.Entry<String, Integer> entry: sorted_map.entrySet()) {
	        	
	        	try {
					promotionList.add(new AdItemBean(entry.getKey(), retrieveItem(entry.getKey(), "search").get(0).getName()));
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        	
	        	if (i >= maxReturned) break;
	        	i++;
	        }
	        
	        return promotionList;
	        
	  

		} else if (reList instanceof Order) {
			
			
			List<ItemBean> cartList = new LinkedList<ItemBean>();
			if (adMap == null) return null;
	
			Order order = (Order) reList;
			cartList = order.getItems();
			
			for (ItemBean cartItem: cartList) {
				System.out.println("===========AD===========\n" + cartItem.getNumber());
				for (Map.Entry<AdItemBean, List<AdItemBean>> entry: adMap.entrySet()) {
					AdItemBean promoted = entry.getKey();
					for (AdItemBean related: entry.getValue()) {
						
						if (cartItem.getNumber().equals(related.getNumber())) {
							if (promotionList.contains(promoted)) {
								AdItemBean temp = promotionList.get(promotionList.indexOf(promoted));
								temp.setHit(temp.getHit() + 1);
							} else {
								promotionList.add(promoted);
							}
						}
						
					}
				}
			}
			
			
			Collections.sort(promotionList, new Comparator<AdItemBean>() {
		        public int compare(AdItemBean o1, AdItemBean o2) {
		            return o2.getHit() - o1.getHit();
		        }
			});

			
			if (promotionList.size() > maxReturned)
				return promotionList.subList(0, maxReturned);
			else
				return promotionList;
			
			
			
		}
			
		
		return null;
		
	}
	
	// Methods (private helper methods) -------------------------------------------------------------------------
	private void updateTotalPrice(Order order) {
		order.setShipping(order.getTotal() >= 100 ? 0.0 : 5.0);
		order.setHST((order.getTotal() + order.getShipping()) * 0.13);
		order.setGrandTotal(order.getTotal() + order.getShipping() + order.getHST());
	}

	
	// Methods (validate and Hide database access) ---------------------------------------------------------------------
	/**
	 * @see FoodDAO#generateImage(String)
	 */
	public void generateImage(String path) throws SQLException, IOException {
		fDao.generateImage(path);
	}
	
	/**
	 * @see FoodDAO#itemImageGenerator(String)
	 */
	public int itemImageGenerator(String outPath) throws SQLException, IOException { 
		return fDao.itemImageGenerator(outPath);
	}
	
	/**
	 * @see FoodDAO#clientValidation(String, String)
	 */
	public ClientBean clientValidation(String name, String passwd) throws SQLException {
		if (name == null || passwd == null) return null;
		
		return fDao.clientValidation(name, passwd);
	}
	
	/**
	 * @see FoodDAO#retrieveCategory()
	 */
	public List<CategoryBean> retrieveCategory() throws SQLException {
		return fDao.retrieveCategory();
	}
	
	/**
	 * @see FoodDAO#retrieveItem(String, String)
	 */
	public List<ItemBean> retrieveItem(String identifier, String sender) throws SQLException {
		if (identifier != null)
			identifier = identifier.matches("/\\d+/?") ? identifier.substring(1) : identifier;
		
		return fDao.retrieveItem(identifier, sender);
	}
	
}




class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
