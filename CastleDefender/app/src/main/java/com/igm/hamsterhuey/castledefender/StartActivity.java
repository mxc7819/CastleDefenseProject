package com.igm.hamsterhuey.castledefender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

    /*
        This activity is the first screen seen by the player, where a play button is present
        to start a new game
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Add a click listener to the button that starts a new game
        findViewById(R.id.newGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open the gameActivity
                Intent i = new Intent(StartActivity.this, GameActivity.class);
                // Start the gameActivity and finish the current activity
                startActivity(i);
                finish();
            }
        });
    }
}
