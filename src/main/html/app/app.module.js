'use strict';

// Declare app level module which depends on views, and components
angular.module('marketplace', [
  'ngRoute',
  'marketplace.item',
  'marketplace.list',
  'marketplace.login',
  'marketplace.signup',
  'marketplace.header',
  'marketplace.searchbar',
  'marketplace.sidebar',
  'marketplace.pagination',
  'marketplace.notification',
  'marketplace.create',
  'marketplace.profile',
  'marketplace.itemService',
  'marketplace.listService',
  'marketplace.loginService',
  'marketplace.searchbarService',
  'marketplace.notificationService'
  
]).
config(['$routeProvider', function($routeProvider) {
	$routeProvider.when('/', {templateUrl: 'app/components/login/login.tpl.html'});
	$routeProvider.when('/signup', {templateUrl: 'app/components/signup/signup.tpl.html'});
	$routeProvider.when('/index', {templateUrl: 'app/components/list/list.tpl.html'});
}])

.factory('bootbox', function() {
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
        },
        
        modalShow: function(id) {
    		$(id).modal('show');
    	},
    	
    	modalDismiss: function(id) {
    		$(id).modal('hide');
    	}
    };
});
