package ser593.com.epilepsy.apiCall;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import ser593.com.epilepsy.app.AppController;

/**
 * Created by chintan on 4/19/17.
 */

public class ServiceCall {

    private boolean flag = false;
    private Context context = null;
    private String pin = null;
    private String URL = null;
    private String httpMethod = "http://";
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

    public JSONObject submitActivityInstance(JSONObject data) throws JSONException {
        String link = constructURL("submitActivity", data);
        callAPI(link,"POST",data);

//        return new JSONObject(callAPI(link,"POST",data));

            return null;
    }

    public JSONObject getScheduledActivities() throws JSONException {
        String link = constructURL("scheduleActivity",null);
        callAPI(link,"GET",null);
        int i= 2;
        while(!flag){
            try {
                Log.d(TAG, "getScheduledActivities: Flag = "+ flag);
                Log.d(TAG, "getScheduledActivities: " + i);
                TimeUnit.SECONDS.sleep(1 + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        return new JSONObject(receivedData);
    }

    private void callAPI(String link, String method, JSONObject data){
        if(isNetworkAvailable()){
            if(method.equalsIgnoreCase("GET")){
                Log.d(TAG, "callAPI: " + link + " data " + data);
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, link, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "onResponse: " + response.toString());
                                Toast.makeText(context,"Your Data Has been sent to the Server", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(context,"Your Data Has been sent to the API", Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                    }
                });

                AppController.getInstance().addToRequestQueue(postRequest);
            }

//            return dataFromAPI[0];
        }else{
            //Network not available
            Log.d(TAG, "submitActivityInstance: Network not available" );
//            AppController.getInstance().writePreference("activityInstanceID",data.toString());
//            return null;
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
                link = httpMethod + URL + getScheduledActivityURL + "?pin=" + pin;
                break;
            case "submitActivity":
                link = httpMethod + URL + postActivityURL + data.get("activityInstanceID") + "?pin=" + pin;
                break;
        }
        Log.d(TAG, "constructURL: " + link);
        return link;

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
