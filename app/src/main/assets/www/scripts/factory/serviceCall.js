(function () {

'use strict';

	//Load controller
  	angular.module('surveyApp').factory('serviceCall', function($http){

  		var url = {};

  		// constructor to set up certain defaults

  		function serviceCall(serviceName,callMethod){
        /*jshint validthis: true */

  			this.url = {
  				domain : localStorage.surveyAppServerSettings,
  				method : callMethod,
  				name : serviceName
  			};
  		}

  		serviceCall.prototype.call = function(payload,successCallback,errorCallback,mockURL){
        console.log(payload);
        var params = JSON.parse(payload);

        var serviceURL;
        console.log(this.url.name);
        console.log(payload);
        serviceURL = mockURL || this.url.domain + "/" + "rest/promis" + "/" + this.url.name;

        if(params.surveyInstanceID && params.userPIN){
          serviceURL = mockURL || this.url.domain + "/rest/activities/"+  this.url.name + "/" +params.surveyInstanceID + "?pin=" + localStorage.surveyAppPin ;

        }else if(params.surveyInstanceID && params.surveyResults){
          console.log(" I am here ");
         //serviceURL = mockURL || this.url.domain + "/" + "rest/promis" + "/" + this.url.name + "/" + params.surveyInstanceID;
        }else if(params.userPIN){
        serviceURL = mockURL || this.url.domain +"/rest/activities/" + this.url.name + "" + "?pin=" +params.userPIN;
        }
        var version  = "";
        console.log(serviceURL);
  			console.log(this.url.method);
  			console.log(JSON.parse(payload));
        console.log(payload);
        console.log(params);
        if(localStorage.nativeAppVersionNumber === undefined){
        version = "3.0";
        }else{
          version = localStorage.nativeAppVersionNumber;
        }
        if(this.url.method === "POST"){
            serviceURL = mockURL || this.url.domain + "/rest/activities/"+  this.url.name + "/" +params.activityInstanceID + "?pin=" + localStorage.surveyAppPin ;
            console.log(serviceURL);
            console.log(params.activityInstanceID);
          $http(
          {
            method: this.url.method,
            url: serviceURL,
            data: params,
            headers: {'Content-Type': 'application/json',
                      'version':version},
            timeout: 5000,
          }
          ).
          success(function(data, status, headers, config) {
             console.log(data);
              successCallback(data, status, headers, config);
          }).
          error(function(data, status, headers, config) {
             console.log("Service Call Errors");
             console.log(data);
             console.log(status);
             console.log(headers);
             console.log(config);
             errorCallback(data, status, headers, config);
          });
        }else{
          $http(
          {
            method: this.url.method,
            url: serviceURL,
            headers: {'version':version},
            timeout: 5000,
          }
          ).
          success(function(data, status, headers, config) {
              successCallback(data, status, headers, config);
          }).
          error(function(data, status, headers, config) {
             console.log("Service Call Errors");
             console.log(data);
             console.log(status);
             console.log(headers);
             console.log(config);
             errorCallback(data, status, headers, config);
          });
        }

  		};

  		return serviceCall;
  	});

})();
