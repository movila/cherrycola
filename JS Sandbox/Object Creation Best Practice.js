/**
 * Best Practice in JS Object Creation
 *
 * @author Shi Bai
 */

/**
 * Constructor/Prototye Object Creation Pattern
 */
function Automobile(type, brand, age) {

	this.type = type || 'Car'; 	// 'this' property will be different 
								// in each Automobile instance
								// note: type is must
	this.brand = (typeof brand === 'undefined') ? 'Automotive Industry' : brand; // brand can be null

	this.age = isNaN(age) ? 0 : Math.abs(+age || 0); 	// redundant, use either one of the checking:
														// (isNaN(x) ? 0 : x) OR (+x || 0)
	var date = new Date();		// private

}
Automobile.prototype = { 		// Use object literal here for better organize & encapsulation;
								// prototype properties works like 'static' properties

	constructor: Automobile,	// Whenever you overwrite the entire object’s prototype (object.prototype = someVal), 
								// you also overwrite the object’s constructor property. So, set it mannually.
	narrate: function() {
		this.print('I\'m a ' + this['brand'] + ' ' + this.type);	// note-1: prototype has access to constructor property
																	// note-2: 'print' is defined later after 'narrate'
	},

	print: function(content) {
		console.log(content);
	},

}

/**
 * Parasitic Combination Inheritance Pattern
 *
 * see: http://javascriptissexy.com/oop-in-javascript-what-you-need-to-know/
 */
function inheritPrototype(childObject, parentObject) {
	var copyOfParent = Object.create(parentObject.prototype);
	copyOfParent.constructor = childObject;
	childObject.prototype = copyOfParent;
}

// Subclass: BMW M6 of Automobile
function BMW_M6() {
	Automobile.call(this, 'Coupé', 'BMW');
	this.price = '$'+124900.245.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');;
}
inheritPrototype(BMW_M6, Automobile);
BMW_M6.prototype.narrate = function(){	// override 'narrate' method
	this.print('I\'m a ' + this.brand + ' ' + this.type + ' worth ' + this.price);
}
// Subclass: Ford Fusion of Automobile
function Ford_Fusion() {
	Automobile.call(this, 'Sedan', 'Ford');
}
inheritPrototype(Ford_Fusion, Automobile);
Ford_Fusion.prototype.year = Number(2014);
Ford_Fusion.prototype.narrate = function(){	// override 'narrate' method
	this.print('I\'m a ' + this.brand + ' ' + this.type + ' made in ' + this.year);
}

/* Test Cases */
console.log('\n\n=============== Test on OOP In JavaScript Practice ===============\n');
var auto = new Automobile;
var bmw = new BMW_M6;
var ford = new Ford_Fusion;
console.log(auto);
console.log(bmw);
console.log(ford);
bmw.hasOwnProperty('price'); // true
ford.hasOwnProperty('year'); // false
console.log('BMW alter its own attribute on Constructor');
// !IMPORTANT: 	instance gain access to its prototype by -instance.constructor.prototype-
var ford2 = new Ford_Fusion;
ford2.year = 2010; // override default __proto__.year; however, __proto__.year still exists
ford2.constructor.prototype.year = 2016; // alter prototype on all existing Ford_Fusion instances
delete ford2.year; // delete ford2's override version of __proto__.year
console.log(ford2);



/* ------------------------- Read below if you still have doubts ------------------------- */
// ===== Trivial Testing =====
console.log('\n --- Tradition VS Parasitic Combination Inheritance Pattern --- \n');
var Z = function(){ this.intro = 'Hello This is Z' };
Z.prototype = new Automobile;
Z.prototype.constructor = Z; // !important
console.log(z1 = new Z);
inheritPrototype(Z, Automobile); // only inherit Protype, no constructor settings
console.log(z2 = new Z);
/**
 * Object.create
 * @author Douglas Crockford
 * Added to ECMAScript5
 * 
	if (typeof Object.create !== 'function') {
		Object.create = function (o) {
			function F() {};
			F.prototype = o;
			return new F();
		};
	}
 */
// --- Use the following to test Object.create concept ---
// note: below is a pure Prototype Object Creation Pattern;
var V = function(){}
V.prototype = {	
	constructor: V,
	character: true, 
	title: 'V for Vendetta', 
	genre: 'Movie', 
	getTitle: function(){
		return this.title;
	} 
};
// note: below is a pure Constructor Object Creation Pattern;
var Eve = function(){
	this.character = true;
	this.title = 'V for Vendetta';
	this.genre = 'Movie';
	this.getTitle = function(){
		return this.title;
	} 
}
// ===== Better Understanding on Object.create =====
console.log(new V); 					// 'new' give combination of properties from 
										// both 1.Prototype and 2.Constructor
console.log(new Eve);	
console.log(V.prototype);				// only provide objects reside in prototype
console.log(Eve.prototype);
//console.log(new V().constructor);		// only provide objects reside in constructor
//console.log(new Eve().constructor);
console.log(Object.create(new V));			// Object->__proto__:V->__proto:Object->title...
console.log(Object.create(V.prototype));	// Object->__proto:Object->title...
console.log(Object.create(new Eve));		// Object->__proto__:Eve->title...
// --- Conclusion ---
var nothing = {none:null};		// A no meaning Object with a no meaning attribute
var x = Object.create(nothing); // Object.create(T) return a __proto__:T wrapper to target.
x.hasOwnProperty('none'); 		// false