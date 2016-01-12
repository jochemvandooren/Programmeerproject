package mprog.nl.findmystuff;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> objectList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);



        //create arrayadapter and list for objects
        objectList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, objectList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);


        //retrieve parseobject based on currentuser
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ObjectList");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());

        //fill array with objects from user
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    for (ParseObject result : results) {
                        //add all the objects to the list
                        objectList.add(result.getString("object"));
                    }
                    //update the listview
                    adapter.notifyDataSetChanged();


                } else {
                    Log.d("mainactivity", "query lukt niet");
                }
            }
        });

        //click on list item and go to map activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                String selected=(String)parent.getItemAtPosition(position);
                intent.putExtra("object", selected);
                Log.d("mainactivity", selected+ ": is aangeklikt");
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //map button
    public void gotoMap(View view)
    {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    //user clicks on floating button to add object
    public void addObject(View view){
        //create dialog text
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("What object to you want to track?");
        alert.setMessage("You can add an object by filling in the form and pressing OK.");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        //Click OK in alert dialog
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //convert input to string and upload to Parse/add to array
                String object = input.getText().toString();
                ParseObject dataObject = new ParseObject("ObjectList");
                dataObject.put("user", ParseUser.getCurrentUser().getUsername());
                dataObject.put("object", object);
                dataObject.saveInBackground();
                objectList.add(object);

            }
        });

        //cancel alert dialog
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
