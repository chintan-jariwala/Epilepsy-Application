package ser593.com.epilepsy.apiCall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ser593.com.epilepsy.app.AppController;

/**
 * Created by Chintan on 4/19/17.
 */

public class ConnectionManagerHelper extends BroadcastReceiver {
    private static final String TAG = ConnectionManagerHelper.class.getSimpleName();
    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();

        if(isConnected){
            String pending = AppController.getInstance().readPreference("pendingSurveys");
            if(pending != null){
                Log.d(TAG, "onReceive: " + pending);

                JSONArray arr = null;
                try {
                    arr = new JSONArray(pending);
                    JSONObject obj;
                    for(int i=0; i<arr.length();i++){
                        obj = (JSONObject) arr.get(i);
                        ServiceCall serviceCall = new ServiceCall(AppController.getInstance());
                        serviceCall.submitActivityInstance(obj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }else{
            Log.d(TAG, "onReceive: Disconnected");
        }
    }

}
