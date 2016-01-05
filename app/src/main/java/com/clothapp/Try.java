package com.clothapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Try extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try);

        //  just a sample button it does nothing when clicked
        Button button_final = (Button) findViewById(R.id.final_button);

        button_final.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.final_button:
                        System.out.println("debug: premuto you won");
                        break;
                }
            }
        });
    }
}

