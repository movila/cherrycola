var rootPath = document.URL.substring(0, document.URL.indexOf("projC/") + "projC/".length);
var appPath = document.URL.substring(rootPath.length);
var allNav = document.getElementsByClassName('nav-item');

var extraPath;
var cateId;
if ((extraPath = appPath.match(/Home\/Cat\//i)[0]) != null && 
	(cateId = appPath.substring(extraPath.length).match(/\d+/)) != null ) {
	
	for (var i = 0; i < allNav.length; i++) {
		var linkId = allNav[i].parentNode.href.match(/\/\d+/i)[0];
		linkId = linkId.substring(1);

		if (linkId == cateId) 
			allNav[i].className = allNav[i].className + " active";
	}
}


// Infinite-scroll (not implemented yet) 
var cntHeight = document.getElementById("content").clientHeight;
var vpHeight = document.documentElement.clientHeight;
window.onscroll = function() {
	var vpTop = window.pageYOffset;
	if (vpTop > (cntHeight - vpHeight)) {
		// Ajax data is sent when reach the bottom, and request back a JSON object
		console.log("You reached BOTTOM!");
	}
};



// click generate button
function generImage() {
	var request = new XMLHttpRequest();
	request.onreadystatechange = function() { 
		if ((request.readyState == 4) && (request.status == 200)) 
		{
			console.log("AJAX finished: " + request.responseText);
			//document.getElementById("result").innerHTML = "Monthly Payment = " + new Number(x).toFixed(2);
		}
	};
	request.open("POST", rootPath + "ImageGenerator", true);
	request.send("generimage=start");
}

//setInterval('generImage();', 3000);