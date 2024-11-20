package com.example.mobile_app_absences;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShowProfile2Activity extends AppCompatActivity {

    private TextView profileName;
    private TextView profileCIN;
    private TextView profileNumber;

    private TextView profileNiveau;

    private TextView profileBranche;

    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile2);


        profileName = findViewById(R.id.name_textview);
        profileCIN = findViewById(R.id.cin_textview);
        profileNumber = findViewById(R.id.phone_textview);
        profileNiveau = findViewById(R.id.niveau_textview);
        profileBranche = findViewById(R.id.branche_textview);

        // Get the user's profile information from the database
        DBConnect dbHelper = new DBConnect(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        MyApplication myApplication = (MyApplication) getApplication();
        User currentUser = myApplication.currentUser;

        // Add WHERE clause to retrieve only the profile of the current user
        String[] projection = {"nom_complet", "cne","numero_tele", "niveau_scolaire", "branche"};
        String selection = "user_id = ?";
        String[] selectionArgs = {String.valueOf(currentUser.getId())};
        Cursor cursor = db.query("profile2", projection, selection, selectionArgs, null, null, null);

        deleteButton = findViewById(R.id.delete_button2);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get an instance of the SQLiteDatabase
                DBConnect dbHelper = new DBConnect(ShowProfile2Activity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Delete the profile of the current user from the database
                String whereClause = "user_id = ?";
                String[] whereArgs = {String.valueOf(currentUser.getId())};
                int rowsDeleted = db.delete("profile", whereClause, whereArgs);

                if (rowsDeleted > 0) {
                    Toast.makeText(ShowProfile2Activity.this, "Profil supprimé avec succès", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after deleting the profile
                } else {
                    Toast.makeText(ShowProfile2Activity.this, "Échec de la suppression du profil", Toast.LENGTH_SHORT).show();
                }

                // Close the database connection
                db.close();
            }
        });

        if (cursor.moveToFirst()) {


            String name = cursor.getString(cursor.getColumnIndexOrThrow("nom_complet"));
            profileName.setText(name);

            String cne = cursor.getString(cursor.getColumnIndexOrThrow("cne"));
            profileCIN.setText(cne);

            String number = cursor.getString(cursor.getColumnIndexOrThrow("numero_tele"));
            profileNumber.setText(number);

            String niveau = cursor.getString(cursor.getColumnIndexOrThrow("niveau_scolaire"));
            profileNiveau.setText(niveau);

            String branche = cursor.getString(cursor.getColumnIndexOrThrow("branche"));
            profileBranche.setText(branche);
        } else {
            // Inform the user to create a profile first
            Toast.makeText(this, "Vous devez créer un profil d'abord", Toast.LENGTH_SHORT).show();
            finish(); // close this activity
        }

        // Close the cursor and database connection
        cursor.close();
        db.close();
    }
}