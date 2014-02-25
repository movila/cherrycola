/**
 * Javascript Constructor 
 * pros: private method
 * cons: change of Class constructor won't affect existing Objects
 *
 * @author Shi Bai
 */
var Person = function(name, age, gender) {
	"use strict"; // under strict mode, 'this' is default -undefined- instead of -global object-
	/** 
	 * Private ----------------------------------------
	 */
	var PersonException = function(message) {
		// 'this' is PersonException
		this.message = message;
		this.name = 'PersonException';
	}
	var ValidAge = function(age) {
		return typeof age === 'number' && 0 < age && age < 100;
	}
	var ValidGender = function(gender) {
		return typeof gender === 'string' && /^(?:male|female|m|f)$/i.test(gender);
	}

	/** 
	 * Public -----------------------------------------
	 */
	/* Public Attributes (run only on initializing constructor) */
	// warning: public attributes should be avoid, use public 'setter' and 'getter' instead.
	// note: there's no validation later for public attribute, once they are initialized.
	this.name = name || 'Mr. Nobody'; 
	this.age = ValidAge(age) ? age : 0;
	this.gender = gender || 'Male';

	/* Public Methods */
	this.getName = function() {
		// 'this' is Person
		return this.name;
	}

	this.getAge = function() {
		if (!ValidAge(this.age))
			throw new PersonException('Age is not valid');
		else
			return this.age;
	}

	this.getGender = function() {
		if (!ValidGender(this.gender))
			throw new PersonException('Gender is not correct');
		else
			return this.gender;
	}

	this.setGender = function(gender) {
		if (!ValidGender(gender))
			throw new PersonException('Gender is not correct');
		else
			this.gender = gender;
	}
	// note: if no return statemnet, (return undefined;) by default
};
/**
 * Javascript prototype
 * pros: change of prototype directly affects its instances
 */
Person.prototype.setName = function(name) {
	var name = name || this.name;
	this.name = 'Dr. ' + name;
};


/**
 * Usage
 */
var person1 = new Person();
var person2 = new Person('Rachel', 111, 'F');
console.log('================ Person Objects (Debug-1) ================');
console.log(person1);
console.log(person2);
console.log('Public attribute is validated at initialization: \t\t\tage-' + person2.age);
person1.age = 132; // should be invalid
console.log('Public attribute is NOT validated after initialization: \tage-' + person1.age);

console.log('================ Person Objects (Debug-2) ================');
console.log('Public methods (backed by private validator): \t\t\t\tage-');
console.log(person1.getAge()); // exception occur, which will terminate following instructions

console.log('================ Person Objects (Debug-3) ================');
person1.setName('Orban');
person2.setName('Whisenhunt');
console.log('Prototype:\t\t\t -setName()- \n\t\t\t\t\t\t|\n\t\t\t\t\t\t|---- P1: ' + person1.getName() + 
			'\n\t\t\t\t\t\t|\n\t\t\t\t\t\t|---- P2: ' + person2.getName());
// prototye changed
Person.prototype.setName = function(name) {
	var name = name || this.name;
	this.name = 'Prof. ' + name;
};
person1.setName('Orban');
person2.setName('Whisenhunt');
console.log('Prototype (changed): -setName()- \n\t\t\t\t\t\t|\n\t\t\t\t\t\t|---- P1: ' + person1.getName() + 
			'\n\t\t\t\t\t\t|\n\t\t\t\t\t\t|---- P2: ' + person2.getName());