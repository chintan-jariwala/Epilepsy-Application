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

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.UserTasks.ActivityDescription;
import ser593.com.epilepsy.app.AppController;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSave = null;
    private EditText txtPatientPin;
    private EditText txtUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        //show stored shared preference value on screen

        String pinPref = AppController.getInstance().readPreference("patientPin");
        if(pinPref == null){
            pinPref = "";
        }
        String urlPref = AppController.getInstance().readPreference("url");
        if(urlPref == null){
            urlPref = "";
        }

        txtPatientPin = (EditText)findViewById(R.id.txtPatientPin);
        txtUrl = (EditText)findViewById(R.id.txtUrl);
        txtPatientPin.setText(pinPref);
        txtUrl.setText(urlPref);
    }

    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        Intent i = new Intent(this, MainActivity.class);
        switch (v.getId()){
            case R.id.btnSave:
                String patientPin = txtPatientPin.getText().toString();
                String url = txtUrl.getText().toString();
                if(validatePatientPin(patientPin) && validateURL(url)){
                    AppController.getInstance().writePreference("patientPin", patientPin);
                    AppController.getInstance().writePreference("url", url);
                    startActivity(i);
                    break;
                }

        }
    }

    private boolean validateURL(String url) {
        if(!url.contains("http") && !url.contains("http:\\\\") && !url.contains("www") && !url.contains("www.") && url.length()>5){
            return  true;
        }else{
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.indigo)
                    .setButtonsColorRes(R.color.darkDeepOrange)
                    .setTitle("Please Enter a valid URL")
                    .setMessage("URL cannot contain \"HTTP\" or \"http\" or \"www\" \n URL cannot be empty")
                    .setPositiveButton("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
            return false;
        }
    }

    private boolean validatePatientPin(String pin){
        if(pin.matches("[0-9]+") && pin.length() == 4 ){
           //PIN is in the correct format
            return true;
        }else{
            //PIN not in the correct format
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.indigo)
                    .setButtonsColorRes(R.color.darkDeepOrange)
                    .setTitle("PIN is in incorrect format")
                    .setMessage("PIN can only contain numbers from 0 to 9 \n PIN has to be 4 numbers")
                    .setPositiveButton("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();
            return false;
        }
    }


}
