package ser593.com.epilepsy.UserTasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.Results.ResultActivity;

/**
 * References:
 *  ImageButtonListener: https://www.mkyong.com/android/android-imagebutton-example/
 *  */

public class PatternComparisonProcessingActivity extends AppCompatActivity {
    String LOG_TAG = PatternComparisonProcessingActivity.class.getSimpleName();
    private NotificationReceiver nReceiver;

    final List<List<Integer>> imageCollection = getImageCollection();
    ImageView ivLeft;
    ImageView ivRight;
    ImageView ivCenter;

    final int pauseTime = 5000;
    final int numQuestions = 5; //total number of test before showing result
    int questionIndex;
    boolean correctAnswer = true; //the correct answer for the question (true:yes, false:no)
    long questionStartTime;
    long startTime;
    long endTime;
    String pattern;
    JSONObject record;
    JSONArray answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_comparison_processing);

        getSupportActionBar().hide();

        ivLeft = (ImageView)findViewById(R.id.ivLeft);
        ivRight = (ImageView)findViewById(R.id.ivRight);
        ivCenter = (ImageView)findViewById(R.id.ivCenter);
        record = new JSONObject();
        answers = new JSONArray();
        questionIndex = 0;

        try
        {
            record.put(getString(R.string.task), getString(R.string.task_pattern_comparison_processing));
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
        }

        addListenerOnYesButton();
        addListenerOnNoButton();
        showQuestion();

        // create notification listener
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ser593.com.epilepsy.NOTIFICATION_LISTENER");
        registerReceiver(nReceiver,filter);
        Log.i(LOG_TAG,"Notification Listener Created");

        startTime = System.currentTimeMillis();
    }

    private void addListenerOnYesButton() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.ibYes);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                receiveInput(true);
            }
        });
    }

    private void addListenerOnNoButton() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.ibNo);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                receiveInput(false);
            }
        });
    }

    public void receiveInput(boolean input)
    {
        correctAnswer = compareImages();
        boolean userAnswer;
        if (correctAnswer==input)
        {
            userAnswer = true;
            Toast.makeText(getApplicationContext(), getString(R.string.answer_correct), Toast.LENGTH_SHORT).show();
        }
        else
        {
            userAnswer = false;
            Toast.makeText(getApplicationContext(), getString(R.string.answer_incorrect), Toast.LENGTH_SHORT).show();
        }
        try {
            JSONObject ans = new JSONObject();
            ans.put("pattern", pattern);
            ans.put(getString(R.string.pattern_comparison_processing_json_question_index), questionIndex);
            ans.put(getString(R.string.pattern_comparison_processing_json_correct), userAnswer);
            ans.put(getString(R.string.pattern_comparison_processing_json_elapsed_time), System.currentTimeMillis()-questionStartTime);
            answers.put(ans);
        }catch (JSONException e)
        {
            Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
        }

        if (questionIndex < numQuestions)
            showQuestion();
        else
        {
            Toast.makeText(getApplicationContext(), "Test complete, redirect to result page", Toast.LENGTH_SHORT).show();

            try{
                endTime = System.currentTimeMillis();
                record.put("elapseTime", endTime-startTime);
                record.put(getString(R.string.task_answer), answers);
            }catch (JSONException e)
            {
                Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
            }
            // pass solution to result page
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra(getString(R.string.task_result), record.toString());
            intent.putExtra("activityInstanceID",getIntent().getExtras().getString("activityInstanceID"));

            startActivity(intent);
            finish();
        }
    }

    private void showQuestion()
    {
        //Reset UI
        ivLeft.setImageResource(0);
        ivRight.setImageResource(0);
        ivCenter.setImageResource(R.drawable.ic_star);
        findViewById(R.id.ibYes).setEnabled(false);
        findViewById(R.id.ibNo).setEnabled(false);

        // pause for 2 seconds before showing the images for comparison
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                int i = rand.nextInt(imageCollection.size());
                int j1 = rand.nextInt(2);
                int j2 = rand.nextInt(2);
                pattern = String.format("%d%d", j1, j2);
                ivLeft.setImageResource(imageCollection.get(i).get(j1));
                ivRight.setImageResource(imageCollection.get(i).get(j2));
                ivCenter.setImageResource(0); //clear the star in the center
                findViewById(R.id.ibYes).setEnabled(true);
                findViewById(R.id.ibNo).setEnabled(true);
                questionStartTime = System.currentTimeMillis();
                questionIndex++;
            }
        }, pauseTime);
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

    private boolean compareImages()
    {
        if (ivLeft.getDrawable().getConstantState().equals(ivRight.getDrawable().getConstantState()))
            return true;
        else
            return false;
    }

    private List<List<Integer>>getImageCollection()
    {
        List<List<Integer>> imageCollection = Arrays.asList(
                Arrays.asList(R.drawable.patterncomparison_color1, R.drawable.patterncomparison_color2),
                Arrays.asList(R.drawable.patterncomparison_baseball1, R.drawable.patterncomparison_baseball2),
                Arrays.asList(R.drawable.patterncomparison_flower1, R.drawable.patterncomparison_flower2),
                Arrays.asList(R.drawable.patterncomparison_present1, R.drawable.patterncomparison_present2)
        );
        return imageCollection;
    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event");
            //Toast.makeText(getApplicationContext(), "Please be aware that responding to the notification will invalid the test result.", Toast.LENGTH_SHORT).show();
            new LovelyStandardDialog(context)
                .setTopColorRes(R.color.indigo)
                .setButtonsColorRes(R.color.darkDeepOrange)
                .setTitle("Warning")
                .setMessage("Responding to the notification will make the test result invalid.")
                .setNeutralButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
        }
    }
}