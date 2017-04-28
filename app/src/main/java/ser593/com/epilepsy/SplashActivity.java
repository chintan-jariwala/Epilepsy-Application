package ser593.com.epilepsy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import ser593.com.epilepsy.Main.MainActivity;
import ser593.com.epilepsy.Main.SettingsActivity;
import ser593.com.epilepsy.UserTasks.SpatialSpanActivity;
import ser593.com.epilepsy.app.AppController;

/**
 * Created by chint on 10/31/2016.
 */

public class SplashActivity extends AppCompatActivity{

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " + NotificationManagerCompat.from(this).areNotificationsEnabled());
        if (NotificationManagerCompat.from(this).areNotificationsEnabled() || Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners").contains(getApplicationContext().getPackageName()))
        {
            String pinPref = AppController.getInstance().readPreference("patientPin");
            String urlPref = AppController.getInstance().readPreference("url");

            //check if there is already stored patient info
            if (pinPref == null && urlPref == null) {
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                finish();
            }
            else
            {
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
            }
        } else {
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.indigo)
                    .setButtonsColorRes(R.color.darkDeepOrange)
                    .setTitle("Enable Access")
                    .setMessage("Please make sure to enable notification access and restart the application.")
                    .setNeutralButton("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                            startActivity(intent);
                            finish();
                        }
                    })
                    .show();
        }


    }
}
