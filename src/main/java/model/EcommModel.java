package model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import bean.EventBean;
import bean.ItemBean;
import bean.POItemBean;
import bean.ReviewBean;
import bean.UserBean;
import dao.ItemDOA;
import dao.PurchaseDOA;
import dao.ReviewDOA;
import dao.UserDOA;

public class EcommModel {
	
	private static EcommModel instance;
	
	private UserDOA userdoa;
	private ItemDOA itemdoa;
	private PurchaseDOA purchasedoa;
	private ReviewDOA reviewdoa;
	
	public static EcommModel getInstance() throws ClassNotFoundException{
		if (instance == null) {
			instance = new EcommModel();
			instance.userdoa = new UserDOA();
			instance.itemdoa = new ItemDOA();
			instance.purchasedoa = new PurchaseDOA();
			instance.reviewdoa = new ReviewDOA();
		}
		return instance;
	}
	private EcommModel() {
		
	}
	//----------------------------------------------------------User-Related----------------------------------------------------------------------------------

	public boolean retrieveUser(String email, String pwd) throws SQLException{
		if (email == null || pwd == null) {
			return false;
		}
		//Check for SQL Injection
		if(!checkSQLInjection(email + pwd)) {
			return false;
		}
		else {
			Map<String, UserBean> user = userdoa.retrieveUser(email);
			//Checks if any user was found
			if (user.get(email) != null) {
				//If a user was found then the password is retrieved and compared
				String password = user.get(email).getPassword();
				if(password.equals(pwd)) {
					userdoa.updateStatus(email, 1);
					return true;
				}
			}
			return false;
		}
	}
	
	public UserBean retrieveUserByEmail(String email) throws SQLException{
		if (email == null) {
			return null;
		}
		//Check for SQL Injection
		if(!checkSQLInjection(email)) {
			return null;
		}
		else {
			Map<String, UserBean> user = userdoa.retrieveUser(email);
			//Checks if any user was found
			if (user.get(email) != null) {
				return user.get(email);
			}
			return null;
		}
	}
	
