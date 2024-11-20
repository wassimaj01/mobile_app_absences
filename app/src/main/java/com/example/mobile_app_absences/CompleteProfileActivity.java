package com.example.mobile_app_absences;



import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CompleteProfileActivity extends AppCompatActivity {
    private Button submit;
    private EditText nomComplet ;
    private EditText cin;
    private EditText numeroTele;
    private EditText specialite;
    private TextView link;
    private TextView link2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        submit = findViewById(R.id.submit);
        nomComplet = findViewById(R.id.nomComplet);
        cin = findViewById(R.id.CIN);
        numeroTele = findViewById(R.id.numero);
        specialite = findViewById(R.id.specialite);

        MyApplication myApplication = (MyApplication) getApplication();
        User currentUser = myApplication.currentUser;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom = nomComplet.getText().toString();
                String cn = cin.getText().toString();
                String tele = numeroTele.getText().toString();
                String spec = specialite.getText().toString();

                // Get an instance of the SQLiteDatabase
                DBConnect dbHelper = new DBConnect(CompleteProfileActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                int idd = currentUser.getId();

                // Check if a profile already exists for the user
                String[] columns = {"id"};
                String selection = "user_id=?";
                String[] selectionArgs = {String.valueOf(idd)};
                Cursor cursor = db.query("profile", columns, selection, selectionArgs, null, null, null);
                if (cursor.getCount() > 0) {
                    // A profile already exists for the user
                    Toast.makeText(CompleteProfileActivity.this, "Vous avez déjà créé un profil", Toast.LENGTH_SHORT).show();
                } else {
                    // Insert a new profile for the user
                    ContentValues values = new ContentValues();
                    values.put("nom_complet", nom);
                    values.put("cin", cn);
                    values.put("numero_tele", tele);
                    values.put("specialite", spec);
                    values.put("user_id", idd);
                    long profileId = db.insert("profile", null, values);

                    if (profileId != -1) {
                        // Profile inserted successfully
                        Toast.makeText(CompleteProfileActivity.this, "Votre profil a été créé avec succès", Toast.LENGTH_SHORT).show();
                    } else {
                        // Failed to insert the profile
                        Toast.makeText(CompleteProfileActivity.this, "Échec de la création du profil", Toast.LENGTH_SHORT).show();
                    }
                }

                // Close the cursor
                cursor.close();
                // Close the database connection
                db.close();
            }
        });


        link=findViewById(R.id.showprofile);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CompleteProfileActivity.this,ShowProfileActivity.class));
            }
        });

        link2=findViewById(R.id.dashboard);
        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get an instance of the SQLiteDatabase
                DBConnect dbHelper = new DBConnect(CompleteProfileActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                int idd = currentUser.getId();


                // Check if a profile exists for the user
                String[] columns = {"id"};
                String selection = "user_id=?";
                String[] selectionArgs = {String.valueOf(idd)};
                Cursor cursor = db.query("profile", columns, selection, selectionArgs, null, null, null);
                if (cursor.getCount() > 0) {
                    // A profile exists for the user
                    startActivity(new Intent(CompleteProfileActivity.this, DashboardActivity.class));
                } else {
                    // A profile doesn't exist for the user
                    Toast.makeText(CompleteProfileActivity.this, "Vous devez créer un profil avant d'accéder au tableau de bord", Toast.LENGTH_SHORT).show();
                }

                // Close the cursor
                cursor.close();
                // Close the database connection
                db.close();
            }
        });




    }
}