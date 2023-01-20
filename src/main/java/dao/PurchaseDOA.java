package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.ItemBean;
import bean.POItemBean;
import bean.UserBean;

public class PurchaseDOA {
	DataSource ds;
	
	public PurchaseDOA() throws ClassNotFoundException {
		try {
			this.ds = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	public int createPurchase(int id, String address, String province, String country, String zip, String phone) throws SQLException, NamingException{
		String preparedStatement = "insert into address values(?,?,?,?,?,?)";
		Connection con = this.ds.getConnection();
		PreparedStatement stmt = con.prepareStatement(preparedStatement);

		stmt.setInt(1, id);
		stmt.setString(2, address);
		stmt.setString(3, province);
		stmt.setString(4, country);
		stmt.setString(5, zip);
		stmt.setString(6, phone);
		return stmt.executeUpdate();
	}
	public int createOrder(int id, String fname, String lname, String status) throws SQLException {
		String preparedStatement = "insert into PO values(?,?,?,?,?)";
		Connection con = this.ds.getConnection();
		PreparedStatement stmt = con.prepareStatement(preparedStatement);

		stmt.setInt(1, id);
		stmt.setString(2, fname);
		stmt.setString(3, lname);
		stmt.setString(4, status);
		stmt.setInt(5, id);
		return stmt.executeUpdate();
	}
	public int createItemPO(int orderID, String bid, int quantity, int price) throws SQLException{
		String preparedStatement = "insert into POItem values(?,?,?,?)";
		Connection con = this.ds.getConnection();
		PreparedStatement stmt = con.prepareStatement(preparedStatement);

		stmt.setInt(1, orderID);
		stmt.setString(2, bid);
		stmt.setInt(3, quantity);
		stmt.setInt(4, price);
		return stmt.executeUpdate();
	}
	public int soldItemAmount(String bid) throws SQLException {
		int amount = 0;
		
		String query = "select * from poitem where bid=?";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, bid);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			amount += r.getInt("amount");
		}
		r.close();
		p.close();
		con.close();
		return amount;
	}
	public Map<String, POItemBean> soldAllItemAmount() throws SQLException {
		String query = "select * from poitem";
		Map<String, POItemBean> rv = new HashMap<String, POItemBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			String bid = r.getString("bid");
			int orderID = r.getInt("id");
			int amount = r.getInt("amount");
			int price= r.getInt("price");
			rv.put(bid, new POItemBean(orderID, bid, amount, price));
		}
		r.close();
		p.close();
		con.close();
		return rv;
	}
	public int getOrderID() throws SQLException {
		String query = "select * from po";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		int orderID = 0;
		while (r.next()) {
			int currID = r.getInt("id");
			if (orderID < currID) {
				orderID = currID;
			}
		}
		r.close();
		p.close();
		con.close();
		return orderID;
	}
}