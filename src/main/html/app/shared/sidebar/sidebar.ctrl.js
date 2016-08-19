'use strict';

angular.module('marketplace.sidebar', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/sidebar', {
    templateUrl: 'sidebar/sidebar.tpl.html',
    controller: 'SidebarCtrl'
  });
}])

.controller('SidebarCtrl',['$http','$scope', '$window', 'loginService', 'listService', 'notificationService', 'bootbox', function($http,$scope, $window, loginService, listService, notificationService, bootbox) {
	
	$scope.showUpdates = function() {
		$http({
			url: 'http://localhost:8090/listItems', 
			method: "GET",
			params: {userId: loginService.getData('App.UserId')}
		}).success(function(data, status, headers, config) {
			var toDisplay = [];
			//Compares the title of each item with the input provided by the user
			for (var index = 0, len = data.length; index < len; ++index) {
				if(data[index].update){
					toDisplay.push(data[index]);
				}
			}				
			listService.arrSearchResults = toDisplay;
			listService.addData(toDisplay, 'App.SelectedValues');
		})
	}
	
	$scope.showList = function() {
		$http({
			url: 'http://localhost:8090/listItems', 
			method: "GET",
			params: {userId: loginService.getData('App.UserId')}
		}).success(function(data, status, headers, config) {
			listService.arrSearchResults = data;
			listService.addData(data, 'App.SelectedValues');
		})
	}
}]);