(function(){  
	angular
	.module('marketplace.loginService', ['ngRoute'])
	.service('loginService', function($window) {
		var FIRSTNAME = 'App.Firstname';
		var LASTNAME = 'App.Lastname';
		var USERID = 'App.UserId';
		var LISTAPP = 'App.UpdateList';

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
	})
})();