package ser593.com.epilepsy.UserTasks;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;

import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.Results.ResultActivity;

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
    AnimatorSet set;
    Animator[] anim;
    private static int pattern_id = 0;
    private long timeForOneTask = 0;
    private long startPerTask = 0;
    private long endPerTask = 0;
    private long timeTakenToComplete = 0;
    private long startTime = 0;
    private long endTime = 0;
    //To implement delays after a button light
    LovelyProgressDialog progressDialog;

    private JSONObject recoJsonObject = null;
    private JSONArray answerJsonArray = null;
    private OrientationEventListener mOrientationEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spatial_span);

        getSupportActionBar().hide();

        //Initialize the default values for the activity
        initialize();


//        mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
//            @Override
//            public void onOrientationChanged(int orientation) {
//                if(orientation >= 240 && orientation <= 280 ){
//                    progressDialog.dismiss();
//                }else{
//                    progressDialog.setTitle("Change the Phone orientation to Lanscape").show();
//                }
//
//            }
//        };
//        if (mOrientationEventListener.canDetectOrientation() == true) {
//            Log.v(TAG, "Can detect orientation");
//            mOrientationEventListener.enable();
//        } else {
//            Log.v(TAG, "Cannot detect orientation");
//            mOrientationEventListener.disable();
//        }

        //putting listener on the start button
        btnStartSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //driver Function which will drive the module
                try {
                    startTime = System.currentTimeMillis();
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
            case 2:
                //lowest difficulty
                currentPattern = randomGenerator(2);
                lightThemUp(currentPattern);
                break;
            case 3:
                //Average difficulty
                currentPattern = randomGenerator(3);
                lightThemUp(currentPattern);
                break;
            case 4:
                //Above average difficulty
                currentPattern = randomGenerator(4);
                lightThemUp(currentPattern);
                break;
            case 5:
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

        set = null;
        anim = null;
        set = new AnimatorSet();
        anim = new Animator[numbers.length];

        for (int i = 0; i < numbers.length; i++) {
            Log.d(TAG, "lightThemUp: " + i + "th execution" );
            Log.d(TAG, "lightThemUp: " + anim[i] );
            anim[i] = AnimatorInflater.loadAnimator(this,R.animator.flip);
            anim[i].setTarget(btnGrid[numbers[i]]);
        }
        set.playSequentially(anim);
        set.start();

        pattern_id++;

        disableAllButtons();
        anim[anim.length - 1].addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                enableAllButtons();
                startPerTask = System.currentTimeMillis();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void disableAllButtons(){
        for(Button b:btnGrid){
            b.setEnabled(false);
        }
    }

    private void enableAllButtons(){
        for(Button b:btnGrid){
            b.setEnabled(true);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    private void initialize() {

        tvScore = (TextView) findViewById(R.id.tvScore);
        tvLives = (TextView) findViewById(R.id.tvLivesCount);
        tvDifficulty = (TextView) findViewById(R.id.tvDifficulty);
        btnStartSS = (Button) findViewById(R.id.btnStartSS);
        tvScore.setText("0");
        tvDifficulty.setText("2");
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
//            b.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_selector));
        }
        parent = (LinearLayout) findViewById(R.id.activity_spatial_span);
        progressDialog = new LovelyProgressDialog(this)
                .setTopColorRes(R.color.teal);
        //set default values
        answerJsonArray = new JSONArray();
        recoJsonObject = new JSONObject();

        try
        {
            recoJsonObject.put(getString(R.string.task), getString(R.string.spatial_span));
        }
        catch (JSONException e)
        {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mOrientationEventListener.disable();

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: " + v.getId());
        if(currentPattern != null){
            JSONObject ans = new JSONObject();
            if (currentNumber<currentPattern.length && v.getId() == btnGrid[currentPattern[currentNumber]].getId()){
                Log.d(TAG, "onClick: "+ currentPattern[currentNumber] + " was clicked");
                currentNumber++;
                if(currentPattern.length == currentNumber){
                    int score = Integer.parseInt(tvScore.getText().toString());
                    if(score >= 3){
                        Log.d(TAG, "onClick: activity completed");
                        progressDialog.setTitle("Awesome, You are done").show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                progressDialog.dismiss();
                                try{
                                    endTime = System.currentTimeMillis();
                                    timeTakenToComplete = endTime - startTime;
                                    recoJsonObject.put("timeTakenToComplete", timeTakenToComplete);
                                    recoJsonObject.put(getString(R.string.task_answer), answerJsonArray);
                                }catch (JSONException e)
                                {
                                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                                }
                                // pass solution to result page
                                Intent intent = new Intent(SpatialSpanActivity.this, ResultActivity.class);
                                intent.putExtra(getString(R.string.task_result), recoJsonObject.toString());
                                intent.putExtra("activityInstanceID",getIntent().getExtras().getString("activityInstanceID"));
                                startActivity(intent);
                                finish();
                            }
                        }, 2000);
                    }else{
                        try {
                            endPerTask = System.currentTimeMillis();
                            timeForOneTask = endPerTask - startPerTask;
                            ans.put("timeTaken",timeForOneTask);

                            ans.put("pattern_id",pattern_id);
                            ans.put("difficulty",tvDifficulty.getText().toString());
                            ans.put("user_answer",true);
                            answerJsonArray.put(ans);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

                }
            }else{
                currentNumber = 0;
                if(Integer.parseInt(tvDifficulty.getText().toString()) != 0)

                try {
                    endPerTask = System.currentTimeMillis();
                    timeForOneTask = endPerTask - startPerTask;
                    ans.put("timeTaken",timeForOneTask);
                    ans.put("pattern_id",pattern_id);
                    ans.put("difficulty",tvDifficulty.getText().toString());
                    ans.put("user_answer",false);
                    answerJsonArray.put(ans);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Decrease difficulty and make sure it does not go below 2
                int dif = Integer.parseInt(tvDifficulty.getText().toString());
                if(dif >2)
                    dif = dif - 1;
                else
                    dif = 2;

                tvDifficulty.setText(dif +"");
                if(Integer.parseInt(tvLives.getText().toString()) != 0){
                    tvLives.setText((Integer.parseInt(tvLives.getText().toString()))-1+"");
                    progressDialog.setTitle("Wrong answer, Let's try again").show();

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
                }else{
                    new LovelyStandardDialog(this)
                            .setTopColorRes(R.color.indigo)
                            .setButtonsColorRes(R.color.darkDeepOrange)
                            .setTitle("Sorry")
                            .setMessage("You are out of lives")
                            .setPositiveButton("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try{
                                        endTime = System.currentTimeMillis();
                                        timeTakenToComplete = endTime - startTime;
                                        recoJsonObject.put("timeTakenToComplete", timeTakenToComplete);

                                        recoJsonObject.put(getString(R.string.task_answer), answerJsonArray);
                                    }catch (JSONException e)
                                    {
                                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                                    }
                                    // pass solution to result page

                                    Intent intent = new Intent(SpatialSpanActivity.this, ResultActivity.class);
                                    intent.putExtra(getString(R.string.task_result), recoJsonObject.toString());
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show();
                }
            }
        }else{
            Snackbar.make(parent,"Please click the start button to begin",Snackbar.LENGTH_LONG).show();
        }


    }
}
