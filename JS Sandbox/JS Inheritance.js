/**
 * Javascript Inheritance
 *
 * @author Shi Bai
 *
 * Reference on MDN: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/new
 *
When the code new foo(...) is executed, the following things happen:

	1. 	A new object is created, inheriting from foo.prototype.
	2. 	The constructor function foo is called with the specified arguments and this bound to 
		the newly created object. new foo is equivalent to new foo(), i.e. if no argument list 
		is specified, foo is called without arguments.
	3.	The object returned by the constructor function becomes the result of the whole new expression. 
		If the constructor function doesn't explicitly return an object, the object created in step 1 
		is used instead. (Normally constructors don't return a value, but they can choose to do so if 
		they want to override the normal object creation process.)
*/
'use strict';

var Foo = function() {
	this.title = 'This is a Foo constructor';
	this.setTitle = function(title) {
		this.title = 'FOO:\t' + title;
	};
};
var Bar = function() {
	this.subTitle = 'This is Bar constructor';
	this.setTitle = function(title) {
		this.title = 'BAR:\t' + title;
		this.subTitle = 'BAR-sub:\t' + title;
	};
};
Bar.prototype = new Foo; // Bar inherit Foo: 
							// 1. call Foo.prototype --> Object {}
							// 2. initialize Foo() --> {}.title = '...', {}.setTitle = function
							// 3. return Foo --> { title:'This...', setTitle:function(){...} }
Bar.prototype.constructor = Bar; // constructor was pointed to Foo when assigning the inheritance

var foo = new Foo; // Foo.prototype = new Object 	--> 	{}
var bar = new Bar; // Bar.prototype = new Foo 		--> 	{ title:'This...', setTitle:function(){...} }

/* Use Case */
console.log('================ Foo & Bar Instance ================');
console.log(foo);
console.log(bar);
console.log('Current Inheritance Chain\nBar:{subTitle,setTitle(override)} --> Foo:{title,setTitle} --> Object:{}');
// note: prototype serves as back-chain to Constructor's parent
console.log('\n============== Object:{} --> Object:{hello} ==============');
Foo.prototype.hello = 'Hello World!'; // Foo.prototype refer to Object
console.log(foo);
console.log(bar);

console.log('\n============ Foo:{title,setTitle} --> Foo:{title,setTitle,world} ============');
Bar.prototype.world = 'One World, One Dream!'; // Bar.prototype refer to Foo
console.log(foo);
console.log(bar);