package ser593.com.epilepsy.Main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

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

        //show stored shared preference value on screen
        SharedPreferences sharedPref = getSharedPreferences("MySharedPreference", Context.MODE_PRIVATE);
        String pinPref = sharedPref.getString("patientPin", "patientPin");
        String urlPref = sharedPref.getString("url", "url");
        EditText txtPatientPin = (EditText)findViewById(R.id.txtPatientPin);
        EditText txtUrl = (EditText)findViewById(R.id.txtUrl);
        txtPatientPin.setText(pinPref);
        txtUrl.setText(urlPref);
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        Intent i = new Intent(this, MainActivity.class);
        switch (v.getId()){
            case R.id.btnSave:
                EditText txtPatientPin = (EditText)findViewById(R.id.txtPatientPin);
                EditText txtUrl = (EditText)findViewById(R.id.txtUrl);
                String patientPin = txtPatientPin.getText().toString();
                String url = txtUrl.getText().toString();


                SharedPreferences sharedPref = getSharedPreferences("MySharedPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("patientPin", patientPin);
                editor.putString("url", url);
                editor.commit();
                break;
        }
        startActivity(i);
    }
}
