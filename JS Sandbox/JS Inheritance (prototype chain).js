/**
 * Javascript Inheritance
 *
 * @author Shi Bai
 */

var A = function(){};
var B = function(){};
B.prototype = new A;

var a = new A;
var b = new B;

console.log('\n================ b:B{} --> a:A{} --> {} ================');
console.log(a);
console.log(b);

console.log('\n================ b:B{two} --> a:A{one} --> {} ================');
A.prototype.one = 'one'; // {one}
B.prototype.two = 'two'; // A{two}
console.log(a);
console.log(b);

console.log('\n================ b2:B{} --> {};  b:B{two} --> a:A{one} --> {} ================');
B.prototype = new function(){}; // B goes for a new prototype
								// note: overriding entire prototype (e.g. F.prototype = someObject) 
								// 		 will not affect existing F instance; however, changes on prototype
								//		 do affect all existing F instance (e.g. F.prototype.someProp = ...)
var b2 = new B;
console.log(a);
console.log(b);
console.log(b2);

console.log('\n================ b2:B{} --> {};  b:B{two} --> a:A{one,last} --> {} ================');
A.prototype.last = 'LAST';
// previous declared variable still consist the 'old B' inheritance chain
console.log(a);
console.log(b);
// newly created variables will use 'new B' constructor instead
console.log(b2);

console.log('\n================ b3:B{} --> a:A{one,last} --> {}; b2:B{} --> {}; ' +
			'b:B{two} --> a:A{one,last} --> {} ================');
B.prototype = new A;
var b3 = new B;
console.log(a);
console.log(b);
console.log(b2);
console.log(b3);