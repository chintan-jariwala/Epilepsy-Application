(function() {

    'use strict';
    // Load controller
    angular.module('surveyApp').controller('homeController', ['$rootScope', '$scope', '$location', 'serviceCall', 'activeData', '$timeout', function($rootScope, $scope, $location, serviceCall, activeData, $timeout) {

        $scope.title = "WELCOME";
        $scope.nextDueSurveyID = "";
        $scope.dueSurveys = [];
        $scope.surveyInProgress = [];
        $scope.dueSurveyNames = [];
        $scope.areMultipleSurveysDue = false;
        $scope.isThirdSurveyDue = false;
        $scope.isSurveyDue = false;
        $scope.allSurveyData = {};
        var okayToStart = false;
        $scope.debugInformation = "";
        $scope.statusReturned = "";
        $scope.troubleshootOptions = [];
        $scope.populateDebugInformation = function() {
            $scope.debugInformation = "";
            var platform = $scope.getMobileOperatingSystem();
            console.log(navigator.vendor);
            $scope.debugInformation = "Network Connection Online: - " + navigator.onLine + "\n" +
                "User PIn: - " + localStorage.surveyAppPin + "\n" +
                "Server Address: - " + localStorage.surveyAppServerSettings + "\n" +
                "Server Status Returned: - " + $scope.statusReturned + "\n" +
                "User Agent: - " + platform + "\n" +
                "Browser Version : - " + navigator.appVersion + "\n";



        };

        $scope.clearAll = function(){
         console.log("Clearing localStorage");
         var questions = activeData.getSurveyQuestions();
         if(questions !== undefined){
            $.each(questions,function(index,val){
              console.log($scope.surveyID+ ":" +val.quesID + ":" + val.activityBlockId);
              localStorage.removeItem($scope.surveyID+ ":" +val.quesID + ":" + val.activityBlockId);
            });
         }
         localStorage.removeItem("surveyInProgress");
         localStorage.removeItem("reloadBackupSurveyID");
         localStorage.removeItem("reloadBackupSurveyName");
         localStorage.removeItem("reloadBackupSurveyQuestions");
         $(".modal-backdrop").hide();
         $scope.changePage("/home");
      };

        $scope.getMobileOperatingSystem = function() {
            var userAgent = navigator.userAgent || navigator.vendor || window.opera;
            if (userAgent.match(/iPad/i) || userAgent.match(/iPhone/i) || userAgent.match(/iPod/i)) {
                return 'iOS';
            } else if (userAgent.match(/Android/i)) {
                return 'Android';
            } else {
                return navigator.appCodeName;
            }
        };

        $scope.controllerInit = function() {
            $("#loader").hide();
            $("#troubleshoot").hide();

            $scope.questions = [];
            activeData.setSurveyCompleted(false);

            if (localStorage.surveyInProgress) {
                console.log("survey in progress exists");
                $scope.surveyInProgress = JSON.parse(localStorage.surveyInProgress);
                console.log($scope.surveyInProgress);
                for (var surveyId in $scope.surveyInProgress) {
                    $scope.allSurveyData[surveyId] = {};
                    $scope.allSurveyData[surveyId].id = surveyId;
                }
            } else {
                localStorage.surveyInProgress = JSON.stringify([]);
            }
            if (!localStorage.surveyAppPin) {
                if (!localStorage.surveyAppServerSettings) {
                    $scope.surveyCommonMessage = "You don't have your PIN and server settings setup.";
                    $("#surveyCommonMessage").addClass("help-block alert alert-danger");
                } else
                    $scope.surveyCommonMessage = "You don't have your PIN setup.";
                $("#surveyCommonMessage").addClass("help-block alert alert-danger");
            } else if (!localStorage.surveyAppServerSettings) {
                $scope.surveyCommonMessage = "You don't have your server settings setup.";
                $("#surveyCommonMessage").addClass("help-block alert alert-danger");
            } else {
                var userPIN = localStorage.surveyAppPin;
                var payloadForService = '{"userPIN":"' + userPIN + '"}';
                /*jshint newcap: false */
                var surveyIDCall = new serviceCall("scheduledactivity", "GET");
                var currentTime = new Date().getTime();
                var logEvent = [];
                if (localStorage.getItem("uiLogger") !== null) {
                    logEvent = JSON.parse(localStorage.getItem("uiLogger"));
                }
                logEvent.push({
                    "pin": localStorage.surveyAppPin,
                    "eventName": "scheduledactivity_call",
                    "metaData": {},
                    "eventTime": currentTime
                });

                var jsonString = JSON.stringify(logEvent);
                localStorage.setItem("uiLogger", jsonString);

                $('#loader').modal('show');
                if (localStorage.dataSource == "remote") {
                    surveyIDCall.call(payloadForService, $scope.surveyIDsuccess, $scope.serviceError);
                }
                if (localStorage.dataSource == "local") {
                    surveyIDCall.call(payloadForService, $scope.surveyIDsuccess, $scope.serviceError, "json/getSurveyID.json");
                }
            }
        };

        $scope.surveyIDsuccess = function(data, status, headers, config) {
            $('#loader').modal('hide');
            console.log("In survey Id success");
            $scope.statusReturned = status;
            $scope.populateDebugInformation();
            var okayToStartCount = 0;
            var surveyDueMessageOne = "";
            var surveyDueMessageTwo = "";
            var surveyDueMessageThree = "";
            $scope.button1text = "";
            $scope.button2text = "";
            $scope.button3text = "";
            $scope.FirstActivityDescription = "";

            ////console.log("Message forn server : "+status);
            ////console.log("Message from server : "+data.message);
            if (status === 200 && data.activities.length !== 0) {
                ////console.log(data.activities);
                var date;
                for (var i = 0; i < 3; i++) {
                    if(data.activities[i] !== undefined){
                        $scope.isSurveyDue = true;
                        okayToStartCount++;
                        $scope.allSurveyData[data.activities[i].activityInstanceID] = {};
                        $scope.allSurveyData[data.activities[i].activityInstanceID].id = data.activities[i].activityInstanceID;
                        if (i === 0) {
                            surveyDueMessageOne = data.activities[i].activityTitle;
                            $scope.button1text = "Start " + surveyDueMessageOne;
                            $scope.FirstActivityDescription = data.activities[i].description + "<id = " + data.activities[i].activityInstanceID + ">";
                        } else if (i === 1) {
                            surveyDueMessageTwo = data.activities[i].activityTitle;
                            $scope.button2text = "Start " + surveyDueMessageTwo;
                            $scope.SecondActivityDescription = data.activities[i].description + "<id = " + data.activities[i].activityInstanceID + ">";
                        } else if (i === 2) {
                            surveyDueMessageThree = data.activities[i].activityTitle;
                            $scope.button3text = "Start " + surveyDueMessageThree;
                            $scope.ThirdActivityDescription = data.activities[i].description + "<id = " + data.activities[i].activityInstanceID + ">";
                        }

                        $scope.dueSurveys.push(data.activities[i].activityInstanceID);
                        $scope.dueSurveyNames.push(data.activities[i].activityTitle);
                        date = $scope.formatDate(data.activities[i].nextDueAt);

                        console.log($scope.allSurveyData);
                        $scope.allSurveyData[data.activities[i].activityInstanceID].date = date;
                    }
                }
                if (okayToStartCount === 2) {
                    okayToStart = true;
                    $scope.areMultipleSurveysDue = true;
                    $("#startFirstSurvey").removeClass("disabled");
                    $("#startSecondSurvey").removeClass("disabled");
                } else if (okayToStartCount === 1) {
                    okayToStart = true;
                    $("#startFirstSurvey").removeClass("disabled");
                } else if (okayToStartCount === 3) {
                    ////console.log("There are three surveys");
                    okayToStart = true;
                    $scope.areMultipleSurveysDue = true;
                    $scope.isThirdSurveyDue = true;
                    $("#startFirstSurvey").removeClass("disabled");
                    $("#startSecondSurvey").removeClass("disabled");
                    $("#startThirdSurvey").removeClass("disabled");
                }
                ////console.log(okayToStartCount);
                ////console.log("Survey in progress");
                ////console.log($scope.surveyInProgress);

                for (i = 0; i < 3; i++) {
                    if(data.activities[i] !== undefined){
                        if (i === 0) {
                            if ($scope.surveyInProgress.length > 0 && $scope.surveyInProgress.indexOf($scope.dueSurveys[i]) != -1) {
                                $scope.surveyDueMessageOne = "You have a " + surveyDueMessageOne + " in progress, due on " + $scope.allSurveyData[data.activities[i].activityInstanceID].date + ". To begin survey, please click on the Start Survey button.";
                                ////console.log($scope.surveyDueMessageOne);
                            } else {
                                if (okayToStart) {
                                    $scope.surveyDueMessageOne = "You have a " + surveyDueMessageOne + " due on " + $scope.allSurveyData[data.activities[i].activityInstanceID].date + ". To begin survey, please click on the Start button.";
                                    ////console.log($scope.surveyDueMessageOne);
                                } else {
                                    $scope.surveyDueMessageOne = "You have a " + surveyDueMessageOne + " due on " + $scope.allSurveyData[data.activities[i].activityInstanceID].date + ". ";
                                    ////console.log($scope.surveyDueMessageOne);
                                }
                            }
                        } else if (i === 1) {
                            if ($scope.surveyInProgress.length > 0 && $scope.surveyInProgress.indexOf($scope.dueSurveys[i]) != -1) {
                                $scope.surveyDueMessageTwo = "You have a " + surveyDueMessageTwo + " in progress, due on " + $scope.allSurveyData[data.activities[i].activityInstanceID].date + ". To begin survey, please click on the Start Survey button.";
                                ////console.log($scope.surveyDueMessageTwo);
                            } else {
                                if (okayToStart) {
                                    $scope.surveyDueMessageTwo = "You have a " + surveyDueMessageTwo + " due on " + $scope.allSurveyData[data.activities[i].activityInstanceID].date + ". To begin survey, please click on the Start button.";
                                    ////console.log($scope.surveyDueMessageTwo);
                                } else {
                                    $scope.surveyDueMessageTwo = "You have a " + surveyDueMessageTwo + " due on " + $scope.allSurveyData[data.activities[i].activityInstanceID].date + ". ";
                                    ////console.log($scope.surveyDueMessageTwo);
                                }
                            }
                        } else if (i === 2) {
                            if ($scope.surveyInProgress.length > 0 && $scope.surveyInProgress.indexOf($scope.dueSurveys[i]) != -1) {
                                $scope.surveyDueMessageThree = "You have a " + surveyDueMessageThree + " in progress, due on " + $scope.allSurveyData[data.activities[i].activityInstanceID].date + ". To begin survey, please click on the Start Survey button.";
                                ////console.log($scope.surveyDueMessageThree);
                            } else {
                                if (okayToStart) {
                                    $scope.surveyDueMessageThree = "You have a " + surveyDueMessageThree + " due on " + $scope.allSurveyData[data.activities[i].activityInstanceID].date + ". To begin survey, please click on the Start button.";
                                    ////console.log($scope.surveyDueMessageThree);
                                } else {
                                    $scope.surveyDueMessageThree = "You have a " + surveyDueMessageThree + " due on " + $scope.allSurveyData[data.activities[i].activityInstanceID].date + ". ";
                                    ////console.log($scope.surveyDueMessageThree);
                                }
                            }
                        }
                    }

                }


            } else if (status === 204) {
                $scope.surveyCommonMessage = "You have no activities due. Please check in again later.";
                $("#surveyCommonMessage").addClass("help-block alert alert-info");
            } else if (status === 204 && data.message == "Your PIN is not active") {
                $scope.surveyCommonMessage = "Your PIN is not active. Please contact the administrator.";
                $("#surveyCommonMessage").addClass("help-block alert alert-info");
            } else if (status === 500) {
                $scope.surveyCommonMessage = "Unexpected Error. Please contact the administrator.";
                $("#surveyCommonMessage").addClass("help-block alert alert-info");
            } else if (data.activities.length === 0) {
                $scope.surveyCommonMessage = "You don't have any activities due at this time. Please check again later.";
                $("#surveyCommonMessage").addClass("help-block alert alert-info");
            }

            /* logging event */
            var currentTime = new Date().getTime();
            var logEvent = [];
            if (localStorage.getItem("uiLogger") !== null) {
                logEvent = JSON.parse(localStorage.getItem("uiLogger"));
            }
            logEvent.push({
                "pin": localStorage.surveyAppPin,
                "eventName": "scheduledactivity_call_success",
                "metaData": {
                    "activityCount": data.activities.length,
                    "message": $scope.surveyCommonMessage
                },
                "eventTime": currentTime
            });

            var jsonString = JSON.stringify(logEvent);
            localStorage.setItem("uiLogger", jsonString);
            /* logging event ends */

        };

        $scope.serviceError = function(data, status, headers, config) {
            $('#loader').modal('hide');
            console.log(status);
            console.log(data);
            console.log(headers);
            $scope.statusReturned = status;
            $scope.populateDebugInformation();
            if (data === null) {
                $scope.surveyCommonMessage = "For some reason the server is not reachable. We apologize for the inconvenience";
                $("#surveyCommonMessage").addClass("help-block alert alert-danger");
                $scope.troubleshootOptions = ['Check the internet connection', 'Check if the entered URL is correct', 'Check in the advance settings if proper "Protocol" is selected', ' If neither of those work, click the "D" button on the top right and contact your administrator'];
                $("#troubleshoot").show();
                $("#troubleshoot").addClass("help-block alert alert-info");
            } else if (status === 500) {
                $scope.surveyCommonMessage = "Oops, There is some problem from our side. We will fix it as soon as possible. We apologize for the inconvenience. Please try again later...";
                $("#surveyCommonMessage").addClass("help-block alert alert-danger");
                $scope.troubleshootOptions = [];
                $scope.troubleshootOptions.push('Please try again after some time');
                $scope.troubleshootOptions.push('If the error persists for more than 3 days, please send the information on the top right corner inside the "D" button to your administrator');
                $("#troubleshoot").show();
                $("#troubleshoot").addClass("help-block alert alert-info");
            } else if (status === 404) {
                if (data.message === "The PIN is invalid") {
                    $scope.surveyCommonMessage = data.message;
                    $("#surveyCommonMessage").addClass("help-block alert alert-danger");
                } else {
                    $scope.surveyCommonMessage = "The requested API path was not present on the server...";
                    $("#surveyCommonMessage").addClass("help-block alert alert-danger");
                    $scope.troubleshootOptions = [];
                    $scope.troubleshootOptions.push('Check if the entered URL is correct');
                    $scope.troubleshootOptions.push('Check if the pin number is correct');
                    $scope.troubleshootOptions.push('If the above steps also fail for you then email us the information inside the "D" button on the top right corner to your administrator');
                    $("#troubleshoot").show();
                    $("#troubleshoot").addClass("help-block alert alert-info");
                }

            } else {
                //console.log("Message from server : "+data.message);
                ////console.log(activeData.getError());
                if (data.message === "Survey instance has expired") {
                    $("#surveyCommonMessage").addClass("help-block alert alert-danger");
                }
            }
            if (activeData.getError() === "offline") {
                $scope.surveyMessage = "Unable to fetch data. Please check internet connectivity.";
            } else if (activeData.getError() === "internalServerError") {
                $scope.surveyMessage = "Unable to fetch data. There's a server error.";
            } else if (activeData.getError() === "notFound") {
                $scope.surveyMessage = "Unable to fetch data. API not found on server. Incorrect route.";
            } else if (activeData.getError() === "unknownError") {
                $scope.surveyMessage = "Unable to fetch data. Please check internet connectivity or contact administrator.";
            } else {
                $scope.surveyCommonMessage = 'Unable to fetch data. Please contact administrator.';
            }
        };

        $scope.getFirstSurvey = function() {
            if (okayToStart) {
                angular.forEach($scope.dueSurveys, function(value, key) {
                    if (key === 0) {
                        $scope.nextDueSurveyID = value;
                    }
                });
                ////console.log($scope.nextDueSurveyID);
                activeData.setSurveyID($scope.nextDueSurveyID);
                $scope.callGetSurveyAPI();
            }
        };

        $scope.getSecondSurvey = function() {
            if (okayToStart) {
                angular.forEach($scope.dueSurveys, function(value, key) {
                    if (key == 1) {
                        $scope.nextDueSurveyID = value;
                    }
                });
                ////console.log($scope.nextDueSurveyID);
                activeData.setSurveyID($scope.nextDueSurveyID);
                $scope.callGetSurveyAPI();
            }
        };

        $scope.getThirdSurvey = function() {
            if (okayToStart) {
                angular.forEach($scope.dueSurveys, function(value, key) {
                    if (key == 2) {
                        $scope.nextDueSurveyID = value;
                    }
                });
                ////console.log($scope.nextDueSurveyID);
                activeData.setSurveyID($scope.nextDueSurveyID);
                activeData.setGameFlag("true");
                $scope.callGetSurveyAPI();
            }
        };

        $scope.callGetSurveyAPI = function() {
            var userPIN = localStorage.surveyAppPin;
            var payloadForService = '{"surveyInstanceID":' + $scope.nextDueSurveyID + ',' + '"userPIN":' + userPIN + '}';
            ////console.log(payloadForService);
            /*jshint newcap: false */
            $('#loader').modal('show');
            var getSurveyCall = new serviceCall("activityinstance", "GET");
            /* logging event */
            var currentTime = new Date().getTime();
            var logEvent = [];
            if (localStorage.getItem("uiLogger") !== null) {
                logEvent = JSON.parse(localStorage.getItem("uiLogger"));
            }
            logEvent.push({
                "pin": localStorage.surveyAppPin,
                "eventName": "activityinstance_call",
                "metaData": {
                    "activityInstanceId": $scope.nextDueSurveyID
                },
                "eventTime": currentTime
            });

            var jsonString = JSON.stringify(logEvent);
            localStorage.setItem("uiLogger", jsonString);
            /* logging event ends */
            if (localStorage.dataSource == "remote") {
                getSurveyCall.call(payloadForService, $scope.getSurveySuccess, $scope.serviceError);
            }
            if (localStorage.dataSource == "local") {
                getSurveyCall.call(payloadForService, $scope.getSurveySuccess, $scope.serviceError, "json/getSurvey.json");
            }
        };

        $scope.getSurveySuccess = function(data, status, headers, config) {
            $('#loader').modal('hide');
            ////console.log(status);
            ////console.log(data);
            ////console.log(data.activityName);

            /* logging event */
            var currentTime = new Date().getTime();
            var logEvent = [];
            if (localStorage.getItem("uiLogger") !== null) {
                logEvent = JSON.parse(localStorage.getItem("uiLogger"));
            }
            logEvent.push({
                "pin": localStorage.surveyAppPin,
                "eventName": "activityinstance_call_success",
                "metaData": {
                    "activityInstanceId": $scope.nextDueSurveyID,
                    "activityInstanceTitle": data.activityName
                },
                "eventTime": currentTime
            });

            var jsonString = JSON.stringify(logEvent);
            localStorage.setItem("uiLogger", jsonString);
            /* logging event ends */

            activeData.setSurveyName(data.activityName);
            console.log("activityInstanceId");
            console.log(data.activityInstanceId);
            activeData.setActivityInstanceID(data.activityInstanceId);
            ////console.log(data.sequence);
            activeData.setSequence(data.sequence);
            ////console.log(data.activitySequence);
            activeData.setActivitySequence(data.activitySequence);
            $scope.generateQuestionArray();
            console.log($scope.surveyInProgress);
            if ($scope.surveyInProgress.indexOf($scope.nextDueSurveyID) == -1) {
                $scope.surveyInProgress.push($scope.nextDueSurveyID);
                localStorage.surveyInProgress = JSON.stringify($scope.surveyInProgress);
                //console.log("Adding surveys to in progress");
                //console.log(localStorage.surveyInProgress);
                //console.log($scope.surveyInProgress);
            }
            localStorage.surveyStartedAt = new Date().getTime();
            ////console.log(data.showGame);
            activeData.setGameFlag(data.showGame);
            //console.log(localStorage.surveyInProgress);
            $scope.changePage('/survey');

        };

        $scope.generateQuestionArray = function() {
            $scope.questions = [];
            $scope.activitySequence = activeData.getActivitySequence();
            var questioncount = 0;
            for (var i = 0; i < $scope.activitySequence.length; i++) {
                for (var j = 0; j < $scope.activitySequence[i].questions.length; j++) {
                    if ($scope.activitySequence[i].activityBlockId == "CAT") {
                        ////console.log($scope.activitySequence[i].questions[j].isMandatory);
                        if ($scope.activitySequence[i].questions[j].isMandatory === false) {

                            ////console.log($scope.activitySequence[i].questions[j].question);
                        } else if ($scope.activitySequence[i].questions[j].isMandatory === true) {

                            ////console.log($scope.activitySequence[i].questions[j].question);

                            if ($scope.activitySequence[i].questions[j].isNested === true) {
                                $scope.questions[questioncount] = {
                                    "questionText": $scope.activitySequence[i].questions[j].question,
                                    "quesID": $scope.activitySequence[i].questions[j].quesID,
                                    "answerOptions": $scope.activitySequence[i].questions[j].answerOptions,
                                    "questionType": $scope.activitySequence[i].questions[j].questionType,
                                    "activityBlockId": $scope.activitySequence[i].activityBlockId,
                                    "adaptive": true

                                };
                            } else {
                                $scope.questions[questioncount] = {
                                    "questionText": $scope.activitySequence[i].questions[j].question,
                                    "quesID": $scope.activitySequence[i].questions[j].quesID,
                                    "answerOptions": $scope.activitySequence[i].questions[j].answerOptions,
                                    "questionType": $scope.activitySequence[i].questions[j].questionType,
                                    "activityBlockId": $scope.activitySequence[i].activityBlockId,
                                    "adaptive": false
                                };
                            }
                            questioncount++;
                        }


                    } else {
                        ////console.log($scope.activitySequence[i].activityBlockId);
                        $scope.questions[questioncount] = {
                            "questionText": $scope.activitySequence[i].questions[j].question,
                            "quesID": $scope.activitySequence[i].questions[j].quesID,
                            "answerOptions": $scope.activitySequence[i].questions[j].answerOptions,
                            "questionType": $scope.activitySequence[i].questions[j].questionType,
                            "activityBlockId": $scope.activitySequence[i].activityBlockId,
                            "shortForm": $scope.activitySequence[i].questions[j].shortForm,
                            "adaptive": false
                        };
                        questioncount++;
                    }


                }
            }
            activeData.setSurveyQuestions($scope.questions);
            ////console.log($scope.questions);

            // activeData.setGameFlag($scope.activitySequence.showGame);
            // ////console.log($scope.activitySequence.showGame);


        };

        $scope.formatDate = function(dateString) {
            // console.log(dateString);
            // var dateSplit = dateString.split(' ');
            // console.log(dateSplit);
            var date = new Date(dateString);
            // console.log(newDate);
            // We need to format date manually because date(String) is dependent on the browser implementation.
            // Also months start from 0 so we need -1 for months
            // var date = new Date(parseInt(dateSplit[dateSplit.length - 1]), 2, parseInt(dateSplit[2]));
            var dd = date.getDate();
            var mm = date.getMonth() + 1; //January is 0!
            var yyyy = date.getFullYear();
            // console.log("dd mm yyyy : "+dd+" "+mm+" "+yyyy);

            if (dd < 10) {
                dd = "0" + dd;
            }
            if (mm < 10) {
                mm = "0" + mm;
            }
            date = mm + "/" + dd + "/" + yyyy;
            return date;
        };

        $scope.changePage = function(path) {
            // $("#loader").show();
            $location.path(path);
        };

        // Date format conversion
        Date.prototype.yyyymmdd = function() {
            var yyyy = this.getFullYear().toString();
            var mm = (this.getMonth() + 1).toString(); // getMonth() is zero-based
            var dd = this.getDate().toString();
            // var hh = this.getHours().toString();
            // var mi = this.getMinutes().toString();
            // var ss = this.getSeconds().toString();
            // return yyyy + "-" +(mm[1]?mm:"0"+mm[0]) + "-" + (dd[1]?dd:"0"+dd[0]) + " " + hh + ":" + (mi[1]?mi:"0"+mi[0]) + ":" + (ss[1]?ss:"0"+ss[0]); // padding
            return yyyy + "-" + (mm[1] ? mm : "0" + mm[0]) + "-" + (dd[1] ? dd : "0" + dd[0]); // padding
        };

        $scope.controllerInit();

    }]);

})();
