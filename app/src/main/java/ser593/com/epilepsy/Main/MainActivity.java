package ser593.com.epilepsy.Main;

import android.content.Intent;
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
    TextView tvActionBarTitle = null;
    ActivityDetails activityDetails = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

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

        activityDetails = new ActivityDetails();
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        Intent i = new Intent(getApplicationContext(),ActivityDescription.class);
        switch (v.getId()){
            case R.id.btnPatternComparison:
                activityDetails.setTitle("Pattern Comparison");
                activityDetails.setBreif("Pattern Comparison activity dfaldjflk;asjdl;fajsdlkfja;sdjf;sadf;l adflkasdjf;lsadjdf;lasj ;sd ;lksa df;lsajd;lfjsadf");
                activityDetails.setDetails("The pattern comparison processing test starts off with a star in the center of the screen and yes/no buttons near the bottom of the screen. After a brief pause, the star will transform into two same or similar images. The patient will have to tap the yes button if the images are the same and no button if the images are different.\n Images used for comparison are read in and stored when the screen initialized. There are two similar images for each completely different image, and the images are stored as a list of lists.");
                b.putParcelable("Class",activityDetails);
                i.putExtras(b);
                startActivity(i);
                break;
            case R.id.btnFingerTapping:
                activityDetails.setTitle("Finger Tapping");
                activityDetails.setBreif("Finger Tapping activity dfaldjflk;asjdl;fajsdlkfja;sdjf;sadf;l adflkasdjf;lsadjdf;lasj ;sd ;lksa df;lsajd;lfjsadf");
                activityDetails.setDetails("The finger tapping task is relatively straightforward. The application will ask the patient to tap the two buttons with two fingers on one hand as fast as he can within the given time, then perform the same task using the other hand.\n The task contains two rounds, one to be completed with right hand and the other with left hand, and it is determined randomly which hand goes first. The countdown will start as soon as the the patient’s first tap. There will be a brief pause between two avoid accidentally starting the next test.");
                b.putParcelable("Class",activityDetails);
                i.putExtras(b);
                startActivity(i);
                break;
            case R.id.btnPromis:
                startActivity(new Intent(this,PromisActivity.class));
                break;
            case R.id.btnSpacialSpan:
                activityDetails.setTitle("Spatial Span");
                activityDetails.setBreif("Spatial Span activity dfaldjflk;asjdl;fajsdlkfja;sdjf;sadf;l adflkasdjf;lsadjdf;lasj ;sd ;lksa df;lsajd;lfjsadf");
                activityDetails.setDetails("The spatial span task starts off with twenty-five buttons (5*5) on the left side of the screen, blue text on the top right  for hint messages, and some current stage info in black text. The task will start once the patient tap the start button, a three-second countdown will be shown in blue text. The application will generate some patterns based on the current difficulty. The pattern will be shown to the patient by blinking the button (turns green then goes back to grey), and the patient will have to repeat the pattern by tapping on the buttons. The difficulty increases if the patient answer the question correctly and decreases if incorrect. The patient will have three chances of making a mistake before ending the task. When the screen is initialized, the twenty five buttons will be added to a list. To generate a pattern, the application will select a number of buttons from the list based on the difficulty and store it in a separate list. For example, if the difficulty is three, the application will select a random item from the button list three times, and add each item to a new list (the testOrder).\n" +
                        "After the countdown, the application will display the order of the new list by blinking the respective button. The hint message will be shown to the patient and allow the patient to begin repeating. When the patient taps the button, each tap will add the button to another list (the userOrder). A counter will be keeping track of the number of buttons the patient taps. \n" +
                        "Once the mentioned counter matches the size of testOrder, the application will compare the testOrder list and the userOrder list. If the two lists matches, the patient repeats the pattern correctly, the score and difficulty will both increment by one. If the patient repeats the pattern incorrectly, lives, score, and difficulty will all decrement by one. The task will end once lives reaches zero.");
                b.putParcelable("Class",activityDetails);
                i.putExtras(b);
                startActivity(i);
                break;
            case R.id.btnFlanker:
                activityDetails.setTitle("Flanker Test");
                activityDetails.setBreif("Flanker Test activity dfaldjflk;asjdl;fajsdlkfja;sdjf;sadf;l adflkasdjf;lsadjdf;lasj ;sd ;lksa df;lsajd;lfjsadf");
                activityDetails.setDetails("The flanker test starts off with a star in the center of the screen and orange left and right arrows near the bottom of the screen. After a brief pause, the star will transform into five black arrows. The patient will have to tap on the orange arrow that is pointing at the same direction as the center black arrow.");
                b.putParcelable("Class",activityDetails);
                i.putExtras(b);
                startActivity(i);
        }
    }
}
