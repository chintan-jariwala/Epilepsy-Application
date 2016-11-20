package ser593.com.epilepsy.Main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.UserTasks.FingerTappingActivity;
import ser593.com.epilepsy.UserTasks.PatternComparisonProcessingActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPatternComparison = null;
    Button btnFingerTapping = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPatternComparison = (Button) findViewById(R.id.btnPatternComparison);
        btnPatternComparison.setOnClickListener(this);
        btnFingerTapping = (Button) findViewById(R.id.btnFingerTapping);
        btnFingerTapping.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPatternComparison:
                startActivity(new Intent(this,PatternComparisonProcessingActivity.class));
                break;
            case R.id.btnFingerTapping:
                startActivity(new Intent(this,FingerTappingActivity.class));
                break;
        }
    }
}
