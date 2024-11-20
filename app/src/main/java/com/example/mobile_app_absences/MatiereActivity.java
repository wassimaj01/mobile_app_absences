package com.example.mobile_app_absences;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

public class MatiereActivity extends AppCompatActivity {

    private EditText matNameEditText;
    private EditText matCatEditText;
    private EditText matDescEditText;
    private Button createButton;
    private Button updateButton;
    private Button deleteButton;
    private DBConnect dbConnect;
    private ListView matListView;
    private SimpleCursorAdapter matCursorAdapter;
    private long selectedMatiereId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matiere);

        // Initialize views
        matNameEditText = findViewById(R.id.mat_name_edittext);
        matCatEditText = findViewById(R.id.mat_cat_edittext);
        matDescEditText = findViewById(R.id.mat_desc_edittext);
        createButton = findViewById(R.id.create_button);
        updateButton = findViewById(R.id.update_button);
        deleteButton = findViewById(R.id.delete_button);
        matListView = findViewById(R.id.mat_listview);


        // Initialize the database helper
        dbConnect = new DBConnect(this);



        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered data
                String matName = matNameEditText.getText().toString();
                String matCat = matCatEditText.getText().toString();
                String matDesc = matDescEditText.getText().toString();

                // Check if any field is empty
                if (matName.isEmpty() || matCat.isEmpty() || matDesc.isEmpty()) {
                    // Show a toast message indicating that fields are empty
                    Toast.makeText(MatiereActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                MyApplication myApplication = (MyApplication) getApplication();
                User currentUser = myApplication.currentUser;
                // Retrieve the user ID of the current user
                int idd = currentUser.getId();

                // Retrieve prof_id and nom_prof from the profile table
                SQLiteDatabase db = dbConnect.getReadableDatabase();
                String[] projection = {"user_id", "nom_complet"};
                String selection = "user_id=?";
                String[] selectionArgs = {String.valueOf(idd)};
                Cursor cursor = db.query("profile", projection, selection, selectionArgs, null, null, null);
                if (cursor.moveToFirst()) {
                    int profIdIndex = cursor.getColumnIndexOrThrow("user_id");
                    int nomProfIndex = cursor.getColumnIndexOrThrow("nom_complet");
                    int profId = cursor.getInt(profIdIndex);
                    String nomProf = cursor.getString(nomProfIndex);

                    // Store the data in the matiere table
                    ContentValues values = new ContentValues();
                    values.put("nom_matiere", matName);
                    values.put("categorie", matCat);
                    values.put("description", matDesc);
                    values.put("prof_id", profId);
                    values.put("nom_prof", nomProf);
                    long newRowId = db.insert("matiere", null, values);
                    if (newRowId != -1) {
                        // Data inserted successfully
                        // Clear the input fields
                        matNameEditText.setText("");
                        matCatEditText.setText("");
                        matDescEditText.setText("");

                        // Refresh the ListView
                        updateListView();

                        // Show a success toast message
                        Toast.makeText(MatiereActivity.this, "Matière enregistrée", Toast.LENGTH_SHORT).show();
                    } else {
                        // Failed to insert data
                        // Handle the error case

                        // Show an error toast message
                        Toast.makeText(MatiereActivity.this, "Échec d'enregistrement", Toast.LENGTH_SHORT).show();
                    }
                }
                cursor.close();
                db.close();
            }
        });


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve prof_id and nom_prof from the profile table
                SQLiteDatabase db = dbConnect.getReadableDatabase();
                String[] projection = {"user_id", "nom_complet"};
                Cursor cursor = db.query("profile", projection, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    int profIdIndex = cursor.getColumnIndexOrThrow("user_id");
                    int nomProfIndex = cursor.getColumnIndexOrThrow("nom_complet");
                    int profId = cursor.getInt(profIdIndex);
                    String nomProf = cursor.getString(nomProfIndex);

                    // Update the selected matiere with the new values
                    SQLiteDatabase updateDb = dbConnect.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("nom_matiere", matNameEditText.getText().toString());
                    values.put("categorie", matCatEditText.getText().toString());
                    values.put("description", matDescEditText.getText().toString());
                    values.put("prof_id", profId);
                    values.put("nom_prof", nomProf);
                    String selection = "id=?";
                    String[] selectionArgs = {String.valueOf(selectedMatiereId)};
                    int count = updateDb.update("matiere", values, selection, selectionArgs);
                    if (count > 0) {
                        // Data updated successfully
                        // Clear the input fields
                        matNameEditText.setText("");
                        matCatEditText.setText("");
                        matDescEditText.setText("");

                        // Refresh the ListView
                        updateListView();
                    } else {
                        // Failed to update data
                        // Handle the error case
                    }
                    updateDb.close();
                }
                cursor.close();
                db.close();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the selected matiere
                SQLiteDatabase deleteDb = dbConnect.getWritableDatabase();
                String selection = "id=?";
                String[] selectionArgs = {String.valueOf(selectedMatiereId)};
                int count = deleteDb.delete("matiere", selection, selectionArgs);
                if (count > 0) {
                    // Data deleted successfully
                    // Clear the input fields
                    matNameEditText.setText("");
                    matCatEditText.setText("");
                    matDescEditText.setText("");

                    // Refresh the ListView
                    updateListView();
                } else {
                    // Failed to delete data
                    // Handle the error case
                }
                deleteDb.close();
            }
        });

        // Set up the ListView and its item click listener
        matCursorAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{"nom_matiere"},
                new int[]{android.R.id.text1},
                0
        );
        matListView.setAdapter(matCursorAdapter);
        matListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected matiere ID
                selectedMatiereId = id;

                // Retrieve the matiere details from the database
                SQLiteDatabase db = dbConnect.getReadableDatabase();
                String[] projection = {"nom_matiere", "categorie", "description"};
                String selection = "id=?";
                String[] selectionArgs = {String.valueOf(selectedMatiereId)};
                Cursor cursor = db.query("matiere", projection, selection, selectionArgs, null, null, null);
                if (cursor.moveToFirst()) {
                    // Display the matiere details in the input fields
                    matNameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("nom_matiere")));
                    matCatEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("categorie")));
                    matDescEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                }
                cursor.close();
                db.close();
            }
        });

        // Initial data population
        updateListView();
    }

    private void updateListView() {

        MyApplication myApplication = (MyApplication) getApplication();
        User currentUser = myApplication.currentUser;
        // Retrieve the user ID of the current user
        int userId = currentUser.getId();

        // Query the profile table to check if the user ID exists
        SQLiteDatabase db = dbConnect.getReadableDatabase();
        String[] projection = {"user_id"};
        String selection = "user_id=?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query("profile", projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            // The user ID exists in the profile table

            // Query the matiere table for the matieres associated with the teacher
            Cursor matiereCursor = db.query(
                    "matiere",
                    new String[]{"id AS _id", "nom_matiere"},
                    "prof_id=?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    null
            );

            // Update the Cursor in the SimpleCursorAdapter
            matCursorAdapter.swapCursor(matiereCursor);
        } else {
            // The user ID does not exist in the profile table
            // Handle the case where the teacher does not have any associated matieres
            // For example, you can display a message or clear the ListView
            matCursorAdapter.swapCursor(null); // Clear the ListView
        }

        cursor.close();
        db.close();
    }
}

