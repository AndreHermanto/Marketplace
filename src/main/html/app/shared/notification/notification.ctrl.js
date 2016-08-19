'use strict';

angular.module('marketplace.notification', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/notification', {
    templateUrl: 'notification/notification.tpl.html',
    controller: 'NotificationCtrl'
  });
}])

.controller('NotificationCtrl',['$http','$scope', '$window', 'searchbarService', 'loginService', 'listService', 'notificationService', 'bootbox', function($http,$scope, $window, searchbarService, loginService, listService, notificationService, bootbox) {
	this.notificationService = notificationService;
	
	$http({
		url: 'http://localhost:8090/checkUpdates', 
		method: "GET",
		params: {userId: loginService.getData('App.UserId')}
	}).
	success(function(data, status, headers, config) {
		var notifications = new Array();
		var updates = new Array(data.length);

		for (var i = 0; i < data.length; i++) { 
			updates.push(data[i].id);
			if (!data[i].discarded){
				data[i].installed = true;
				notifications.push(data[i]);
			}
		}
		
		notificationService.arrNotificationResults = notifications;
		notificationService.addData(updates, 'App.UpdatedItems');
		notificationService.addData(notifications, 'App.UpdatedItemsNotification');
	})
	
	$scope.itemClicked = function(item) {	
		listService.addData(item, 'App.SelectedValue');		
		window.location.reload();
		$window.location.href = "#/item";
		
	}
	
	this.discardNotification = function(item) {
		$http({
			url: 'http://localhost:8090/discardNotification', 
			method: "GET",
			params: {id: item.id, userId: loginService.getData('App.UserId')}
		}).
		success(function(data, status, headers, config) {
		})
	}
}]);