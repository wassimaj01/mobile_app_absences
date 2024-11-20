package com.example.mobile_app_absences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AbsenceActivity extends AppCompatActivity {

    private List<String> matiereList;
    private List<String> etudiantsList;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;
    private DBConnect dbConnect;
    private RadioGroup seance;
    private EditText date;
    private EditText penalite;
    private Button show;
    private Button create_button;
    private Button update_button;
    private Button delete_button;
    private Button pdf_button;
    private Button contact;
    private ListView absences_listview;
    private Spinner etudiant;
    private Spinner matiere;
    private int selectedItemPosition=-1;

    private ArrayAdapter<String> absencesAdapter; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence);
        seance = findViewById(R.id.seance);
        date = findViewById(R.id.date);
        penalite = findViewById(R.id.penalite);
        create_button = findViewById(R.id.create_button);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        contact=findViewById(R.id.contact_button);
        show=findViewById(R.id.show_button);
        absences_listview = findViewById(R.id.absences_listview);
        etudiant = findViewById(R.id.etudiant);
        matiere = findViewById(R.id.matiere);

        matiereList = new ArrayList<>();
        etudiantsList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, R.layout.spinner_item, matiereList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        adapter2 = new ArrayAdapter<>(this, R.layout.spinner_item, etudiantsList);
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);

        matiere.setAdapter(adapter);
        etudiant.setAdapter(adapter2);

        dbConnect = new DBConnect(this);

        MyApplication myApplication = (MyApplication) getApplication();
        User currentUser = myApplication.currentUser;

        retrieveMatiereData();
        retrieveEtudiantsData();

        List<String> absencesData = retrieveAbsencesData();
        absencesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, absencesData);
        absences_listview.setAdapter(absencesAdapter);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = seance.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String sc = selectedRadioButton.getText().toString();
                    String selectedMatiere = matiere.getSelectedItem().toString();
                    String selectedEtudiant = etudiant.getSelectedItem().toString();
                    String dt = date.getText().toString();
                    String pn = penalite.getText().toString();
                    Intent intent = new Intent(AbsenceActivity.this, ShowAbsenceActivity.class);
                    intent.putExtra("seance", sc);
                    intent.putExtra("date", dt);
                    intent.putExtra("penalite", pn);
                    intent.putExtra("nomMatiere", selectedMatiere);
                    intent.putExtra("nomEtudiant", selectedEtudiant);

                    startActivity(intent);

                }
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedEtudiant = etudiant.getSelectedItem().toString();

                // Get the user ID for the selected item
                int userId = getUserIdForSelectedEtudiant(selectedEtudiant);

                // Retrieve the email using the user ID
                String email = dbConnect.getEmailForUser(userId);

                if (email != null) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + Uri.encode(email)));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Absence");
                    intent.putExtra(Intent.EXTRA_TEXT, "Cher " + selectedEtudiant + ",\n\n");
                    try {
                        startActivity(Intent.createChooser(intent, "Send Email"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(AbsenceActivity.this, "No email app found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the email is not found
                    Toast.makeText(AbsenceActivity.this, "Email not found for the selected user.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        absences_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedItemPosition != -1) {
                    View previousSelectedItemView = absences_listview.getChildAt(selectedItemPosition - absences_listview.getFirstVisiblePosition());
                    if (previousSelectedItemView != null) {
                        previousSelectedItemView.setBackgroundColor(0); // Change this to the default background color
                    }
                }

                // Set the background color of the newly selected item to a light transparent blue
                int backgroundColor = Color.parseColor("#80ADD8E6");
                view.setBackgroundColor(backgroundColor);

                // Update the selected item position
                selectedItemPosition = position;

                String selectedItem = absencesAdapter.getItem(position);
                String[] parts = selectedItem.split(", ");

                String seance = parts[0];
                String dte = parts[1];
                String pen = parts[2];
                String nomMatiere = parts[3];
                String nomEtudiant = parts[4];


                // Set the retrieved data to the corresponding fields
                RadioButton selectedRadioButton;

                if (seance.equals("8-10")) {
                    selectedRadioButton = findViewById(R.id.RadioButton1);
                } else if (seance.equals("10-12")) {
                    selectedRadioButton = findViewById(R.id.RadioButton2);
                } else if (seance.equals("14-16")) {
                    selectedRadioButton = findViewById(R.id.RadioButton3);
                } else if (seance.equals("16-18")) {
                    selectedRadioButton = findViewById(R.id.RadioButton4);
                } else {
                    // Handle the case where the retrieved seance value is not one of the predefined options
                    // You can implement your own logic here
                    return;
                }

                selectedRadioButton.setChecked(true);

                date.setText(dte);
                penalite.setText(pen);

                // Set the corresponding selection in the Spinners
                int matierePosition = adapter.getPosition(nomMatiere);
                if (matierePosition != -1) {
                    matiere.setSelection(matierePosition);
                }

                int etudiantPosition = adapter2.getPosition(nomEtudiant);
                if (etudiantPosition != -1) {
                    etudiant.setSelection(etudiantPosition);
                }
            }
        });


        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = seance.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String sc = selectedRadioButton.getText().toString();
                    String selectedMatiere = matiere.getSelectedItem().toString();
                    String selectedEtudiant = etudiant.getSelectedItem().toString();
                    String selectedEtudiantCNE = retrieveEtudiantCNE(selectedEtudiant);
                    String dt = date.getText().toString();
                    String pn = penalite.getText().toString();

                    if (sc.isEmpty() || selectedMatiere.isEmpty() || selectedEtudiant.isEmpty() || dt.isEmpty() || pn.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "SVP remplir tous les informations", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            int idd = currentUser.getId();
                            SQLiteDatabase insertDb = dbConnect.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("prof_id", idd);
                            values.put("nom_etudiant", selectedEtudiant);
                            values.put("nom_matiere", selectedMatiere);
                            values.put("cne", selectedEtudiantCNE);
                            values.put("seance", sc);
                            values.put("date", dt);
                            values.put("penalite", pn);

                            insertDb.insert("absence", null, values);
                            insertDb.close();
                            updateListView();

                            Toast.makeText(getApplicationContext(), "created", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });




        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = seance.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String sc = selectedRadioButton.getText().toString();
                    String selectedMatiere = matiere.getSelectedItem().toString();
                    String selectedEtudiant = etudiant.getSelectedItem().toString();
                    String dt = date.getText().toString();
                    String pn = penalite.getText().toString();

                    SQLiteDatabase deleteDb = dbConnect.getWritableDatabase();
                    String selection = "seance = ? AND date = ? AND penalite = ? AND nom_matiere = ? AND nom_etudiant = ?";
                    String[] selectionArgs = {sc, dt, pn, selectedMatiere, selectedEtudiant};
                    int deletedRows = deleteDb.delete("absence", selection, selectionArgs);
                    deleteDb.close();

                    if (deletedRows > 0) {
                        Toast.makeText(AbsenceActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        updateListView(); // Update the ListView after deleting an absence
                        clearFields(); // Clear the fields after deleting an absence
                    }
                }
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = seance.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String sc = selectedRadioButton.getText().toString();
                    String selectedMatiere = matiere.getSelectedItem().toString();
                    String selectedEtudiant = etudiant.getSelectedItem().toString();
                    String dt = date.getText().toString();
                    String pn = penalite.getText().toString();

                    SQLiteDatabase updateDb = dbConnect.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("seance", sc);
                    values.put("date", dt);
                    values.put("penalite", pn);

                    String selection = "nom_matiere = ? AND nom_etudiant = ?";
                    String[] selectionArgs = {selectedMatiere, selectedEtudiant};
                    int updatedRows = updateDb.update("absence", values, selection, selectionArgs);
                    updateDb.close();

                    if (updatedRows > 0) {
                        Toast.makeText(AbsenceActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        updateListView(); // Update the ListView after updating an absence
                        clearFields(); // Clear the fields after updating an absence
                    }
                }
            }
        });


    }

    // Other methods...

    private void updateListView() {
        List<String> absencesData = retrieveAbsencesData();
        absencesAdapter.clear();
        absencesAdapter.addAll(absencesData);
        absencesAdapter.notifyDataSetChanged();
    }



    private void retrieveMatiereData() {
        SQLiteDatabase db = dbConnect.getReadableDatabase();
        Cursor cursor = db.query("matiere", new String[]{"nom_matiere"}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("nom_matiere");
            do {
                String nomMatiere = cursor.getString(columnIndex);
                Log.d("Matiere", nomMatiere);
                matiereList.add(nomMatiere);
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }


    private void retrieveEtudiantsData() {
        SQLiteDatabase db = dbConnect.getReadableDatabase();
        Cursor cursor = db.query("profile2", new String[]{"nom_complet"}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("nom_complet");
            do {
                String nomEtudiant = cursor.getString(columnIndex);
                etudiantsList.add(nomEtudiant);
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter2.notifyDataSetChanged();
    }

    private String retrieveEtudiantCNE(String nomEtudiant) {
        String cne = "";

        SQLiteDatabase db = dbConnect.getReadableDatabase();
        String[] projection = {"cne"};
        String selection = "nom_complet = ?";
        String[] selectionArgs = {nomEtudiant};
        Cursor cursor = db.query("profile2", projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("cne");
            cne = cursor.getString(columnIndex);
        }

        cursor.close();
        return cne;
    }



    private List<String> retrieveAbsencesData() {
        List<String> absencesList = new ArrayList<>();

        SQLiteDatabase db = dbConnect.getReadableDatabase();

        MyApplication myApplication = (MyApplication) getApplication();
        User currentUser = myApplication.currentUser;
        // Retrieve the user ID of the current user
        int idd = currentUser.getId();
        String selection = "prof_id=?";
        String[] selectionArgs = {String.valueOf(idd)};
        Cursor cursor = db.query("absence", new String[]{"seance", "date", "penalite", "nom_matiere", "nom_etudiant","cne"}, selection, selectionArgs, null, null, null);

        int seanceIndex = cursor.getColumnIndex("seance");
        int dateIndex = cursor.getColumnIndex("date");
        int penaliteIndex = cursor.getColumnIndex("penalite");
        int matiereIndex = cursor.getColumnIndex("nom_matiere");
        int etudiantIndex = cursor.getColumnIndex("nom_etudiant");
        int cneIndex = cursor.getColumnIndex("cne");


        if (cursor.moveToFirst()) {
            do {
                String seance = cursor.getString(seanceIndex);
                String dte = cursor.getString(dateIndex);
                String penal = cursor.getString(penaliteIndex);
                String nomMatiere = cursor.getString(matiereIndex);
                String nomEtudiant = cursor.getString(etudiantIndex);
                String cneEtudiant = cursor.getString(cneIndex);

                String absenceItem = seance + ", " + dte + ", "+ penal + ", "+ nomMatiere + ", "+ nomEtudiant+ ", "+cneEtudiant;
                absencesList.add(absenceItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return absencesList;
    }

    // Add this method to retrieve the user ID for the selected item
    private int getUserIdForSelectedEtudiant(String selectedEtudiant) {
        // Perform a database query to retrieve the user ID for the selected item
        SQLiteDatabase db = dbConnect.getReadableDatabase();
        String[] columns = {"id"};
        String selection = "nom_complet=?";
        String[] selectionArgs = {selectedEtudiant};
        Cursor cursor = db.query("profile2", columns, selection, selectionArgs, null, null, null);

        int userId = -1;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("id");
            if (columnIndex != -1) {
                userId = cursor.getInt(columnIndex);
            }
        }

        cursor.close();
        return userId;
    }

    private int getIdForSelectedAbsence(String seance, String dte, String pen, String nomMatiere, String nomEtudiant) {
        SQLiteDatabase db = dbConnect.getReadableDatabase();

        // Perform a database query to retrieve the ID for the selected absence
        String[] columns = {"id"};
        String selection = "seance=? AND date=? AND penalite=? AND nom_matiere=? AND nom_etudiant=?";
        String[] selectionArgs = {seance, dte, pen, nomMatiere, nomEtudiant};
        Cursor cursor = db.query("absence", columns, selection, selectionArgs, null, null, null);

        int absenceId = -1;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("id");
            if (columnIndex != -1) {
                absenceId = cursor.getInt(columnIndex);
            }
        }

        cursor.close();
        return absenceId;
    }



    private void clearFields() {
        seance.clearCheck();
        date.setText("");
        penalite.setText("");
        matiere.setSelection(0);
        etudiant.setSelection(0);
    }
}
