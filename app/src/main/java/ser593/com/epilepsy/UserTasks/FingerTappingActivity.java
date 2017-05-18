package ser593.com.epilepsy.UserTasks;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.Results.EndActivity;
import ser593.com.epilepsy.Results.ResultActivity;

public class FingerTappingActivity extends AppCompatActivity {
    String LOG_TAG = FingerTappingActivity.class.getSimpleName();
    int currentTestSide; //0-left, 1-right
    boolean currentTapSide; //false-left, true-right
    boolean start = false;
    int count;
    long time = 5;
    long startTime;
    long endTime;
    TextView tvSide;
    TextView tvHint;
    TextView tvCount;
    TextView tvTimer;
    JSONObject record;
    JSONArray answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_tapping);

        getSupportActionBar().hide();

        tvSide = (TextView)findViewById(R.id.tvSide);
        tvHint = (TextView)findViewById(R.id.tvHint);
        tvCount = (TextView)findViewById(R.id.tvCount);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        record = new JSONObject();
        answers = new JSONArray();

        try {
            record.put(getString(R.string.task), getString(R.string.task_finger_tapping));
            record.put(getString(R.string.finger_tapping_json_timer_length), time);
        }catch(JSONException e)
        {
            Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
        }

        addListenerOnLeftButton();
        addListenerOnRightButton(); //double check the bool value

        Random rand = new Random();
        currentTestSide = rand.nextInt(2);
        tvSide.setText(currentTestSide == 0 ? getString(R.string.finger_tapping_left) : getString(R.string.finger_tapping_right));
        tvHint.setText(getString(R.string.finger_tapping_go));
        tvTimer.setText(Long.toString(time));
        count = 0;
        start = false;
        startTime = System.currentTimeMillis();
    }

    private void addListenerOnLeftButton() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.ibTapLeft);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                receiveInput(true);
            }
        });
    }

    private void addListenerOnRightButton() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.ibTapRight);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                receiveInput(false);
            }
        });
    }

    public void receiveInput(boolean input)
    {
        if(!start) // handles the very first tap
        {
            start = true;
            count++;
            currentTapSide = input;
            UpdateCounter();
            TimerClass tc = new TimerClass(time*1000, 1000);
            tc.start();
        }
        else
        {
            if(input != currentTapSide) //means alternating side, this tap will count
            {
                currentTapSide = input;
                count++;
                UpdateCounter();
            }
        }
    }

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

    public void UpdateCounter()
    {
        tvCount.setText(Integer.toString(count));
    }

    public class TimerClass extends CountDownTimer
    {
        public TimerClass(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            tvTimer.setText(String.format("%d", TimeUnit.MILLISECONDS.toSeconds(millis)));
        }

        @Override
        public void onFinish() {
            tvTimer.setText(Integer.toString(0));

            //add record to answer
            String side = currentTestSide == 0 ? getString(R.string.finger_tapping_left) : getString(R.string.finger_tapping_right);
            try {
                JSONObject ans = new JSONObject();
                ans.put(getString(R.string.finger_tapping_json_side), side);
                ans.put(getString(R.string.finger_tapping_json_tap_count), count);
                answers.put(ans); //add to JSONArray

                //if answer has 2 records: go to result page
                if (answers.length() == 2) {
                    endTime = System.currentTimeMillis();
                    record.put("elapseTime", endTime-startTime);
                    // pass solution to result page
                    record.put(getString(R.string.task_answer), answers);
                    //Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    Intent intent = new Intent(getApplicationContext(), EndActivity.class);
                    intent.putExtra(getString(R.string.task_result), record.toString());
                    intent.putExtra("activityInstanceID",getIntent().getExtras().getString("activityInstanceID"));
                    startActivity(intent);
                    finish();
                } else //else start another round with different hand
                {
                    tvHint.setText(getString(R.string.finger_tapping_wait));
                    // pause for 2 seconds before showing the images for comparison
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentTestSide = currentTestSide == 0 ? 1 : 0;
                            tvSide.setText(currentTestSide == 0 ? getString(R.string.finger_tapping_left) : getString(R.string.finger_tapping_right));
                            tvHint.setText(getString(R.string.finger_tapping_go));
                            tvTimer.setText(Long.toString(time));
                            count = 0;
                            start = false;
                        }
                    }, 2000);
                }
            }
            catch (JSONException e)
            {
                Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
            }
        }
    }
}
