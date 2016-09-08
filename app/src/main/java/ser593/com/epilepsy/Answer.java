package ser593.com.epilepsy;

/**
 * Created by MengJungLin on 9/8/2016.
 *
 * Used to store each answer's info
 */
public class Answer {
    private int questionIndex;
    private boolean correct;
    private long elapsedTime;

    public Answer(int i, boolean b, long e)
    {
        questionIndex = i;
        correct = b;
        elapsedTime = e;
    }

    public int getQuestionIndex() { return questionIndex; }
    public boolean getCorrect() { return correct; }
    public long getElapsedTime() { return elapsedTime; }
}
