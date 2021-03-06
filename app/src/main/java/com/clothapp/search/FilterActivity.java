package com.clothapp.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.clothapp.BaseActivity;
import com.clothapp.R;
import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.Image;
import com.clothapp.settings.SettingsActivity;

import java.util.ArrayList;

/**
 * Created by jack1 on 28/02/2016.
 */
public class FilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RadioGroup radioGroup;
    private RadioButton man;
    private RadioButton woman;
    private EditText prezzoDa;
    private EditText prezzoA;
    private int radioselect;
    private float pricefrom;
    private float priceto;
    private ImageView done;
    private String order=null;
    private Button reset;
    private ArrayAdapter<CharSequence> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.Filter);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        final String query=getIntent().getStringExtra("query");


        radioGroup=(RadioGroup)findViewById(R.id.radios);
        man=(RadioButton)findViewById(R.id.man);
        woman=(RadioButton)findViewById(R.id.woman);
        prezzoDa=(EditText)findViewById(R.id.da);
        prezzoA=(EditText)findViewById(R.id.a);
        done=(ImageView)findViewById(R.id.done);
        final String[] sex = {"all"};
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(man.getId()==radioGroup.getCheckedRadioButtonId()){
                    sex[0] ="man";}
                if(woman.getId()==radioGroup.getCheckedRadioButtonId()){
                    sex[0] ="woman";}
                try {
                    pricefrom = Float.parseFloat(String.valueOf(prezzoDa.getText()));
                }catch (NumberFormatException e){
                    pricefrom=-1;
                }
                try{
                    priceto=Float.parseFloat(String.valueOf(prezzoA.getText()));
                }catch (NumberFormatException e){
                    priceto=-1;
                }
                Intent i=new Intent(getBaseContext(),SearchResultsActivity.class);
                i.putExtra("query", query);
                System.out.println("selezionato:" + sex);
                i.putExtra("sex", sex[0]);
                i.putExtra("prezzoDa",pricefrom);
                i.putExtra("prezzoA",priceto);
                i.putExtra("order", order);
                ApplicationSupport global = (ApplicationSupport) getApplicationContext();
                global.setCloth(new ArrayList<Image>());
                global.setTag(new ArrayList<Image>());
                startActivity(i);
                finish();
            }
        });

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.array_order, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);




        reset=(Button)findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup.clearCheck();
                prezzoDa.getText().clear();
                prezzoA.getText().clear();
                spinner.setSelection(0);
            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // In caso sia premuto il pulsante indietro termino semplicemente l'activity
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        order=parent.getItemAtPosition(position).toString();
        System.out.println(order);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        order=getResources().getStringArray(R.array.array_order)[0];
    }


}
