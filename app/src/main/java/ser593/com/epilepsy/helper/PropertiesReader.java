package ser593.com.epilepsy.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by chint on 11/20/2016.
 */

public class PropertiesReader {
    private Context context;
    private Properties properties;

    public PropertiesReader(Context context){
        this.context=context;

        //create new properties obj
        properties=new Properties();
    }

    public Properties getProperties(String fileName){
        try {
            AssetManager assetManager = context.getAssets();

            InputStream inputStream = assetManager.open(fileName);

            properties.load(inputStream);

        } catch (IOException e) {
            Log.e("propertyReader", e.toString());
        }
        return properties;
    }
}
