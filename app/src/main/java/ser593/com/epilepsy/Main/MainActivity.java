package ser593.com.epilepsy.Main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.UserTasks.ActivityDescription;
import ser593.com.epilepsy.apiCall.CheckForInternet;
import ser593.com.epilepsy.app.AppController;
import ser593.com.epilepsy.painReport.PromisActivity;
import ser593.com.epilepsy.pojo.ActivityDetails;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private HashMap<String,String> activityInstanceIDMap = new HashMap<String,String>();
    ArrayList<String> listActivities = new ArrayList<>();
    Button btnPatternComparison = null;
    Button btnFingerTapping = null;
    Button btnPromis = null;
    Button btnSpacialSpan = null;
    Button btnFlanker = null;
    Button btnSettings = null;
    TextView tvActionBarTitle = null;
    ActivityDetails activityDetails = null;
    private String patientPin = null;
    private String URL = null;
    private String httpMethod = null;
    private RelativeLayout relativeLayout = null;

    private boolean btnFlankerState = false;
    private boolean btnPatternCompState = false;
    private boolean btnFingerTappingState = false;
    private boolean btnSpatialSpanState = false;
    ProgressDialog progress;
    private static final int MY_SOCKET_TIMEOUT_MS = 5000;


    private boolean netwrok = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().hide();

        tvActionBarTitle = (TextView) findViewById(R.id.action_bar_title);
        tvActionBarTitle.setText("WELCOME");

        btnPatternComparison = (Button) findViewById(R.id.btnPatternComparison);
        btnPatternComparison.setOnClickListener(this);
        btnFingerTapping = (Button) findViewById(R.id.btnFingerTapping);
        btnFingerTapping.setOnClickListener(this);
        btnPromis = (Button) findViewById(R.id.btnPromis);
        btnPromis.setOnClickListener(this);
        btnSpacialSpan = (Button) findViewById(R.id.btnSpacialSpan);
        btnSpacialSpan.setOnClickListener(this);
        btnFlanker = (Button) findViewById(R.id.btnFlanker);
        btnFlanker.setOnClickListener(this);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);
        relativeLayout = (RelativeLayout) findViewById(R.id.mainContainer);

        btnPatternComparison.setBackgroundResource(R.drawable.btn_pattern_grey);

        btnSpacialSpan.setBackgroundResource(R.drawable.btn_span_grey);

        btnFingerTapping.setBackgroundResource(R.drawable.btn_tapping_grey);

        btnFlanker.setBackgroundResource(R.drawable.btn_flankered_grey);
        progress = new ProgressDialog(this);

        getScheduledActivities();

        activityDetails = new ActivityDetails();

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvFlanker = (TextView) findViewById(R.id.tvFlanker);
        TextView tvPattern = (TextView) findViewById(R.id.tvPattern);
        TextView tvTapping = (TextView) findViewById(R.id.tvTapping);
        TextView tvSpatial = (TextView) findViewById(R.id.tvSpatial);
        TextView tvPromis = (TextView) findViewById(R.id.tvPromis);
        TextView tvSettings = (TextView) findViewById(R.id.tvSettings);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/SourceSansPro-Regular.otf");
        tvTitle.setTypeface(custom_font);
        tvFlanker.setTypeface(custom_font);
        tvPattern.setTypeface(custom_font);
        tvTapping.setTypeface(custom_font);
        tvSpatial.setTypeface(custom_font);
        tvPromis.setTypeface(custom_font);
        tvSettings.setTypeface(custom_font);

    }

    private void enableActivities(JSONObject scheduledActivities) {
        Log.d(TAG, "enableActivities: " + scheduledActivities.toString());
        try {

            JSONArray activities = (JSONArray) scheduledActivities.get("activities");
            for(int i=0; i<activities.length(); i++){
                listActivities.clear();
                JSONObject item = activities.getJSONObject(i);
                JSONArray sequence  = (JSONArray) item.get("sequence");
                for(int j=0; j<sequence.length(); j++){
                    String activity = sequence.get(j).toString();
                    listActivities.add(activity);
                    if(activity.equalsIgnoreCase("FINGERTAPPING")){
                        Log.d(TAG, "enableActivities: " + activity);
                        //change image
                        btnFingerTappingState = true;
                        btnFingerTapping.setBackgroundResource(R.drawable.btn_tapping_normal);

                    }else if(activity.equalsIgnoreCase("FLANKER")){
                        Log.d(TAG, "enableActivities: " + activity);
                        btnFlankerState = true;
                        btnFlanker.setBackgroundResource(R.drawable.btn_flankered_normal);
                    }else if(activity.equalsIgnoreCase("PATTERNCOMPARISON")){
                        Log.d(TAG, "enableActivities: " + activity);

                        btnPatternCompState = true;
                        btnPatternComparison.setBackgroundResource(R.drawable.btn_pattern_normal);
                    }else if(activity.equalsIgnoreCase("SPATIALSPAN")){
                        Log.d(TAG, "enableActivities: " + activity);

                        btnSpatialSpanState = true;
                        btnSpacialSpan.setBackgroundResource(R.drawable.btn_span_normal);
                    }
                }
                Log.d(TAG, "enableActivities: " + sequence.toString());
                activityInstanceIDMap.put(item.getString("activityInstanceID"), listActivities.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getScheduledActivities() {

        patientPin = AppController.getInstance().readPreference("patientPin");
        URL = AppController.getInstance().readPreference("url");
        httpMethod = AppController.getInstance().readPreference("httpMethod");

        if(patientPin == null || URL == null){
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.indigo)
                    .setButtonsColorRes(R.color.darkDeepOrange)
                    .setTitle("Error")
                    .setMessage("For some reason, Application did not receive the patientPin or URL\n Please enter those details again.")
                    .setPositiveButton("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this , SettingsActivity.class));
                        }
                    })
                    .show();
            return null;
        }else{
            String link = null;

            try {
                link = constructGetScheduleActivitiesURL();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Call the API to get data
           callAPI(link);
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getScheduledActivities();
    }

    @Override
    public void onBackPressed() {
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.indigo)
                .setButtonsColorRes(R.color.darkDeepOrange)
                .setTitle("Exit")
                .setCancelable(false)
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .show();

    }

    private void callAPI(String link){
        if(CheckForInternet.isNetworkAvailable(this)){
                netwrok = true;
                if(progress.isShowing()){
                    progress.dismiss();
                    Log.d(TAG, "callAPI: ");
                }
                progress.setTitle("Fetching pending activities");
                progress.setMessage("Please wait while we get data from the server...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                Log.d(TAG, "callAPI: " + link );
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, link, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(progress.isShowing()){
                                    progress.dismiss();
                                    Log.d(TAG, "onResponse: ");
                                }
                                Log.d(TAG, "onResponse: " + response.toString());
                                try {
                                    JSONArray act = (JSONArray) response.get("activities");
                                    if(act.length()>0){
                                        Snackbar.make(relativeLayout,"Welcome, You have " + act.length() + " pending activities",Snackbar.LENGTH_LONG).show();
                                    }else{
                                        Snackbar.make(relativeLayout,"Welcome, You don't have any pending activities",Snackbar.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                enableActivities(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                        if(progress.isShowing()){
                            progress.dismiss();
                            Log.d(TAG, "onErrorResponse: ");
                        }
                        errorHandler(error);
                    }
                });
                getRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(getRequest);


        }else{
            if(progress.isShowing()){
               progress.dismiss();
                Log.d(TAG, "callAPI: ");
            }
            //Alert User to get internet
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.indigo)
                    .setButtonsColorRes(R.color.darkDeepOrange)
                    .setTitle("No Network")
                    .setCancelable(false)
                    .setMessage(R.string.no_internet)
                    .setPositiveButton("Try Again", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            callAPIWithDelay();

                        }
                    })
                    .setNegativeButton("Exit", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishAffinity();
                        }
                    })
                    .show();
        }

    }

    private void callAPIWithDelay() {
        progress.setTitle("Fetching Data");
        progress.setMessage("Please wait while we connect...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                getScheduledActivities();
            }
        }, 2000);
    }

    private void errorHandler(VolleyError error) {
        if(error != null){
            Log.d(TAG, "errorHandler: Message = " + error.getMessage());
            Log.d(TAG, "errorHandler: Cause = " + error.getCause());
            if(error.toString().contains("TimeoutError")){
                new LovelyStandardDialog(this)
                        .setTopColorRes(R.color.indigo)
                        .setButtonsColorRes(R.color.darkDeepOrange)
                        .setTitle("Request Timeout")
                        .setCancelable(false)
                        .setMessage("I am sorry, the request timed out")
                        .setPositiveButton("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                callAPIWithDelay();
                            }
                        })
                        .setNegativeButton("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        })
                        .show();
            }else if(error.toString().contains("SSLHandshakeException")){
                new LovelyStandardDialog(this)
                        .setTopColorRes(R.color.indigo)
                        .setButtonsColorRes(R.color.darkDeepOrange)
                        .setTitle("Secure Connection Not Allowed")
                        .setMessage("There seems to be an issue connecting to the server\n\nPlease check the HTTP method and change it to \"http\" from \"https\"")
                        .setPositiveButton("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }else if(error.toString().contains("Connection reset")){
                new LovelyStandardDialog(this)
                        .setTopColorRes(R.color.indigo)
                        .setButtonsColorRes(R.color.darkDeepOrange)
                        .setTitle("Error")
                        .setMessage("There is an error connecting to the Server.\n\nWe apologise for the inconvenience\n\nPlease come back later")
                        .setCancelable(false)
                        .setPositiveButton("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("Exit", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finishAffinity();
                            }
                        })
                        .show();
            }

            if(error.networkResponse != null){
                int status = error.networkResponse.statusCode;
                String error_message = "";
                Log.d(TAG, "errorHandler: Status Code = " + status);
                Log.d(TAG, "errorHandler: data = " + error.networkResponse.data);
                switch (status){
                    case 404:
                        String obj = error.getMessage();
                        error_message = "For some reason the server is not reachable. We apologize for the inconvenience\n\nPlease check the server address and try again";
                        break;
                    case 500:
                        error_message = "Oops, There is some problem from our side.\n\n We will fix it as soon as possible. We apologize for the inconvenience. \n\nPlease try again later...";
                        break;
                    default:
                        error_message = "For some reason the server is not reachable. We apologize for the inconvenience\n\nPlease check the server address and try again";
                }

                new LovelyStandardDialog(this)
                        .setTopColorRes(R.color.indigo)
                        .setButtonsColorRes(R.color.darkDeepOrange)
                        .setTitle("Error Connecting")
                        .setMessage(error_message)
                        .setCancelable(false)
                        .setPositiveButton("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setNegativeButton("Exit", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finishAffinity();
                            }
                        })
                        .show();
            }

        }
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        Intent i = new Intent(getApplicationContext(),ActivityDescription.class);
        switch (v.getId()){
            case R.id.btnPatternComparison:
                if(btnPatternCompState){
                    activityDetails.setTitle("Pattern Comparison");
                    activityDetails.setBreif("The pattern comparison task measures the player's ability to identify whether two images are the same or not.");
                    activityDetails.setDetails("1. The star in the center indicates the beginning of a question.\n\n" +
                            "2. After a brief pause, two images will be shown on the screen.\n\n" +
                            "3. The player will identify whether the images are the same. It will be very obvious if the two images are different.\n\n" +
                            "4. If the images are the same, tap on the green yes button; if the images are different, tap on the red no button.");
                    b.putParcelable("Class",activityDetails);
                    i.putExtras(b);
                    i.putExtra("activityInstanceID", addActivityInstID("PATTERNCOMPARISON"));
                    startActivity(i);
                }
                else{
                    Snackbar.make(relativeLayout,"This Activity is not available right now",Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.btnFingerTapping:
                if(btnFingerTappingState){
                    activityDetails.setTitle("Finger Tapping");
                    activityDetails.setBreif("The finger tapping task measures the player's finger moving ability.");
                    activityDetails.setDetails("1. When enter the screen, the screen indicates the player to start with right or left hand.\n\n" +
                            "2. Hover your hand over with index finger and middle finger line up to the two tap buttons.\n\n" +
                            "3. Alternating between the two fingers and tap on the buttons while the timer is counting down.");
                    b.putParcelable("Class",activityDetails);
                    i.putExtras(b);
                    i.putExtra("activityInstanceID", addActivityInstID("FINGERTAPPING"));
                    startActivity(i);
                }else{
                    Snackbar.make(relativeLayout,"This Activity is not available right now",Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.btnPromis:
                startActivity(new Intent(this,PromisActivity.class));
                break;
            case R.id.btnSpacialSpan:
                if(btnSpatialSpanState) {
                    activityDetails.setTitle("Spatial Span");
                    activityDetails.setBreif("The spatial span task measures the player's short term memory by repeating a sequence.");
                    activityDetails.setDetails("1. Depending on current level of difficulty, the screen will show a sequence by lighting up 3-5 buttons.\n\n" +
                            "2. Repeat the sequence by tapping the buttons in the same order.");
                    b.putParcelable("Class", activityDetails);
                    i.putExtras(b);
                    i.putExtra("activityInstanceID", addActivityInstID("SPATIALSPAN"));

                    startActivity(i);
                }else{
                    Snackbar.make(relativeLayout,"This Activity is not available right now",Snackbar.LENGTH_LONG).show();
                }
                break;

            case R.id.btnFlanker:
                if(btnFlankerState) {
                    activityDetails.setTitle("Flanker");
                    activityDetails.setBreif("The flanker task measures the player's ability to identify certain target with distractions.");
                    activityDetails.setDetails("1. The star in the center indicates the beginning of a question.\n\n" +
                            "2. After a brief pause, a series of arrows will be shown on the screen.\n\n" +
                            "3. Identify the direction of the center arrow and tap on the button that's point the same direction.");
                    b.putParcelable("Class", activityDetails);
                    i.putExtras(b);
                    i.putExtra("activityInstanceID", addActivityInstID("FLANKER"));

                    startActivity(i);
                }else{
                    Snackbar.make(relativeLayout,"This Activity is not available right now",Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.btnSettings:
                Intent in = new Intent(this, SettingsActivity.class);
                startActivity(in);
                break;
        }
    }

    private String constructGetScheduleActivitiesURL() throws JSONException {
        String link = httpMethod + "://" + URL + getString(R.string.getScheduledActivityURL) + "?pin=" + patientPin;

        Log.d(TAG, "constructURL: " + link);
        return link;

    }

    private String addActivityInstID(String btnName){
        Iterator it = activityInstanceIDMap.keySet().iterator();
        while (it.hasNext()){
            String key = (String) it.next();
            String val = activityInstanceIDMap.get(key);
            if(val.contains(btnName)) {
                return key;
            }

        }
        return null;
    }
}
