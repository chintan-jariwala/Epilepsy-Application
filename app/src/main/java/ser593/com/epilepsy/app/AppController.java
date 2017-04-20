package ser593.com.epilepsy.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import ser593.com.epilepsy.utils.LruBitmapCache;

/**
 * Created by chint on 2/15/2017.
 */

public class AppController extends Application {
    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;
    private SharedPreferences mMyPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mMyPreferences = PreferenceManager.getDefaultSharedPreferences(mInstance);

    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void writePreference(String key, String value){
        Log.d(TAG, "writePreference: Saving Data to Shared Preferences");
        SharedPreferences.Editor e = mMyPreferences.edit();
        e.putString(key, value);
        e.commit();
        Log.d(TAG, "writePreference: Data saved");

    }

    public String readPreference(String key){
        if(mMyPreferences.getString(key,null) != null){
            return mMyPreferences.getString(key,null);
        }else{
            return null;
        }
    }

}
