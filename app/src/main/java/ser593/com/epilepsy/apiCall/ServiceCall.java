package ser593.com.epilepsy.apiCall;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ser593.com.epilepsy.Main.MainActivity;
import ser593.com.epilepsy.R;
import ser593.com.epilepsy.app.AppController;

/**
 * Created by chintan on 4/19/17.
 */

public class ServiceCall {

    private RelativeLayout resultsLayout = null;
    private boolean flag = false;
    private Context context = null;
    private String pin = null;
    private String URL = null;
    private String httpMethod = AppController.getInstance().readPreference("httpMethod");
    private static final String TAG = ServiceCall.class.getSimpleName();
    private static String receivedData = null;
    private String postActivityURL =
            "/rest/activities/activityinstanceresult/";

    private String getScheduledActivityURL =
            "/rest/activities/scheduledactivity";

    private String getActivityInstanceURL =
            "/rest/activities/activityinstance/";

    public ServiceCall() {
        super();
        flag = false;
    }

    public ServiceCall(Context context) {
        this.context = context;
    }
    public ServiceCall(Context context, RelativeLayout resultsLayout) {
        this.context = context;
        this.resultsLayout = resultsLayout;
    }

    public JSONObject submitActivityInstance(JSONObject data) throws JSONException {
        String link = constructURL("submitActivity", data);
        callAPI(link,"POST",data);
        return null;
    }

    private void callAPI(String link, String method, JSONObject data){
        if(CheckForInternet.isNetworkAvailable(context)){
            if(method.equalsIgnoreCase("GET")){
                Log.d(TAG, "callAPI: " + link + " data " + data);
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, link, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "onResponse: " + response.toString());
                                if(resultsLayout != null){
                                    Snackbar.make(resultsLayout,"Results have been sent to the server", Toast.LENGTH_LONG).show();
                                }

                                populateReceivedData(response.toString());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                        receivedData = error.toString();
                    }
                });

                AppController.getInstance().addToRequestQueue(getRequest);
            }else {
                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, link, data,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "onResponse: " + response.toString());
                                if(resultsLayout != null){
                                    Snackbar.make(resultsLayout,"Results have been sent to the server", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                        //data loss... We need to handle that.
                        Log.e(TAG, "onErrorResponse: Data lost");

                    }
                });

                AppController.getInstance().addToRequestQueue(postRequest);
            }

        }else{


        }

    }

    private void populateReceivedData(String s) {
        Log.d(TAG, "populateReceivedData: " + s);
        receivedData = s;
        flag = true;
    }


    private String constructURL(String urlToCall, JSONObject data) throws JSONException {
        pin = AppController.getInstance().readPreference("patientPin");
        URL = AppController.getInstance().readPreference("url");
        String link = null;
        switch (urlToCall){
            case "scheduleActivity":
                link = httpMethod + "://" + URL + getScheduledActivityURL + "?pin=" + pin;
                break;
            case "submitActivity":
                link = httpMethod + "://" +  URL + postActivityURL + data.get("activityInstanceID") + "?pin=" + pin;
                break;
        }
        Log.d(TAG, "constructURL: " + link);
        return link;

    }

}
