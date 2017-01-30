package ser593.com.epilepsy.painReport;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONObject;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.helper.jsHandler;

public class PromisActivity extends AppCompatActivity {

    private Bundle webViewBundle;
    private static int DEFAULT_SAVE_STATE_TIME=4; //in hours
    private static int DEFAULT_REMINDER_INTERVAL=1; // in hours, used both for polling server for new surveys and reminding if app in background
    private static int PENDING_INTENT_ID_APPINBACKGROUNDSERVICE=0;
    private static int PENDING_INTENT_ID_INVALIDATESERVICE=1;
    private static int PENDING_INTENT_ID_PAINREPORTNOTIFICATIONSERVICE=2;
    private WebView webView;
    private boolean firstPageFinishedLoading=false;
    private String pin = "";
    private int nativeVersionCode;
    private String  nativeVersionName;
    private String appVersionNumber;
    private String nativeVersionNumber;
    private jsHandler _jsHandler;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    @JavascriptInterface
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        PackageInfo pinfo = null;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        nativeVersionCode = pinfo.versionCode;
        nativeVersionName = pinfo.versionName;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=sharedPrefs.edit();
        editor.putString("logger", "");
        editor.putString("appVersionNumber","");
        editor.apply();

        setContentView(R.layout.activity_promis);

        String URL = "file:///android_asset/www/index.html";
        int currentapiVersion = Build.VERSION.SDK_INT;

        webView = (WebView) findViewById(R.id.webView);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        settings.setUserAgentString(
                this.webView.getSettings().getUserAgentString()
                        + " "
                        + "painreport"
        );

