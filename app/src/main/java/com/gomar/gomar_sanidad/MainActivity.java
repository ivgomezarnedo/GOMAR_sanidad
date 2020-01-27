package com.gomar.gomar_sanidad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;



/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * The constant EXTRA_MESSAGE.
     */
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Called when the user taps the Send button  @param view the view
     */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, NuevaFicha.class);
        startActivity(intent);
        // Do something in response to button
    }

    /**
     * Send message consultar ficha.
     *
     * @param view the view
     */
    public void sendMessageConsultarFicha(View view) {
        Intent intent = new Intent(this, ConsultarFichas.class);
        startActivity(intent);
        // Do something in response to button
    }

}
