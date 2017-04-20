package ser593.com.epilepsy.apiCall;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import ser593.com.epilepsy.app.AppController;

/**
 * Created by chintan on 4/19/17.
 */

public class ServiceCall {

    private Context context = null;

    private static final String TAG = ServiceCall.class.getSimpleName();

    private String postActivityURL =
            "http://10.143.6.1:8080/Epilepsy/rest/activities/activityinstanceresult/";

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public ServiceCall() {
        super();
    }

    public ServiceCall(Context context) {
        this.context = context;
    }

    public void submitActivityInstance(JSONObject data) throws JSONException {
        String URL = postActivityURL + data.get("activityInstanceID") + "?pin=2023";

        if(isNetworkAvailable()){
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URL, data,
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

        }else{
            //Network not available
            Log.d(TAG, "submitActivityInstance: Network not available" );
            AppController.getInstance().writePreference("activityInstanceID",data.toString());
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
