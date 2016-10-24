package ser593.com.epilepsy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * References:
 *  ImageButtonListener: https://www.mkyong.com/android/android-imagebutton-example/
 *  */

public class PatternComparisonProcessingActivity extends AppCompatActivity {
    String LOG_TAG = PatternComparisonProcessingActivity.class.getSimpleName();
    final List<List<Integer>> imageCollection = getImageCollection();
    ImageView ivLeft;
    ImageView ivRight;
    ImageView ivCenter;

    final int numQuestions = 5; //total number of test before showing result
    int questionIndex;
    boolean correctAnswer = true; //the correct answer for the question (true:yes, false:no)
    long questionStartTime;
    ArrayList answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_comparison_processing);

        ivLeft = (ImageView)findViewById(R.id.ivLeft);
        ivRight = (ImageView)findViewById(R.id.ivRight);
        ivCenter = (ImageView)findViewById(R.id.ivCenter);
        answers = new ArrayList();
        questionIndex = 0;

        addListenerOnYesButton();
        addListenerOnNoButton();
        showQuestion();
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
        int userAnswer;
        if (correctAnswer==input)
        {
            userAnswer = 1;
            Toast.makeText(getApplicationContext(), getString(R.string.answer_correct), Toast.LENGTH_SHORT).show();
        }
        else
        {
            userAnswer = 0;
            Toast.makeText(getApplicationContext(), getString(R.string.answer_incorrect), Toast.LENGTH_SHORT).show();
        }
        answers.add(new Answer(questionIndex, userAnswer, System.currentTimeMillis()-questionStartTime));

        if (questionIndex < numQuestions)
            showQuestion();
        else
        {
            Toast.makeText(getApplicationContext(), "Test complete, redirect to result page", Toast.LENGTH_SHORT).show();
            //for(Answer a: answers)
            //    Log.v(LOG_TAG, String.format("QuestionIndex: %s, Correct: %s, ElapsedTime: %sms", a.getQuestionIndex(), a.getCorrect(), a.getElapsedTime()));

            // pass solution to result page
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putParcelableArrayListExtra(getString(R.string.answers_key), answers);
            startActivity(intent);
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
                ivLeft.setImageResource(imageCollection.get(i).get(j1));
                ivRight.setImageResource(imageCollection.get(i).get(j2));
                ivCenter.setImageResource(0); //clear the star in the center
                findViewById(R.id.ibYes).setEnabled(true);
                findViewById(R.id.ibNo).setEnabled(true);
                questionStartTime = System.currentTimeMillis();
                questionIndex++;
            }
        }, 2000);
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
}