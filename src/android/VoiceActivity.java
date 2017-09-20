package edu.cmu.pocketsphinx.demo.wear;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import edu.cmu.pocketsphinx.demo.wear.WearActivity;

/*
 * Cordova/Phonegap plugin.
 *
 * Call using action "process" with parameters processId, processParams, title.
 * Call using action "processList" to get a list of processIds available
 */
public class VoiceActivity extends CordovaPlugin {
	private static final String TAG = VoiceActivity.class.getSimpleName();

	private static int REQUEST_OPENVOICE_ACTIVITY = 1234;
	CallbackContext currentCallbackContext;

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if ("process".equals(action)) {
			// Launch OpenCV
			currentCallbackContext = callbackContext;
			Intent intent = new Intent(cordova.getActivity(), WearActivity.class);
			intent.putExtra("state", args.getString(0));
			cordova.startActivityForResult(this, intent, REQUEST_OPENVOICE_ACTIVITY);
			return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// Log.d("detect","resultVoice");
		if (requestCode == REQUEST_OPENVOICE_ACTIVITY) {
			// Call Javascript with results
			try {
				if (resultCode == Activity.RESULT_OK) {
					String result = intent.getStringExtra("resultVoice");
					JSONObject res = new JSONObject();
					res.put("resultVoice",result);
	
					currentCallbackContext.success(res);
				}
				else
					currentCallbackContext.error("Request failed");
			} catch (JSONException e) {
				currentCallbackContext.error(e.getLocalizedMessage());
			}
    	}
	}
}
