var userrole = 1;

function loadcart(address){
	var request = new XMLHttpRequest();		
	request.open("GET", (address), true);
	request.onreadystatechange = function() {
		formatcart(request);
	};
	request.send(null);
}

function removeFromCart(address){
	var request = new XMLHttpRequest();		
	request.open("GET", (address), true);
	request.onreadystatechange = function() {
		formatcart(request);
	};
	request.send(null);
}

function formatcart(request){
	if ((request.readyState == 4) && (request.status == 200)){
		let totalCost = 0;
		let numItems = 0;
		var indexEmail = document.getElementById("email");
		var target = document.getElementById("cartContent");
		var rs=JSON.parse(request.responseText);
		var table = document.getElementById("cartContent");
		var text = "<table class=\"table\"><thead><tr><th>Product name</th><th>Brand</th><th>Description</th><th>Price</th><th>Quantity</th><th>Product Total</th></tr></thead>";
		var brands = new Set(); 
		var types  = new Set();
		for (var i = 0; i < rs.length; i++){
			var row = `<tr>
			<td>${rs[i].name}</td>
			<td>${rs[i].brand}</td>
			<td>${rs[i].description}</td>
			<td>$${rs[i].price}</td> 
			<td>${rs[i].quantity}</td>
			<td>$${rs[i].itemtotal}<button onClick="removeFromCart('/GroupProject/Ecomm?removeFromCart=true&bid=${rs[i].bid}');" class="btn btn-sm btn-light">Remove</button></td> 
			</tr>`;
			text += row;
			totalCost += rs[i].itemtotal;
			numItems += rs[i].quantity;
		}
		text += "</table>";
		table.innerHTML = text; 
		document.getElementById("itemNum").innerHTML = " &nbsp&nbspNumber of Items: " + numItems;
		document.getElementById("cost").innerHTML = " &nbsp&nbspTotal Cost: $" + totalCost;
		
		if (numItems > 0){
			document.getElementById("checkoutButton").innerHTML = `<button class="btn btn-sml btn-primary" onClick="verifyUser('/GroupProject/Ecomm?isSigned=true');">Proceed to Checkout</button>`;
		}
	}
}
function verifyUser(address){
	var request = new XMLHttpRequest();
	var data = '';
	request.open("GET", (address), true);
	request.onreadystatechange = function() {
		verification(request);
	};
	request.send(null);
}
function verification(request){
	if ((request.readyState == 4) && (request.status == 200)){
		var target = document.getElementById("cartContent");
		var rs=JSON.parse(request.responseText);
		if (rs.email != '-1'){
			window.location.href = ("./CheckoutPage.html?email=" + rs.email);
		}
		else{
			window.location.href = ("./SignIn.html?code=6");
		}
	}
	
}
function loadaddress(address){
	var request = new XMLHttpRequest();		
	var indexEmail = document.URL.indexOf('email');
	var email = document.URL.substring(indexEmail + 6, document.URL.length);
	var indexRole = document.URL.indexOf('role');
	window.userrole = document.URL.substring(indexRole + 5, indexEmail);
	var data = '';
	data += "email=" + email;
	
	request.open("GET", (address + "&" + data), true);
	request.onreadystatechange = function() {
		showGivenAddress(request);
	};
	request.send(null);
}

function showGivenAddress(request){
	if ((request.readyState == 4) && (request.status == 200)){
		var rs=JSON.parse(request.responseText);
		document.getElementById("name").innerHTML = `${rs.name}`;
		document.getElementById("phone").innerHTML = `${rs.phone}`;
		document.getElementById("email").innerHTML = `${rs.email}`;
		document.getElementById("address").innerHTML = `${rs.address}`;
		document.getElementById("province").innerHTML =`${rs.province}`;
		document.getElementById("country").innerHTML =`${rs.country}`;
		document.getElementById("zip").innerHTML = `${rs.zip}`;
	}
}

function showNewAddress(){
	var checkDiff = document.getElementById("different");
	var diffAddress = document.getElementById("newAddress");
	var sameAddress = document.getElementById("userAddress");
	if (checkDiff.checked){
		diffAddress.style.display = "block";
		sameAddress.style.display = "none";
	}
	else{
		diffAddress.style.display = "none";
		sameAddress.style.display = "block";
	}
}

