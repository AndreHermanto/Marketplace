(function(){  
	angular
	.module('marketplace.searchbarService', ['ngRoute'])
	.service('searchbarService', function($window) {
		var KEYLIST = 'App.SelectedValues';
		var KEYALL = 'App.AllValues'

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