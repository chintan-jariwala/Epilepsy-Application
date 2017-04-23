package ser593.com.epilepsy.UserTasks;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ser593.com.epilepsy.R;
import ser593.com.epilepsy.Results.ResultActivity;

public class FlankerActivity extends AppCompatActivity {
    String LOG_TAG = FlankerActivity.class.getSimpleName();
    final List<Integer> imageCollection = getImageCollection(); //list containing left and right arrow
    ImageView ivTarget;
    ImageView iv1;
    ImageView iv2;
    ImageView iv3;
    ImageView iv4;
    ImageView ivHid;

    final int pauseTime = 5000;
    final int numQuestions = 5; //total number of test before showing result
    int questionIndex;
    boolean correctAnswer = true; //the correct answer for the question (true:left, false:right)
    long questionStartTime;
    String pattern;
    JSONObject record;
    JSONArray answers;
    long startTime;
    long endTime;

    LovelyProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flanker);

        getSupportActionBar().hide();

        ivTarget = (ImageView)findViewById(R.id.ivTarget);
        iv1 = (ImageView)findViewById(R.id.iv1);
        iv2 = (ImageView)findViewById(R.id.iv2);
        iv3 = (ImageView)findViewById(R.id.iv3);
        iv4 = (ImageView)findViewById(R.id.iv4);
        ivHid = (ImageView)findViewById(R.id.ivHid);
        progressDialog = new LovelyProgressDialog(this)
                .setTopColorRes(R.color.teal);
        record = new JSONObject();
        answers = new JSONArray();
        questionIndex = 0;

        try
        {
            record.put(getString(R.string.task), getString(R.string.task_flanker));
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
        }

        addListenerOnLeftButton();
        addListenerOnRightButton();
        showQuestion();

        startTime = System.currentTimeMillis();
    }

    private void addListenerOnLeftButton() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.ibLeft);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                receiveInput(true);
            }
        });
    }

    private void addListenerOnRightButton() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.ibRight);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                receiveInput(false);
            }
        });
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

    public void receiveInput(boolean input)
    {
        correctAnswer = getCorrectAnswer();
        boolean userAnswer;
        if (correctAnswer==input)
        {
            userAnswer = true;
            progressDialog.setTitle("Correct answer, Let's solve one more").show();
            //Toast.makeText(getApplicationContext(), getString(R.string.answer_correct), Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, 2000);
        }
        else
        {
            userAnswer = false;
            //Toast.makeText(getApplicationContext(), getString(R.string.answer_incorrect), Toast.LENGTH_SHORT).show();
            progressDialog.setTitle("Wrong answer, Let's try again").show();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, 2000);
        }
        try {
            JSONObject ans = new JSONObject();
            ans.put("pattern", pattern);
            ans.put(getString(R.string.flanker_json_question_index), questionIndex);
            ans.put(getString(R.string.flanker_json_correct), userAnswer);
            ans.put(getString(R.string.flanker_json_elapsed_time), System.currentTimeMillis()-questionStartTime);
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
        iv1.setImageResource(0);
        iv2.setImageResource(0);
        iv3.setImageResource(0);
        iv4.setImageResource(0);
        ivTarget.setImageResource(R.drawable.ic_star);
        findViewById(R.id.ibLeft).setEnabled(false);
        findViewById(R.id.ibRight).setEnabled(false);

        // pause for 2 seconds before showing the images for comparison
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                int j1 = rand.nextInt(2);
                int j2 = rand.nextInt(2);
                pattern = String.format("%d%d%d%d%d", j2, j2, j1, j2, j2);
                ivTarget.setImageResource(imageCollection.get(j1));
                iv1.setImageResource(imageCollection.get(j2));
                iv2.setImageResource(imageCollection.get(j2));
                iv3.setImageResource(imageCollection.get(j2));
                iv4.setImageResource(imageCollection.get(j2));
                findViewById(R.id.ibLeft).setEnabled(true);
                findViewById(R.id.ibRight).setEnabled(true);
                questionStartTime = System.currentTimeMillis();
                questionIndex++;
            }
        }, pauseTime);
    }

    private boolean getCorrectAnswer()
    {
        if (ivTarget.getDrawable().getConstantState().equals(ivHid.getDrawable().getConstantState()))
            return true;
        else
            return false;
    }

    private List<Integer>getImageCollection()
    {
        List<Integer> imageCollection = Arrays.asList(
                R.drawable.flanker_leftarrow,
                R.drawable.flanker_rightarrow
        );
        return imageCollection;
    }
}
