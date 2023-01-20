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

import bean.ReviewBean;

public class ReviewDOA {
	DataSource ds;

	public ReviewDOA() throws ClassNotFoundException {
		try {
			this.ds = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public Map<String, ReviewBean> retrieveReviewByID(String productID) throws SQLException {
		String query = "select * from reviews where bid like '%" + productID + "%'";
		return retreiveQuery(query);
	}

	public void addReview(String productID,String email, String review) throws SQLException {
		String query = "INSERT INTO reviews(bid, email, review) VALUES (?,?,?)";
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		p.setString(1, productID);
		p.setString(2,email);
		p.setString(3, review);
		System.out.println(email);
		p.executeUpdate();
	    p.close();
		
		
	}
	
	private Map<String, ReviewBean> retreiveQuery(String query) throws SQLException {
		Map<String, ReviewBean> rv = new HashMap<String, ReviewBean>();
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			String bid = r.getString("bid");
			String email = r.getString("email");
			String review = r.getString("review");
			rv.put(bid + " : " + email, new ReviewBean(bid, email, review));
		}
		r.close();
		p.close();
		con.close();
		return rv;
	}

}
