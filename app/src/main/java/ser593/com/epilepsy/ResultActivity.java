package ser593.com.epilepsy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * Result Activity
 *  Show Result and simple stats
 *  */

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        addListenerOnReturnButton();

        /*List<Answer> answers = new ArrayList<Answer>();
        answers.add(new Answer(0, true, 100));
        answers.add(new Answer(1, false, 200));
        answers.add(new Answer(2, true, 300));
        answers.add(new Answer(3, false, 400));
        answers.add(new Answer(4, true, 500));*/
        Intent i =this.getIntent();
        ArrayList<Answer> answers = i.getParcelableArrayListExtra(getString(R.string.answers_key));
        String task = i.getStringExtra(getString(R.string.task));


        int numAnswer = answers.size();
        long totalTime = 0;
        int correct = 0;

        // Find Tablelayout
        TableLayout tl = (TableLayout) findViewById(R.id.tlResult);

        if (task.equals(getString(R.string.task_finger_tapping))) //change his around after fixing pcpa
        {
            TextView txtAccuracyLabel = (TextView)findViewById(R.id.txtAccuracyLabel);
            txtAccuracyLabel.setText("");
            TextView txtAvgLabel = (TextView)findViewById(R.id.txtAvgLabel);
            txtAvgLabel.setText("");

            // Create header row, format with padding
            TableRow trHeader = new TableRow(this);
            TextView tHeader1 = new TextView(this);
            tHeader1.setText(String.format("%1$-20s", "Side"));
            trHeader.addView(tHeader1);
            TextView tHeader2 = new TextView(this);
            tHeader2.setText(String.format("%1$-20s", "Number of Taps"));
            trHeader.addView(tHeader2);
            tl.addView(trHeader);

            // Loop through the list and add row to table
            for(Answer ans: answers)
            {
                Log.v("looping", String.valueOf(ans.getSide()) + ", " + String.valueOf(ans.getTapCount()));
                TableRow tr = new TableRow(this);

                TextView t = new TextView(this);
                t.setText(String.valueOf(ans.getSide()));
                tr.addView(t);

                TextView t1 = new TextView(this);
                t1.setText(String.valueOf(ans.getTapCount()));
                tr.addView(t1);

                tl.addView(tr);
            }
        }
        else if (task.equals(getString(R.string.task_pattern_comparison_processing)))
        {
            // Create header row, format with padding
            TableRow trHeader = new TableRow(this);
            TextView tHeader1 = new TextView(this);
            tHeader1.setText(String.format("%1$-20s", "Question Index"));
            trHeader.addView(tHeader1);
            TextView tHeader2 = new TextView(this);
            tHeader2.setText(String.format("%1$-20s", "Correct"));
            trHeader.addView(tHeader2);
            TextView tHeader3 = new TextView(this);
            tHeader3.setText(String.format("%1$-20s", "Elapsed Time"));
            trHeader.addView(tHeader3);
            tl.addView(trHeader);

            // Loop through the list and add row to table
            for(Answer ans: answers)
            {
                TableRow tr = new TableRow(this);

                TextView t = new TextView(this);
                t.setText(String.valueOf(ans.getQuestionIndex()));
                tr.addView(t);

                if (ans.getCorrect() == 1) correct++;
                TextView t1 = new TextView(this);
                t1.setText(String.valueOf(ans.getCorrect() == 1 ? true : false));
                tr.addView(t1);

                totalTime += ans.getElapsedTime();
                TextView t2 = new TextView(this);
                t2.setText(String.valueOf(ans.getElapsedTime()));
                tr.addView(t2);

                tl.addView(tr);
            }

            // print out stats
            TextView txtAccuracy = (TextView)findViewById(R.id.txtAccuracy);
            txtAccuracy.setText(String.format("%1$d/%2$d", correct, numAnswer));

            TextView txtAccuracyPercentage = (TextView)findViewById(R.id.txtAccuracyPercentage);
            txtAccuracyPercentage.setText(String.format("%.2f%%", ((double)correct)/numAnswer*100));

            TextView txtAvgTIme = (TextView)findViewById(R.id.txtAvgTime);
            txtAvgTIme.setText(String.format("%1$dms", totalTime/numAnswer));
        }

    }

    private void addListenerOnReturnButton() {
        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}