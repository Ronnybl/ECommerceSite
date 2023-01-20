package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.ItemBean;

public class ItemDOA {
	DataSource ds;
	
	public ItemDOA() throws ClassNotFoundException {
		try {
			this.ds = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public Map<String, ItemBean> retrieveItemByName(String iName) throws SQLException {
		String query = "select * from item where name like '%" + iName + "%'";
		return retreiveQuery(query);
	}
	
	public Map<String, ItemBean> retrieveItemByID(String productID) throws SQLException {
		String query = "select * from item where bid like '%" + productID + "%'";
		return retreiveQuery(query);
	}

	public Map<String, ItemBean> retrieveItemByPrice(String iName, double iPrice) throws SQLException {
		String query = "select * from item where name like '%" + iName + "%' and price <= " + iPrice;
		return retreiveQuery(query);
	}
	
	public Map<String, ItemBean> retrieveItemByType(String itemType) throws SQLException {
		String query = "select * from item where type like '%" + itemType + "%'";
		return retreiveQuery(query);
	}
	
	public Map<String, ItemBean> retrieveItemByBrand(String itemBrand) throws SQLException {
		String query = "select * from item where brand like '%" + itemBrand + "%'";
		return retreiveQuery(query);
	}
	
	public LinkedHashMap<String, ItemBean> retrieveItemLowToHigh() throws SQLException {
		String query = "select * from item order by price ASC";
		return retreiveQueryKeepOrder(query);
	}
	
	public LinkedHashMap<String, ItemBean> retrieveItemHighToLow() throws SQLException {
		String query = "select * from item order by price DESC";
		return retreiveQueryKeepOrder(query);
	}
	


	private Map<String,ItemBean> retreiveQuery(String query) throws SQLException{
		Map<String, ItemBean> rv = new HashMap<String, ItemBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			String bid = r.getString("bid");
			String name = r.getString("name");
			String description = r.getString("description");
			String type = r.getString("type");
			String brand = r.getString("brand");
			int quantity = r.getInt("quantity");
			int price = r.getInt("price");
			rv.put(bid, new ItemBean(bid, name, description, type, brand, quantity, price));
		}
		r.close();
		p.close();
		con.close();
		return rv;
	}
	
	private LinkedHashMap<String,ItemBean> retreiveQueryKeepOrder(String query) throws SQLException{
		LinkedHashMap<String, ItemBean> rv = new LinkedHashMap<String, ItemBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			String bid = r.getString("bid");
			String name = r.getString("name");
			String description = r.getString("description");
			String type = r.getString("type");
			String brand = r.getString("brand");
			int quantity = r.getInt("quantity");
			int price = r.getInt("price");
			rv.put(bid, new ItemBean(bid, name, description, type, brand, quantity, price));
		}
		r.close();
		p.close();
		con.close();
		return rv;
	}
	
	
	public Map<String, ItemBean> generateStore() throws SQLException {
		String query = "select * FROM item";
		Map<String, ItemBean> rv = new HashMap<String, ItemBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			String bid = r.getString("bid");
			String name = r.getString("name");
			String description = r.getString("description");
			String type = r.getString("type");
			String brand = r.getString("brand");
			int quantity = r.getInt("quantity");
			int price = r.getInt("price");
			rv.put(bid, new ItemBean(bid, name, description, type, brand, quantity, price));
		}
		r.close();
		p.close();
		con.close();
		return rv;
	}
	public int retrieveItemPrice(String bid) throws SQLException {
		int price = 0;
		String query = "select * from item where bid=?";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, bid);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			price = r.getInt("price");
			return price;
		}
		r.close();
		p.close();
		con.close();
		return price;
	}

	public int updateQuantity(String bid, Integer quantity) throws SQLException {
		String preparedStatement = "update item SET quantity=? where bid=?";
		Connection con = this.ds.getConnection();
		PreparedStatement stmt = con.prepareStatement(preparedStatement);

		stmt.setInt(1, quantity);
		stmt.setString(2, bid);
		return stmt.executeUpdate();
		
	}
}
