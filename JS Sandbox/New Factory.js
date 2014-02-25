// a simple MD5 object
var MD5 = function(data, options) {
	this.data = data || null;
	this.isSecure = options.isSecure || false;
	this.size = options.size || 0;
	this.type = 'MD5';
	this.out = function(){
		console.log('Data: \t\t\t' + this.data + '\nSecure: \t\t' + 
					this.isSecure + '\nDigest Sizes:\t' + this.size +
					'\nType: \t\t\t' + this.type + '\n');
	};
};
// a simple Keccak (known as SHA-3) object
var Keccak = function(data, options) {
	this.data = data || null;
	this.isSecure = options.isSecure || false;
	this.size = options.size || 0;
	this.type = 'SHA-3 (also known as Keccak)';
	this.out = function(){
		console.log('Data: \t\t\t' + this.data + '\nSecure: \t\t' + 
					this.isSecure + '\nDigest Sizes:\t' + this.size +
					'\nType: \t\t\t' + this.type + '\n');
	};
};

// a Factory function wrapper; *inspired by Rdio
var Factory = function(c) {
	c = c || this;
	return function(data, options) {
		return new c(data, options);
	};
};


// Usage Test
console.log('=================\tJS Factory Pattern (wrapper-bridge)\t=================\n');
var Hash = null;
// Test 1
Hash = new Factory(MD5);
var hash = Hash('79054025255fb1a26e4bc422aef54eb4', {isSecure: true, size: 128});
hash.out();
console.log(hash); console.log('\n');
// Test 2
Hash = new Factory(Keccak);
var hash = Hash('310aee6b30c47350576ac2873fa89fd190cdc488442f3ef654cf23fe', {isSecure: true, size: 'arbitrary'});
hash.out();
console.log(hash);