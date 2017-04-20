package ser593.com.epilepsy.apiCall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by chintan on 4/19/17.
 */

public class ConnectionManagerHelper extends BroadcastReceiver {
    private static final String TAG = ConnectionManagerHelper.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        boolean isConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if(isConnected){
            Toast.makeText(context, "Internet Connection Lost", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context, "Internet Connected", Toast.LENGTH_LONG).show();
        }
    }
}
