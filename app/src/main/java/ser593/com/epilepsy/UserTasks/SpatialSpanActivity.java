package ser593.com.epilepsy.UserTasks;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import ser593.com.epilepsy.R;

public class SpatialSpanActivity extends AppCompatActivity {

    private static final String TAG = SpatialSpanActivity.class.getSimpleName();
    TextView tvScore = null;
    TextView tvLives = null;
    TextView tvDifficulty = null;
    Button btnStartSS = null;
    Button[] btnGrid = null;

    //To implement delays after a button light
    Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spatial_span);

        //Initialize the default values for the activity
        initialize();

        //putting listener on the start button
        btnStartSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //driver Function which will drive the module
                try {
                    spacialSpanDriver();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int[] randomGenerator(int limit) {
        Random rand = new Random();
        int[] numbers = new int[limit];
        for (int i = 0; i < limit; i++) {
            numbers[i] = rand.nextInt(24);
        }
        return numbers;
    }

    private void spacialSpanDriver() throws InterruptedException {

        int difficulty = Integer.parseInt(tvDifficulty.getText().toString());
        switch (difficulty) {
            case 0:
                //lowest difficulty
                lightThemUp(randomGenerator(3));
                break;
            case 1:
                //Average difficulty
                break;
            case 2:
                //Above average difficulty
                break;
            case 3:
                //Highest difficulty
                break;
        }
    }

    private synchronized void lightThemUp(final int[] numbers) throws InterruptedException {

        //int length = numbers.length;
        for (int i : numbers) {
            Log.d(TAG, "lightThemUp: Numbers: - " + i);
            //Log.d(TAG, "lightThemUp: Numbers: - " + i);
        }

        for (int i = 0; i < numbers.length; i++) {
            synchronized (this){
                lightTheButton(i,Color.BLUE);
                lightTheButton(i,Color.GRAY);


            }

        }
    }

    private synchronized void lightTheButton(final int i, final int blue) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "button Id: " + i );
                btnGrid[i].setBackgroundColor(blue);
            }
        },2000);
    }


    private void initialize() {
        tvScore = (TextView) findViewById(R.id.tvScore);
        tvLives = (TextView) findViewById(R.id.tvLivesCount);
        tvDifficulty = (TextView) findViewById(R.id.tvDifficulty);
        btnStartSS = (Button) findViewById(R.id.btnStartSS);
        tvScore.setText("0");
        tvDifficulty.setText("0");
        tvLives.setText("3");
        btnGrid = new Button[25];
        btnGrid[0] = (Button) findViewById(R.id.btn0);
        btnGrid[1] = (Button) findViewById(R.id.btn1);
        btnGrid[2] = (Button) findViewById(R.id.btn2);
        btnGrid[3] = (Button) findViewById(R.id.btn3);
        btnGrid[4] = (Button) findViewById(R.id.btn4);
        btnGrid[5] = (Button) findViewById(R.id.btn5);
        btnGrid[6] = (Button) findViewById(R.id.btn6);
        btnGrid[7] = (Button) findViewById(R.id.btn7);
        btnGrid[8] = (Button) findViewById(R.id.btn8);
        btnGrid[9] = (Button) findViewById(R.id.btn9);
        btnGrid[10] = (Button) findViewById(R.id.btn10);
        btnGrid[11] = (Button) findViewById(R.id.btn11);
        btnGrid[12] = (Button) findViewById(R.id.btn12);
        btnGrid[13] = (Button) findViewById(R.id.btn13);
        btnGrid[14] = (Button) findViewById(R.id.btn14);
        btnGrid[15] = (Button) findViewById(R.id.btn15);
        btnGrid[16] = (Button) findViewById(R.id.btn16);
        btnGrid[17] = (Button) findViewById(R.id.btn17);
        btnGrid[18] = (Button) findViewById(R.id.btn18);
        btnGrid[19] = (Button) findViewById(R.id.btn19);
        btnGrid[20] = (Button) findViewById(R.id.btn20);
        btnGrid[21] = (Button) findViewById(R.id.btn21);
        btnGrid[22] = (Button) findViewById(R.id.btn22);
        btnGrid[23] = (Button) findViewById(R.id.btn23);
        btnGrid[24] = (Button) findViewById(R.id.btn24);

        handler = new Handler();
    }
}
