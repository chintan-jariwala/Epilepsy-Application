package ser593.com.epilepsy.Main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.UserTasks.ActivityDescription;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnSave = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        Intent i = new Intent(getApplicationContext(),ActivityDescription.class);
        switch (v.getId()){
            case R.id.btnSave:
                break;
        }
    }
}
