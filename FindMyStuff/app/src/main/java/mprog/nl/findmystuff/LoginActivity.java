package mprog.nl.findmystuff;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.Console;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public void signUp(View v)
    {
        EditText username   = (EditText)findViewById(R.id.usernameEdt);
        EditText password   = (EditText)findViewById(R.id.passwordEdt);

        ParseUser user = new ParseUser();
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "You successfully signed up!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void logIn(View v)
    {
        EditText username   = (EditText)findViewById(R.id.usernameEdt);
        EditText password   = (EditText)findViewById(R.id.passwordEdt);

        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), e.toString(),
                            Toast.LENGTH_LONG).show();
                    // Signup failed. Look at the ParseException to see what happened.
                }
            }
        });
    }





}
