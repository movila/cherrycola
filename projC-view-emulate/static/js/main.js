console.log(document.URL);
// nav-item
var allNav = document.getElementsByClassName('nav-item');
console.log(allNav);
console.log("NAV[0]: " + allNav[0]);
console.log("Length: " + allNav.length);
//console.log(document.getElementsByTagName('ul'));
for (var i = 0; i < allNav.length; i++) {
	console.log("i: " + i);
	console.log(allNav[i].innerHTML);
}
	