'use strict';

angular.module('marketplace.list', ['ngRoute', 'angularUtils.directives.dirPagination'])

.config(['$routeProvider', function($routeProvider) {
	$routeProvider.when('/list', {
		templateUrl: 'app/components/list/list.tpl.html',
		controller: 'ListCtrl'
	});
}])

.controller('ListCtrl',['$http','$scope', '$window', 'searchbarService', 'loginService', 'listService', 'notificationService', 'bootbox', function($http,$scope, $window, searchbarService, loginService, listService, notificationService, bootbox) {
	var selectedCategory = null;
	$scope.listServiceArray = listService.getData('App.SelectedValues');
	$scope.currentPage = 1;
	this.listService = listService
	var update = false;
	
	$scope.updatedItems = notificationService.getData('App.UpdatedItems');

	$http({
		url: 'http://localhost:8090/listItems', 
		method: "GET",
		params: {userId: loginService.getData('App.UserId')}
	}).success(function(data){
		if(listService.getData('App.SelectedValues').length < 1) {
			listService.addData(data, 'App.AllValues')
			listService.addData(5, 'App.Paging')
			listService.arrSearchResults = listService.getData('App.AllValues')
		} else {
			listService.arrSearchResults = listService.getData('App.SelectedValues');
		}
	})

	$scope.displayItem = function(item) {
		listService.addData(item, 'App.SelectedValue');
		$window.location.href = '#/item';
	};

	$scope.installItem = function(item) {
		$http({
			url: 'http://localhost:8090/installItem', 
			method: "GET",
			params: {id: item.id, userId: loginService.getData('App.UserId')}
		}).
		success(function(data, status, headers, config) {
			bootbox.displayAlert("Success", item.title + " has been installed", "OK!", "btn-success");
			$http({
				url: 'http://localhost:8090/listItems', 
				method: "GET",
				params: {userId: loginService.getData('App.UserId')}
			}).success(function(data){
				item.installed = true;
				listService.addData(data, 'App.AllValues');
			})
		}).
		error(function(data, status, headers, config) {
			bootbox.displayAlert("Error", item.title + " has not been installed", "Discard", "btn-danger");
		});
	};
	
	$scope.updateItem = function(item) {
		var updates, notifications = [];
		$http({
			url: 'http://localhost:8090/installItem', 
			method: "GET",
			params: {id: item.id, userId: loginService.getData('App.UserId')}
		}).
		success(function(data, status, headers, config) {
			bootbox.displayAlert("Success", item.title + " has been updated", "OK!", "btn-success");

			updates = notificationService.getData('App.UpdatedItems');
			updates = updates.splice(updates.indexOf(item));
			notificationService.addData(updates, 'App.UpdatedItems');
			
			$http({
				url: 'http://localhost:8090/listItems', 
				method: "GET",
				params: {userId: loginService.getData('App.UserId')}
			}).success(function(data){
				item.installed = true;
				listService.arrSearchResults = data;
			})
			
			var listNotif = notificationService.getData('App.UpdatedItemsNotification');
			
			for (var index = 0, len = listNotif.length; index < len; index++) {
				console.log(listNotif[index])
				console.log(index)
				if(listNotif[index].id = item.id){
					listNotif.splice(index, 1);
				}
			}
			
			notificationService.arrNotificationResults = listNotif;
			notificationService.addData(listNotif, 'App.UpdatedItemsNotification');
		}).
		error(function(data, status, headers, config) {
			bootbox.displayAlert("Error", item.title + " has not been updated", "Discard", "btn-danger");
		});
	};
	
}]);






