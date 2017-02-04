package ser593.com.epilepsy.UserTasks;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.Arrays;
import java.util.Random;

import ser593.com.epilepsy.R;

public class SpatialSpanActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = SpatialSpanActivity.class.getSimpleName();
    TextView tvScore = null;
    TextView tvLives = null;
    TextView tvDifficulty = null;
    Button btnStartSS = null;
    Button[] btnGrid = null;
    int currentPattern[] = null;
    private static int currentNumber = 0;
    LinearLayout parent = null;
    //To implement delays after a button light
    LovelyProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spatial_span);

        getSupportActionBar().hide();

        //Initialize the default values for the activity
        initialize();

        //putting listener on the start button
        btnStartSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //driver Function which will drive the module
                try {
                    spacialSpanDriver();
                    btnStartSS.setEnabled(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.indigo)
                .setButtonsColorRes(R.color.darkDeepOrange)
                .setTitle("Are you sure?")
                .setMessage("Your progress will be lost")
                .setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setNegativeButton("No", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
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
                currentPattern = randomGenerator(2);
                lightThemUp(currentPattern);
                break;
            case 1:
                //Average difficulty
                currentPattern = randomGenerator(3);
                lightThemUp(currentPattern);
                break;
            case 2:
                //Above average difficulty
                currentPattern = randomGenerator(4);
                lightThemUp(currentPattern);
                break;
            case 3:
                //Highest difficulty
                currentPattern = randomGenerator(5);
                lightThemUp(currentPattern);
                break;
        }
    }

    private synchronized void lightThemUp(final int[] numbers) throws InterruptedException {

        //int length = numbers.length;
        for (int i : numbers) {
            Log.d(TAG, "lightThemUp: Numbers: - " + i);
            //Log.d(TAG, "lightThemUp: Numbers: - " + i);
        }
        AnimatorSet set = null;
        Animator anim[] = null;
        set = new AnimatorSet();
        anim = new Animator[numbers.length];
//        Snackbar.make(parent, Arrays.toString(currentPattern),Snackbar.LENGTH_LONG).show();
        for (int i = 0; i < numbers.length; i++) {
            Log.d(TAG, "lightThemUp: " + i + "th execution" );
            anim[i] = AnimatorInflater.loadAnimator(this,R.animator.flip);
            anim[i].setTarget(btnGrid[numbers[i]]);
        }
        set.playSequentially(anim);
        set.start();
    }

//    private void lightTheButton(Button b) {
//
//    }

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

        for(Button b: btnGrid){
            b.setOnClickListener(this);
        }
        parent = (LinearLayout) findViewById(R.id.activity_spatial_span);
        progressDialog = new LovelyProgressDialog(this)
                .setTopColorRes(R.color.teal);
        //set default values
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: " + v.getId());

        if (currentNumber<currentPattern.length && v.getId() == btnGrid[currentPattern[currentNumber]].getId()){
            Log.d(TAG, "onClick: "+ currentPattern[currentNumber] + " was clicked");
            currentNumber++;
            if(currentPattern.length == currentNumber){
                currentNumber = 0;
                progressDialog.setTitle("Great, Lets solve one more").show();
                tvDifficulty.setText(((Integer.parseInt(tvDifficulty.getText().toString()))+1)+"");
                tvScore.setText(((Integer.parseInt(tvScore.getText().toString()))+1)+"");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        progressDialog.dismiss();
                        try {
                            spacialSpanDriver();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, 2000);

            }
        }else{
            currentNumber = 0;
            if(Integer.parseInt(tvDifficulty.getText().toString()) != 0)
                tvDifficulty.setText(((Integer.parseInt(tvDifficulty.getText().toString()))-1)+"");

            if(Integer.parseInt(tvLives.getText().toString()) != 0){
                tvLives.setText((Integer.parseInt(tvLives.getText().toString()))-1+"");
                new LovelyStandardDialog(this)
                        .setTopColorRes(R.color.indigo)
                        .setButtonsColorRes(R.color.darkDeepOrange)
                        .setTitle("Sorry")
                        .setMessage("You lost.")
                        .setPositiveButton("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    spacialSpanDriver();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Exit", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        })
                        .show();
            }

            else{
                new LovelyStandardDialog(this)
                        .setTopColorRes(R.color.indigo)
                        .setButtonsColorRes(R.color.darkDeepOrange)
                        .setTitle("Sorry")
                        .setMessage("You are out of lives")
                        .setPositiveButton("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        })
                        .show();
            }
        }

    }
}
