package ser593.com.epilepsy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ser593.com.epilepsy.Main.MainActivity;

/**
 * Created by chint on 10/31/2016.
 */

public class SplashActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
