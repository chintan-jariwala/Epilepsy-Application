package ser593.com.epilepsy.Main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.UserTasks.ActivityDescription;
import ser593.com.epilepsy.UserTasks.FingerTappingActivity;
import ser593.com.epilepsy.UserTasks.FlankerActivity;
import ser593.com.epilepsy.UserTasks.PatternComparisonProcessingActivity;
import ser593.com.epilepsy.UserTasks.SpatialSpanActivity;
import ser593.com.epilepsy.painReport.PromisActivity;
import ser593.com.epilepsy.pojo.ActivityDetails;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPatternComparison = null;
    Button btnFingerTapping = null;
    Button btnPromis = null;
    Button btnSpacialSpan = null;
    Button btnFlanker = null;
    Button btnSettings = null;
    TextView tvActionBarTitle = null;
    ActivityDetails activityDetails = null;
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
                startActivity(i);
                break;
            case R.id.btnSettings:
                Intent in = new Intent(this, SettingsActivity.class);
                startActivity(in);
                break;
        }
    }
}
