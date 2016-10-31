package ser593.com.epilepsy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPatternComparision = null;
    Button btnFingerTapping = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPatternComparision = (Button) findViewById(R.id.btnPatternComparision);
        btnPatternComparision.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPatternComparision:
                startActivity(new Intent(this,PatternComparisonProcessingActivity.class));
                break;
        }
    }
}
