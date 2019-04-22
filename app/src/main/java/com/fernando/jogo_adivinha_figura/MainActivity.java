package com.fernando.jogo_adivinha_figura;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
TextView tituloTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tituloTextView = findViewById(R.id.tituloTextView);

        tituloTextView.setText(tituloTextView.getText() + ": " + ShapeRandom());


    }

    public String ShapeRandom(){

        String shape = "";
        int number = (int)(Math.random()*4);

        switch (number) {

            case 0:
                shape = "square";
                break;
            case 1:
                shape = "circle";
                break;
            case 2:
                shape = "rectangle";
                break;
            case 3:
                shape = "triangle";
                break;

        }
        return shape;

    }
}
