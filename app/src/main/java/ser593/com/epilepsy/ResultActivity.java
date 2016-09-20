package ser593.com.epilepsy;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

        // TODO: will read actual result from another page later
        List<Answer> answers = new ArrayList<Answer>();
        answers.add(new Answer(0, true, 100));
        answers.add(new Answer(1, false, 200));
        answers.add(new Answer(2, true, 300));
        answers.add(new Answer(3, false, 400));
        answers.add(new Answer(4, true, 500));

        int numAnswer = answers.size();
        long totalTime = 0;
        int correct = 0;

        // Find Tablelayout
        TableLayout tl = (TableLayout) findViewById(R.id.tlResult);

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

            if (ans.getCorrect()) correct++;
            TextView t1 = new TextView(this);
            t1.setText(String.valueOf(ans.getCorrect()));
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