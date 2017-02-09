package ser593.com.epilepsy.UserTasks;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.pojo.ActivityDetails;

public class ActivityDescription extends AppCompatActivity implements View.OnClickListener{

    TextView tvActivityDesc, tvDetailsDesc = null;
    Button btnBack, btnStart = null;
    ActivityDetails activityDetails = null;
    TextView tvActionBarTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        initialize();
        populateInformation();
    }

    private void populateInformation() {
        activityDetails = getIntent().getParcelableExtra("Class");
        tvActivityDesc.setText(activityDetails.getBreif());
        tvDetailsDesc.setText(activityDetails.getDetails());
        tvActionBarTitle.setText(activityDetails.getTitle());
    }

    private void initialize() {
        tvActionBarTitle = (TextView) findViewById(R.id.action_bar_title);
        tvActivityDesc = (TextView) findViewById(R.id.tvActivityDesc);
        tvDetailsDesc = (TextView) findViewById(R.id.tvDetailsDesc);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnStart = (Button) findViewById(R.id.btnStart);

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
                    case "Flanker Test":
                        startActivity(new Intent(this,FlankerActivity.class));
                        break;

                }
                break;
            default:
                break;
        }
    }

}