	public int createUser(String role, String lastName, String firstName, String email, String pwd, String address, String province, String country, String zip, String phone) {
		int roleInt = Integer.parseInt(role);
		if(!checkSQLInjection(lastName + firstName + email + pwd + address + phone)) {
			//-2 will symbolize SQL Injection
			return -2;
		}
		else {
			try {
				return userdoa.createUser(roleInt, 1, lastName, firstName, email, pwd, address, province, country, zip, phone);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		//-1 will symbolize some error
		return -1;
	}
	public int logOutUser(String email) {
		if(email == null) {
			return -1;
		}
		
		if (!checkSQLInjection(email)) {
			return -2;
		}
		else {
			try {
				return userdoa.updateStatus(email, 0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	public int recordEvent(String ip, String stringDate, String bid, String eventType) throws SQLException {
		
		return userdoa.recordEvent(ip, stringDate, bid, eventType);
	}
	//----------------------------------------------------------item-Related----------------------------------------------------------------------------------
	public Map<String, ItemBean> retrieveItemByName(String pName) throws SQLException{
		if(pName == null) {
			return null;
		}
		
		if (!checkSQLInjection(pName)) {
			return null;
		}
		else {
			return itemdoa.retrieveItemByName(pName);
		}
	}
	public Map<String, ItemBean> retrieveItemByID(String itemID) throws SQLException{
		if(itemID == null) {
			return null;
		}
		
		if (!checkSQLInjection(itemID)) {
			return null;
		}
		else {
			return itemdoa.retrieveItemByID(itemID);
		}
	}
	
	public Map<String, ItemBean> retrieveItemByType(String type) throws SQLException {
		if(type == null) {
			return null;
		}
		
		if (!checkSQLInjection(type)) {
			return null;
		}
		else {
			return itemdoa.retrieveItemByType(type);
		}
	}
	
	public Map<String, ItemBean> retrieveItemByBrand(String brand) throws SQLException {
		if(brand == null) {
			return null;
		}
		
		if (!checkSQLInjection(brand)) {
			return null;
		}
		else {
			return itemdoa.retrieveItemByBrand(brand);
		}
	}
	
	public LinkedHashMap<String, ItemBean> retrieveItemLowToHigh() throws SQLException {
		return itemdoa.retrieveItemLowToHigh();

	}
	
	public LinkedHashMap<String, ItemBean> retrieveItemHighToLow() throws SQLException {
		return itemdoa.retrieveItemHighToLow();
	}
	
	public Map<String, ItemBean> retrieveItemByPrice(String pName, double price) throws SQLException{
		if(pName == null) {
			return null;
		}
		
		if (!checkSQLInjection(pName)) {
			return null;
		}
		else {
			return itemdoa.retrieveItemByPrice(pName, price);
		}
	}
	
	public Map<String, ItemBean> generateStore() throws SQLException{
		return itemdoa.generateStore();
	}
	
	
	public int recordIPO(int orderID, String bid, int quantity, int price) throws SQLException {
		if (!checkSQLInjection(bid)) {
			return -2;
		}
		return purchasedoa.createItemPO(orderID, bid, quantity, price);
	}
	
	public int calculateSales(String bid) throws SQLException {
		if (!checkSQLInjection(bid)) {
			return -2;
		}
		int soldAmount = purchasedoa.soldItemAmount(bid);
		int price = itemdoa.retrieveItemPrice(bid);
		return soldAmount * price;
	}
	public int[] generalInfo() throws SQLException {
		Map<String, POItemBean> totalAmount = purchasedoa.soldAllItemAmount();
		Set keys = totalAmount.keySet();
		int counterSales = 0;
		int counterQuantity = 0;
		for (Object key : keys) {
			counterQuantity += totalAmount.get(key).getAmount();
			counterSales += (totalAmount.get(key).getAmount() * totalAmount.get(key).getPrice());
		}
		int[] totals = {counterQuantity, counterSales}; 
		return totals;
	}
	public int fetchOrderID() throws SQLException {
		return purchasedoa.getOrderID();
	}
	//--------------------------------------------------------Cart-Related----------------------------------------------------
	
	
	
	//--------------------------------------------------------Review-related-----------------------------------------------------
	public Map<String, ReviewBean> retrieveReviewByID(String itemID) throws SQLException{
		if(itemID == null) {
			return null;
		}
		
		if (!checkSQLInjection(itemID)) {
			return null;
		}
		else {
			return reviewdoa.retrieveReviewByID(itemID);
		}
	}
	
	public void addReview(String itemID, String email, String review) throws SQLException{
		reviewdoa.addReview(itemID, email, review);
	}
	
	//--------------------------------------------------------Review-related-----------------------------------------------------
	
	public ItemBean addToCart(String bid, int quantity) {
		if(bid == null || quantity <= 0) {
			return null;
		}
		
		if (!checkSQLInjection(bid + quantity)) {
			return null;
		}
		else {
			try {
				Map<String, ItemBean> itemSearched = itemdoa.retrieveItemByID(bid);
				ItemBean addedItem = itemSearched.get(bid);
				if (quantity > addedItem.getQuantity()) {
					return null;
				}
				else {
					return addedItem;
				}
			} catch (SQLException e) {
				System.out.println("Could not retrieve item with bid=" + bid);
			}
		}
		return null;
	}
	
	public ItemBean removeFromCart(String bid) {
		if(bid == null) {
			return null;
		}
		
		if (!checkSQLInjection(bid)) {
			return null;
		}
		else {
			try {
				Map<String, ItemBean> itemSearched = itemdoa.retrieveItemByID(bid);
				ItemBean removedItem = itemSearched.get(bid);
				return removedItem;
			} catch (SQLException e) {
				System.out.println("Could not retrieve item with bid=" + bid);
			}
		}
		return null;
	}
	
	public int confirmPurchase(int id, String address, String phone, String province, String country, String zip) {
		if(address == null || phone == null || province == null || country == null || zip == null) {
			return -1;
		}
		else if(!checkSQLInjection(address + phone + province + country + zip)) {
			return -2;
		}
		else {
			try {
				int status = purchasedoa.createPurchase(id, address, province, country, zip, phone);
				return status;
			} catch (SQLException | NamingException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	public int confirmOrder(int id, String fname, String lname) {
		if (fname == null || lname == null) {
			return -1;
		}
		else if(!checkSQLInjection(fname + lname)) {
			return -2;
		}
		else {
			try {
				System.out.println("{MODEL}First name: " + fname + " Last name: " + lname);
				//Unsure what the different statuses meant
				int status = purchasedoa.createOrder(id, fname, lname, "ORDERED");
				return status;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	public int updateInventory(Map<ItemBean, Integer> cartItems) throws SQLException {
		if (cartItems != null) {
			Set keys = cartItems.keySet();
			for (Object key : keys) {
				Map<String, ItemBean> item = retrieveItemByID(((ItemBean) key).getBid());
				int currInv = item.get(((ItemBean) key).getBid()).getQuantity();
				itemdoa.updateQuantity(((ItemBean) key).getBid(), currInv - cartItems.get(key));
			}
		}
		return 0;
	}
	public List<EventBean> retrieveItemStats(String bid) {
		System.out.println("BID: " + bid);
		if(bid == null) {
			return null;
		}
		
		if (!checkSQLInjection(bid)) {
			return null;
		}
		else {
			try {
				return userdoa.getEventsByBid(bid);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private boolean checkSQLInjection(String input) {
		String inputCheck = input.toLowerCase();
		if(inputCheck == null || inputCheck.equals("") || inputCheck.equals("") || inputCheck.contains("select") || inputCheck.contains("update") || inputCheck.contains("delete") || 
				inputCheck.contains("insert") || inputCheck.contains("create") || inputCheck.contains("alter") || inputCheck.contains("drop")) {
			return false;
		}
		return true;
	}
}
