(function(){  
	angular
	.module('marketplace.itemService', ['ngRoute'])
	.service('itemService', function($window) {
		var KEYITEM = 'App.SelectedValue';
		var ITEMCOMMENTS = 'App.ItemComments';

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