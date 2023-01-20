package bean;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CartBean {
	private String custID;
	private Map<ItemBean, Integer> itemsPurchased;
	private int numItems;
	private int totalCost;

	public CartBean(String custID, Map<ItemBean, Integer> itemsPurchased, int numItems, int totalCost) {
		super();
		this.custID = custID;
		this.itemsPurchased = itemsPurchased;
		this.numItems = numItems;
		this.totalCost = totalCost;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public Map<ItemBean, Integer> getItemsPurchased() {
		return itemsPurchased;
	}

	public void setItemsPurchased(Map<ItemBean, Integer> itemsPurchased) {
		this.itemsPurchased = itemsPurchased;
	}
	
	public boolean addItem(ItemBean item, int quantity) {
		Set keys = this.itemsPurchased.keySet();

		for (Object key : keys) {
			if(item.getBid().equals(((ItemBean) key).getBid())) {
				System.out.println("provided ID " + item.getBid() + "KEY ID" + ((ItemBean) key).getBid());
				int newQuantity = this.itemsPurchased.get(key) + quantity;
				if (newQuantity <= item.getQuantity()) { 
					this.itemsPurchased.remove(((ItemBean)key));
					this.itemsPurchased.put(item, newQuantity);
					this.numItems += quantity;
					this.totalCost += (quantity * item.getPrice());
					System.out.println("{CART BEAN} Item Amount updated " + item.getName() + " updated number:" + newQuantity + " numItems " + this.numItems + " cost= " + + this.getTotalCost());
					return true;
				}
			}
		}
		this.itemsPurchased.put(item, quantity);

		this.numItems += quantity;
		this.totalCost += (quantity * item.getPrice());
		System.out.println("{CART BEAN} Item added " + item.getName() + " numItems " + this.numItems + " cost= " + + this.getTotalCost());
		return true;
	}
	
	public void removeItem(ItemBean removedItem) {
		Set keys = this.itemsPurchased.keySet();

		for (Object key : keys) {
			if(removedItem.getBid().equals(((ItemBean) key).getBid())) {
				this.itemsPurchased.remove((ItemBean)key);
				System.out.println("{CART BEAN} Removed Item");
			}
		}
	}
	public void clearCart() {
		this.itemsPurchased.clear();
		this.numItems = 0;
		this.totalCost = 0;
	}

	public int getNumItems() {
		return numItems;
	}

	public void setNumItems(int numItems) {
		this.numItems = numItems;
	}

	public int getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(int totalCost) {
		this.totalCost = totalCost;
	}
}
