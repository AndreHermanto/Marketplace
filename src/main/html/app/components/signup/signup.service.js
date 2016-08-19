(function(){  
	angular
	.module('marketplace', [])
	.service('signupService', function($window) {
		var FIRSTNAME = 'App.Firstname';
		var LASTNAME = 'App.Lastname';
		var USERID = 'App.UserId';

		var addData = function(newObj, key) {
			$window.sessionStorage.setItem(key, JSON.stringify(newObj));
		};

		var getData = function(key){
			var mydata = $window.sessionStorage.getItem(key);
			if (mydata) {
				mydata = JSON.parse(mydata);
			}
			return mydata || [];
		};

		return {
			addData: addData,
			getData: getData
		};
	});
})();