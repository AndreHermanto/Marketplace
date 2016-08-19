(function(){  
	angular
	.module('marketplace.notificationService', ['ngRoute'])
	.service('notificationService', function($window) {
		var UPDATEDITEMNOTIF = 'App.UpdatedItemsNotification';
		var UPDATEDITEM = 'App.UpdatedItems';
		var arrNotificationResults = null;

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
			arrNotificationResults: arrNotificationResults
		};
	});
})();