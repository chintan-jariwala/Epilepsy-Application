package ser593.com.epilepsy.UserTasks;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.net.Uri;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.pojo.ActivityDetails;

public class ActivityDescription extends AppCompatActivity implements View.OnClickListener{

    TextView tvDescTitle, tvActivityTitle, tvActivityDesc, tvDemoTitle, tvDetailsTitle, tvDetailsDesc = null;
    Button btnBack, btnStart = null;
    ActivityDetails activityDetails = null;
    TextView tvActionBarTitle = null;

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
        videoView.setMediaController(mediaController);
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
                //Toast.makeText(this,"BC Shnati",Toast.LENGTH_LONG).show();
                switch (activityDetails.getTitle()){
                    case "Pattern Comparison":
                        startActivity(new Intent(this,PatternComparisonProcessingActivity.class));
                        break;
                    case "Finger Tapping":
                        startActivity(new Intent(this,FingerTappingActivity.class));
                        break;
                    case "Spatial Span":
                        startActivity(new Intent(this,SpatialSpanActivity.class));
                        break;
                    case "Flanker":
                        startActivity(new Intent(this,FlankerActivity.class));
                        break;
                }
                break;
            default:
                break;
        }
    }

}
