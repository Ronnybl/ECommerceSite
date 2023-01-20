package ctrl;

import java.io.IOException;
import java.io.Writer;

import java.sql.SQLException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.CartBean;
import bean.EventBean;
import bean.ItemBean;
import bean.ReviewBean;
import bean.UserBean;
import dao.ItemDOA;
import model.EcommModel;

/**
 * Servlet implementation class ecomm
 */
@WebServlet({"/Ecomm", "/Ecomm/*"})
public class Ecomm extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Ecomm() {
        super();
        // TODO Auto-generated constructor stub
    }
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	//The context object is initialized with data from web.xml
    	ServletContext context = getServletContext();
		EcommModel ecomm;
		try {
			ecomm = EcommModel.getInstance();
			context.setAttribute("mEcomm", ecomm);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext context = getServletContext();
    	Writer resOut = response.getWriter();
    	EcommModel model = (EcommModel) context.getAttribute("mEcomm");
    	
    	if (request.getSession().getAttribute("cart") != null) {
			CartBean cart = (CartBean) request.getSession().getAttribute("cart");
		}
    	else {
        	CartBean cart = new CartBean("-1", new HashMap<ItemBean, Integer>(), 0, 0);
    		request.getSession().setAttribute("cart", cart);
    	}

    	CartBean cart = (CartBean) request.getSession().getAttribute("cart");
    	if (request.getParameter("register") != null && request.getParameter("register").equals("true")) {
    		//Role 0 = admin Role 1 = customer
    		String role = "1";
    		String lastName = request.getParameter("lname");
    		String firstName = request.getParameter("fname");
    		String email = request.getParameter("email");
    		String pwd = request.getParameter("pwd");
    		String address = request.getParameter("address");
    		String phone = request.getParameter("phone");
    		String province = request.getParameter("province");
    		String country = request.getParameter("country");
    		String zip = request.getParameter("zip");
    		String registerResult = userRegister(response, model, cart, role, lastName, firstName, email, pwd, address, province, country, zip, phone);
    		resOut.write(registerResult);
    		resOut.flush();
    	}
    	else if (request.getParameter("signin") != null && request.getParameter("signin").equals("true")) {
    		String email = request.getParameter("email");
    		String pwd = request.getParameter("pwd");
    		boolean signStatus = userSignIn(model, email, pwd);
    		response.setContentType("application/json");
			String joinString = "";
			joinString += ("{\"status\":");
			joinString += ("" + signStatus + "");
    		if (signStatus == true) {
    			cart.setCustID(email);
    			joinString +=(",\"email\":");
    			joinString +=("\"" + email+ "\"");
    			try {
					UserBean user = model.retrieveUserByEmail(email);
					if (user != null) {
	    				joinString +=(",\"role\":");
	        			joinString +=("\"" + user.getRole()+ "\"");
	    			}
				} catch (SQLException e) {
					e.printStackTrace();
				}
    		}
    		joinString += "}";
    		resOut.write(joinString);
    		resOut.flush();
    		
    	}
    	else if (request.getParameter("isSigned") != null && request.getParameter("isSigned").equals("true")) {
			response.setContentType("application/json");
    		String jsonForm = "";
    		jsonForm += ("{\"email\":");
    		jsonForm +=("\"" + cart.getCustID() + "\"}");
    		resOut.write(jsonForm);
    		resOut.flush();
    	}
    	else if (request.getParameter("logout") != null && request.getParameter("logout").equals("true")) {
    		cart.setCustID("-1");
    		cart.clearCart();
    		String email = request.getParameter("email");
    		String logoutResults = logout(model, email);
    		response.setContentType("application/json");
    		resOut.write(logoutResults);
    		resOut.flush();
    	}
    	else if (request.getParameter("loadmain") != null && request.getParameter("loadmain").equals("true")) {
    		String products = generateFrontPage(model, response);
    		resOut.append(products);
    		resOut.flush();
    		
    	}else if (request.getParameter("filtered") != null && request.getParameter("filtered").equals("true")) {
    		String products = new String();
    		if (request.getParameter("brand") != null ) {
				String brand = request.getParameter("brand");
				products = generateFilteredFrontPage(brand, model,response, true);
    		}else if (request.getParameter("type") != null ) { 
    			if (request.getParameter("type").equals("low")) {
    				products = this.lowToHigh(model, response, true);
    			}
    			else if (request.getParameter("type").equals("high")) {
    				products = this.lowToHigh(model, response, false);
    			}else {
    				String type = request.getParameter("type");
    				products = generateFilteredFrontPage(type, model,response, false);
    			}
    		}
    		resOut.append(products);
    		resOut.flush();
    	}
    	else if(request.getParameter("purchaseproduct") != null && request.getParameter("purchaseproduct").equals("true")) {
    		String prodID = request.getParameter("bid");
    		String email = request.getParameter("email");
    		String productPurchase = purchasePage(model, response, prodID);
    		try {
				recordEvent(model, request.getRemoteAddr(), prodID, "VIEW");
			} catch (SQLException e) {
				e.printStackTrace();
			}
    		resOut.append(productPurchase);
    		resOut.flush();
    	}
    	else if (request.getParameter("printReviews") != null && request.getParameter("printReviews").equals("true")) {
    		String prodID = request.getParameter("bid");
    		String reviews = getReviews(model,response,prodID);
    		resOut.append(reviews);
    		resOut.flush();
    	}else if (request.getParameter("addReview") != null && request.getParameter("addReview").equals("true")) {
    		String prodID = request.getParameter("bid");
    		String review = request.getParameter("review");
    		String email  = request.getParameter("email");
    		try {
				model.addReview(prodID, email, review);
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    	else if(request.getParameter("addToCart") != null && request.getParameter("addToCart").equals("true")) {
    		String bid = request.getParameter("bid");
    		int quantity = Integer.parseInt(request.getParameter("quantity"));
    		ItemBean addedItem = model.addToCart(bid, quantity);
    		cart.addItem(addedItem, quantity);
    		try {
				recordEvent(model, request.getRemoteAddr(), bid, "CART");
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    	else if(request.getParameter("removeFromCart") != null && request.getParameter("removeFromCart").equals("true")) {
    		String bid = request.getParameter("bid");
    		ItemBean removedItem = model.removeFromCart(bid);
    		cart.removeItem(removedItem);
    		String cartInfo = cartPage(model, response, cart);
    		System.out.println(cartInfo);
    		resOut.append(cartInfo);
    		resOut.flush();
    	}
    	
    	else if(request.getParameter("loadCart") != null && request.getParameter("loadCart").equals("true")) {
    		String cartInfo = cartPage(model, response, cart);
    		resOut.append(cartInfo);
    		resOut.flush();
    	}
    	else if(request.getParameter("loadAddress") != null && request.getParameter("loadAddress").equals("true")) {
    		//String email = request.getParameter("email");
    		String userInfo = loadAddress(model, response, cart.getCustID());
    		resOut.append(userInfo);
    		resOut.flush();
    	}
    	else if(request.getParameter("confirmPurchase") != null && request.getParameter("confirmPurchase").equals("true")) {
    		String email = request.getParameter("email");
    		String fname = request.getParameter("fname");
    		String lname = request.getParameter("lname");
    		String address = request.getParameter("address");
    		String phone = request.getParameter("phone");
    		String province = request.getParameter("province");
    		String country = request.getParameter("country");
    		String zip = request.getParameter("zip");
    		int orderID = -1;
			try {
				orderID = nextOrderID(model);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
    		confirmPurchase(model, orderID, address, phone, province, country, zip);
    		confirmOrder(model, orderID, fname, lname);
    		String jsonString = "";
    		jsonString += ("{\"orderID\":");
    		jsonString +=("\"" + orderID+ "\"}");
    		try {
				updateInventory(model, orderID, request.getRemoteAddr(), cart);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			context.setAttribute("orderID", orderID);
    		response.setContentType("application/json");
    		resOut.write(jsonString);
    		resOut.flush();
    	}
    	else if(request.getParameter("retrieveStats") != null && request.getParameter("retrieveStats").equals("true")) {
    		String bid = request.getParameter("bid");
    		String itemStats;
			try {
				itemStats = retrieveItemStats(model, bid);
				response.setContentType("application/json");
	    		resOut.write(itemStats);
	    		resOut.flush();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    	else if(request.getParameter("generalStats") != null && request.getParameter("generalStats").equals("true")) {
    		try {
				String stats = generalInfo(model);
				response.setContentType("application/json");
	    		resOut.write(stats);
	    		resOut.flush();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    		
    	}
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private String userRegister(HttpServletResponse response, EcommModel model, CartBean cart, String role, String lastName, String firstName, String email, String pwd, String address, String province, String country, String zip, String phone) {
		UserBean userBean;
		int status = model.createUser(role, lastName, firstName, email, pwd, address, province, country, zip, phone);
		response.setContentType("application/json");
		String jsonString = "";
		jsonString += ("{\"status\":");
		jsonString += (status + "");
		if(status == 1) {
			jsonString +=(",\"email\":");
			jsonString +=("\"" + email+ "\"");
		}
		jsonString += "}";
		if (status == 1) {
			cart.setCustID(email);
		}
		return jsonString;
	}
	
	private String lowToHigh( EcommModel model, HttpServletResponse response, Boolean is_low ) {
		try {
			LinkedHashMap<String, ItemBean> randProducts = null;
			if (is_low) {
				randProducts = model.retrieveItemLowToHigh();
			}else {
				randProducts = model.retrieveItemHighToLow();
			}
			
			response.setContentType("application/json");
			String joinString = "";
			if (randProducts != null) {
				Set keys = randProducts.keySet();
				String[] jsonList = new String[keys.size()];
				int counter = 0;
				for (Object key : keys) {
					ItemBean product = (ItemBean) randProducts.get(key);
        			String jsonForm = "";
    				jsonForm += ("{\"bid\":");
    				jsonForm +=("\"" + product.getBid() + "\"");
    				jsonForm +=(",\"name\":");
    				jsonForm +=("\"" + product.getName() + "\"");
    				jsonForm +=(",\"description\":");
    				jsonForm +=("\"" + product.getDescription() + "\"");
    				jsonForm +=(",\"type\":");
    				jsonForm +=("\"" + product.getType() + "\"");
    				jsonForm +=(",\"brand\":");
    				jsonForm +=("\"" + product.getBrand() + "\"");
    				jsonForm +=(",\"quantity\":");
    				jsonForm +=("" + product.getQuantity() + "");
    				jsonForm +=(",\"price\":");
    				jsonForm +=("" + product.getPrice() + "");
    				jsonForm += ("}");
    				jsonList[counter] = jsonForm;
    				counter++;
    			}
			joinString = "["+ String.join(",", jsonList) + "]";
			}
			return joinString;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String generateFilteredFrontPage(String filter, EcommModel model, HttpServletResponse response, Boolean is_brand) {
		try {
			Map randProducts = null;
			if (is_brand) {
				randProducts = model.retrieveItemByBrand(filter);
			}else {
				randProducts = model.retrieveItemByType(filter);
			}
			
			response.setContentType("application/json");
			String joinString = "";
			if (randProducts != null) {
				Set keys = randProducts.keySet();
				String[] jsonList = new String[keys.size()];
				int counter = 0;
				for (Object key : keys) {
					ItemBean product = (ItemBean) randProducts.get(key);
        			String jsonForm = "";
    				jsonForm += ("{\"bid\":");
    				jsonForm +=("\"" + product.getBid() + "\"");
    				jsonForm +=(",\"name\":");
    				jsonForm +=("\"" + product.getName() + "\"");
    				jsonForm +=(",\"description\":");
    				jsonForm +=("\"" + product.getDescription() + "\"");
    				jsonForm +=(",\"type\":");
    				jsonForm +=("\"" + product.getType() + "\"");
    				jsonForm +=(",\"brand\":");
    				jsonForm +=("\"" + product.getBrand() + "\"");
    				jsonForm +=(",\"quantity\":");
    				jsonForm +=("" + product.getQuantity() + "");
    				jsonForm +=(",\"price\":");
    				jsonForm +=("" + product.getPrice() + "");
    				jsonForm += ("}");
    				jsonList[counter] = jsonForm;
    				counter++;
    			}
			
			joinString = "["+ String.join(",", jsonList) + "]";
			}
			return joinString;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String generateFrontPage(EcommModel model, HttpServletResponse response) {
		try {
			Map randProducts = model.generateStore();
			response.setContentType("application/json");
			String joinString = "";
			if (randProducts != null) {
				Set keys = randProducts.keySet();
				String[] jsonList = new String[keys.size()];
				int counter = 0;
				for (Object key : keys) {
					ItemBean product = (ItemBean) randProducts.get(key);
        			String jsonForm = "";
    				jsonForm += ("{\"bid\":");
    				jsonForm +=("\"" + product.getBid() + "\"");
    				jsonForm +=(",\"name\":");
    				jsonForm +=("\"" + product.getName() + "\"");
    				jsonForm +=(",\"description\":");
    				jsonForm +=("\"" + product.getDescription() + "\"");
    				jsonForm +=(",\"type\":");
    				jsonForm +=("\"" + product.getType() + "\"");
    				jsonForm +=(",\"brand\":");
    				jsonForm +=("\"" + product.getBrand() + "\"");
    				jsonForm +=(",\"quantity\":");
    				jsonForm +=("" + product.getQuantity() + "");
    				jsonForm +=(",\"price\":");
    				jsonForm +=("" + product.getPrice() + "");
    				jsonForm += ("}");
    				jsonList[counter] = jsonForm;
    				counter++;
    			}
			
			joinString = "["+ String.join(",", jsonList) + "]";
			}
			return joinString;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String purchasePage(EcommModel model, HttpServletResponse response, String pid) {
		try {
			Map productPurchase = model.retrieveItemByID(pid);
			response.setContentType("application/json");
			String joinString = "";
			if (productPurchase != null) {
				Set keys = productPurchase.keySet();
				String[] jsonList = new String[keys.size()];
				int counter = 0;
				for (Object key : keys) {
					if(key.equals(pid)) {
						ItemBean product = (ItemBean) productPurchase.get(key);
						String jsonForm = "";
	        			jsonForm += ("{\"bid\":");
	    				jsonForm +=("\"" + product.getBid() + "\"");
	    				jsonForm +=(",\"name\":");
	    				jsonForm +=("\"" + product.getName() + "\"");
	    				jsonForm +=(",\"description\":");
	    				jsonForm +=("\"" + product.getDescription() + "\"");
	    				jsonForm +=(",\"type\":");
	    				jsonForm +=("\"" + product.getType() + "\"");
	    				jsonForm +=(",\"brand\":");
	    				jsonForm +=("\"" + product.getBrand() + "\"");
	    				jsonForm +=(",\"quantity\":");
	    				jsonForm +=("" + product.getQuantity() + "");
	    				jsonForm +=(",\"price\":");
	    				jsonForm +=("" + product.getPrice() + "");
	    				jsonForm += ("}");
	    				jsonList[counter] = jsonForm;
	    				counter++;
					}
					joinString = "["+ String.join(",", jsonList) + "]";
    			}
				return joinString;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String getReviews(EcommModel model, HttpServletResponse response, String pid) {
		try {
			Map<String, ReviewBean> productPurchase = model.retrieveReviewByID(pid);
			response.setContentType("application/json");
			String joinString = "";
			if (productPurchase != null) {
				Set keys = productPurchase.keySet();
				String[] jsonList = new String[keys.size()];
				int counter = 0;
				for (Object key : keys) {
					ReviewBean product = (ReviewBean) productPurchase.get(key);
        			String jsonForm = "";
        			jsonForm += ("{\"bid\":");
    				jsonForm +=("\"" + product.getBid() + "\"");
    				jsonForm +=(",\"email\":");
    				jsonForm +=("\"" + product.getEmail() + "\"");
    				jsonForm +=(",\"review\":");
    				jsonForm +=("\"" + product.getReview() + "\"");
    				jsonForm += ("}");
    				jsonList[counter] = jsonForm;
    				counter++;
    			}
			
			joinString = "["+ String.join(",", jsonList) + "]";
			}
			return joinString;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String cartPage(EcommModel model, HttpServletResponse response, CartBean cart) {
		response.setContentType("application/json");
		String joinString = "";
		Map<ItemBean, Integer> items = cart.getItemsPurchased();
		if (items != null) {
			Set keys = items.keySet();
			String[] jsonList = new String[keys.size()];
			int counter = 0;
			for (Object key : keys) {
				int quantity = items.get(key);
    			String jsonForm = "";
    			jsonForm += ("{\"bid\":");
				jsonForm +=("\"" + ((ItemBean) key).getBid() + "\"");
				jsonForm +=(",\"name\":");
				jsonForm +=("\"" + ((ItemBean) key).getName() + "\"");
				jsonForm +=(",\"description\":");
				jsonForm +=("\"" + ((ItemBean) key).getDescription() + "\"");
				jsonForm +=(",\"brand\":");
				jsonForm +=("\"" + ((ItemBean) key).getBrand() + "\"");
				jsonForm +=(",\"quantity\":");
				jsonForm +=("" + quantity + "");
				jsonForm +=(",\"price\":");
				jsonForm +=("" + ((ItemBean) key).getPrice() + "");
				jsonForm +=(",\"itemtotal\":");
				jsonForm +=("" + (((ItemBean) key).getPrice() * quantity) + "");
				jsonForm += ("}");
				jsonList[counter] = jsonForm;
				counter++;
			}
			joinString = "["+ String.join(",", jsonList) + "]";
		}
		return joinString;
	}
	private String loadAddress(EcommModel model, HttpServletResponse response, String email) {
		try {
			UserBean user = model.retrieveUserByEmail(email);
			response.setContentType("application/json");
			String joinString = "";
			joinString += ("{\"address\":");
			joinString +=("\"" + user.getAddress() + "\"");
			joinString +=(",\"province\":");
			joinString +=("\"" + user.getProvince()+ "\"");
			joinString +=(",\"country\":");
			joinString +=("\"" + user.getCountry() + "\"");
			joinString +=(",\"zip\":");
			joinString +=("\"" + user.getZip() + "\"");
			joinString +=(",\"name\":");
			joinString +=("\"" + user.getName()+ "\"");
			joinString +=(",\"email\":");
			joinString +=("\"" + user.getEmail()+ "\"");
			joinString +=(",\"phone\":");
			joinString +=("\"" + user.getPhoneNum()+ "\"");
			joinString += ("}");
			return joinString;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private int confirmPurchase(EcommModel model, int id, String address, String phone, String province, String country, String zip) {
		return model.confirmPurchase(id, address, phone, province, country, zip);
	}
	private int confirmOrder(EcommModel model, int id, String fname, String lname) {
		return model.confirmOrder(id, fname, lname);
	}
	private int updateInventory(EcommModel model, int orderID, String ip, CartBean cart) throws SQLException {
		Map<ItemBean, Integer> cartItems= cart.getItemsPurchased();
		Set keys = cartItems.keySet();
		for (Object key : keys) {
			recordEvent(model, ip, ((ItemBean) key).getBid(), "PURCHASE");
			recordItemPurchaseOrder(model, orderID, ((ItemBean) key).getBid(), cartItems.get(key) , ((ItemBean) key).getPrice());
		}
		return model.updateInventory(cartItems);
	}

	private int recordEvent(EcommModel model, String ip, String bid, String eventType) throws SQLException {
		int month = java.time.LocalDate.now().getMonth().getValue();
		int day = java.time.LocalDate.now().getDayOfMonth();
		int year = java.time.LocalDate.now().getYear();
		String stringDate = "" + month + day + year;
		return model.recordEvent(ip, stringDate, bid, eventType);
	}
	private int recordItemPurchaseOrder(EcommModel model, int orderID, String bid, int quantity, int price) throws SQLException {
		return model.recordIPO(orderID, bid, quantity, price);
	}
	
	private String retrieveItemStats(EcommModel model, String bid) throws SQLException {
		List<EventBean> stats = model.retrieveItemStats(bid);
		int countView = 0;
		int countCart = 0;
		int countPurchase = 0;
		for (EventBean event : stats) {
			if(event.getEventType().equals("VIEW")) {
				countView++;
			}
			else if(event.getEventType().equals("CART")) {
				countCart++;
			}
			else if(event.getEventType().equals("PURCHASE")) {
				countPurchase++;
			}
		}
		int totalSales = model.calculateSales(bid);
		String joinString = "";
		joinString += ("{\"viewcount\":");
		joinString +=("" + countView + "");
		joinString +=(",\"cartcount\":");
		joinString +=("" + countCart + "");
		joinString +=(",\"purchasecount\":");
		joinString +=("" + countPurchase + "");
		joinString +=(",\"totalItemSales\":");
		joinString +=("" + totalSales + "}");
		
		return joinString;
	}
	private String generalInfo(EcommModel model) throws SQLException {
		int[] totalInfo = model.generalInfo();
		String joinString = "";
		joinString += ("{\"totalQuantity\":");
		joinString +=("" + totalInfo[0] + "");
		joinString +=(",\"totalSales\":");
		joinString +=("" + totalInfo[1] + "}");
		return joinString;
	}
	
	private String logout(EcommModel model, String email) {
		int userSignOut = model.logOutUser(email);
		String jsonString = "";
		jsonString += ("{\"status\":");
		jsonString += (userSignOut + "}");
		return jsonString;
	}
	
	private int nextOrderID(EcommModel model) throws SQLException {
		int nextID = model.fetchOrderID() + 1;
		return nextID;
	}

	private boolean userSignIn(EcommModel model, String email, String pwd) {
		try {
			return model.retrieveUser(email, pwd);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
