(function(){  
	angular
	.module('marketplace.listService', ['ngRoute'])
	.service('listService', function($window) {
		var KEYLIST = 'App.SelectedValues';
		var KEYALL = 'App.AllValues';
		var KEYPAGING = 'App.Paging';
		var arrSearchResults = null;
		var pageSize = 5;

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
			getData: getData,
			arrSearchResults: arrSearchResults,
			pageSize: pageSize
		};
	})
})();