'use strict';

angular.module('marketplace.login', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: 'app/components/login/login.tpl.html',
    controller: 'LoginCtrl'
  });
}])

.controller('LoginCtrl',['$http','$scope', '$window', 'loginService', 'bootbox', function($http,$scope, $window, loginService, bootbox) {
	
	this.openSession = function(firstname, lastname) {	
		$http({
			url: 'http://localhost:8090/connectUser', 
			method: "GET",
			params: {firstname: firstname, lastname: lastname}
		}).
		success(function(data, status, headers, config) {
			if(data != null) {
				loginService.addData(data, 'App.UserId');
				loginService.addData(firstname, 'App.Firstname');
				loginService.addData(lastname, 'App.Lastname');
				
				$window.location.href = '#/index';
			} else {
				bootbox.displayAlert("Error", "Failed identification", "Discard", "btn-danger");
			}
			
			$http({
				url:'http://localhost:8090/checkUpdates',
				method: "GET",
				params: {userId: loginService.getData('App.UserId')}
			}).
			success(function(data,status,header,config){	
				loginService.addData(data,'App.UpdateList');
				console.log(loginService.getData('App.UpdateList'))
				//bootbox.displayAlert("Error",loginService.getData('App.UserId') , "Discard", "btn-danger");
			})
		})
	}
}]);