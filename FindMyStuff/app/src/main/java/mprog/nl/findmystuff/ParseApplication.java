package mprog.nl.findmystuff;

//Jochem van Dooren
//jochemvandooren@hotmail.nl
//10572929

import android.app.Application;

import com.parse.Parse;

//initialize Parse when app is launched
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}