function confirmPurchase(address){
	var request = new XMLHttpRequest();		
	var index = document.URL.indexOf('email');
	var email = document.URL.substring(index + 6, document.URL.length);
	var data = '';
	var ok = true;
	var diffAddress = document.getElementById("different");
	var indexComma =  document.getElementById("name").innerHTML.indexOf(',');
	var firstName = document.getElementById("name").innerHTML.substring(0, indexComma);
	var lastName = document.getElementById("name").innerHTML.substring(indexComma + 2);
	console.log("First Name:" + firstName + " Last Name:" + lastName);
	data += "email=" + document.getElementById("email").innerHTML + "&";
	data += "fname=" + firstName + "&";
	data += "lname=" + lastName + "&";
	data += "phone=" + document.getElementById("phone").innerHTML + "&";
	console.log(diffAddress.checked);
	if (diffAddress.checked){
		var textField = document.getElementById("diffaddress").value;
		console.log("textField=" + textField);
		if (textField == ""){
			alert("Please Fill in the Address");
			ok = false;
		}
		else {
			data += "address=" + document.getElementById("diffaddress").value + "&";
		}
		textField = document.getElementById("diffprovince").value;
		console.log("textField=" + textField);

		if (textField == ""){
			alert("Please Fill in the Province");
			ok = false;
		}
		else {
			data += "province=" + document.getElementById("diffprovince").value + "&";
		}
		textField = document.getElementById("diffcountry").value;
				console.log("textField=" + textField);
		if (textField == ""){
			alert("Please Fill in the Country");
			ok = false;
		}
		else {
			data += "country=" + document.getElementById("diffcountry").value + "&";
		}
		textField = document.getElementById("diffzip").value;
				console.log("textField=" + textField);

		if (textField == ""){
			alert("Please Fill in the Zip code");
			ok = false;
		}
		else{
			data += "zip=" + document.getElementById("diffzip").value;
		}
		
	}
	else{
		var textField = document.getElementById("address").value;
		if (textField == ""){
			alert("Please Fill in the Address");
			ok = false;
		}
		else{
			data += "address=" + document.getElementById("address").innerHTML + "&";
		}
		textField = document.getElementById("province").value;
		if (textField == ""){
			alert("Please Fill in the Province");
			ok = false;
		}
		else{
			data += "province=" + document.getElementById("province").innerHTML + "&";
		}
		textField = document.getElementById("country").value;
		if (textField == ""){
			alert("Please Fill in the Country");
			ok = false;
		}
		else{
			data += "country=" + document.getElementById("country").innerHTML + "&";
		}
		textField = document.getElementById("zip").value;
		if (textField == ""){
			alert("Please Fill in the Zip code");
			ok = false;
		}
		else{
			data += "zip=" + document.getElementById("zip").innerHTML;
		}

	}
	textField = document.getElementById("cardNum").value;
	if (textField == ""){
		alert("Please Fill in the Card Number");
		ok = false;
	}
	textField = document.getElementById("creditName").value;
	if (textField == ""){
		alert("Please Fill in the Cardholder Name");
		ok = false;
	}
	textField = document.getElementById("securityNumber").value;
	if (textField == ""){
		alert("Please Fill in the 3 digits on the back");
		ok = false;
	}
	textField = document.getElementById("expiryDate").value;
	if (textField == ""){
		alert("Please Fill in the expiry date in the format of MM/YY");
		ok = false;
	}
	if(ok === true){
		console.log("WENT FINE");
		request.open("GET", (address + "&" + data), true);
		request.onreadystatechange = function() {
			handlePurchase(request);
		};
		request.send(null);
	}
}

function handlePurchase(request){
	if ((request.readyState == 4) && (request.status == 200)){
		console.log("request recieved");
		var rs=JSON.parse(request.responseText);
		console.log("ID:" + rs.orderID);
		alert(`Order has been placed, your Order ID is ${rs.orderID}`);
		window.location.href = ("./MainPage.html?email=" + rs.email +  "&role=" + window.userrole );
	}
}