        //add javascript interface
        _jsHandler = new jsHandler(this, webView);
//        webView.addJavascriptInterface(_jsHandler, "jsHandler");
        webView.addJavascriptInterface(this, "mainActivity");
        //In kitkat if hardware acceleration is not turned off
        //we get a blank screen on start of the app
        if (currentapiVersion == Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        //These settings are not available before Jelly Bean
        if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowContentAccess(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                settings.setAllowFileAccessFromFileURLs(true);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                settings.setAllowUniversalAccessFromFileURLs(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(false);   // KG set to false for release version
        }
        //necessary for local storage to work
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setDatabasePath("/data/data/" + webView.getContext().getPackageName() + "/databases/");
        }
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            private ProgressDialog progress;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress = new ProgressDialog(PromisActivity.this);
                progress.setMessage("Loading.. Please Wait!");
                progress.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progress.dismiss();
                Log.e("Debug","Page loaded");
                //this method gets called every time any page finishes loading
                //firstPageFinishedLoading will make sure to call the getPIN JavaScript
                //method only once
                if (!firstPageFinishedLoading) {
                    webView.loadUrl("javascript:getSettings()");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        webView.evaluateJavascript("getAppVersionNumber()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {

                                s = s.replace("\"","");
                                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor=sharedPrefs.edit();
                                editor.putString("appVersionNumber",s);
                                editor.commit();
                                appVersionNumber = getFromSharedPreferences("appVersionNumber");
                                nativeVersionNumber = nativeVersionCode +"-"+nativeVersionName+"-"+getFromSharedPreferences("appVersionNumber")+ "-Android";
                                editor.putString("nativeVersionNumber", nativeVersionNumber);
                                editor.commit();

                            }
                        });
                    }
                    Log.d("Debug","Calling setNativeVersionNumber js function");
                    webView.loadUrl("javascript:setNativeVersionNumber(\""+getFromSharedPreferences("nativeVersionNumber")+"\")");
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    firstPageFinishedLoading = true;

                }
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl(URL);    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Are you sure you want to go back ?");

        // set dialog message
        alertDialogBuilder
                .setMessage("All the progress will be lost")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        PromisActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void onStop(){
        super.onStop();

        pin = getFromSharedPreferences("PIN");
        if(pin != null){

            try {
                Long timestamp = System.currentTimeMillis()/1000;
                JSONObject obj = new JSONObject();
                obj.put("deviceType","Android");
                obj.put("osVersion", Build.VERSION.RELEASE);
                obj.put("nativeVersionCode", nativeVersionCode);
                obj.put("nativeVersionName", nativeVersionName);
                String metaData = obj.toString();
                String eventName = "appOnStop";
//                logUserInteractions.pushLoggerEvents(pin,eventName,metaData,timestamp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(!hasWindowFocus()){
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor=sharedPrefs.edit();
            editor.putBoolean("AppInForeground",false);
            editor.commit();
            //int waitingTimeInMilli=minsToWaitInBackGround*60*1000;
            webView.loadUrl("javascript:checkSurveyInProgress()");

            int alarmIntervalInHrs=sharedPrefs.getInt("reminderInterval",DEFAULT_REMINDER_INTERVAL);
            int alarmIntervalInMilli=alarmIntervalInHrs*60*60*1000;
            //          int alarmIntervalInMilli = 60*1000;

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//            Intent i = new Intent(this, painReportNotificationService.class);
//            PendingIntent pi = PendingIntent.getService(this, PENDING_INTENT_ID_PAINREPORTNOTIFICATIONSERVICE, i, 0);
//
//            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    SystemClock.elapsedRealtime() + alarmIntervalInMilli,
//                    alarmIntervalInMilli, pi);

        }

    }

    private String getFromSharedPreferences(String key){
        String value="";
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharedPrefs.contains(key)) {
            value= sharedPrefs.getString(key, null);
        }
        return value;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.wtf("Debug","Inside onResume activity ");

        pin = getFromSharedPreferences("PIN");
        if(pin != null){
            try {
                Long timestamp = System.currentTimeMillis()/1000;
                JSONObject obj = new JSONObject();
                obj.put("deviceType","Android");
                obj.put("osVersion", Build.VERSION.RELEASE);
                obj.put("nativeVersionCode", nativeVersionCode);
                obj.put("nativeVersionName", nativeVersionName);
                String metaData = obj.toString();
                String eventName = "appOnResume";
//                logUserInteractions.pushLoggerEvents(pin,eventName,metaData,timestamp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=sharedPrefs.edit();
        editor.putBoolean("AppInForeground",true);
        editor.commit();

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        //as app is in foreground now remove all the previously generated notifications.
//        Intent i = new Intent(this, painReportNotificationService.class);
//        PendingIntent pi = PendingIntent.getService(this, PENDING_INTENT_ID_PAINREPORTNOTIFICATIONSERVICE, i, 0);
//        am.cancel(pi);

        NotificationManager nMgr = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        nMgr.cancelAll();

    }

    @Override
    protected void onStart() {
        super.onStart();

        pin = getFromSharedPreferences("PIN");
        if(pin != null){
            try {
                Long timestamp = System.currentTimeMillis()/1000;
                JSONObject obj = new JSONObject();
                obj.put("deviceType","Android");
                obj.put("osVersion", Build.VERSION.RELEASE);
                obj.put("nativeVersionCode", nativeVersionCode);
                obj.put("nativeVersionName", nativeVersionName);
                String metaData = obj.toString();
                String eventName = "appOnStart";
//                logUserInteractions.pushLoggerEvents(pin,eventName,metaData,timestamp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        pin = getFromSharedPreferences("PIN");
        if(pin != null){
            try {
                Long timestamp = System.currentTimeMillis()/1000;
                JSONObject obj = new JSONObject();
                obj.put("deviceType","Android");
                obj.put("osVersion", Build.VERSION.RELEASE);
                obj.put("nativeVersionCode", nativeVersionCode);
                obj.put("nativeVersionName", nativeVersionName);
                String metaData = obj.toString();
                String eventName = "appOnRestart";
//                logUserInteractions.pushLoggerEvents(pin,eventName,metaData,timestamp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        pin = getFromSharedPreferences("PIN");
        if(pin != null){
            try {
                Long timestamp = System.currentTimeMillis()/1000;
                JSONObject obj = new JSONObject();
                obj.put("deviceType","Android");
                obj.put("osVersion", Build.VERSION.RELEASE);
                obj.put("nativeVersionCode", nativeVersionCode);
                obj.put("nativeVersionName", nativeVersionName);
                String metaData = obj.toString();
                String eventName = "appOnPause";
//                logUserInteractions.pushLoggerEvents(pin,eventName,metaData,timestamp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pin = getFromSharedPreferences("PIN");
        if(pin != null){
            try {
                Long timestamp = System.currentTimeMillis()/1000;
                JSONObject obj = new JSONObject();
                obj.put("deviceType","Android");
                obj.put("osVersion", Build.VERSION.RELEASE);
                obj.put("nativeVersionCode", nativeVersionCode);
                obj.put("nativeVersionName", nativeVersionName);
                String metaData = obj.toString();
                String eventName = "appOnDestroy";
//                logUserInteractions.pushLoggerEvents(pin,eventName,metaData,timestamp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
