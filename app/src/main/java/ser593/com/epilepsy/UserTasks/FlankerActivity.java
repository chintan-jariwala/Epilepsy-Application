package ser593.com.epilepsy.UserTasks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.Results.ResultActivity;

public class FlankerActivity extends AppCompatActivity {
    String LOG_TAG = PatternComparisonProcessingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flanker);
    }
}