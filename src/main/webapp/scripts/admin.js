function retrieveStats(address){
	var request = new XMLHttpRequest();
	var data = '';
	if(document.getElementById("bid").innerHTML != null){
		var bidInfo = document.getElementById("bid").value;
		data += "bid=" + bidInfo;
		request.open("GET", (address + "&" + data), true);
		request.onreadystatechange = function() {
			statsFormat(request);
		};
		request.send(null);
	}
	else{
		alert("Please Enter a Product ID");
	}
}
function statsFormat(request) {
	if ((request.readyState == 4) && (request.status == 200)){
		var rs=JSON.parse(request.responseText);
		document.getElementById("itemStats").style.display = "block";
		document.getElementById("numViews").innerHTML = "Number of Views: " + rs.viewcount;
		document.getElementById("numCart").innerHTML = "Number of Time It was Added to Cart: " + rs.cartcount;
		document.getElementById("numPurchase").innerHTML = "Number of Purchases: " + rs.purchasecount;
		document.getElementById("itemSales").innerHTML = ">Total Sales From Product: $" + rs.totalItemSales;

	}
}
function generalStats(address){
	var request = new XMLHttpRequest();
	request.open("GET", (address), true);
	request.onreadystatechange = function() {
		generalStatsFormat(request);
	};
	request.send(null);
}
function generalStatsFormat(request){
	if ((request.readyState == 4) && (request.status == 200)){
		var rs=JSON.parse(request.responseText);
		document.getElementById("generalQuantity").innerHTML = "Total Products Sold: " + rs.totalQuantity;
		document.getElementById("generalSales").innerHTML = "Total Sales: $" + rs.totalSales;
	}
}