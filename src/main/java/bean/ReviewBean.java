package bean;

public class ReviewBean {
	private String bid;
	private String email;
	private String review;
	
	public ReviewBean(String bid, String email, String review) {
		this.bid = bid;
		this.email = email;
		this.review = review;
	}
	
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	
	
}
