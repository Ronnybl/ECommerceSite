package bean;

public class POItemBean {
	private int orderID;
	private String bid;
	private int amount;
	private int price;
	public POItemBean(int orderID, String bid, int amount, int price) {
		super();
		this.orderID = orderID;
		this.bid = bid;
		this.amount = amount;
		this.price = price;
	}
	public int getOrderID() {
		return orderID;
	}
	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}
