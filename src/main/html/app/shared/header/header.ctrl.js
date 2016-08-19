'use strict';

angular.module('marketplace.header', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/header', {
    templateUrl: 'header/header.tpl.html',
    controller: 'HeaderCtrl'
  });
}])

.controller('HeaderCtrl',['$http','$scope', 'searchbarService', 'loginService', 'listService', 'notificationService', 'itemService', function($http,$scope, searchbarService, loginService, listService, notificationService, itemService) {
	this.loginService = loginService
	
	this.resetValues = function() {	
		listService.addData(null, 'App.SelectedValues')
		
		notificationService.addData(null, 'App.UpdatedItemsNotification')
		notificationService.addData(null, 'App.UpdatedItems')
		
		itemService.addData(null, 'App.SelectedValue')
		
		listService.addData(null, 'App.SelectedValues')
		listService.addData(null, 'App.AllValues')
		listService.addData(null, 'App.Paging')
		
		loginService.addData(null, 'App.Firstname')
		loginService.addData(null, 'App.Lastname')
		loginService.addData(null, 'App.UserId')
		loginService.addData(null, 'App.UpdateList')
		
		searchbarService.addData(null, 'App.SelectedValues')
		searchbarService.addData(null, 'App.AllValues')
	}
}]);