package com.clothapp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.clothapp.Get;
import com.clothapp.Post;
import com.clothapp.R;


public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //prendo tutti valori
        final EditText edit_password_confirm = (EditText) findViewById(R.id.edit_password_confirm);
        final EditText edit_password = (EditText) findViewById(R.id.edit_password);
        final EditText edit_username = (EditText) findViewById(R.id.edit_username);
        final EditText edit_mail = (EditText) findViewById(R.id.edit_mail);
        final EditText edit_name = (EditText) findViewById(R.id.edit_name);
        final EditText edit_lastname = (EditText) findViewById(R.id.edit_lastname);
        final EditText edit_date = (EditText) findViewById(R.id.edit_date);

        Button button = (Button) findViewById(R.id.form_register_button); //inizializzo bottone registrati
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //listener sul bottone
                switch (v.getId()) {
                    case R.id.form_register_button:
                        if (checkPassWordAndConfirmPassword(edit_password.getText().toString(), edit_password_confirm.getText().toString())) {
                            //nel caso in cui le password non coincidano
                            Snackbar.make(v, "Le password devono coincidere", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        } else {

                            //altrimenti ficco nel bundle e invio l'intent alla nuova attività
                            /*Bundle bundle = new Bundle();
                            bundle.putString("username", edit_username.getText().toString());
                            bundle.putString("mail", edit_mail.getText().toString());
                            bundle.putString("name", edit_username.getText().toString());
                            bundle.putString("lastname", edit_username.getText().toString());
                            bundle.putString("password", edit_password.getText().toString());
                            Intent form_intent = new Intent(getApplicationContext(), HomePage.class);
                            form_intent.putExtras(bundle);
                            startActivity(form_intent);*/

                            //prova di una post
                            int dim_param = 6;
                            String indirizzo = "http://www.ceribbo.com/server.php";
                            String[] data = new String[2*dim_param+1];
                            data[0] = indirizzo;
                            data[1] = "username";
                            data[2] = edit_username.getText().toString();
                            data[3] = "name";
                            data[4] = edit_username.getText().toString();
                            data[5] = "lastname";
                            data[6] = edit_username.getText().toString();
                            data[7] = "password";
                            data[8] = edit_username.getText().toString();
                            data[9] = "mail";
                            data[10] = edit_username.getText().toString();
                            data[11] = "data";
                            data[12] = edit_username.getText().toString();
                            System.out.println(new Post().execute(data));

                            //prova di una get
                            System.out.println(new Get().execute("http://testone-1161.appspot.com/"));
                        }
                        break;
                }
            }
        });
    }

    //funzione per controllare le 2 password siano uguali e non nulle
    private boolean checkPassWordAndConfirmPassword(String password, String confirmPassword) {
        boolean pstatus = true;
        if (confirmPassword != null && password != null) {
            if (password.equals(confirmPassword)) {
                pstatus = false;
            }
        }
        return pstatus;
    }


}
