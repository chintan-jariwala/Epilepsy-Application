(function() {

    'use strict';

    //Load controller
    angular.module('surveyApp').controller('surveyEndController', ['$scope', '$location', '$timeout', 'serviceCall', 'activeData', function($scope, $location, $timeout, serviceCall, activeData) {
        $scope.title = "Submit";

        $scope.debugInformation = "";
        // Error Handling
        $scope.error = {};
        /*jshint -W030 */
        $scope.error.message;

        $scope.success = {};
        /*jshint -W030 */
        $scope.success.message;
        /*jshint -W030 */
        $scope.payloadForService;
        $scope.troubleshootOptions = [];


        $scope.submit = function() {
            var reg = /^\d+$/;
            if (!reg.test($scope.pin)) {
                if (angular.isUndefined($scope.pin) || $scope.pin === '' || $scope.pin === ' ') {
                    $scope.error.message = 'PIN cannot be blank.';
                } else {
                    $scope.error.message = 'PIN should contain numbers only.';
                }
            } else {
                $scope.error.message = '';
                if (localStorage.surveyAppPin === $scope.pin) {
                    console.log("matched");
                    $scope.submitCall();
                } else {
                    $scope.error.message = 'PIN mismatch. Please try again.';
                }
            }
        };

        $scope.submitCall = function() {
            $("#loader").show();

            $scope.surveyID = activeData.getSurveyID();
            console.log("survey id  - " + $scope.surveyID);
            var isBodyPain = false;
            var surveyResults = [];
            var pr_anxiety = [];
            var pr_fatigue = [];
            var pr_physfuncmob = [];
            var pr_painInt = [];
            var pr_PI = [];
            var pr_PI_Weekly = [];
            var ma = [];
            var cat = [];
            var isNewGeneralizedPain = false;
            angular.forEach(localStorage, function(value, key) {
                if (key.indexOf($scope.surveyID.toString() + ":") > -1) {
                    console.log(key);
                    console.log(value);
                    var questionType = key.split(":")[3];
                    var activityBlockId = key.split(":")[2];
                    var quesID = key.split(":")[1];
                    var selectedOptions = [];
                    var bodyPain = [];
                    var generalizedpain = [];
                    console.log(activityBlockId);

                    // console.log(activityBlockId);
                    // console.log(activityBlockId);
                    if (questionType === "bodypain") {
                        //body pain question
                        // var pain = {"pain": JSON.parse(value)};
                        isBodyPain = true;
                        bodyPain.push(JSON.parse(value));
                        $scope.isPainIntensity = true;

                    } else if (value.indexOf("intensity") > -1) {
                        //generalizedpain question
                        bodyPain.push(JSON.parse(value));
                        $scope.isPainIntensity = true;
                    } else if (questionType === "MCMADW") {
                        var temp1 = JSON.parse(value);
                        //selectedOptions.push(temp1);
                        angular.forEach(temp1, function(value, key) {
                            console.log(typeof(value));
                            console.log(typeof(key));
                            var answerString = '{"answerID":' + key + ',"dosage":"' + value + '"}';
                            console.log(answerString);
                            selectedOptions.push(JSON.parse(answerString));
                        });
                        $scope.isPainIntensity = false;
                    } else if (questionType === "MCMA" || questionType === "PI_WEEKLY") {
                        //checkbox question
                        isNewGeneralizedPain = true;
                        console.log("checkbox");
                        var temp = value.split(",");
                        for (var x = 0; x < temp.length; x++) {
                            selectedOptions.push(temp[x]);
                        }
                        $scope.isPainIntensity = false;
                    } else {
                        //normal question
                        selectedOptions.push(value);
                        $scope.isPainIntensity = false;
                    }
                    // var totalAnswer = '{"quesID":' + quesID +',"selectedOptions":[' + selectedOptions + '],"bodyPain":[' + bodyPain + ']}';
                    if (quesID != 'undefined') {

                        if (activityBlockId === "PI_DAILY") {
                            var totalAnswer_pi = "";
                            if (isBodyPain) {
                                totalAnswer_pi = {
                                    "quesID": quesID,
                                    "bodyPain": bodyPain
                                };
                                isBodyPain = false;
                            }
                            pr_PI.push(totalAnswer_pi);

                        }
                        if (activityBlockId === "PI_WEEKLY") {
                            var totalAnswer_pi_weekly = "";
                            totalAnswer_pi_weekly = {
                                "quesID": quesID,
                                "generalizedpain": selectedOptions
                            };
                            pr_PI_Weekly.push(totalAnswer_pi_weekly);
                        } else if (activityBlockId === "PR_Anxiety") {

                            var totalAnswer_id = {
                                "quesID": quesID,
                                "selectedOptions": selectedOptions
                            };
                            pr_anxiety.push(totalAnswer_id);

                        } else if (activityBlockId === "PR_PainInt") {

                            var totalAnswer_PR_PainInt = {
                                "quesID": quesID,
                                "selectedOptions": selectedOptions
                            };
                            pr_painInt.push(totalAnswer_PR_PainInt);

                        } else if (activityBlockId === "PR_Fatigue") {

                            var totalAnswer_PR_Fatigue = {
                                "quesID": quesID,
                                "selectedOptions": selectedOptions
                            };
                            pr_fatigue.push(totalAnswer_PR_Fatigue);

                        } else if (activityBlockId === "PR_PhysFuncMob") {

                            var totalAnswer_PR_PhysFuncMob = {
                                "quesID": quesID,
                                "selectedOptions": selectedOptions
                            };
                            pr_physfuncmob.push(totalAnswer_PR_PhysFuncMob);

                        } else if (activityBlockId === "MA") {
                            var totalAnswer_MA = {
                                "quesID": quesID,
                                "selectedOptions": selectedOptions
                            };
                            ma.push(totalAnswer_MA);
                        } else if (activityBlockId === "CAT") {
                            var totalAnswer_CAT = {
                                "quesID": quesID,
                                "selectedOptions": selectedOptions
                            };
                            cat.push(totalAnswer_CAT);
                        }


                    }
                    console.log("Answer JSON :");
                    // console.log(totalAnswer);

                }
            });
            var surveyID = $scope.surveyID.toString();
            var today = new Date().getTime();

            // $scope.payloadForLogger = '{"loggerResults":'+JSON.stringify(loggerResults)+'}';
            // var surveyLogger = new serviceCall("survey_logger","POST");
            // surveyLogger.call($scope.payloadForLogger,$scope.submitLoggerSuccess,$scope.submitLoggerError);
            var middle = "";
            var _testing = "";
            if (pr_fatigue.length > 0) {
                var _pr_fatigue = JSON.stringify(pr_fatigue);
                middle = '{"activityBlockId" : "PR_Fatigue", "answers" :' + (_pr_fatigue) + ' }';
                surveyResults.push(middle);
            }
            if (pr_physfuncmob.length > 0) {
                var _pr_physfuncmob = JSON.stringify(pr_physfuncmob);
                middle = '{"activityBlockId" : "PR_PhysFuncMob", "answers" :' + (_pr_physfuncmob) + ' }';
                surveyResults.push(middle);
            }
            if (pr_painInt.length > 0) {
                var _pr_painInt = JSON.stringify(pr_painInt);
                middle = '{"activityBlockId" : "PR_PainInt", "answers" :' + (_pr_painInt) + ' }';
                surveyResults.push(middle);
            }
            if (pr_PI.length > 0) {
                _testing = JSON.stringify(pr_PI);
                middle = '{"activityBlockId" : "PI_DAILY", "answers" :' + _testing + ' }';
                surveyResults.push(middle);
            }
            if (pr_anxiety.length > 0) {
                var _pr_anxiety = JSON.stringify(pr_anxiety);
                middle = '{"activityBlockId" : "PR_Anxiety", "answers" :' + (_pr_anxiety) + ' }';
                surveyResults.push(middle);
            }
            if (ma.length > 0) {
                var _ma = JSON.stringify(ma);
                middle = '{"activityBlockId" : "MA", "answers" :' + (_ma) + ' }';
                surveyResults.push(middle);
            }
            if (cat.length > 0) {
                var _cat = JSON.stringify(cat);
                middle = '{"activityBlockId" : "CAT", "answers" :' + (_cat) + ' }';
                surveyResults.push(middle);
            }
            if (pr_PI_Weekly.length > 0) {
                var _pr_weekly = JSON.stringify(pr_PI_Weekly);
                middle = '{"activityBlockId" : "PI_WEEKLY", "answers" :' + _pr_weekly + ' }';
                surveyResults.push(middle);
            }
            today = new Date().getTime();
            var thisisjustatest = '{"activityInstanceID":' + $scope.surveyID.toString() + ',"timeStamp":' + today + ',"activityResults": [' + surveyResults + ']}';
            $scope.payloadForService = (thisisjustatest);
            console.log(JSON.stringify($scope.payloadForService));

            /* Logging event */

            var logEvent = [];
            var loggerResults = [];
            if (localStorage.getItem("uiLogger") !== null) {
                logEvent = JSON.parse(localStorage.getItem("uiLogger"));
            }
            logEvent.push({
                "pin": localStorage.surveyAppPin,
                "eventName": "activityinstanceresult_call",
                "metaData": {
                    "sid": surveyID
                },
                "eventTime": today
            });

            var jsonString = JSON.stringify(logEvent);
            localStorage.setItem("uiLogger", jsonString);

            if (localStorage.getItem("uiLogger") !== null) {
                loggerResults = JSON.parse(localStorage.getItem("uiLogger"));
            }
            /* Logging event ends */

            var surveySubmitCall = new serviceCall("activityinstanceresult", "POST");

            if (localStorage.dataSource == "remote") {
                surveySubmitCall.call($scope.payloadForService, $scope.submitCallSuccess, $scope.submitCallError);
            }
            // if(localStorage.dataSource == "local"){
            //   surveySubmitCall.call($scope.payloadForService,$scope.submitCallSuccess,$scope.submitCallError,"json/submitSurvey.json");
            // }



        };

        // $scope.submitLoggerSuccess = function(data,status){
        //   console.log(status);
        //   if(data.message === "Success"){
        //     console.log("Logger results submiited successfully");
        //     if(localStorage.getItem("logging") !== null){
        //        localStorage.removeItem("logging");
        //     }
        //   }
        // };
        //
        // $scope.submitLoggerError = function(data,status){
        //   console.log(status);
        //   console.log(data);
        //   if(localStorage.getItem("logging") !== null){
        //      localStorage.removeItem("logging");
        //   }
        // };

        $scope.submitCallSuccess = function(data, status) {
            console.log(activeData.getError());
            console.log(status);
            $("#loader").hide();
            console.log(data.message);
            $scope.gameFlag = activeData.getGameFlag();
            console.log($scope.gameFlag);
            if (data.message === "SUCCESS") {
                console.log("In Success");
                $scope.success.message = "Submitted successfully.";
                $("#submitBtn").hide();

                var SurveysInProgress = JSON.parse(localStorage.surveyInProgress);
                SurveysInProgress.splice(SurveysInProgress.indexOf($scope.surveyID), 1);
                localStorage.surveyInProgress = JSON.stringify(SurveysInProgress);
                console.log("After me timeout should be called");
                $timeout(function () {
                    $scope.changePage('/home');
                    $("#loader").show();
                }, 2000);

            } else {
                $scope.error.message = data.message;
            }

            /*Logging event*/
            var today = new Date().getTime();
            var logEvent = [];
            var loggerResults = [];
            if (localStorage.getItem("uiLogger") !== null) {
                logEvent = JSON.parse(localStorage.getItem("uiLogger"));
            }
            logEvent.push({
                "pin": localStorage.surveyAppPin,
                "eventName": "activityinstanceresult_call_success",
                "metaData": {
                    "sid": $scope.surveyID,
                    "message": data.message
                },
                "eventTime": today
            });

            var jsonString = JSON.stringify(logEvent);
            localStorage.setItem("uiLogger", jsonString);

            if (localStorage.getItem("uiLogger") !== null) {
                loggerResults = JSON.parse(localStorage.getItem("uiLogger"));
            }
            /*Logging event ends*/
            //   var platform = $scope.getMobileOperatingSystem();
            //   var surveyID = $scope.surveyID;
            //   if(platform === 'iOS'){
            //     // ios
            //     var standalone = window.navigator.standalone;
            //     var userAgent = window.navigator.userAgent.toLowerCase();
            //     var safari = /safari/.test( userAgent );
            //     if ( !standalone && safari ) {
            //       //browser
            //       // do nothing
            //     } else if (userAgent.indexOf("painreport") > -1){
            //       // custom webview
            //       $("#exitBtn").hide();
            //     } else if ( !standalone && !safari ) {
            //       //uiwebview
            //       $("#exitBtn").hide();
            //     }
            //   }
            // }else{
            //   $scope.error.message = data.message;
            //   $scope.saveSubmissionToLocalStorage();
            // }
            // activeData.setSurveyCompleted(true);
            // $scope.clearLocalStorage();

        };
        $scope.goToHome = function() {
            $scope.changePage('/home');
        };

        $scope.submitCallError = function(data, status) {
            console.log(activeData.getError());
            $("#loader").hide();
            console.log("here");
            console.log(status);
            console.log(data);
            $scope.saveSubmissionToLocalStorage();
            if (activeData.getError() === "offline") {
                $scope.error.message = "Unable to submit. There seems to be an issue with internet connectivity";
                $("#submitBtn").hide();
                $("#homeBtn").show();
                $scope.troubleshootOptions = [];
                $scope.troubleshootOptions.push('Please make sure you have internet connection and try again.');
                $("#troubleshoot").show();
                $("#troubleshoot").addClass("help-block alert alert-info");
            } else if (data.message === "Survey_instance has been completed") {
                $scope.error.message = "Unable to submit. This acitvity instance has already been subitted.";
                $("#submitBtn").hide();
                $("#homeBtn").show();
                $scope.troubleshootOptions = [];
                $scope.troubleshootOptions.push('If you were not the one to submit the survey please contact the administrator.');
                $("#troubleshoot").show();
                $("#troubleshoot").addClass("help-block alert alert-info");
            } else if (data.message === "Survey instance has expired") {
                $scope.error.message = "Unable to submit. This acitvity instance has expired.";
                $("#submitBtn").hide();
                $("#homeBtn").show();
            } else if (activeData.getError() === "offline") {
                $scope.error.message = "Unable to submit. Please check internet connectivity.";
            } else if (activeData.getError() === "internalServerError") {
                $("#homeBtn").show();
                $scope.error.message = "Unable to submit. There's a server error";
                $scope.troubleshootOptions = [];
                $scope.troubleshootOptions.push('There is some problem with our server. We apologize for this. Please, try again later');
                $scope.troubleshootOptions.push('If the problem persists for more than 3 days, please contact the administrator');
                $("#troubleshoot").show();
                $("#troubleshoot").addClass("help-block alert alert-info");
            } else if (activeData.getError() === "notFound") {
                $scope.error.message = "Unable to submit. API not found on server. Incorrect route.";
            } else if (activeData.getError() === "unknownError") {
                $scope.error.message = "Unable to submit. Please check internet connectivity or contact administrator.";
            } else {
                $scope.error.message = 'Unable to submit. Please contact administrator.';
            }
            // if(!navigator.onLine){
            //   alert("Error! Unable to fetch data. Please check internet connectivity.");
            // } else if(status === 500){
            //   alert("Error! Unable to fetch data. There's a server error.");
            // } else if(status === 404){
            //   alert("Error! API not found on server. Incorrect route.");
            // } else{
            //   alert("Error! Unable to fetch data. Please check the internet connectivity or app settings or contact administrator.");
            // }

            /*Logging event*/
            var today = new Date().getTime();
            var logEvent = [];
            var loggerResults = [];
            if (localStorage.getItem("uiLogger") !== null) {
                logEvent = JSON.parse(localStorage.getItem("uiLogger"));
            }
            logEvent.push({
                "pin": localStorage.surveyAppPin,
                "eventName": "activityinstanceresult_call_error",
                "metaData": {
                    "sid": $scope.surveyID,
                    "message": $scope.error.message
                },
                "eventTime": today
            });

            var jsonString = JSON.stringify(logEvent);
            localStorage.setItem("uiLogger", jsonString);

            if (localStorage.getItem("uiLogger") !== null) {
                loggerResults = JSON.parse(localStorage.getItem("uiLogger"));
            }
            /*Logging event ends*/
        };

        $scope.saveSubmissionToLocalStorage = function() {
            var surveyID = $scope.surveyID;
            var payload = $scope.payloadForService;
            console.log(surveyID + " : " + payload);
            localStorage[surveyID] = payload;
            $scope.callNativeSubmitErrorHandler();
        };

        $scope.clearLocalStorage = function() {
            console.log("here");
            var questions = activeData.getSurveyQuestions();
            $.each(questions, function(index, val) {
                console.log($scope.surveyID + ":" + val.quesID + ":" + val.activityBlockId);
                localStorage.removeItem($scope.surveyID + ":" + val.quesID + ":" + val.activityBlockId);
            });
            localStorage.removeItem("reloadBackupSurveyID");
            localStorage.removeItem("reloadBackupSurveyName");
            localStorage.removeItem("reloadBackupSurveyQuestions");
        };

        $scope.changePage = function(path) {
            $location.path(path);
        };

        $scope.callNativeSubmitErrorHandler = function() {
            var platform = $scope.getMobileOperatingSystem();
            var surveyID = $scope.surveyID;
            console.log(platform);
            if (platform === 'iOS') {
                // ios
                var standalone = window.navigator.standalone;
                var userAgent = window.navigator.userAgent.toLowerCase();
                var safari = /safari/.test(userAgent);
                if (!standalone && safari) {
                    //browser
                    // do nothing
                } else if (userAgent.indexOf("painreport") > -1) {
                    // custom webview
                    // call handler
                    window.location = 'jsHandler://submitError?json=' + localStorage[surveyID];
                } else if (!standalone && !safari) {
                    //uiwebview
                    // call handler
                    window.location = 'jsHandler://submitError?json=' + localStorage[surveyID];
                }
            } else if (platform === 'Android') {
                // android
                if ($scope.testNativeAndroidAppUA()) {
                    //webview
                    // call handler
                    jsHandler.submitError(localStorage.surveyID);
                } else {
                    //browser
                    // do nothing
                }
            } else {
                // some other browser
                // do nothing
            }
        };

        $scope.closeApp = function() {
            // window.close();
            var platform = $scope.getMobileOperatingSystem();
            console.log(platform);
            if (platform === 'iOS') {
                // ios
                var standalone = window.navigator.standalone;
                var userAgent = window.navigator.userAgent.toLowerCase();
                var safari = /safari/.test(userAgent);
                if (!standalone && safari) {
                    //browser
                    $scope.changePage('/home');
                } else if (userAgent.indexOf("painreport") > -1) {
                    // custom webview
                    window.location = 'jsHandler://killApp';
                } else if (!standalone && !safari) {
                    //uiwebview
                    window.location = 'jsHandler://killApp';
                }
            } else if (platform === 'Android') {
                // android
                if ($scope.testNativeAndroidAppUA()) {
                    //webview
                    jsHandler.killApp();
                } else {
                    //browser
                    $scope.changePage('/home');
                }
            } else {
                // some other browser
                $scope.changePage('/home');
            }
        };

        $scope.testNativeAndroidAppUA = function() {
            return /painreport/.test(navigator.userAgent);
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

        $scope.populateDebugInformation = function() {
            $("#homeBtn").hide();
            $("#troubleshoot").hide();
            $scope.debugInformation += "Patient Pin: - " + localStorage.surveyAppPin + "\n";
            $scope.debugInformation += "Activity Instance Id: - " + activeData.getActivityInstanceID() + "\n";
            $scope.debugInformation += "User Agent: - " + $scope.getMobileOperatingSystem() + "\n";
        };

        $scope.populateDebugInformation();

    }]);

})();
