package ser593.com.epilepsy.UserTasks;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONObject;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.apiCall.CheckForInternet;
import ser593.com.epilepsy.app.AppController;
import ser593.com.epilepsy.pojo.ActivityDetails;

public class ActivityDescription extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = ActivityDescription.class.getSimpleName();
    TextView tvDescTitle, tvActivityTitle, tvActivityDesc, tvDemoTitle, tvDetailsTitle, tvDetailsDesc = null;
    Button btnBack, btnStart = null;
    ActivityDetails activityDetails = null;
    TextView tvActionBarTitle = null;
    private String activityInstanceID = null;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().hide();
        initialize();
        populateInformation();

        String path = "android.resource://" + getPackageName() + "/" + R.raw.test_video;
        VideoView videoView = (VideoView) findViewById(R.id.vvDemo);
        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(videoView);
        videoView.setVideoURI(Uri.parse(path));
        videoView.setMediaController(mediaController);getSupportActionBar().hide();
        videoView.requestFocus();
        videoView.start();
        mediaController.show();

    }

    private void populateInformation() {
        activityDetails = getIntent().getParcelableExtra("Class");
        tvActivityTitle.setText(activityDetails.getTitle());
        tvActivityDesc.setText(activityDetails.getBreif());
        tvDetailsDesc.setText(activityDetails.getDetails());
        tvActionBarTitle.setText(activityDetails.getTitle());
        activityInstanceID = getIntent().getExtras().getString("activityInstanceID");
    }

    private void initialize() {
        tvActionBarTitle = (TextView) findViewById(R.id.action_bar_title);
        tvActivityTitle = (TextView)findViewById(R.id.tvActivityTitle);
        tvDescTitle = (TextView)findViewById(R.id.tvDescTitle);
        tvActivityDesc = (TextView) findViewById(R.id.tvActivityDesc);
        tvDemoTitle = (TextView) findViewById(R.id.tvDemoTitle);
        tvDetailsTitle = (TextView) findViewById(R.id.tvDetailsTitle);
        tvDetailsDesc = (TextView) findViewById(R.id.tvDetailsDesc);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnStart = (Button) findViewById(R.id.btnStart);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/SourceSansPro-Regular.otf");
        tvActivityTitle.setTypeface(custom_font);
        tvDescTitle.setTypeface(custom_font);
        tvActivityDesc.setTypeface(custom_font);
        tvDemoTitle.setTypeface(custom_font);
        tvDetailsTitle.setTypeface(custom_font);
        tvDetailsDesc.setTypeface(custom_font);

        btnBack.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnStart:

                Intent i;
                if(getActivityDetails()){
                    switch (activityDetails.getTitle()){
                        case "Pattern Comparison":
                            i = new Intent(this,PatternComparisonProcessingActivity.class);
                            i.putExtra("activityInstanceID",activityInstanceID);
                            startActivity(i);
                            break;
                        case "Finger Tapping":
                            i = new Intent(this,FingerTappingActivity.class);
                            i.putExtra("activityInstanceID",activityInstanceID);
                            startActivity(i);
                            break;
                        case "Spatial Span":
                            i = new Intent(this,SpatialSpanActivity.class);
                            i.putExtra("activityInstanceID",activityInstanceID);
                            startActivity(i);
                            break;
                        case "Flanker":
                            i = new Intent(this,FlankerActivity.class);
                            i.putExtra("activityInstanceID",activityInstanceID);
                            startActivity(i);
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean getActivityDetails() {

        String pin = AppController.getInstance().readPreference("patientPin");
        String URL = AppController.getInstance().readPreference("url");
        String httpMethod = AppController.getInstance().readPreference("httpMethod");

        String link = httpMethod + "://" + URL + getString(R.string.getActivityDetailsURL) + activityInstanceID + "?pin=" + pin;

        if(CheckForInternet.isNetworkAvailable(this)){
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, link, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: " + response.toString());
                            Toast.makeText(getApplicationContext(),"Your Data Has been sent to the Server", Toast.LENGTH_LONG).show();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: " + error.toString());
                }
            });

            AppController.getInstance().addToRequestQueue(getRequest);
            return true;
        }else{
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.indigo)
                    .setButtonsColorRes(R.color.darkDeepOrange)
                    .setTitle("No Internet")
                    .setMessage("You will need internet to get the details of the Activity")
                    .setPositiveButton("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setNegativeButton("Exit Application", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finishAffinity();
                        }
                    })
                    .show();
            return false;
        }


    }

}
