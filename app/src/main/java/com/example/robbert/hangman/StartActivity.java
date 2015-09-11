package com.example.robbert.hangman;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // initialize the buttons
        Button quitButton = (Button)findViewById(R.id.quitButton);
        Button newGame = (Button)findViewById(R.id.newGame);

        // setup the chalk font
        Typeface chalkFont = Typeface.createFromAsset(getAssets(), "fonts/DK Crayon Crumble.ttf");
        quitButton.setTypeface(chalkFont);
        newGame.setTypeface(chalkFont);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
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
    public void newGame(View v) {
        Intent i = new Intent(getApplicationContext(), GameActivity.class);
        v.getContext().startActivity(i);
    }

    public void quit(View v) {
        finish();
    }
}
