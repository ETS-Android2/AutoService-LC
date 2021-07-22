package kk.techbytecare.simplechatbot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
/*
    splash screen
    In this small code is the splash screen that shows a screen with the app logo while
    it is loading, and it automatically exits when the screen is loaded correctly
 */
public class startGreeting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_greeting);
    }
}