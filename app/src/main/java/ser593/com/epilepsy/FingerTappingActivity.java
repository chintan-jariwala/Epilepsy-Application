package ser593.com.epilepsy;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class FingerTappingActivity extends AppCompatActivity {
    String LOG_TAG = FingerTappingActivity.class.getSimpleName();
    int currentTestSide; //0-left, 1-right
    boolean currentTapSide; //false-left, true-right
    boolean start = false;
    int count;
    int time = 5;
    int recordCount = 0; //temporary
    TextView tvSide;
    TextView tvHint;
    TextView tvCount;
    TextView tvTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_tapping);

        tvSide = (TextView)findViewById(R.id.tvSide);
        tvHint = (TextView)findViewById(R.id.tvHint);
        tvCount = (TextView)findViewById(R.id.tvCount);
        tvTimer = (TextView) findViewById(R.id.tvTimer);

        addListenerOnLeftButton();
        addListenerOnRightButton(); //double check the bool value

        Random rand = new Random();
        currentTestSide = rand.nextInt(2);
        tvSide.setText(currentTestSide == 0 ? getString(R.string.finger_tapping_left) : getString(R.string.finger_tapping_right));
        tvHint.setText(getString(R.string.finger_tapping_go));
        tvTimer.setText(Integer.toString(time));
        count = 0;
        start = false;
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
            //Log.v(LOG_TAG, String.format("%d", TimeUnit.MILLISECONDS.toMillis(millis)));
            tvTimer.setText(String.format("%d", TimeUnit.MILLISECONDS.toSeconds(millis)));
        }

        @Override
        public void onFinish() {
            tvTimer.setText(Integer.toString(0));

            //add record to answer
            recordCount++;

            //if answer has 2 records: go to result page
            if (recordCount == 2)
            {
                Toast.makeText(getApplicationContext(), "DONE! Go to Result", Toast.LENGTH_SHORT).show();
            }
            else //else start another round with different hand
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
                        tvTimer.setText(Integer.toString(time));
                        count = 0;
                        start = false;
                    }
                }, 2000);
            }
        }
    }
}
