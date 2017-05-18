package ser593.com.epilepsy.Results;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ser593.com.epilepsy.Main.MainActivity;
import ser593.com.epilepsy.R;
import ser593.com.epilepsy.apiCall.CheckForInternet;
import ser593.com.epilepsy.apiCall.ServiceCall;
import ser593.com.epilepsy.app.AppController;

public class EndActivity extends AppCompatActivity {
    String LOG_TAG = ResultActivity.class.getSimpleName();
    private RelativeLayout resultsLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        //hide action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().hide();

        //use custom font
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/SourceSansPro-Regular.otf");
        TextView tv_heading = (TextView) findViewById(R.id.tv_heading);
        TextView tv_message = (TextView) findViewById(R.id.tv_message);
        TextView tv_praise = (TextView) findViewById(R.id.tv_praise);
        tv_heading.setTypeface(custom_font);
        tv_message.setTypeface(custom_font);
        tv_praise.setTypeface(custom_font);


        resultsLayout = (RelativeLayout) findViewById(R.id.resultsContainerLayout);
        addListenerOnReturnButton();

        Intent intent =this.getIntent();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        //String jsonStr = "{\"task\":\"Finger Tapping\",\"timer_length\":10,\"answer\":[{\"side\":\"left\",\"tap_count\":15},{\"side\":\"right\",\"tap_count\":21}]}";
        //String jsonStr = "{\"task\":\"Pattern Comparison Processing\",\"answer\":[{\"question_index\":1,\"correct\":true,\"elapsed_time\":15},{\"question_index\":2,\"correct\":false,\"elapsed_time\":25},{\"question_index\":3,\"correct\":true,\"elapsed_time\":35},{\"question_index\":4,\"correct\":true,\"elapsed_time\":45},{\"question_index\":5,\"correct\":false,\"elapsed_time\":55}]}";
        String jsonStr = intent.getStringExtra(getString(R.string.task_result));
        Log.d("ResultJSON", jsonStr);
        try {
            //create json obj for api
            JSONObject jsonForApi = new JSONObject();
            jsonForApi.put("activityInstanceID", getIntent().getExtras().getString("activityInstanceID"));
            jsonForApi.put("timeStamp", System.currentTimeMillis());

            JSONObject jsonObj = new JSONObject(jsonStr);
            String task = jsonObj.getString(getString(R.string.task));


            if (task.equals(getString(R.string.task_finger_tapping)))
            {
                JSONArray answers = jsonObj.getJSONArray(getString(R.string.task_answer));
                JSONArray answersArr = new JSONArray();

                // Loop through the list and add to json object (for push to api)
                for(int i = 0; i < answers.length(); i++)
                {
                    //add to answersArr
                    JSONObject ans = new JSONObject();
                    ans.put("operatingHand", answers.getJSONObject(i).getString(getString(R.string.finger_tapping_json_side)).toLowerCase());
                    ans.put("tapNumber", Integer.parseInt(answers.getJSONObject(i).getString(getString(R.string.finger_tapping_json_tap_count))));
                    answersArr.put(ans);
                }

                //continue building json obj for api
                JSONArray activityResultsArray = new JSONArray();
                JSONObject activityResults = new JSONObject();
                activityResults.put("activityBlockId", "FINGERTAPPING");
                activityResults.put("timeToTap", Integer.parseInt(jsonObj.getString("timer_length")));
                activityResults.put("screenWidth", width);
                activityResults.put("screenHeight", height);
                activityResults.put("timeTakenToComplete", Integer.parseInt(jsonObj.get("elapseTime").toString()));
                activityResults.put("answers", answersArr);
                activityResultsArray.put(activityResults);
                jsonForApi.put("activityResults", activityResultsArray);

                Log.d(LOG_TAG, jsonForApi.toString());
            }
            else if (task.equals(getString(R.string.task_flanker))) //flanker
            {
                int numAnswer = jsonObj.getJSONArray(getString(R.string.task_answer)).length();

                // Loop through the list and add to json object (for push to api)
                JSONArray answers = jsonObj.getJSONArray(getString(R.string.task_answer));
                JSONArray answersArr = new JSONArray();
                for(int i = 0; i < numAnswer; i++)//for(Answer ans: record)
                {
                    //add to answersArr
                    JSONObject ans = new JSONObject();
                    ans.put("pattern", answers.getJSONObject(i).getString("pattern"));
                    ans.put("result", answers.getJSONObject(i).getBoolean("correct"));
                    ans.put("timeTaken", answers.getJSONObject(i).getInt("elapsed_time"));
                    ans.put("questionIndex", i+1);
                    answersArr.put(ans);
                }

                //continue building json obj for api
                JSONArray activityResultsArray = new JSONArray();
                JSONObject activityResults = new JSONObject();
                activityResults.put("activityBlockId", "FLANKER");
                activityResults.put("screenWidth", width);
                activityResults.put("screenHeight", height);
                activityResults.put("timeTakenToComplete", Integer.parseInt(jsonObj.get("elapseTime").toString()));
                activityResults.put("answers", answersArr);
                activityResultsArray.put(activityResults);
                jsonForApi.put("activityResults", activityResultsArray);

                Log.d(LOG_TAG, jsonForApi.toString());
            }
            else if (task.equals(getString(R.string.task_pattern_comparison_processing))) //pattern comparison
            {
                int numAnswer = jsonObj.getJSONArray(getString(R.string.task_answer)).length();

                // Loop through the list and to json object (for push to api)
                JSONArray answers = jsonObj.getJSONArray(getString(R.string.task_answer));
                JSONArray answersArr = new JSONArray();
                for(int i = 0; i < numAnswer; i++)//for(Answer ans: record)
                {
                    //add to answersArr
                    JSONObject ans = new JSONObject();
                    ans.put("pattern", answers.getJSONObject(i).getString("pattern"));
                    ans.put("result", answers.getJSONObject(i).getBoolean("correct"));
                    ans.put("timeTaken", answers.getJSONObject(i).getInt("elapsed_time"));
                    ans.put("questionIndex", i+1);
                    answersArr.put(ans);
                }

                //continue building json obj for api
                JSONArray activityResultsArray = new JSONArray();
                JSONObject activityResults = new JSONObject();
                activityResults.put("activityBlockId", "PATTERNCOMPARISON");
                activityResults.put("screenWidth", width);
                activityResults.put("screenHeight", height);
                activityResults.put("timeTakenToComplete", Integer.parseInt(jsonObj.get("elapseTime").toString()));
                activityResults.put("answers", answersArr);
                activityResultsArray.put(activityResults);
                jsonForApi.put("activityResults", activityResultsArray);

                Log.d(LOG_TAG, jsonForApi.toString());
            }
            else if(task.equals(getString(R.string.spatial_span))){

                int numAnswer = jsonObj.getJSONArray(getString(R.string.task_answer)).length();
                int correct = 0;

                // Loop through the list and add row to table
                JSONArray answers = jsonObj.getJSONArray(getString(R.string.task_answer));
                JSONArray answersArr = new JSONArray();

                for(int i = 0; i < numAnswer; i++)//for(Answer ans: record)
                {
                    JSONObject ans = new JSONObject();
                    ans.put("questionIndex", Integer.parseInt(answers.getJSONObject(i).getString("pattern_id")));
                    ans.put("result", answers.getJSONObject(i).getBoolean("user_answer"));
                    ans.put("timeTaken", Integer.parseInt(answers.getJSONObject(i).getString("timeTaken")));
                    ans.put("difficulty", Integer.parseInt(answers.getJSONObject(i).getString("difficulty")));
                    answersArr.put(ans);
                }

                //continue building json obj for api
                JSONArray activityResultsArray = new JSONArray();
                JSONObject activityResults = new JSONObject();
                activityResults.put("activityBlockId", "SPATIALSPAN");
                activityResults.put("screenWidth", width);
                activityResults.put("screenHeight", height);
                activityResults.put("timeTakenToComplete", Integer.parseInt(jsonObj.get("timeTakenToComplete").toString()));
                activityResults.put("answers", answersArr);
                activityResultsArray.put(activityResults);
                jsonForApi.put("activityResults", activityResultsArray);

                Log.d(LOG_TAG, jsonForApi.toString());

            }

            if(CheckForInternet.isNetworkAvailable(this)){
                ServiceCall serviceCall = new ServiceCall(getApplicationContext(),resultsLayout);
                serviceCall.submitActivityInstance(jsonForApi);
            }else{
                //Alert User to get internet
                new LovelyStandardDialog(this)
                        .setTopColorRes(R.color.indigo)
                        .setButtonsColorRes(R.color.darkDeepOrange)
                        .setTitle("No Network")
                        .setMessage("Your results have been recorded successfully.\n\nThe application will try to submit the results when the wifi will be made available.")
                        .setPositiveButton("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        })
                        .show();
                String pendingSurveys = AppController.getInstance().readPreference("pendingSurveys");
                ArrayList<String> pendingSurveysList = new ArrayList<>();
                if(pendingSurveys == null){
                    pendingSurveysList.add(jsonForApi.toString());
                    AppController.getInstance().writePreference("pendingSurveys",pendingSurveysList.toString());
                    Log.d(LOG_TAG, "onCreate: " + pendingSurveysList.toString());
                }else{
                    JSONArray arr = new JSONArray(pendingSurveys);
                    JSONObject obj;
                    for(int i=0; i<arr.length();i++){
                        obj = (JSONObject) arr.get(i);
                        pendingSurveysList.add(obj.toString());
                    }
                    pendingSurveysList.add(jsonForApi.toString());
                    AppController.getInstance().writePreference("pendingSurveys",pendingSurveysList.toString());
                }

            }
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
        }
    }

    private void addListenerOnReturnButton() {
        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
