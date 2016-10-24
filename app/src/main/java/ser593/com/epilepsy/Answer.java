package ser593.com.epilepsy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MengJungLin on 9/8/2016.
 *
 * Used to store each answer's info
 */
public class Answer implements Parcelable{
    private int questionIndex;
    private int correct;
    private long elapsedTime;

    public Answer(Parcel source) {
        questionIndex = source.readInt();
        correct = source.readInt();
        elapsedTime = source.readLong();
    }

    public Answer(int i, int b, long e)
    {
        questionIndex = i;
        correct = b;
        elapsedTime = e;
    }

    public int getQuestionIndex() { return questionIndex; }
    public int getCorrect() { return correct; }
    public long getElapsedTime() { return elapsedTime; }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(questionIndex);
        dest.writeInt(correct);
        dest.writeLong(elapsedTime);
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };
}
