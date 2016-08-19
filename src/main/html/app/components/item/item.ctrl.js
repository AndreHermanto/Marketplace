'use strict';

angular.module('marketplace.item', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
	$routeProvider.when('/item', {
		templateUrl: 'app/components/item/item.tpl.html',
		controller: 'ItemCtrl'
	});
}])

.controller('ItemCtrl',['$http','$scope', '$window', 'loginService', 'listService', 'notificationService', 'itemService', 'bootbox', function($http,$scope, $window, loginService, listService, notificationService, itemService, bootbox) {
	$scope.currentItem = listService.getData('App.SelectedValue');
	$scope.updatedItems = notificationService.getData('App.UpdatedItems');
	$scope.currentUser = loginService.getData('App.Firstname') + " " + loginService.getData('App.Lastname');
	$scope.file = "";
	$scope.stars = 0;
	$scope.comment ="";
	
	$scope.bootbox = bootbox;
	this.itemService = itemService;
	$scope.loginService = loginService;

	var comments = {};
	$scope.arrName = [];
	$scope.arrComments = [];

	$http.get('http://localhost:8090/listCategories').success(function(data){
		$scope.categories = data;
	})

	$http({
		url: 'http://localhost:8090/listComments', 
		method: "GET",
		params: {id: $scope.currentItem.id}
	}).success(function(data){
		var dict = [];
		for (var i = 0; i < data.length; i++) { 
			var temp = data[i].data.split(",");	
			var arrKey = [temp[0],temp[1],temp[2]];
			dict.push({
				key:   arrKey,
				value: temp[3]
			});
			comments[$scope.currentItem.id] = dict;

			
		}
		itemService.addData(comments, 'App.ItemComments');
	})

	$scope.installItem = function(item) {
		$http({
			url: 'http://localhost:8090/installItem', 
			method: "GET",
			params: {id: item.id, userId: loginService.getData('App.UserId')}
		}).
		success(function(data, status, headers, config) {
			bootbox.displayAlert("Success", "The item has been installed", "OK!", "btn-success");
			$http({
				url: 'http://localhost:8090/listItems', 
				method: "GET",
				params: {userId: loginService.getData('App.UserId')}
			}).success(function(data){
				item.installed = true;
				listService.arrSearchResults = data;
				listService.addData(data, 'App.SelectedValues');
			})
		}).
		error(function(data, status, headers, config) {
			bootbox.displayAlert("Error", "The item has not been installed", "Discard", "btn-danger");
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
			bootbox.displayAlert("Success", "The item has been updated", "OK!", "btn-success");

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

			$scope.currentItem.update = false;
			$scope.currentItem.installed = true;

			listService.addData($scope.currentItem, 'App.SelectedValue');

			var list = notificationService.getData('App.UpdatedItemsNotification');

			for (var index = 0, len = list.length; index < len; ++index) {
				if(list[index].id = item.id){
					list.splice(index, 1);
				}
			}

			notificationService.arrNotificationResults = list;
			notificationService.addData(list, 'App.UpdatedItemsNotification');
		}).
		error(function(data, status, headers, config) {
			bootbox.displayAlert("Error", "The item has not been updated", "Discard", "btn-danger");
		});
	};

	$scope.openFile = function(event) {
		var input = event.target;

		var reader = new FileReader();
		reader.onload = function(){
			var text = reader.result;
			$scope.file = reader.result;
			console.log($scope.file)
		};
		reader.readAsText(input.files[0]);
	};

	$scope.editItem = function(item) {
		var category = $('#category-select').val().split(":");

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

		if ($('#title').val() != "" && item.title != $('#title').val())
			item.title = $('#title').val();

		if ($('#subtitle').val() != "" && item.subtitle != $('#subtitle').val())
			item.subtitle = $('#subtitle').val();

		if ($('#description').val() != "" && item.description != $('#description').val())
			item.description = $('#description').val();

		if (category[category.length-1] != "" && item.category != category[category.length-1])
			item.category = category[category.length-1];

		if (String($scope.file) != "" && item.datapack != String($scope.file))
			item.datapack = String($scope.file);


		$http({
			url: 'http://localhost:8090/updateItem', 
			method: "GET",
			params: {id: item.id, title: item.title, subtitle: item.subtitle, description: item.description, category: item.category, datapack: item.datapack, installed: item.installed}
		}).success(function(data){

			$("#myModalEdit").modal('hide');

			$window.location.href = "#/list";
		}).
		error(function(data, status, headers, config) {
			bootbox.displayAlert("Error", "The item has not been uploaded", "Discard", "btn-danger");
		});
	};

	$scope.rateItem = function(item,stars) {
		$http({
			url: 'http://localhost:8090/rateItem', 
			method: "GET",
			params: {id: item.id , userId: loginService.getData('App.UserId'), rate: stars}
		}).success(function(data){
			$http({
				url: 'http://localhost:8090/checkItemRate', 
				method: "GET",
				params: {id: item.id}
			}).success(function(data){
				$("#myModalRate").modal('hide');
				$scope.currentItem.rate = data;
				listService.addData($scope.currentItem, 'App.SelectedValue');
				
				$window.location.reload();
			})


		}).
		error(function(data, status, headers, config) {
			bootbox.displayAlert("Error", "You Cannot Rate the Item!", "Discard", "btn-danger");
		});
	};

	$("#myModalEdit").keyup(function(event){
		if(event.keyCode == 13){
			$("#updateButton").click();
		}
	});
	
	$scope.addComment = function(item,comments) {
		$http({
			url: 'http://localhost:8090/addComment', 
			method: "GET",
			params: {id: item.id , userId: loginService.getData('App.UserId'), comment: comments}
		}).success(function(data){
			$scope.comment =""
			bootbox.displayAlert("Success", "Thankyou for your comment", "OK!", "btn-success");
			$("#myModalComment").modal('hide');
			
			$window.location.reload();
		}).
		error(function(data, status, headers, config) {
			bootbox.displayAlert("Error", "You Cannot Comment the Item!", "Discard", "btn-danger");
		});
	};
	
	$scope.deleteComment = function(comments) {
		$http({
			url: 'http://localhost:8090/deleteComment', 
			method: "GET",
			params: {id: $scope.currentItem.id , userId: loginService.getData('App.UserId'), comment: comments}
		}).success(function(data){
			$scope.comment =""
			bootbox.displayAlert("Success", "Your Comment has been Deleted", "OK!", "btn-success");
		
			$window.location.reload();
		}).
		error(function(dfaata, status, headers, config) {
			bootbox.displayAlert("Error", "You Cannot Delete the Comment", "Discard", "btn-danger");
		});
	};
	




}]);