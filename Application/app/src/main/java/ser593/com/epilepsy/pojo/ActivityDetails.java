package ser593.com.epilepsy.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chint on 2/8/2017.
 */

public class ActivityDetails implements Parcelable {

    private String title = "";
    private String breif = "";
    private String details = "";

    public ActivityDetails() {
    }

    public ActivityDetails(String title, String breif, String details) {
        this.title = title;
        this.breif = breif;
        this.details = details;
    }

    protected ActivityDetails(Parcel in) {
        title = in.readString();
        breif = in.readString();
        details = in.readString();
    }

    public static final Creator<ActivityDetails> CREATOR = new Creator<ActivityDetails>() {
        @Override
        public ActivityDetails createFromParcel(Parcel in) {
            return new ActivityDetails(in);
        }

        @Override
        public ActivityDetails[] newArray(int size) {
            return new ActivityDetails[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBreif() {
        return breif;
    }

    public void setBreif(String breif) {
        this.breif = breif;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(breif);
        dest.writeString(details);
    }
}
