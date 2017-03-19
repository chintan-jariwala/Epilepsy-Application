(function() {

    'use strict';
    //Load controller
    angular.module('surveyApp').controller('surveyController', ['$scope', '$timeout', '$location', 'activeData', '$http', function($scope, $timeout, $location, activeData, $http) {

        $scope.title = "Survey";

        $scope.questions = [];
        $scope.questionCounter = 0;
        $scope.answers = [];
        $scope.selectedAnswer = '';
        $scope.bodyPainAnswer = {
            location: "",
            intensity: ""
        };
        $scope.generalizedPainAnswer = {
            intensity: ""
        };
        $scope.surveyID = '';
        $scope.answeredCounter = 0;
        /*jshint -W030 */
        $scope.dropDownAnswer = [];
        $scope.noPainRect;
        $scope.noPainCheck = false;
        $scope.noPainCheckGP = false;
        $scope.isAnswered = false;
        $scope.additionalQues = [];
        $scope.isadditionalpushed = false;
        $scope.checkboxesValue = [];
        $scope.customSymptom = "";
        $scope.customMedication = "";
        $scope.otherAnswerID = "00";
        $scope.selectedAnswerFromDropdown = {};
        $scope.debugInformation = "Testing";

        $scope.$watch('selectedAnswer', function(value, oldValue) {

            var currentQuesID = "";
            var answerID = "";
            var activityBlockId = "";
            var questionType = "";
            if ($scope.questions[$scope.questionCounter].questionType === "multiChoiceSingleAnswer") {
                if (value !== '' && value !== undefined) {
                    $scope.isAnswered = true;
                }
                if ($scope.isAnswered) {
                    currentQuesID = $scope.questions[$scope.questionCounter].quesID;
                    answerID = value;
                    console.log(answerID);
                    activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
                    questionType = $scope.questions[$scope.questionCounter].questionType;
                    console.log(activityBlockId);
                    $scope.saveAnswerToLocalStorage(currentQuesID, answerID, activityBlockId, questionType);
                    $scope.nextDisabled = false;
                }
            }
        });

        $scope.saveDropdownState = function() {
            $scope.nextDisabled = true;
            $scope.isAnswered = false;
            var currentQuesID = "";
            var answerID;
            var questionType = "";
            var activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
            currentQuesID = $scope.questions[$scope.questionCounter].quesID;
            if ($scope.questions[$scope.questionCounter].questionType === "MCMADW") {
                answerID = $scope.checkFromLocalStorage(currentQuesID);
                questionType = $scope.questions[$scope.questionCounter].questionType;
                if ($scope.selectedAnswerFromDropdown !== undefined) {
                    answerID = JSON.stringify($scope.selectedAnswerFromDropdown);
                    $scope.saveAnswerToLocalStorage(currentQuesID, answerID, activityBlockId, questionType);
                    console.log(answerID);
                    if (Object.keys($scope.selectedAnswerFromDropdown).length === $scope.questions[$scope.questionCounter].answerOptions.length - 1 || Object.keys($scope.selectedAnswerFromDropdown).length === $scope.questions[$scope.questionCounter].answerOptions.length) {
                        $scope.isAnswered = true;
                        console.log("Not disabled");
                    }
                }
                if ($scope.isAnswered) {
                    $scope.saveAnswerToLocalStorage(currentQuesID, answerID, activityBlockId, questionType);
                    $scope.nextDisabled = false;
                    console.log("Not disabled");
                    console.log("Not disabled");
                }
            }
        };

        $scope.checkLength = function() {
            if ($scope.customSymptom.length > 0) {
                $scope.nextDisabled = false;
                console.log("Not disabled");
                $scope.checkboxAnswer_new.splice($scope.checkboxAnswer_new.length - 1, 1, $scope.customSymptom);
                console.log($scope.checkboxAnswer_new);
                var currentQuesID = $scope.questions[$scope.questionCounter].quesID;
                var answerID = $scope.checkboxAnswer_new.toString();
                var activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
                var questionType = $scope.questions[$scope.questionCounter].questionType;
                $scope.saveAnswerToLocalStorage(currentQuesID, answerID, activityBlockId, questionType);
            } else {
                $scope.nextDisabled = true;
                console.log("disabled");

            }
        };

        $scope.checkCustomMedicationLength = function() {
            $scope.selectedAnswerFromDropdown[$scope.otherAnswerID] = $scope.customMedication;
            console.log($scope.selectedAnswerFromDropdown);
            var currentQuesID = $scope.questions[$scope.questionCounter].quesID;
            var activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
            var answerID = JSON.stringify($scope.selectedAnswerFromDropdown);
            var  questionType = $scope.questions[$scope.questionCounter].questionType;
            $scope.saveAnswerToLocalStorage(currentQuesID, answerID, activityBlockId, questionType);


        };

        $scope.$watchCollection('checkboxesValue', function(value, oldValue) {
            var currentQuesID = "";
            var answerID = "";
            var activityBlockId = "";
            $scope.checkboxAnswer_new = [];

            for (var q = 0; q < $scope.checkboxesValue.length; q++) {
                if ($scope.checkboxesValue[q] === true) {
                    // checkboxAnswer = checkboxAnswer + " "+$scope.questions[$scope.questionCounter].answerOptions[q].answerID;
                    $scope.checkboxAnswer_new.push($scope.questions[$scope.questionCounter].answerOptions[q].answerID);
                    // checkboxAnswer = checkboxAnswer.trim();
                    $scope.isAnswered = true;
                    $scope.nextDisabled = false;
                    console.log("Not disabled");
                    console.log($scope.questions[$scope.questionCounter].answerOptions[q].answerText);
                    if($scope.questions[$scope.questionCounter].answerOptions[q].answerText === "None of these"){
                        x = 0;
                        for (var x = 0; x < $scope.checkboxesValue.length-1; x++) {
                            $scope.checkboxesValue[x] = false;
                        }
                        $scope.checkboxesValue[$scope.checkboxesValue.length-1] = true;
                        console.log("None of these...");
                        console.log($scope.checkboxesValue);
                    }
                    if ($scope.questions[$scope.questionCounter].answerOptions[q].answerText === "Other") {
                        $("#optionalTextarea").show();
                        $scope.checkboxAnswer_new.push($scope.customSymptom);
                        $scope.nextDisabled = true;
                        console.log("disabled");
                        $scope.isAnswered = false;
                        if ($scope.customSymptom.length > 0) {
                            $scope.nextDisabled = false;
                            console.log("Not disabled");
                            $scope.isAnswered = true;
                            //$scope.checkboxAnswer_new.splice($scope.checkboxAnswer_new.length-1,1,$scope.customSymptom);
                        }
                    } else {
                        $("#optionalTextarea").hide();
                    }
                }
            }
            if ($scope.isAnswered) {
                if ($scope.questions[$scope.questionCounter].questionType === "MCMA") {
                    console.log($scope.checkboxAnswer_new);
                    console.log($scope.checkboxAnswer_new.length);
                    if ($scope.checkboxAnswer_new.length === 0) {
                        console.log("Next should be disabled");
                        $("#optionalTextarea").hide();
                        $scope.nextDisabled = true;
                        console.log("disabled");
                    } else {
                        currentQuesID = $scope.questions[$scope.questionCounter].quesID;
                        answerID = $scope.checkboxAnswer_new.toString();
                        activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
                        var  questionType = $scope.questions[$scope.questionCounter].questionType;
                        $scope.saveAnswerToLocalStorage(currentQuesID, answerID, activityBlockId, questionType);
                        $scope.nextDisabled = false;
                        console.log("Not disabled");
                    }
                }

            }
            console.log(answerID);
        });


        $scope.$watch('partClicked', function() {
            $scope.bodyPainAnswer.location = $scope.partClicked;
            $scope.bodyPainAnswerWatcher();
            console.log("Calling body pain answer watcher");
        });

        $scope.$watch('painIntensityValue', function() {
            $scope.bodyPainAnswer.intensity = $scope.painIntensityValue;
            $scope.bodyPainAnswerWatcher();
            console.log("Calling body pain answer watcher");
        });
        $scope.$watch('painIntensityValueGP', function() {
            $scope.generalizedPainAnswer.intensity = $scope.painIntensityValueGP;
        });

        $scope.bodyPainAnswerWatcher = function() {
            if ($scope.questions[$scope.questionCounter].questionType === "bodypain") {
                console.log($scope.bodyPainAnswer);
                console.log($scope.noPainCheck);
                console.log($scope.painIntensityValue);
                console.log($scope.lastSelectedPart);
                if ((($scope.lastSelectedPart !== "No Part Selected" && !angular.isUndefined($scope.lastSelectedPart) && $scope.lastSelectedPart !== "") && $scope.painIntensityValue !== 0) || ($scope.noPainCheck && $scope.painIntensityValue === 0)) {
                    $scope.isAnswered = true;
                } else {
                    $scope.isAnswered = false;
                }
                if ($scope.isAnswered) {

                    $timeout(function() {
                        $scope.$apply(function() {
                            $scope.nextDisabled = false;
                        });
                    }, 10);
                    var quesID = $scope.questions[$scope.questionCounter].quesID;
                    var clickedPart = "";
                    console.log($scope.partClicked);
                    console.log(document.getElementById('bodyPartText').textContent);
                    var clickedPartName = document.getElementById('bodyPartText').textContent;
                    // $scope.partNames=["Front Head Area", "Chest Area", "Right Hand Front", "Abdomen Area", "Left Hand Front", "Right Leg Front", "Left Leg Front", "Head Back Area", "Back", "Left Leg Back", "Right Leg Back", "Right Hand Back", "Left Hand Back", "Lower Back"];
                    // $scope.partNames=["Head Front", "Chest", "Abdomen", "Right Hand Front", "Left Hand Front", "Right Leg Front", "Left Leg Front", "Head Back", "Upper Back", "Lower Back", "Right Hand Back", "Left Hand Back", "Left Leg Back", "Right Leg Back"];
                    switch (clickedPartName) {
                        case "Head Front":
                            clickedPart = "front head";
                            break;
                        case "Chest":
                            clickedPart = "front chest";
                            break;
                        case "Right Hand Front":
                            clickedPart = "front right arm";
                            break;
                        case "Abdomen":
                            clickedPart = "front abdominal";
                            break;
                        case "Left Hand Front":
                            clickedPart = "front left arm";
                            break;
                        case "Right Leg Front":
                            clickedPart = "front right leg";
                            break;
                        case "Left Leg Front":
                            clickedPart = "front left leg";
                            break;
                        case "Head Back":
                            clickedPart = "back head";
                            break;
                        case "Upper Back":
                            clickedPart = "back";
                            break;
                        case "Left Leg Back":
                            clickedPart = "back left leg";
                            break;
                        case "Right Leg Back":
                            clickedPart = "back right leg";
                            break;
                        case "Right Hand Back":
                            clickedPart = "back right arm";
                            break;
                        case "Left Hand Back":
                            clickedPart = "back left arm";
                            break;
                        case "Lower Back":
                            clickedPart = "lower back";
                            break;
                    }
                    console.log(clickedPart);
                    if ($scope.bodyPainAnswer.location === "No Pain" || $scope.noPainCheck) {
                        clickedPart = "No Pain";
                    }
                    var answer = '{"location":"' + clickedPart + '","intensity":"' + $scope.painIntensityValue + '"}';
                    var activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
                    console.log(activityBlockId);
                    var questionType = $scope.questions[$scope.questionCounter].questionType;
                    $scope.saveAnswerToLocalStorage(quesID, answer, activityBlockId, questionType);
                } else {

                    $scope.nextDisabled = true;
                    console.log("disabled");
                }
            }
        };

        $scope.saveAnswerToLocalStorage = function(quesID, ansID, activityBlockId, questionType) {
            var surveyID = $scope.surveyID;
            console.log(quesID);
            localStorage.setItem(surveyID + ":" + quesID + ":" + activityBlockId + ":" + questionType, ansID);
            var currentTime = new Date().getTime();
            var logEvent = [];
            //   var answerID = JSON.parse(ansID);
            var answerID = ansID;
            if (localStorage.getItem("uiLogger") !== null) {
                logEvent = JSON.parse(localStorage.getItem("uiLogger"));
            }

            logEvent.push({
                "pin": localStorage.surveyAppPin,
                "eventName": "answer_option_selected",
                "metaData": {
                    "sid": $scope.surveyID,
                    "qid": quesID,
                    "aid": answerID
                },
                "eventTime": currentTime
            });

            var jsonString = JSON.stringify(logEvent);
            localStorage.setItem("uiLogger", jsonString);
        };

        $scope.checkFromLocalStorage = function(quesID) {
            var surveyID = $scope.surveyID;
            var activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
            var questionType = $scope.questions[$scope.questionCounter].questionType;
            console.log(localStorage.getItem(surveyID + ":" + quesID + ":" + activityBlockId + ":" + questionType));

            if (localStorage.getItem(surveyID + ":" + quesID + ":" + activityBlockId + ":" + questionType) !== null)
                return localStorage.getItem(surveyID + ":" + quesID + ":" + activityBlockId + ":" + questionType);
            else
                return "not set";
        };

        $scope.controllerInit = function() {
            $scope.prevDisabled = true;
            $scope.nextDisabled = true;
            console.log("disabled");
            $scope.iterator = 0;
            if (activeData.getSurveyName() === "") {
                console.log("Survey Name is blank");
                activeData.setSurveyID(localStorage.getItem("reloadBackupSurveyID"));
                activeData.setSurveyName(localStorage.getItem("reloadBackupSurveyName"));
                activeData.setSurveyQuestions(JSON.parse(localStorage.getItem("reloadBackupSurveyQuestions")));
            }

            $scope.title = activeData.getSurveyName();
            $scope.questions = activeData.getSurveyQuestions();
            $scope.addAdaptiveQuestions();
            $scope.surveyID = activeData.getSurveyID();
            console.log($scope.questions);

            if ($scope.questionCounter === 0) {
                $("#prev").hide();
                $scope.prevDisabled = true;
            }

            $scope.populateQuestions();
            //set up event listeners for SVG
            for (var i = $scope.sliderElements.length - 1; i >= 0; i--) {
                document.getElementById($scope.sliderElements[i]).addEventListener("click", $scope.onSliderElementClick);
                // console.log("Added a listener");
            }
            for (i = $scope.sliderElementsGP.length - 1; i >= 0; i--) {
                document.getElementById($scope.sliderElementsGP[i]).addEventListener("click", $scope.onSliderElementClickGP);
                //console.log("Added a listener");
            }
            for (i = $scope.parts.length - 1; i >= 0; i--) {
                document.getElementById($scope.parts[i]).addEventListener("click", $scope.onBodyPartClick);
            }
            document.getElementById('noPainRect').addEventListener("click", $scope.noPainRectClick);
            document.getElementById('noPainRect1').addEventListener("click", $scope.noPainRectClickGP);

        };



        $scope.onSliderElementClick = function(event) {
            console.log("Event triggered");
            $scope.isAnswered = false;
            $timeout(function() {
                $scope.$apply(function() {
                    $scope.nextDisabled = true;
                    console.log("disabled");
                });
            }, 10);
            //console.log("Event triggered");
            var id = event.target.id;
            var num = parseInt(id.substr(id.length - 1));
            $scope.painIntensityValue = num + 1;
            for (var i = 0; i < $scope.sliderElements.length; i++) {
                if (i <= num) {
                    document.getElementById($scope.sliderElements[i]).style.fill = "#000000";
                } else {
                    document.getElementById($scope.sliderElements[i]).style.fill = "none";
                }
            }
            $scope.bodyPainAnswer.intensity = $scope.painIntensityValue;
            $scope.bodyPainAnswerWatcher();
            console.log("Calling body pain answer watcher");
            if ($scope.noPainCheck) {
                $scope.noPainCheck = false;
                document.getElementById('noPainRect').style.fill = "none";
                document.getElementById('bodyPartText').textContent = "";
            }
        };

        $scope.onSliderElementClickGP = function(event) {
            console.log("Event triggered");
            $scope.isAnswered = true;
            $timeout(function() {
                $scope.$apply(function() {
                    $scope.nextDisabled = false;
                    console.log("Not disabled");
                });
            }, 10);
            //console.log("Event triggered");
            var id = event.target.id;
            var num = parseInt(id.substr(id.length - 1));
            $scope.painIntensityValueGP = num + 1;
            for (var i = 0; i < $scope.sliderElements.length; i++) {
                if (i <= num) {
                    document.getElementById($scope.sliderElementsGP[i]).style.fill = "#000000";
                } else {
                    document.getElementById($scope.sliderElementsGP[i]).style.fill = "none";
                }
            }
            $scope.generalizedPainAnswer.intensity = $scope.painIntensityValueGP;
            console.log($scope.generalizedPainAnswer);
            var answer = '{"intensity":' + $scope.painIntensityValueGP + '}';
            var quesID = $scope.questions[$scope.questionCounter].quesID;
            var activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
            var  questionType = $scope.questions[$scope.questionCounter].questionType;
            $scope.saveAnswerToLocalStorage(quesID, answer, activityBlockId, questionType);
            if ($scope.noPainCheckGP) {
                $scope.noPainCheckGP = false;
                document.getElementById('noPainRect1').style.fill = "none";
            }
        };


        $scope.noPainRectClick = function() {
            $scope.isAnswered = false;
            console.log("No pain clicked");
            $timeout(function() {
                $scope.$apply(function() {
                    $scope.nextDisabled = true;
                    console.log("disabled");
                });
            }, 10);
            if (!$scope.noPainCheck) {
                this.style.fill = "#000000";
                $scope.noPainCheck = true;
                document.getElementById('bodyPartText').textContent = "No Pain";
                $scope.painIntensityValue = 0;
                $scope.bodyPainAnswer.location = "No Pain";
                $scope.bodyPainAnswer.intensity = $scope.painIntensityValue;
                $scope.bodyPainAnswerWatcher();
                console.log("Calling body pain answer watcher");
                if (document.getElementById($scope.lastSelectedPart) !== null) {
                    console.log(document.getElementById($scope.lastSelectedPart));
                    document.getElementById($scope.lastSelectedPart).style.fill = "#ecf0f1";
                    if (document.getElementById($scope.lastSelectedPart).id == "headBack") {
                        document.getElementById("headBack").style.fill = '#6F6F6F';
                    }
                }
                $scope.lastSelectedPart = '';
            } else {
                this.style.fill = "none";
                $scope.noPainCheck = false;
                document.getElementById('bodyPartText').textContent = "";
                $scope.painIntensityValue = 0;
                $scope.bodyPainAnswer.intensity = $scope.painIntensityValue;
                $scope.bodyPainAnswerWatcher();
                console.log("Calling body pain answer watcher");
            }
            for (var i = 0; i < $scope.sliderElements.length; i++) {
                document.getElementById($scope.sliderElements[i]).style.fill = "none";
            }
        };

        $scope.noPainRectClickGP = function() {
            $scope.isAnswered = true;
            console.log("No pain clicked for GP");
            $timeout(function() {
                $scope.$apply(function() {
                    $scope.nextDisabled = false;
                    console.log("not disabled");
                });
            }, 10);
            if (!$scope.noPainCheckGP) {
                this.style.fill = "#000000";
                $scope.noPainCheckGP = true;
                $scope.nextDisabled = false;
                console.log("Not disabled");
                $scope.isAnswered = true;
                //document.getElementById('bodyPartText').textContent="No Pain";
                $scope.painIntensityValueGP = 0;
                $scope.generalizedPainAnswer.intensity = $scope.painIntensityValueGP;
                console.log($scope.generalizedPainAnswer);
                //$scope.bodyPainAnswerWatcher();
            }
            var answer = '{"intensity":' + $scope.painIntensityValueGP + '}';
            var activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
            var quesID = $scope.questions[$scope.questionCounter].quesID;
            var  questionType = $scope.questions[$scope.questionCounter].questionType;
            $scope.saveAnswerToLocalStorage(quesID, answer, activityBlockId, questionType);
            for (var i = 0; i < $scope.sliderElementsGP.length; i++) {
                document.getElementById($scope.sliderElementsGP[i]).style.fill = "none";
            }
        };

        $scope.populateQuestions = function() {

            if (($scope.questions[$scope.questionCounter] === 0) || ($scope.questions[$scope.questionCounter].answerOptions === 0)) {

                $scope.changePage('/error/' + $scope.questionCounter);
            }

            console.log($scope.questions[$scope.questionCounter]);
            var questionText = $scope.questions[$scope.questionCounter].questionText;
            $scope.question = questionText;
            console.log("Question Text - " + questionText);
            console.log("Question Type - " + $scope.questions[$scope.questionCounter].questionType);

            if ($scope.questions[$scope.questionCounter].questionType === "multiChoiceSingleAnswer") {
                $("#optionalTextarea").hide();
                $("#optionsTwo").hide();
                $("#optionsThree").hide();
                $("#optionsOne").show();
                $("#optionsFour").hide();
                $("#optionsFive").hide();
            } else if ($scope.questions[$scope.questionCounter].questionType === "MCMA") {
                $scope.checkboxesValue = [];
                for (var t = 0; t < $scope.questions[$scope.questionCounter].answerOptions.length; t++) {
                    $scope.checkboxesValue.push(false);
                }
                $("#optionalTextarea").hide();
                $("#optionsOne").hide();
                $("#optionsTwo").hide();
                $("#optionsThree").show();
                $("#optionsFour").hide();
                $("#optionsFive").hide();
            } else if ($scope.questions[$scope.questionCounter].questionType === "bodypain") {
                console.log("It is a body Pain");
                $("#optionalTextarea").hide();
                $("#optionsOne").hide();
                $("#optionsThree").hide();
                $("#optionsTwo").show();
                $("#optionsFour").hide();
                $("#optionsFive").hide();
                $scope.bodyPainAnswerWatcher();
                console.log("Calling body pain answer watcher");
                $scope.checkForBodyPainAnswerStored();
            } else if ($scope.questions[$scope.questionCounter].questionType === "MCMADW") {
                console.log("It is a dropdown text");

                $("#optionalTextarea").hide();
                $("#optionsOne").hide();
                $("#optionsThree").hide();
                $("#optionsTwo").hide();
                $("#optionsFour").show();
                $("#optionsFive").hide();
            } else if ($scope.questions[$scope.questionCounter].questionType === "generalizedpain") {
                console.log("It is a generalizedpain question");

                $("#optionalTextarea").hide();
                $("#optionsOne").hide();
                $("#optionsThree").hide();
                $("#optionsTwo").hide();
                $("#optionsFour").hide();
                $("#optionsFive").show();
                $scope.checkForGeneralizedPainAnswerStored();

            }
            $scope.populateAnswers();
        };

        $scope.changePage = function(path) {
            $location.path(path);
        };

        $scope.checkForBodyPainAnswerStored = function() {
            var activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
            if (localStorage.getItem($scope.surveyID + ":" + $scope.questions[$scope.questionCounter].quesID + ":" + activityBlockId + ":" + $scope.questions[$scope.questionCounter].questionType)) {
                console.log("Test - " + localStorage.getItem($scope.surveyID + ":" + $scope.questions[$scope.questionCounter].quesID + ":" + activityBlockId + ":" + $scope.questions[$scope.questionCounter].questionType));

                var value = JSON.parse(localStorage.getItem($scope.surveyID + ":" + $scope.questions[$scope.questionCounter].quesID + ":" + activityBlockId + ":" + $scope.questions[$scope.questionCounter].questionType));
                console.log(value);
                console.log(value.location);
                console.log(value.intensity);
                if (value.location === "No Pain") {
                    document.getElementById('noPainRect').style.fill = "#000000";
                    $scope.noPainCheck = true;
                    document.getElementById('bodyPartText').textContent = "No Pain";
                    $scope.painIntensityValue = 0;
                    $scope.bodyPainAnswer.location = "No Pain";
                    $scope.bodyPainAnswer.intensity = 0;
                    if (document.getElementById($scope.lastSelectedPart) !== null) {
                        document.getElementById($scope.lastSelectedPart).style.fill = "#ecf0f1";
                    }
                    $scope.lastSelectedPart = '';

                } else {
                    $scope.painIntensityValue = value.intensity;
                    $scope.bodyPainAnswer.intensity = $scope.painIntensityValue;
                    var num = $scope.painIntensityValue - 1;
                    for (var i = 0; i < $scope.sliderElements.length; i++) {
                        if (i <= num) {
                            document.getElementById($scope.sliderElements[i]).style.fill = "#000000";
                        } else {
                            document.getElementById($scope.sliderElements[i]).style.fill = "none";
                        }
                    }

                    console.log(value.location);
                    switch (value.location) {
                        case "front head":
                            document.getElementById("headFront").style.fill = '#000000';
                            $scope.lastSelectedPart = "headFront";
                            document.getElementById('bodyPartText').textContent = "Head Front";
                            break;
                        case "front chest":
                            document.getElementById("chest").style.fill = '#000000';
                            $scope.lastSelectedPart = "chest";
                            document.getElementById('bodyPartText').textContent = "Chest";
                            break;
                        case "front right arm":
                            document.getElementById("rightHandFront").style.fill = '#000000';
                            $scope.lastSelectedPart = "rightHandFront";
                            document.getElementById('bodyPartText').textContent = "Right Hand Front";
                            break;
                        case "front abdominal":
                            document.getElementById("abdomen").style.fill = '#000000';
                            $scope.lastSelectedPart = "abdomen";
                            document.getElementById('bodyPartText').textContent = "Abdomen";
                            break;
                        case "front left arm":
                            document.getElementById("leftHandFront").style.fill = '#000000';
                            $scope.lastSelectedPart = "leftHandFront";
                            document.getElementById('bodyPartText').textContent = "Left Hand Front";
                            break;
                        case "front right leg":
                            document.getElementById("rightLegFront").style.fill = '#000000';
                            $scope.lastSelectedPart = "rightLegFront";
                            document.getElementById('bodyPartText').textContent = "Right Leg Front";
                            break;
                        case "front left leg":
                            document.getElementById("leftLegFront").style.fill = '#000000';
                            $scope.lastSelectedPart = "leftLegFront";
                            document.getElementById('bodyPartText').textContent = "Left Leg Front";
                            break;
                        case "back head":
                            document.getElementById("headBack").style.fill = '#000000';
                            $scope.lastSelectedPart = "headBack";
                            document.getElementById('bodyPartText').textContent = "Head Back";
                            break;
                        case "back":
                            document.getElementById("upperBack").style.fill = '#000000';
                            $scope.lastSelectedPart = "upperBack";
                            document.getElementById('bodyPartText').textContent = "Upper Back";
                            break;
                        case "back left leg":
                            document.getElementById("leftLegBack").style.fill = '#000000';
                            $scope.lastSelectedPart = "leftLegBack";
                            document.getElementById('bodyPartText').textContent = "Left Leg Back";
                            break;
                        case "back right leg":
                            document.getElementById("rightLegBack").style.fill = '#000000';
                            $scope.lastSelectedPart = "rightLegBack";
                            document.getElementById('bodyPartText').textContent = "Right Leg Back";
                            break;
                        case "back right arm":
                            document.getElementById("rightHandBack").style.fill = '#000000';
                            $scope.lastSelectedPart = "rightHandBack";
                            document.getElementById('bodyPartText').textContent = "Right Hand Back";
                            break;
                        case "back left arm":
                            document.getElementById("leftHandBack").style.fill = '#000000';
                            $scope.lastSelectedPart = "leftHandBack";
                            document.getElementById('bodyPartText').textContent = "Left Hand Back";
                            break;
                        case "lower back":
                            document.getElementById("lowerBack").style.fill = '#000000';
                            $scope.lastSelectedPart = "lowerBack";
                            document.getElementById('bodyPartText').textContent = "Lower Back";
                            break;
                    }
                }
                $scope.bodyPainAnswerWatcher();
                console.log("Calling body pain answer watcher");
            } else {
                $scope.isAnswered = false;
                $scope.nextDisabled = true;
            }
        };

        $scope.checkForGeneralizedPainAnswerStored = function() {
            var activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
            var quesID = $scope.questions[$scope.questionCounter].quesID;
            var surveyID = $scope.surveyID;
            if (localStorage.getItem(surveyID + ":" + quesID + ":" + activityBlockId + ":" + $scope.questions[$scope.questionCounter].questionType)) {
                console.log("GP Test - " + localStorage.getItem(surveyID + ":" + quesID + ":" + activityBlockId + ":" + $scope.questions[$scope.questionCounter].questionType));

                var value = JSON.parse(localStorage.getItem(surveyID + ":" + quesID + ":" + activityBlockId + ":" + $scope.questions[$scope.questionCounter].questionType));
                console.log(value);
                console.log(value.intensity);
                if (value.intensity === 0) {
                    console.log("Have to fill the no pain rect");
                    document.getElementById('noPainRect1').style.fill = "#000000";
                    $scope.noPainCheckGP = true;
                    $scope.painIntensityValueGP = 0;
                    $scope.generalizedPainAnswer.intensity = 0;

                } else {
                    $scope.painIntensityValueGP = value.intensity;
                    $scope.generalizedPainAnswer.intensity = $scope.painIntensityValueGP;
                    var num = $scope.painIntensityValueGP - 1;
                    for (var i = 0; i < $scope.sliderElements.length; i++) {
                        if (i <= num) {
                            document.getElementById($scope.sliderElementsGP[i]).style.fill = "#000000";
                        } else {
                            document.getElementById($scope.sliderElementsGP[i]).style.fill = "none";
                        }
                    }
                }

                $scope.isAnswered = true;
                $scope.nextDisabled = false;
                console.log("Next disabled is false");
            } else {
                $scope.isAnswered = false;
                $scope.nextDisabled = true;
            }
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
            $scope.debugInformation = "";
            var shortForm = "";
            var currentQuesID = $scope.questions[$scope.questionCounter].quesID;
            var pin = localStorage.surveyAppPin;
            var activityBlockId = $scope.questions[$scope.questionCounter].activityBlockId;
            var activityInstaceId = activeData.getActivityInstanceID();
            if ($scope.questions[$scope.questionCounter].shortForm !== undefined) {
                shortForm = $scope.questions[$scope.questionCounter].shortForm;
            }

            console.log("Are aa rahyu" + activeData.getActivityInstanceID());
            $scope.debugInformation = "Question Id: - " + currentQuesID + "\n" +
                " Patient Pin: - " + pin + "\n" +
                " ActivityBlock Id: - " + activityBlockId + "\n" +
                " Activity Instance Id: - " + activityInstaceId + "\n" +
                " ShortForm: - " + shortForm + "\n" +
                "User Agent: - " + $scope.getMobileOperatingSystem() + "\n";
        };

        $scope.populateAnswers = function() {
            $scope.answers = [];
            $scope.checkboxesValue = [];
            var selectedAnswer = $scope.checkFromLocalStorage($scope.questions[$scope.questionCounter].quesID);
            var temporaryVar = 0;
            angular.forEach($scope.questions[$scope.questionCounter].answerOptions, function(value, key) {
                temporaryVar++;
                console.log("The answer that was Selected - " + selectedAnswer);
                console.log(value);

                if ($scope.questions[$scope.questionCounter].questionType === "MCMA") {
                    console.log("found The question type");
                    var tempSelectedAnswer = selectedAnswer.split(",");
                    if (tempSelectedAnswer.indexOf(value.answerID.toString()) > -1) {
                        for (var x = 0; x < tempSelectedAnswer.length; x++) {
                            console.log("it is inside");
                            if (parseInt(value.answerID) === parseInt(tempSelectedAnswer[x])) {
                                $scope.checkboxesValue.push(true);
                                console.log("selected Answer - " + value.answerText);
                                if (value.answerText === "Other") {
                                    $scope.customSymptom = tempSelectedAnswer[x + 1];
                                    console.log(typeof(tempSelectedAnswer[x + 1]));
                                }
                            }
                        }
                    } else {
                        $scope.checkboxesValue.push(false);
                    }
                }
                if ($scope.questions[$scope.questionCounter].questionType === "MCMADW") {
                    var text = "";
                    console.log(value.answerID);
                    console.log(value.answerText);
                    if (value.answerText === "Other") {
                        console.log("OTher encountered");
                        $scope.otherAnswerID = value.answerID;
                        text = $scope.otherAnswerID;
                    }
                    if (selectedAnswer !== 'not set') {
                        console.log("Inside " + selectedAnswer);
                        $scope.selectedAnswerFromDropdown = JSON.parse(selectedAnswer);
                        if ($scope.selectedAnswerFromDropdown[$scope.otherAnswerID] !== undefined)
                            $scope.customMedication = $scope.selectedAnswerFromDropdown[$scope.otherAnswerID];
                    }

                }
                $scope.answers.push(value);
                console.log($scope.checkboxesValue);

                if (selectedAnswer !== 'not set') {
                    // console.log("The answer that was Selected - "+selectedAnswer);
                    if (parseInt(selectedAnswer) === parseInt(value.answerID)) {
                        // console.log("here " + value.answerID);
                        // $scope.selectedAnswer = JSON.stringify(value);
                        // $scope.selectedAnswer = value.answerID;
                        $timeout(function() {
                            $scope.$apply(function() {
                                $scope.selectedAnswer = value.answerID;
                            });
                        }, 10);
                        // console.log("here"  + $scope.selectedAnswer);
                        // var id = $scope.selectedAnswer;
                        // console.log(jQuery("#"+id));
                        // $("#"+value.answerID).prop('checked',true);
                    }
                }
            });
            console.log($scope.answers);
            console.log(temporaryVar);
            $scope.populateDebugInformation();
        };

        $scope.goPrev = function() {
            $scope.questionCounter--;
            if ($scope.questionCounter === 0) {
                $("#prev").hide();
                $scope.prevDisabled = true;
                $scope.nextDisabled = true;
                console.log("disabled");
            }

            var ques = $scope.questionCounter;
            var currentTime = new Date().getTime();
            var logEvent = [];
            if (localStorage.getItem("uiLogger") !== null) {
                logEvent = JSON.parse(localStorage.getItem("uiLogger"));
                //       console.log("Parsed array is : ", logEvent);
            }
            logEvent.push({
                "pin": localStorage.surveyAppPin,
                "eventName": "previous_btn_click",
                "metaData": {
                    "sid": $scope.surveyID,
                    "qid": $scope.questions[ques].quesID
                },
                "startTime": currentTime
            });
            //console.log("JSON ARRAY : ", logEvent);
            var jsonString = JSON.stringify(logEvent);
            //    console.log("JSON STRING : ", jsonString);
            localStorage.setItem("uiLogger", jsonString);

            if ($scope.questions[$scope.questionCounter].questionType === "MCMADW") {
                console.log("Foudn MCMADW");
                $scope.nextDisabled = true;
                $scope.saveDropdownState();
            } else {
                $scope.nextDisabled = true;
            }
            $scope.populateQuestions();
            $scope.isAnswered = false;
        };

        $scope.adaptiveQuestions = function() {
            var temporary = $scope.questionCounter;
            console.log('Question number : - ' + $scope.questionCounter);
            console.log('Selected Answer : - ' + $scope.selectedAnswer);

            if ($scope.questions[$scope.questionCounter].adaptive === true) {
                console.log("Adaptive aayo la");
                for (var x = 0; x < $scope.questions[$scope.questionCounter].answerOptions.length; x++) {
                    if ($scope.selectedAnswer === $scope.questions[$scope.questionCounter].answerOptions[x].answerID) {
                        temporary++;
                        console.log("Next question will be - " + $scope.questions[temporary].quesID);
                        console.log("Next question Should be - " + $scope.questions[$scope.questionCounter].answerOptions[x].nextQuestion);

                        if ($scope.questions[temporary].quesID == $scope.questions[$scope.questionCounter].answerOptions[x].nextQuestion) {
                            console.log("WIll not add adaptive");
                        } else {
                            if ($scope.questions[temporary].quesID > $scope.questions[$scope.questionCounter].answerOptions[x].nextQuestion) {
                                var nextques = $scope.questions[temporary].questionText;
                                for (var i = 0; i < $scope.additionalQues.length; i++) {

                                    console.log("length of additional questions -" + $scope.additionalQues.length);
                                    console.log($scope.additionalQues[i]);

                                    if ($scope.additionalQues[i].quesID == $scope.questions[$scope.questionCounter].answerOptions[x].nextQuestion) {
                                        if (((nextques.indexOf('The reason for missing') > -1) === false)) {
                                            $scope.questions.splice(temporary, 0, $scope.additionalQues[i]);
                                            $scope.isadditionalpushed = true;
                                        }
                                    }
                                }
                            } else {
                                console.log("adaptive needs to be removeddd");
                                $scope.questions.splice(temporary, 1);
                            }
                        }
                    }
                }
            }

        };

        $scope.addAdaptiveQuestions = function() {
            var tempVariable = 0;
            $scope.activitySequence = activeData.getActivitySequence();

            for (var i = 0; i < $scope.activitySequence.length; i++) {
                for (var j = 0; j < $scope.activitySequence[i].questions.length; j++) {
                    if ($scope.activitySequence[i].activityBlockId == "CAT") {
                        if ($scope.activitySequence[i].questions[j].isMandatory !== true) {
                            $scope.additionalQues[tempVariable] = {
                                "questionText": $scope.activitySequence[i].questions[j].question,
                                "quesID": $scope.activitySequence[i].questions[j].quesID,
                                "answerOptions": $scope.activitySequence[i].questions[j].answerOptions,
                                "questionType": $scope.activitySequence[i].questions[j].questionType,
                                "activityBlockId": $scope.activitySequence[i].activityBlockId
                            };
                            tempVariable++;
                        }
                    }

                }
            }
            console.log($scope.additionalQues);
        };

        $scope.goNext = function() {

            $scope.adaptiveQuestions();
            var ques = $scope.questionCounter;
            var currentTime = new Date().getTime();
            var logEvent = [];
            var jsonString = null;
            // console.log($scope.questions);
            if (localStorage.getItem("uiLogger") !== null) {
                logEvent = JSON.parse(localStorage.getItem("uiLogger"));
            }

            if ($scope.questionCounter === $scope.questions.length - 1) {
                logEvent.push({
                    "pin": localStorage.surveyAppPin,
                    "eventName": "next_btn_click",
                    "metaData": {
                        "sid": $scope.surveyID,
                        "qid": $scope.questions[ques].quesID
                    },
                    "eventTime": currentTime
                });
                jsonString = JSON.stringify(logEvent);
                localStorage.setItem("uiLogger", jsonString);

                $scope.changePage('/surveyEnd');
            } else {

                $scope.questionCounter++;
                if ($scope.questionCounter > 0) {

                    $scope.prevDisabled = false;
                }
                if ($scope.questionCounter === $scope.questions.length - 1) {

                    $scope.nextDisabled = true;
                    console.log("disabled");
                }

                if ($scope.selectedAnswer === '') {

                    $scope.nextDisabled = true;
                    console.log("disabled");
                }

                logEvent.push({
                    "pin": localStorage.surveyAppPin,
                    "eventName": "next_btn_click",
                    "metaData": {
                        "sid": $scope.surveyID,
                        "qid": $scope.questions[ques].quesID
                    },
                    "eventTime": currentTime
                });
                jsonString = JSON.stringify(logEvent);
                localStorage.setItem("uiLogger", jsonString);
                console.log($scope.questionCounter);
                $scope.populateQuestions();
                $scope.selectedAnswer = '';
                if ($scope.questions[$scope.questionCounter].questionType === "MCMADW") {
                    console.log("Foudn MCMADW");
                    $scope.nextDisabled = true;
                    $scope.saveDropdownState();
                } else {
                    $scope.nextDisabled = true;
                }

                console.log("disabled");

                if ($scope.questionCounter === 1) {
                    $("#prev").show();
                }
                $scope.isAnswered = false;
            }
        };



        $scope.changePage = function(path) {
            $location.path(path);
        };

        $scope.getAnswered = function() {
            var temp = $scope.questionCounter + 1;
            return temp;
        };

        $scope.getTotal = function() {
            return $scope.questions.length;
        };

        $scope.getPercentage = function() {
            return ($scope.getAnswered() / $scope.getTotal()) * 100;
        };

        //body part IDs
        $scope.parts = ["headFront", "chest", "abdomen", "rightHandFront", "leftHandFront", "rightLegFront", "leftLegFront", "headBack", "upperBack", "lowerBack", "rightHandBack", "leftHandBack", "leftLegBack", "rightLegBack"];

        //body part names that will be printed in the text area
        $scope.partNames = ["Head Front", "Chest", "Abdomen", "Right Hand Front", "Left Hand Front", "Right Leg Front", "Left Leg Front", "Head Back", "Upper Back", "Lower Back", "Right Hand Back", "Left Hand Back", "Left Leg Back", "Right Leg Back"];
        $scope.partClicked = "No Part Selected";
        $scope.painIntensityValue = 0;
        $scope.painIntensityValueGP = 0;

        /*jshint -W030 */
        $scope.position;
        /*jshint -W030 */
        $scope.lastSelectedPart;
        $scope.noPartSelected = true;

        $scope.onBodyPartClick = function() {
            $scope.isAnswered = false;
            $timeout(function() {
                $scope.$apply(function() {

                    $scope.nextDisabled = true;
                    console.log("disabled");
                });
            }, 10);
            console.log(this.id);
            console.log($scope.lastSelectedPart);
            if (this.id != $scope.lastSelectedPart) {
                if ($scope.painIntensityValue === 0) {
                    $scope.noPainCheck = false;
                    document.getElementById('bodyPartText').textContent = "";
                    document.getElementById('noPainRect').style.fill = "none";
                    $scope.partClicked = "No Part Selected";
                }
                var position = $scope.parts.indexOf(this.id);
                this.style.fill = '#000000';
                if (document.getElementById($scope.lastSelectedPart) !== null) {
                    if ($scope.lastSelectedPart === "headBack") {
                        document.getElementById($scope.lastSelectedPart).style.fill = '#6f6f6f';
                    } else {
                        document.getElementById($scope.lastSelectedPart).style.fill = '#ecf0f1';
                    }
                }
                $scope.lastSelectedPart = this.id;
                document.getElementById('bodyPartText').textContent = $scope.partNames[position];
                $scope.partClicked = $scope.lastSelectedPart;
                $scope.bodyPainAnswer.location = $scope.partClicked;
                $scope.bodyPainAnswerWatcher();
                console.log("Calling body pain answer watcher");
            } else {
                if (this.id === "headBack") {
                    this.style.fill = '#6f6f6f';
                } else {
                    this.style.fill = '#ecf0f1';
                }
                $scope.lastSelectedPart = null;
                document.getElementById('bodyPartText').textContent = "";
            }
        };

        $scope.sliderElements = ["sliderElem0", "sliderElem1", "sliderElem2", "sliderElem3", "sliderElem4", "sliderElem5", "sliderElem6", "sliderElem7", "sliderElem8", "sliderElem9"];
        $scope.sliderElementsGP = ["sliderElem10", "sliderElem11", "sliderElem12", "sliderElem13", "sliderElem14", "sliderElem15", "sliderElem16", "sliderElem17", "sliderElem18", "sliderElem19"];
        $scope.controllerInit();


        // window refresh event
        window.onbeforeunload = function(event) {
            if (window.location.hash === "#/survey") {
                localStorage.setItem("reloadBackupSurveyName", activeData.getSurveyName());
                localStorage.setItem("reloadBackupSurveyQuestions", JSON.stringify(activeData.getSurveyQuestions()));
                localStorage.setItem("reloadBackupSurveyID", activeData.getSurveyID());
                var message = 'If you leave this page you will have to start over again, are you sure you want to leave?';
                if (typeof event == 'undefined') {
                    event = window.event;
                }
                if (event) {
                    event.returnValue = message;
                }
                return message;
            } else
                return;
        };

        $scope.$on('$destroy', function() {
            delete window.onbeforeunload;
        });

    }]);

})();
