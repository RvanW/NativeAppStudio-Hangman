package com.example.robbert.hangman;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private String guessedLetters = "";
    private String randomWord;
    private String hiddenWord;
    private int mistakes = 0;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("guessedLetters", guessedLetters);
        savedInstanceState.putString("randomWord", randomWord);
        savedInstanceState.putString("hiddenWord", hiddenWord);
        savedInstanceState.putInt("mistakes", mistakes);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore variables
            guessedLetters = savedInstanceState.getString("guessedLetters");
            randomWord = savedInstanceState.getString("randomWord");
            hiddenWord = savedInstanceState.getString("hiddenWord");
            mistakes = savedInstanceState.getInt("mistakes");
            // rebuild the display
            updateDisplay();

        } else {
            // initialize a new random word
            if(randomWord == null ) newRandomWord();
        }
        // initialize the buttons
        Button quitButton = (Button)findViewById(R.id.quitButton);
        Button restartButton = (Button)findViewById(R.id.restartButton);
        Button okButton = (Button)findViewById(R.id.ok);

        // set filters for input (1 char max and ALLCAPS)
        EditText letterInput = (EditText)findViewById(R.id.letterInput);
        letterInput.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(1)});

        // setup the chalk font
        Typeface chalkFont = Typeface.createFromAsset(getAssets(), "fonts/DK Crayon Crumble.ttf");
        letterInput.setTypeface(chalkFont);
        quitButton.setTypeface(chalkFont);
        restartButton.setTypeface(chalkFont);
        okButton.setTypeface(chalkFont);

        // set the input action listener
        letterInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            checkInput(null);
                            Log.v("ACTION"," ENTER or DONE");
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        Log.v("randomWord: ", randomWord + "");
    }


    private void newRandomWord() {
        // get all the words
        String[] words = getResources().getStringArray(R.array.words);

        // choose a random word from the list
        randomWord = words[randomNumber(words.length)];

        // set and display the new word hint
        char hiddenWordChars[] = new char[randomWord.length()];
        for (int i=0; i<randomWord.length(); i++) {
            hiddenWordChars[i] = '_';
        }
        hiddenWord = new String(hiddenWordChars);

        updateDisplay();
    }

    private void updateDisplay() {

        // update the word hint display
        TextView displayWord = (TextView)findViewById(R.id.displayWord);
        displayWord.setText(hiddenWord);

        // this textview displays guessed letters and amount of mistakes
        TextView error = (TextView) findViewById(R.id.errorMessage);
        error.setText(guessedLetters + "(" + mistakes + "/6)");

        ImageView hangmanImage = (ImageView)findViewById(R.id.hangmanImage);
        // change hangman resource path based on mistakes
        int h = getResources().getIdentifier("hangman" + (mistakes), "drawable", getPackageName());
        hangmanImage.setImageResource(h);

        // setup the chalk font
        Typeface chalkFont = Typeface.createFromAsset(getAssets(), "fonts/DK Crayon Crumble.ttf");
        displayWord.setTypeface(chalkFont);
        error.setTypeface(chalkFont);

        // check if player won / lost
        checkGameState();
    }

    private void checkGameState() {
        if(mistakes >= 6) {
            endGame(false);
        }
        else if(randomWord.equals(hiddenWord)) {
            endGame(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);

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

    private Integer randomNumber(int max) {
        Random r = new Random();
        return r.nextInt(max - 1);
    }

    public void checkInput(View v){
        // get the letter filled in by a user
        EditText letterInput = (EditText)findViewById(R.id.letterInput);
        String letter = letterInput.getText().toString().toLowerCase();

        // clear the input field
        letterInput.setText("");

        // validate if input is filled in and not a number
        if(letter.length() != 1 || !Character.isLetter(letter.charAt(0))) {
            Toast.makeText(getApplicationContext(), "Please fill in a valid character",
                    Toast.LENGTH_SHORT).show();
        }
        // check if letter is already guessed
        else if(guessedLetters != null && guessedLetters.contains(letter)) {
            Toast.makeText(getApplicationContext(), "You've already used this character",
                    Toast.LENGTH_SHORT).show();
        }
        // otherwise it is a valid input
        else {
            guessedLetters += letter + " ";

            // first look for a match
            StringBuilder hiddenWordBuilder = new StringBuilder(hiddenWord);
            boolean match = false;
            for (int i = 0; i < randomWord.length(); i++) { // loop over word characters
                char c = randomWord.charAt(i);
                if(c == letter.charAt(0)) { // if there is a match..
                    match = true;
                    hiddenWordBuilder.setCharAt(i, c);
                    hiddenWord = hiddenWordBuilder.toString();
                }
            }
            if(!match) { // there was no match so add a mistake
                mistakes++;
            }
            // update display calls checkGameState to see if user has won / lost
            updateDisplay();
        }
    }

    public void restart(View v) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void endGame(boolean win){
        // Disable the inputs
        EditText letterInput = (EditText)findViewById(R.id.letterInput);
        letterInput.clearFocus();
        letterInput.setVisibility(View.GONE);

        Button okButton = (Button)findViewById(R.id.ok);
        okButton.setVisibility(View.GONE);

        // initialize message
        TextView error = (TextView)findViewById(R.id.errorMessage);

        if(win){
            error.setText("You win!");
        }
        else {
            error.setText("You lose!\r\nThe word was " + randomWord);
            // TODO display some image?
        }

    }



    public void quit(View v) {
        finish();
    }
}
