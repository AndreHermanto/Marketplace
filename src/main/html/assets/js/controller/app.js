(function(){
	var app=angular.module('marketplace',['angularUtils.directives.dirPagination']);

	app.factory('SearchService', ["$http", 'serviceShareData', function($http, serviceShareData) {
		var SearchService = {};

		if(serviceShareData.getData('App.SelectedValues') != null)
			SearchService.arrSearchResults = serviceShareData.getData('App.SelectedValues');
		else
			SearchService.arrSearchResults = serviceShareData.getData('App.AllValues');

		return SearchService;
	}]);
	
	app.factory('bootbox', function() {
        return {
            displayAlert: function(type, text, label, button) {
            	bootbox.dialog({
					message: text,
					title: type,
					buttons: {
						success: {
							label: label,
							className: button,
							callback: function() {
							}
						}
					}
				});
            }
        };
    });

	app.service('serviceShareData', function($window) {
		var KEYITEM = 'App.SelectedValue';
		var KEYLIST = 'App.SelectedValues';
		var KEYALL = 'App.AllValues';
		var KEYPAGING = 'App.Paging';
		var FIRSTNAME = 'App.Firstname';
		var LASTNAME = 'App.Lastname';
		var USERID = 'App.UserId';

		var addData = function(newObj, key) {
			$window.sessionStorage.setItem(key, JSON.stringify(newObj));
		};

		var getData = function(key){
			var mydata = $window.sessionStorage.getItem(key);
			if (mydata) {
				mydata = JSON.parse(mydata);
			}
			return mydata || [];
		};

		return {
			addData: addData,
			getData: getData
		};
	});
	
	app.controller('SearchController',['$http','$scope', 'serviceShareData', 'SearchService', function($http,$scope, serviceShareData, SearchService){
		searchController = this;
		
		this.pageSize = 5;
		this.currentPage = 1;
		searchService = SearchService;	
		
		$http.get('http://localhost:8090/listCategories').success(function(data){
			searchController.categories = data;
		})
		
		this.clearSearch = function() {
			//Reset the text input field and paging option
			this.search_input = "";
			searchService.pageSize = 5;

			//Reset the category drop-down list
			if($scope.selectedCategory != undefined)
				$scope.selectedCategory = $scope.selectedCategory.selected;
		
			//Display all the fetched items
			SearchService.arrSearchResults = serviceShareData.getData('App.AllValues');
			
			//Reset the selectedValues stack
			serviceShareData.addData(null, 'App.SelectedValues');
		};
		
		this.submitSearch = function(searchTerm, selectedCategory, pageSize) {
			searchService.pageSize = pageSize;
			//If the user input a text without selecting a category
			if(selectedCategory == undefined && searchTerm != null) {	
				$http({
					url: 'http://localhost:8090/listItems', 
					method: "GET",
					params: {userId: serviceShareData.getData('App.UserId')}
				}).success(function(data, status, headers, config) {
					var toDisplay = [];
					//Compares the title of each item with the input provided by the user
					for (index = 0, len = data.length; index < len; ++index) {
						if(data[index].title.toLowerCase().indexOf(searchTerm.toLowerCase()) > -1){
							toDisplay.push(data[index]);
						}
					}
					SearchService.arrSearchResults = toDisplay;					
					serviceShareData.addData(toDisplay, 'App.SelectedValues');
				}).
				error(function(data, status, headers, config) {
					SearchService.arrSearchResults = [];
				});
			//If the user input a text and selects a category
			} else if (selectedCategory != undefined) {
				$http({
					url: 'http://localhost:8090/listItemsByCategory', 
					method: "GET",
					params: {category: selectedCategory}
				}).
				success(function(data, status, headers, config) {
					//If the user has entered a text input
					if(searchTerm != undefined) {
						var toDisplay = [];
						//Compares the title of each item with the input provided by the user
						for (index = 0, len = data.length; index < len; ++index) {
							if(data[index].title.toLowerCase().indexOf(searchTerm.toLowerCase()) > -1){
								toDisplay.push(data[index]);
							} 
						}
						SearchService.arrSearchResults = toDisplay;						
						serviceShareData.addData(toDisplay, 'App.SelectedValues');
					//If the user didn't enter any text input
					} else {
						SearchService.arrSearchResults = data;			
						serviceShareData.addData(data, 'App.SelectedValues');
					}
				}).
				error(function(data, status, headers, config) {
					SearchService.arrSearchResults = [];
				});
			//If both the category and input field are empty
			} else {
				$http({
					url: 'http://localhost:8090/listItems', 
					method: "GET",
					params: {userId: serviceShareData.getData('App.UserId')}
				}).success(function(data){
					SearchService.arrSearchResults = data;
				})
			}
		}
		
		this.currentItem = serviceShareData.getData('App.SelectedValue');
	}]);

	app.controller('ListController',['$http','$scope', 'serviceShareData', 'SearchService', 'bootbox', function($http,$scope, serviceShareData, SearchService, bootbox){
		var selectedCategory = null;
		listController = this;
		this.searchService = SearchService;	
		this.searchService.currentPage = 1;

		$http({
			url: 'http://localhost:8090/listItems', 
			method: "GET",
			params: {userId: serviceShareData.getData('App.UserId')}
		}).success(function(data){
			if (serviceShareData.getData('App.SelectedValues').length > 0){
				SearchService.arrSearchResults = serviceShareData.getData('App.SelectedValues');
			} else {
				serviceShareData.addData(data, 'App.AllValues');
				SearchService.arrSearchResults = data;
			}
		}).
		error(function(data, status, headers, config) {
		});

		$scope.displayItem = function(item) {
			serviceShareData.addData(item, 'App.SelectedValue');
		};

		$scope.installItem = function(item) {
			$http({
				url: 'http://localhost:8090/installItem', 
				method: "GET",
				params: {id: item.id, userId: serviceShareData.getData('App.UserId')}
			}).
			success(function(data, status, headers, config) {
				bootbox.displayAlert("Success", "The item has been installed", "OK!", "btn-success");
				
				$http({
					url: 'http://localhost:8090/listItems', 
					method: "GET",
					params: {userId: serviceShareData.getData('App.UserId')}
				}).success(function(data){
					if (serviceShareData.getData('App.SelectedValues').length > 0){
						SearchService.arrSearchResults = serviceShareData.getData('App.SelectedValues');
					} else {
						serviceShareData.addData(data, 'App.AllValues');
						SearchService.arrSearchResults = data;
					}
				})
			}).
			error(function(data, status, headers, config) {
				bootbox.displayAlert("Error", "The item has not been installed", "Discard", "btn-danger");
			});
		};
	}]);

	app.controller('ItemController',['$http','$scope', 'serviceShareData',  function($http,$scope, serviceShareData){
		this.currentItem = serviceShareData.getData('App.SelectedValue');
	}]);
	
	app.controller('LoginController',['$http','$scope', '$window', 'serviceShareData', 'bootbox', function($http,$scope, $window, serviceShareData, bootbox){
		
		$scope.openSession = function(firstname, lastname) {	
			$http({
				url: 'http://localhost:8090/connectUser', 
				method: "GET",
				params: {firstname: firstname, lastname: lastname}
			}).
			success(function(data, status, headers, config) {
				if(data != null) {
					serviceShareData.addData(data, 'App.UserId');
					serviceShareData.addData(firstname, 'App.Firstname');
					serviceShareData.addData(lastname, 'App.Lastname');
					
					$window.location.href = '/itemList.html';
				} else {
					bootbox.displayAlert("Error", "Failed identification", "Discard", "btn-danger");
				}
			})
		}
	}]);
	
	app.controller('HeaderController',['$http','$scope', 'serviceShareData', function($http, $scope, serviceShareData){
		headerController = this;
		headerController.currentUser = serviceShareData.getData('App.Firstname').concat(" ").concat(serviceShareData.getData('App.Lastname'));
		
	}]);
	
	app.controller('RegisterController',['$http','$scope', '$window', 'serviceShareData', 'bootbox', function($http, $scope, $window, serviceShareData, bootbox){
		registerController = this;
		
		$http.get('http://localhost:8090/listCompanies').success(function(data){
			registerController.companies = data;
		})
		
		$scope.createUser = function(firstname, lastname, company) {
			$http({
				url: 'http://localhost:8090/createUser', 
				method: "GET",
				params: {firstname: firstname, lastname: lastname, companyId: company.id}
			}).
			success(function(data, status, headers, config) {
				if(data != null) {
					serviceShareData.addData(data, 'App.UserId');
					serviceShareData.addData(firstname, 'App.Firstname');
					serviceShareData.addData(lastname, 'App.Lastname');
					
					$window.location.href = '/itemList.html';
				} else {
					bootbox.displayAlert("Error", "Failed identification", "Discard", "btn-danger");
				}
				
			}).
			error(function(data, status, headers, config) {
				bootbox.displayAlert("Error", "Failed identification", "Discard", "btn-danger");
			});
		}
	}]);
})();




