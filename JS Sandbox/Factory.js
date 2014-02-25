/**
 * Javascript Factory Patterns 
 *
 * @see http://www.dofactory.com/javascript-factory-method-pattern.aspx
 */
function Factory() {
    this.createEmployee = function (type) {
        var employee;

        if (type === "fulltime") {
            employee = new FullTime();
        } else if (type === "parttime") {
            employee = new PartTime();
        } else if (type === "temporary") {
            employee = new Temporary();
        } else if (type === "contractor") {
            employee = new Contractor();
        }

        employee.type = type;

        employee.say = function () {
            console.log(this.type + ":\trate " + this.hourly + "/hour");
        }

        return employee; // Object {type: "employeeType", say: function}
    }
        
}

var FullTime = function () {
    this.hourly = "$12";
};
var PartTime = function () {
    this.hourly = "$11";
};
var Temporary = function () {
    this.hourly = "$10";
};
var Contractor = function () {
    this.hourly = "$15";
};

/**
 * Usage
 */
(function(){

	console.log('=================\tJS Factory Pattern\t=================');

	var employees = [];

    var factory = new Factory();

    employees.push(factory.createEmployee("fulltime"));
    employees.push(factory.createEmployee("parttime"));
    employees.push(factory.createEmployee("temporary"));
    employees.push(factory.createEmployee("contractor"));

    for (var i = 0, len = employees.length; i < len; i++) {
        employees[i].say();
    }

}());