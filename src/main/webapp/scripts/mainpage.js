var email = "";
var userrole = 1;
function loadpage(address) {
	var request = new XMLHttpRequest();		
	request.open("GET", (address), true);
	request.onreadystatechange = function() {
		formatproducts(request);
	};
	request.send(null);
}



function formatproducts(request){
	if ((request.readyState == 4) && (request.status == 200)){
		var indexEmail = document.URL.indexOf('email');
		var indexRole = document.URL.indexOf('role');
		var email = document.URL.substring(indexEmail + 6, indexRole - 1);
		console.log(indexRole);
		if(email.length == 0 || indexEmail == -1){
            document.getElementById("logouta").style.display="none";
            document.getElementById("logina").style.display="block";
            document.getElementById("admina").style.display="none";
        }
        else {
            document.getElementById("logouta").style.display="block";
            document.getElementById("logina").style.display="none";
            var index = document.URL.indexOf('email');
            var indexRole = document.URL.indexOf('role');
            window.email = document.URL.substring(index + 6, indexRole - 1);
            var indexRole = document.URL.indexOf('role');
            var userrole = document.URL.substring(indexRole + 5, document.URL.length);
            
            if (userrole == 0 ){
                document.getElementById("admina").style.display="block";
            }else {
                document.getElementById("admina").style.display="none";
            }
        }
		
		var target = document.getElementById("randomItem");
		var rs=JSON.parse(request.responseText);
		var table = document.getElementById("randomItem");
		var text = "<table class=\"table\"><thead><tr><th>Product name</th><th>Brand</th><th>Description</th><th>Price</th></tr></thead>";
		var brands = new Set(); 
		var types  = new Set();
		for (var i = 0; i < rs.length; i++){
			var row = `<tr>
			<td>${rs[i].name}</td>
			<td>${rs[i].brand}</td>
			<td>${rs[i].description}</td>
			<td>$${rs[i].price}<a href="ProductPage.html?bid=${rs[i].bid}&email=${window.email}"> <button class="btn btn-sm btn-light">Buy Now!</button></a></td> 
			</tr>`;
			brands.add(rs[i].brand);
			types.add(rs[i].type);
			text += row;
		}
		text += "</table>";
		table.innerHTML = text; 
		formatfilter(request, brands, types)
	}
}

function formatfilter(request, brands, types){
	if ((request.readyState == 4) && (request.status == 200)){
		console.log(request);
		var target = document.getElementById("filters");
		var rs=JSON.parse(request.responseText);
		var table = document.getElementById("filters");
		var text = "<table class=\"table\"><thead><tr><th>Filters</th></tr></thead>"
		
		// Drop down for brands start//
		text += `<tr><td><div class="dropdown">`;
		text += `<button class="btn btn-sm btn-light dropdown-toggle" 
		type="button" 
		data-bs-toggle="dropdown" 
		aria-expanded="false">Brand</button> `;
		
		
		text += '<ul class="dropdown-menu dropdown-menu-dark">'
		
		for (let item of brands.values()){
			text +=`<li><a class="dropdown-item" href="FilteredPage.html?brand=${item}">`+ item +`</a></li>`;
		}
		text += `</ul></div></td></tr>`
		// Drop down for brands end//
		
		// Drop down for types start//
		text += `<tr><td><div class="dropdown">`;
		text += `<button class="btn btn-sm btn-light dropdown-toggle" 
		type="button" 
		data-bs-toggle="dropdown" 
		aria-expanded="false">Type</button>`;
		
		text += 
		'<ul class="dropdown-menu dropdown-menu-dark">'
		
		for (let item of types.values()){
			text +=`<li><a class="dropdown-item" href="FilteredPage.html?type=${item}">`+ item +`</a></li>`;
		}
		text += "</ul></div></td></tr>";
		// Drop down for types end//
		// Drop down for filter by price //
		text += `<tr><td><div class="dropdown">`;
		text += `<button class="btn btn-sm btn-light dropdown-toggle" 
		type="button" 
		data-bs-toggle="dropdown" 
		aria-expanded="false">Filter by Price</button>`;
		
		text += 
		'<ul class="dropdown-menu dropdown-menu-dark">'
		text += `<li><a class="dropdown-item" href="FilteredPage.html?type=low">Lowest to Highest</a></li>`;
		text += `<li><a class="dropdown-item" href="FilteredPage.html?type=high">Highest to Lowest</a></li>`;
		text += "</ul></div></td></tr></table>";
		table.innerHTML = text; 
	}
}


function filtered(address) {
	var data = '';
	var request = new XMLHttpRequest();
	if (document.URL.indexOf('brand') > 0 ){
		var index_of_brand = document.URL.indexOf('brand');
		var brand = document.URL.substring(index_of_brand + 6, document.URL.length);
		data += "brand=" + brand;
	}else if( document.URL.indexOf('type') > 0) {
		var index_of_type = document.URL.indexOf('type');
		var type = document.URL.substring(index_of_type + 5, document.URL.length);
		data += "type=" + type;
	}
	request.open("GET", (address + "&" + data), true);
	request.onreadystatechange = function() {
	formatproducts(request);
		};
	request.send(null);
}


function purchaseproduct(address){
	var request = new XMLHttpRequest();		
	var index = document.URL.indexOf('bid');
	var bid = document.URL.substring(index + 4, document.URL.length);
	var index_email = document.URL.indexOf('email');
	var email = document.URL.substring(index_email + 6, document.URL.length);
	var data = '';
	data += "bid=" + bid + "&email=" + email;
	request.open("GET", (address + "&" + data), true);
	request.onreadystatechange = function() {
	formatpurchase(request);
		};
	request.send(null);
}


