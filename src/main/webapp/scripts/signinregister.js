function register(address) {
	var request = new XMLHttpRequest();
	var data = '';
	data += "fname=" + document.getElementById("fname").value + "&";
	data += "lname=" + document.getElementById("lname").value + "&";
	data += "email=" + document.getElementById("email").value + "&";
	data += "pwd=" + document.getElementById("password").value + "&";
	data += "address=" + document.getElementById("address").value + "&";
	data += "province=" + document.getElementById("province").value + "&";
	data += "country=" + document.getElementById("country").value + "&";
	data += "zip=" + document.getElementById("zip").value + "&";
	data += "phone=" + document.getElementById("phonenumber").value;
	request.open("GET", (address + "&" + data), true);
	request.onreadystatechange = function() {
		registerResult(request)
	};
	request.send(null);
}
function registerResult(request){
	var rs=JSON.parse(request.responseText);
	var index = document.URL.indexOf('code');
	var code = document.URL.substring(index + 5, document.URL.length);
	if (rs.status == 1){
		alert("Registration Successful");
		if (code == '5'){
				window.location.href = (`./MainPage.html?email=${rs.email}&role=1`);
			}
			else if (code == '6'){
				window.location.href = (`./CheckoutPage.html?email=${rs.email}`);
			}
	}
	else {
		alert("Registration Failed");
	}
}

function signin(address){
	var request = new XMLHttpRequest();
	var data = '';
	data += "email=" + document.getElementById("email").value + "&";
	data += "pwd=" + document.getElementById("password").value;
	request.open("GET", (address + "&" + data), true);
	request.onreadystatechange = function() {
		handler(request, "Log In Authorized", "Log In Failed");
	};
	request.send(null);
}

function handler(request, messageSuccess, messageFail) {
	if ((request.readyState == 4) && (request.status == 200)){
		
		var index = document.URL.indexOf('code');
		var code = document.URL.substring(index + 5, index + 6);
		var index_of_bid = document.URL.indexOf('bid');
		
		var index_of_email = document.URL.indexOf('email');
		var bid = document.URL.substring(index_of_bid + 4,document.URL.length);
		console.log(index_of_email);
		var rs= JSON.parse(request.responseText);
		
		if (rs.status == true){
			alert(messageSuccess);
			if (code == '5'){
                window.location.href = ("./MainPage.html?email=" + rs.email + "&role=" + rs.role);
            }
			else if (code == '6'){
				window.location.href = ("./CheckoutPage.html?role=" + rs.role + "&email=" + rs.email);
			}
		}
		else{
			alert(messageFail);
		}
	}
}
function toRegister(){
	var index = document.URL.indexOf('code');
	var code = document.URL.substring(index + 5, document.URL.length);
	window.location.href = ("./Register.html?code=" + code);
}