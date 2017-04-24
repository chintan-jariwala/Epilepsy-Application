package ser593.com.epilepsy.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.UserTasks.ActivityDescription;
import ser593.com.epilepsy.UserTasks.FingerTappingActivity;
import ser593.com.epilepsy.UserTasks.FlankerActivity;
import ser593.com.epilepsy.UserTasks.PatternComparisonProcessingActivity;
import ser593.com.epilepsy.UserTasks.SpatialSpanActivity;
import ser593.com.epilepsy.apiCall.ServiceCall;
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

        //disable all buttons
        btnPatternComparison.setEnabled(false);
//        btnPatternComparison.setBackground();

        btnSpacialSpan.setEnabled(false);
//        btnSpacialSpan.setBackground();

        btnFingerTapping.setEnabled(false);
//        btnFingerTapping.setBackground();

        btnFlanker.setEnabled(false);
//        btnFlanker.setBackground();

        getScheduledActivities();

        activityDetails = new ActivityDetails();

        String colorHexCode = "#85248F"; //purple
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvFlanker = (TextView) findViewById(R.id.tvFlanker);
        TextView tvPattern = (TextView) findViewById(R.id.tvPattern);
        TextView tvTapping = (TextView) findViewById(R.id.tvTapping);
        TextView tvSpatial = (TextView) findViewById(R.id.tvSpatial);
        TextView tvPromis = (TextView) findViewById(R.id.tvPromis);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/SourceSansPro-Regular.otf");
        tvTitle.setTypeface(custom_font);
        tvTitle.setTextColor(Color.parseColor(colorHexCode));
        tvFlanker.setTypeface(custom_font);
        tvFlanker.setTextColor(Color.parseColor(colorHexCode));
        tvPattern.setTypeface(custom_font);
        tvPattern.setTextColor(Color.parseColor(colorHexCode));
        tvTapping.setTypeface(custom_font);
        tvTapping.setTextColor(Color.parseColor(colorHexCode));
        tvSpatial.setTypeface(custom_font);
        tvSpatial.setTextColor(Color.parseColor(colorHexCode));
        tvPromis.setTypeface(custom_font);
        tvPromis.setTextColor(Color.parseColor(colorHexCode));
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
                        btnFingerTapping.setEnabled(true);

                    }else if(activity.equalsIgnoreCase("FLANKER")){
                        Log.d(TAG, "enableActivities: " + activity);

                        btnFlanker.setEnabled(true);
                    }else if(activity.equalsIgnoreCase("PATTERNCOMPARISON")){
                        Log.d(TAG, "enableActivities: " + activity);

                        btnPatternComparison.setEnabled(true);
                    }else if(activity.equalsIgnoreCase("SPATIALSPAN")){
                        Log.d(TAG, "enableActivities: " + activity);

                        btnSpacialSpan.setEnabled(true);
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

    private void callAPI(String link){
        if(isNetworkAvailable()){
                Log.d(TAG, "callAPI: " + link );
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, link, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "onResponse: " + response.toString());
                                Toast.makeText(getApplicationContext(),"Your Data Has been sent to the Server", Toast.LENGTH_LONG).show();
                                enableActivities(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                    }
                });

                AppController.getInstance().addToRequestQueue(getRequest);


        }else{
            //Alert User to get internet
        }

    }

    @Override
    public void onClick(View v) {


        Bundle b = new Bundle();
        Intent i = new Intent(getApplicationContext(),ActivityDescription.class);
        switch (v.getId()){
            case R.id.btnPatternComparison:
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
                break;
            case R.id.btnFingerTapping:
                activityDetails.setTitle("Finger Tapping");
                activityDetails.setBreif("The finger tapping task measures the player's finger moving ability.");
                activityDetails.setDetails("1. When enter the screen, the screen indicates the player to start with right or left hand.\n\n" +
                        "2. Hover your hand over with index finger and middle finger line up to the two tap buttons.\n\n" +
                        "3. Alternating between the two fingers and tap on the buttons while the timer is counting down.");
                b.putParcelable("Class",activityDetails);
                i.putExtras(b);
                i.putExtra("activityInstanceID", addActivityInstID("FINGERTAPPING"));

                startActivity(i);
                break;
            case R.id.btnPromis:
                startActivity(new Intent(this,PromisActivity.class));
                break;
            case R.id.btnSpacialSpan:
                activityDetails.setTitle("Spatial Span");
                activityDetails.setBreif("The spatial span task measures the player's short term memory by repeating a sequence.");
                activityDetails.setDetails("1. Depending on current level of difficulty, the screen will show a sequence by lighting up 3-5 buttons.\n\n" +
                        "2. Repeat the sequence by tapping the buttons in the same order.");
                b.putParcelable("Class",activityDetails);
                i.putExtras(b);
                i.putExtra("activityInstanceID", addActivityInstID("SPATIALSPAN"));

                startActivity(i);
                break;
            case R.id.btnFlanker:
                activityDetails.setTitle("Flanker");
                activityDetails.setBreif("The flanker task measures the player's ability to identify certain target with distractions.");
                activityDetails.setDetails("1. The star in the center indicates the beginning of a question.\n\n" +
                        "2. After a brief pause, a series of arrows will be shown on the screen.\n\n" +
                        "3. Identify the direction of the center arrow and tap on the button that's point the same direction.");
                b.putParcelable("Class",activityDetails);
                i.putExtras(b);
                i.putExtra("activityInstanceID", addActivityInstID("FLANKER"));

                startActivity(i);
                break;
            case R.id.btnSettings:
                Intent in = new Intent(this, SettingsActivity.class);
                startActivity(in);
                break;
        }
    }

    private String constructGetScheduleActivitiesURL() throws JSONException {
        String link = "http://" + URL + getString(R.string.getScheduledActivityURL) + "?pin=" + patientPin;

        Log.d(TAG, "constructURL: " + link);
        return link;

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
