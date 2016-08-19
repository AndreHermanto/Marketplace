'use strict';

angular.module('marketplace.signup', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/signup', {
    templateUrl: 'signup/signup.tpl.html',
    controller: 'RegisterCtrl'
  });
}])

.controller('RegisterCtrl' ,['$http','$scope', '$window', 'loginService', 'bootbox', function($http,$scope, $window, loginService, bootbox) {
	
	$http.get('http://localhost:8090/listCompanies').success(function(data){
		$scope.companies = data;
	})
	
	$scope.createUser = function(firstname, lastname, company) {
		$http({
			url: 'http://localhost:8090/createUser', 
			method: "GET",
			params: {firstname: firstname, lastname: lastname, companyId: company.id}
		}).
		success(function(data, status, headers, config) {
			if(data != null) {
				loginService.addData(data, 'App.UserId');
				loginService.addData(firstname, 'App.Firstname');
				loginService.addData(lastname, 'App.Lastname');
				
				$window.location.href = '#/list';
			} else {
				bootbox.displayAlert("Error", "Failed identification", "Discard", "btn-danger");
			}
			
		}).
		error(function(data, status, headers, config) {
			bootbox.displayAlert("Error", "Failed identification", "Discard", "btn-danger");
		});
	}
}]);