package mprog.nl.findmystuff;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Jochem on 6-1-2016.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}