package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.EventBean;
import bean.UserBean;

public class UserDOA {
	
	DataSource ds;
	public UserDOA() throws ClassNotFoundException {
		try {
			this.ds = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	public Map<String, UserBean> retrieveUser(String email) throws SQLException{
		String query = "select * from users where email=?";
		Map<String, UserBean> rv = new HashMap<String, UserBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, email);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			String name = r.getString("fname") + ", " + r.getString("lname");
			String address = r.getString("address");
			String phoneNum = r.getString("phone");
			String password = r.getString("pwd");
			String province = r.getString("province");
			String country = r.getString("country");
			String zip = r.getString("zip");
			int role = r.getInt("role");
			int status = r.getInt("status");
			rv.put(email, new UserBean(role, status ,name, email, password, address, province, country, zip,phoneNum));
		}
		r.close();
		p.close();
		con.close();
		return rv;
	}
	
	public int updateStatus(String email, int status) throws SQLException{
		String preparedStatement = "update users set status=? where email=?";
		Connection con = this.ds.getConnection();
		PreparedStatement stmt = con.prepareStatement(preparedStatement);
		
		stmt.setInt(1, status);
		stmt.setString(2, email);
		return status;
	}

	public int createUser(int role, int status, String lname, String fname, String email, String pwd, String address, String province, String country, String zip, String phone) throws SQLException, NamingException{
		String preparedStatement = "insert into users values(?,?,?,?,?,?,?,?,?,?,?)";
		Connection con = this.ds.getConnection();
		PreparedStatement stmt = con.prepareStatement(preparedStatement);

		stmt.setInt(1, role);
		stmt.setInt(2, status);
		stmt.setString(3, lname);
		stmt.setString(4, fname);
		stmt.setString(5, email);
		stmt.setString(6, pwd);
		stmt.setString(7, address);
		stmt.setString(8, province);
		stmt.setString(9, country);
		stmt.setString(10, zip);
		stmt.setString(11, phone);
		return stmt.executeUpdate();
	}
	public int recordEvent(String ip, String stringDate, String bid, String eventType) throws SQLException {
		
		String preparedStatement = "insert into VisitEvent values(?,?,?,?)";
		Connection con = this.ds.getConnection();
		PreparedStatement stmt = con.prepareStatement(preparedStatement);

		stmt.setString(1, ip);
		stmt.setString(2, stringDate);
		stmt.setString(3, bid);
		stmt.setString(4, eventType);
		return stmt.executeUpdate();
	}
	public List<EventBean> getEventsByBid(String bid) throws SQLException {
		String query = "select * from VisitEvent where bid=?";
		List<EventBean> rv = new ArrayList<EventBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, bid);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			String ip = r.getString("ipaddress");
			String day = r.getString("day");
			String typeEvent = r.getString("eventtype");
			rv.add(new EventBean(ip, day, bid, typeEvent));
		}
		r.close();
		p.close();
		con.close();
		return rv;
	}
}
