(function () {

'use strict';

  angular.module('surveyApp', ['ngRoute','ngIOS9UIWebViewPatch'])

  .config(['$routeProvider','$httpProvider',function($routeProvider,$httpProvider) {
      // routes
      $routeProvider
        .when("/", {
          templateUrl: "partials/home.html",
          controller: "homeController"
        })
        .when("/survey",{
          templateUrl: "partials/survey.html",
          controller: "surveyController"
        })
        .when("/surveyEnd",{
          templateUrl: "partials/surveyEnd.html",
          controller: "surveyEndController"
        })
        .when("/settings",{
          templateUrl: "partials/settings.html",
          controller: "settingsController"
        })
        .otherwise({
           redirectTo: '/'
        });

      $httpProvider.interceptors.push(function ($q, $rootScope, $location, activeData) {
            return {
                'responseError': function(rejection) {
                    var status = rejection.status;
                    var config = rejection.config;
                    var method = config.method;
                    var url = config.url;
                    console.log("The Entire thing");
                    console.log(rejection);
                    console.log("navigator.onLine - "+navigator.onLine);
                    console.log("status is - "+status);
                    console.log("Config is - "+config);
                    console.log("Method is - "+method);
                    console.log("Url is  - "+url);
                    if($("#loader").is(":visible")){
                      $("#loader").hide();
                    }
                    if(!navigator.onLine){
                      console.log("offline");
                      // alert("Error! Unable to fetch data. Please check internet connectivity.");
                      activeData.setError('offline');
                      rejection.message = "offline";
                    }
                    else if(status == 500) {
                      console.log("internal server error");
                      // alert("Error! Unable to fetch data. There's a server error.");
                      activeData.setError('internalServerError');
                      rejection.message = "Internal Server Error";
                    }
                    else if(status == 404){
                      console.log("internal server error");

                      // alert("Error! API not found on server. Incorrect route.");
                      activeData.setError('notFound');
                      rejection.message = "Not found";
                    } else if(status == 403){
                      // alert("Error! Please check internet connectivity or contact administrator.");
                      console.log("403 error");
                      activeData.setError('Bad Request');
                      rejection.message = "403 error";
                    } else{
                      activeData.setError('unknownError');
                      rejection.message = "Unknown error";
                    }
                    return $q.reject(rejection);
                }
            };
        });

      //Enable cross domain calls
      $httpProvider.defaults.useXDomain = true;

      //Remove the header used to identify ajax call  that would prevent CORS from working
      delete $httpProvider.defaults.headers.common['X-Requested-With'];

  }])
  .run(['$rootScope', '$location', 'activeData', function ($rootScope, $location, activeData) {
    $rootScope.$on('$routeChangeStart', function (event){
        console.log(activeData.getSurveyCompleted());
        if(activeData.getSurveyCompleted()){
          // event.preventDefault();
          $location.path("/");
        }
    });
  }]);

}());
