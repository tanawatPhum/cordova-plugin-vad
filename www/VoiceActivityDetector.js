// var exec = require('cordova/exec');

// exports.process = function(processId, params, title,success, error) {
//     console.log("Hello World");
//     exec(success, error, "OpenCVActivity", "process", [processId, params, title]);
// };

var VoiceActivity = function() {
	var startDectection = function(state,success, error) {
		if (cordova.exec)
			cordova.exec(success, error, 'VoiceActivity', 
			   'process',[state]);
		else
			error("Cordova not present");
	};
	return {
		startDectection: startDectection
	};
}();

module.exports = VoiceActivity;