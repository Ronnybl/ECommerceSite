package bean;

public class UserBean {
	private int role;
	private int status;
	private String name;
	private String email;
	private String password;
	private String address;
	private String province;
	private String country;
	private String zip;
	private String phoneNum;

	public UserBean(int role, int status, String name, String email, String password, String address, String province,
			String country, String zip, String phoneNum) {
		super();
		this.role = role;
		this.status = status;
		this.name = name;
		this.email = email;
		this.password = password;
		this.address = address;
		this.province = province;
		this.country = country;
		this.zip = zip;
		this.phoneNum = phoneNum;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public void changeStatus() {
		// Status 0 = Signed out Status 1 = Signed In
		if (this.status == 0) {
			this.status = 1;
		} else {
			this.status = 0;
		}
	}
}
