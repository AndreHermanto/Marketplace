'use strict';

angular.module('marketplace.searchbar', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/searchbar', {
    templateUrl: 'searchbar/searchbar.tpl.html',
    controller: 'SearchbarCtrl'
  });
}])
    
.controller('SearchbarCtrl',['$http','$scope', 'searchbarService', 'listService', 'loginService', function($http,$scope, searchbarService, listService, loginService) {	
	this.pageSize = 5;
	this.currentPage = 1;
	
	$http.get('http://localhost:8090/listCategories').success(function(data){
		$scope.categories = data;
	})
	
	this.clearSearch = function() {
		//Reset the text input field and paging option
		this.search_input = "";
		
		listService.pageSize = 5;
		this.pageSize = listService.pageSize;

		//Reset the category drop-down list
		if($scope.selectedCategory != undefined)
			$scope.selectedCategory = $scope.selectedCategory.selected;
	
		//Display all the fetched items
		$http({
			url: 'http://localhost:8090/listItems', 
			method: "GET",
			params: {userId: searchbarService.getData('App.UserId')}
		}).success(function(data){
			listService.arrSearchResults = data;
			listService.addData(data, 'App.SelectedValues');
		})
		
		//Reset the selectedValues stack
		searchbarService.addData(null, 'App.SelectedValues');
	};
	
	this.submitSearch = function(searchTerm, selectedCategory, pageSize) {
		listService.pageSize = pageSize;
		//If the user input a text without selecting a category
		if(selectedCategory == undefined && searchTerm != null) {	
			$http({
				url: 'http://localhost:8090/listItems', 
				method: "GET",
				params: {userId: loginService.getData('App.UserId')}
			}).success(function(data, status, headers, config) {
				var toDisplay = [];
				//Compares the title of each item with the input provided by the user
				for (var index = 0, len = data.length; index < len; ++index) {
					if(data[index].title.toLowerCase().indexOf(searchTerm.toLowerCase()) > -1){
						toDisplay.push(data[index]);
					}
				}				
				listService.arrSearchResults = toDisplay;
				listService.addData(toDisplay, 'App.SelectedValues');
			})
		//If the user input a text and selects a category
		} else if (selectedCategory != undefined) {
			$http({
				url: 'http://localhost:8090/listItemsByCategory', 
				method: "GET",
				params: {userId: loginService.getData('App.UserId'), category: selectedCategory}
			}).
			success(function(data, status, headers, config) {
				//If the user has entered a text input
				if(searchTerm != undefined) {
					var toDisplay = [];
					//Compares the title of each item with the input provided by the user
					for (var index = 0, len = data.length; index < len; ++index) {
						if(data[index].title.toLowerCase().indexOf(searchTerm.toLowerCase()) > -1){
							toDisplay.push(data[index]);
						} 
					}		
					listService.arrSearchResults = toDisplay;
					listService.addData(toDisplay, 'App.SelectedValues');
				//If the user didn't enter any text input
				} else {		
					listService.arrSearchResults = data;
					listService.addData(data, 'App.SelectedValues');
				}
			}).
			error(function(data, status, headers, config) {
				listService.addData(null, 'App.SelectedValues');
			});
		//If both the category and input field are empty
		} else {
			$http({
				url: 'http://localhost:8090/listItems', 
				method: "GET",
				params: {userId: searchbarService.getData('App.UserId')}
			}).success(function(data){
				listService.arrSearchResults = data;
				listService.addData(data, 'App.SelectedValues');;
			})
		}
	}
}]);