package edu.cmu.pocketsphinx.demo.wear;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;


/*
 * Cordova/Phonegap plugin.
 *
 * Call using action "process" with parameters processId, processParams, title.
 * Call using action "processList" to get a list of processIds available
 */
public class VoiceActivity extends CordovaPlugin implements edu.cmu.pocketsphinx.RecognitionListener {
	private static final String TAG = VoiceActivity.class.getSimpleName();
  private static int REQUEST_OPENVOICE_ACTIVITY = 1234;
	CallbackContext currentCallbackContext;
  private String KWS_SEARCH = "wakeup";
  private String KEYPHRASE = "computer";
  private String stateInitial = "initial";
  private String stateStart= "start";
  private static SpeechRecognizer recognizer;


	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
    currentCallbackContext = callbackContext;
		if ("process".equals(action)) {
        if(args.getString(0).equals(stateInitial)){
          Toast.makeText(cordova.getActivity(), "stateInitial",Toast.LENGTH_SHORT).show();
          runRecognizerSetup();
        }else if(args.getString(0).equals(stateStart)){
          Toast.makeText(cordova.getActivity(), "stateStart",Toast.LENGTH_SHORT).show();
          switchSearch(KWS_SEARCH);
        }
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
  private void runRecognizerSetup() {
    // Recognizer initialization is a time-consuming and it involves IO,
    // so we execute it in async task
    new AsyncTask<Void, Void, Exception>() {
      @Override
      protected Exception doInBackground(Void... params) {
        try {
          Assets assets = new Assets(cordova.getActivity());
          File assetDir = assets.syncAssets();
          setupRecognizer(assetDir);
        } catch (IOException e) {
          return e;
        }
        return null;
      }
      @Override
      protected void onPostExecute(Exception result) {
        if (result != null) {
        } else {
          switchSearch(KWS_SEARCH);
        }
      }
    }.execute();
  }
  private void setupRecognizer(File assetsDir) throws IOException {
    // The recognizer can be configured to perform multiple searches
    // of different kind and switch between them
    recognizer = SpeechRecognizerSetup.defaultSetup()
      .setAcousticModel(new File(assetsDir, "en-us-ptm"))
      .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
      .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
      .getRecognizer();
    recognizer.addListener(this);
    /** In your application you might not need to add all those searches.
     * They are added here for demonstration. You can leave just one.
     */
    // Create keyword-activation search.
    recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
  }
  private void switchSearch(String searchName) {
    recognizer.stop();
    if(searchName.equals(KWS_SEARCH)) {
      recognizer.startListening(searchName);
    }
  }

  @Override
  public void onBeginningOfSpeech() {

  }

  @Override
  public void onEndOfSpeech() {

  }
  @Override
  public void onPartialResult(Hypothesis hypothesis) {
    if (hypothesis == null) {
      return;
    }
    else{
      String text = hypothesis.getHypstr();
      if (text.equals(KEYPHRASE)) {
        switchSearch(KWS_SEARCH);
      }
    }
  }
  @Override
  public void onResult(Hypothesis hypothesis) {
    if (hypothesis != null) {
      recognizer.stop();
      Toast.makeText(cordova.getActivity(), "result-->"+hypothesis.getHypstr(),Toast.LENGTH_SHORT).show();
      String text = hypothesis.getHypstr();
      currentCallbackContext.success(text);
    }
  }
  @Override
  public void onError(Exception e) {
    currentCallbackContext.error("error");
  }

  @Override
  public void onTimeout() {
    currentCallbackContext.error("timeout");
  }

}
