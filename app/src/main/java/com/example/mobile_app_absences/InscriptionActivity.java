package com.example.mobile_app_absences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InscriptionActivity extends AppCompatActivity {

    private Spinner combomat;
    private Spinner comboetud;
    private List<String> matiereList;
    private List<String> etudiantsList;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;
    private Button submit;

    private DBConnect dbConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        combomat = findViewById(R.id.combomatieres);
        comboetud = findViewById(R.id.comboetudiants);
        submit = findViewById(R.id.btninscrire);

        matiereList = new ArrayList<>();
        etudiantsList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, R.layout.spinner_item, matiereList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        adapter2 = new ArrayAdapter<>(this, R.layout.spinner_item, etudiantsList);
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);

        combomat.setAdapter(adapter);
        comboetud.setAdapter(adapter2);

        dbConnect = new DBConnect(this);

        MyApplication myApplication = (MyApplication) getApplication();
        User currentUser = myApplication.currentUser;

        retrieveMatiereData();
        retrieveEtudiantsData();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected matiere and nom_etudiant
                String selectedMatiere = combomat.getSelectedItem().toString();
                String selectedEtudiant = comboetud.getSelectedItem().toString();

                int idd = currentUser.getId();
                SQLiteDatabase insertDb = dbConnect.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("prof_id", idd);
                values.put("nom_etudiant", selectedEtudiant);
                values.put("nom_matiere", selectedMatiere);
                insertDb.insert("inscription", null, values);
                insertDb.close();
                Toast.makeText(InscriptionActivity.this, "Etudiant inscrit", Toast.LENGTH_SHORT).show();


            }

        });

    }

    private void retrieveMatiereData() {
        SQLiteDatabase db = dbConnect.getReadableDatabase();

        MyApplication myApplication = (MyApplication) getApplication();
        User currentUser = myApplication.currentUser;
        // Retrieve the user ID of the current user
        int idd = currentUser.getId();
        String selection = "prof_id=?";
        String[] selectionArgs = {String.valueOf(idd)};
        Cursor cursor = db.query("matiere", new String[]{"nom_matiere"}, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("nom_matiere");
            do {
                String nomMatiere = cursor.getString(columnIndex);

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
}