function formatpurchase(request) {
	if ((request.readyState == 4) && (request.status == 200)){
		var rs=JSON.parse(request.responseText);
		for (var i = 0; i < rs.length; i++){
			document.getElementById("pageTitle").innerHTML = `${rs[i].name}`;
			document.getElementById("itemTitle").innerHTML = `${rs[i].name}`;
			document.getElementById("itemBrand").innerHTML = `${rs[i].brand}`;
			document.getElementById("itemDesc").innerHTML = `${rs[i].description}`;
			document.getElementById("price").innerHTML =`$${rs[i].price}`;
			document.getElementById("quantity").innerHTML =`In stock: ${rs[i].quantity}`;
		}

	}
}

function loadReviews(address){
	var request = new XMLHttpRequest();		
	var index = document.URL.indexOf('bid');
	var index_email = document.URL.indexOf('email');
	var bid = document.URL.substring(index + 4, index_email - 1 );
	var index_email = document.URL.indexOf('email');
	var email = document.URL.substring(index_email + 6, document.URL.length);
	console.log("loadreviews bid : " + bid);
	var data = '';
	data += "bid=" + bid + "&email=" + email;
	request.open("GET", (address + "&" + data), true);
	request.onreadystatechange = function() {
	printReviews(request);
		};
	request.send(null);
}



function printReviews(request){
		var target = document.getElementById("reviews");
		var index_bid = document.URL.indexOf('bid');
		var bid = document.URL.substring(index_bid + 4, document.URL.indexOf('email') - 1);
		var rs=JSON.parse(request.responseText);
		var table = document.getElementById("reviews");
		console.log("printReviews bid : " + bid)
		var text = "<table class='table'> <thead><tr><th>Name</th><th>review</th></tr></thead>"
		for (var i = 0; i < rs.length; i++){
			if (rs[i].bid == bid){
				text += `<tr><td style="width:10%">${rs[i].email}</td>`
				text += `<td>${rs[i].review}</td>` 
				text += `</tr>`
			}
		}
		text += `</table>`
		table.innerHTML = text; 
}


function addReview(address){
	var request = new XMLHttpRequest();		
	
	var indexEmail = document.URL.indexOf('email');
	var email = document.URL.substring(indexEmail + 6, document.URL.length);

	var index_bid = document.URL.indexOf('bid');
	var bid = document.URL.substring(index_bid + 4, indexEmail - 1);
	
	if (email.length == 0) {
		alert("Must be signed in to submit a review!");
	}else {
		var data = '';
		var indexEmail = document.URL.indexOf('email');
		var email = document.URL.substring(indexEmail + 6, document.URL.length);
		data += "bid=" + bid + "&";
		data += "review=" + document.getElementById("reviewInput").value;
		data += "&email=" +email;
		request.open("GET", (address + "&" + data), true);
		request.onreadystatechange = function() {
		formatpurchase(request);
			};
		request.send(null);
	}
	
}



function addtoCart(address){
	var ok = true;
	var amount = document.getElementById("amount").value;
	var indexInv = document.getElementById("quantity").innerHTML.indexOf(':');
	var inventory = document.getElementById("quantity").innerHTML.substring(indexInv + 1);
	if (isNaN(amount) || amount <= 0) {
		alert("Invalid Amount. Please Enter a valid value");
	}
	else if(inventory - amount < 0){
		alert("Invalid Amount. Order exceeds inventory amount");
	}
	else {
		var index = document.URL.indexOf('bid');
		var indexEmail = document.URL.indexOf('email');
		var bid = document.URL.substring(index + 4, document.URL.length);
		var request = new XMLHttpRequest();
		var data = '';
		data += "bid=" + bid + "&";
		data += "quantity=" + amount;
		request.open("GET", (address + "&" + data), true);
		request.onreadystatechange = function() {
			confirmAddToCart(request);
		};
		request.send(null);
	}
}
function confirmAddToCart(request){
	if ((request.readyState == 4) && (request.status == 200)){
		alert("Added Successfully To Cart!");
		var indexEmail = document.URL.indexOf('email');
		var email = document.URL.substring(indexEmail + 6, document.URL.length);
		window.location.href = ("./MainPage.html?email=" + email +  "&role=" + window.userrole);
	}
}

function toCart(){
	var index = document.URL.indexOf('email');
	var index_role = document.URL.indexOf('role');
	if (index != -1){
		var email = document.URL.substring(index + 6, document.URL.indexOf('role') - 1);
		var role = document.URL.substring(index_role + 5, document.URL.length);
		window.location.href = ("./CartPage.html?email=" + email + "&role=" + role);
	} else {
		window.location.href = ("./CartPage.html?email=");
	}
	
}


function logout(address){
	var request = new XMLHttpRequest();		
	var index = document.URL.indexOf('email');
	var email = document.URL.substring(index + 6, document.URL.length);
	var data = '';
	data += "email=" + email 
	request.open("GET", (address + "&" + data), true);
	request.onreadystatechange = function() {
		logoutUser(request);
	};
	request.send(null);
}
function logoutUser(request){
	if ((request.readyState == 4) && (request.status == 200)){
		var rs= JSON.parse(request.responseText);
		if (rs.status == 1){
			alert("Sign Out Successfully");
			window.email = "";
			window.userrole = "1";
			window.location.href = (`./MainPage.html`);
		}
	}
	alert("Sign Out Successfully");
	window.email = "";
	window.location.href = (`./MainPage.html`);
}
