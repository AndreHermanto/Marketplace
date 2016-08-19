'use strict';

angular.module('marketplace.profile', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/profile', {
    templateUrl: 'app/components/profile/profile.tpl.html',
    controller: 'ProfileCtrl'
  });
}])

.controller('ProfileCtrl' ,['$http','$scope', '$window', 'loginService', 'bootbox', function($http,$scope, $window, loginService, bootbox) {
	this.loginService = loginService;
	
	$http({
		url: 'http://localhost:8090/listItemsUploaded', 
		method: "GET",
		params: {userId: loginService.getData('App.UserId')}
	}).
	success(function(data, status, headers, config) {
		$scope.listItems = data;
	}).
	error(function(data, status, headers, config) {
		bootbox.displayAlert("Error", "Please fill the fields", "Discard", "btn-danger");
	});
	
	$scope.updateUser = function(firstname, lastname) {
		$http({
			url: 'http://localhost:8090/updatePerson', 
			method: "GET",
			params: {id: loginService.getData('App.UserId'), firstname: firstname, lastname: lastname}
		}).
		success(function(data, status, headers, config) {
			$("#myModal").modal('hide');
			loginService.addData(firstname, 'App.Firstname');
			loginService.addData(lastname, 'App.Lastname');
		}).
		error(function(data, status, headers, config) {
			bootbox.displayAlert("Error", "Please fill the fields", "Discard", "btn-danger");
		});
	}
	
	$("#myModal").keyup(function(event){
	    if(event.keyCode == 13){
	        $("#updateButton").click();
	    }
	});
}]);