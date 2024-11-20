package com.example.mobile_app_absences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Profile2Activity extends AppCompatActivity {

    private Button submit;
    private EditText nomComplet ;
    private EditText cin;
    private EditText numeroTele;

    private RadioGroup niveau_scolaire;
    private RadioGroup branche;
    private TextView link;
    private TextView link2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        submit = findViewById(R.id.submit2);
        nomComplet = findViewById(R.id.nomComplet2);
        cin = findViewById(R.id.CIN2);
        numeroTele = findViewById(R.id.numero2);
        niveau_scolaire = findViewById(R.id.niveau_scolaire);
        branche = findViewById(R.id.branche);
        link=findViewById(R.id.showprofile2);

        MyApplication myApplication = (MyApplication) getApplication();
        User currentUser = myApplication.currentUser;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom = nomComplet.getText().toString();
                String cn = cin.getText().toString();
                String tele = numeroTele.getText().toString();
                String niv = "";
                String brn = "";

                int selectedId = niveau_scolaire.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    niv = selectedRadioButton.getText().toString();
                }

                int selectedId2 = branche.getCheckedRadioButtonId();
                if (selectedId2 != -1) {
                    RadioButton selectedRadioButton2 = findViewById(selectedId2);
                    brn = selectedRadioButton2.getText().toString();
                }

                // Get an instance of the SQLiteDatabase
                DBConnect dbHelper = new DBConnect(Profile2Activity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                int idd = currentUser.getId();

                // Check if a profile already exists for the user
                String[] columns = {"id"};
                String selection = "user_id=?";
                String[] selectionArgs = {String.valueOf(idd)};
                Cursor cursor = db.query("profile2", columns, selection, selectionArgs, null, null, null);
                if (cursor.getCount() > 0) {
                    // A profile already exists for the user
                    Toast.makeText(Profile2Activity.this, "Vous avez déjà créé un profil", Toast.LENGTH_SHORT).show();
                } else {
                    // Insert a new profile for the user
                    ContentValues values = new ContentValues();
                    values.put("nom_complet", nom);
                    values.put("cne", cn);
                    values.put("numero_tele", tele);
                    values.put("niveau_scolaire", niv);
                    values.put("branche", brn);
                    values.put("user_id",idd);
                    long profileId = db.insert("profile2", null, values);

                    if (profileId != -1) {
                        // Profile inserted successfully
                        Toast.makeText(Profile2Activity.this, "Votre profil a été créé avec succès", Toast.LENGTH_SHORT).show();

                        // Retrieve the user_id from the users table

                    } else {
                        // Failed to insert the profile
                        Toast.makeText(Profile2Activity.this, "Échec de la création du profil", Toast.LENGTH_SHORT).show();
                    }
                }

                // Close the cursor
                cursor.close();
                // Close the database connection
                db.close();
            }
        });

        link=findViewById(R.id.showprofile2);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile2Activity.this,ShowProfile2Activity.class));
            }
        });

        link2=findViewById(R.id.dashboard2);
        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get an instance of the SQLiteDatabase
                DBConnect dbHelper = new DBConnect(Profile2Activity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                int idd = currentUser.getId();


                // Check if a profile exists for the user
                String[] columns = {"id"};
                String selection = "user_id=?";
                String[] selectionArgs = {String.valueOf(idd)};
                Cursor cursor = db.query("profile2", columns, selection, selectionArgs, null, null, null);
                if (cursor.getCount() > 0) {
                    // A profile exists for the user
                    startActivity(new Intent(Profile2Activity.this, Dashboard2Activity.class));
                } else {
                    // A profile doesn't exist for the user
                    Toast.makeText(Profile2Activity.this, "Vous devez créer un profil avant d'accéder au tableau de bord", Toast.LENGTH_SHORT).show();
                }

                // Close the cursor
                cursor.close();
                // Close the database connection
                db.close();
            }
        });
    }
}