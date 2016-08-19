'use strict';

angular.module('marketplace.create', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
	$routeProvider.when('/create', {
		templateUrl: 'app/components/create/create.tpl.html',
		controller: 'CreateCtrl'
	});
}])

.controller('CreateCtrl',['$http','$scope', '$window', 'loginService', 'listService', 'notificationService', 'bootbox', function($http,$scope, $window, loginService, listService, notificationService, bootbox) {
	$http.get('http://localhost:8090/listCategories').success(function(data){
		$scope.categories = data;
	})

	$scope.file = "";

	$(document).on('change', '.btn-file :file', function() {
		var input = $(this),
		numFiles = input.get(0).files ? input.get(0).files.length : 1,
				label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
		input.trigger('fileselect', [numFiles, label]);
	});

	$(document).ready( function() {
		$('.btn-file :file').on('fileselect', function(event, numFiles, label) {

			var input = $(this).parents('.input-group').find(':text'),
			log = numFiles > 1 ? numFiles + ' files selected' : label;

			if( input.length ) {
				input.val(log);
			} else {
				if( log ) alert(log);
			}

		});
	});

	$scope.openFiles = function(event) {

		var input = event.target;

		var reader = new FileReader();
		reader.onload = function(){
			var text = reader.result;
			$scope.file = reader.result;

		};
		reader.readAsText(input.files[0]);
	};

	$scope.createItem = function() {
		var category = $('#category-select').val().split(":");

		if($('#title').val() != "" && $('#subtitle').val() != "" && $('#description').val() != "" && category != "" && $scope.file != "") {
			$http({
				url: 'http://localhost:8090/createItem', 
				method: "GET",
				params: {userId: loginService.getData('App.UserId'), title: $('#title').val(), subtitle: $('#subtitle').val(), description: $('#description').val(), category: category[category.length-1], datapack: String($scope.file)}
			}).success(function(data){
				bootbox.displayAlert("Success", "The item has been uploaded", "OK!", "btn-success");
				$window.location.href = '#/list';
			}).
			error(function(data, status, headers, config) {
				bootbox.displayAlert("Error", "The item has not been uploaded", "Discard", "btn-danger");
			});
		}
		else{
			bootbox.displayAlert("Error", "Please fill out all item in the form", "Discard", "btn-danger");
		}
	}
}]);