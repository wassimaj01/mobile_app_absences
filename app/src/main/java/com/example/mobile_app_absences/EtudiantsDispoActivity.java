package com.example.mobile_app_absences;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EtudiantsDispoActivity extends AppCompatActivity {

    private Spinner myComboBox;
    private ListView etudiantsListView;
    private List<String> matiereList;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> etudiantsAdapter;
    private List<String> etudiantsList;

    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etudiants_dispo);

        myComboBox = findViewById(R.id.myComboBox);
        etudiantsListView = findViewById(R.id.etudiants_listview);
        add = findViewById(R.id.btnetud);

        matiereList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.spinner_item, matiereList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        myComboBox.setAdapter(adapter);

        etudiantsList = new ArrayList<>();
        etudiantsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, etudiantsList);
        etudiantsListView.setAdapter(etudiantsAdapter);



        retrieveMatiereData();

        myComboBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedMatiere = matiereList.get(position);
                retrieveEtudiantsData(selectedMatiere);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EtudiantsDispoActivity.this, InscriptionActivity.class));
            }
        });
    }

    private void retrieveMatiereData() {
        DBConnect dbConnect = new DBConnect(this);
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

    private void retrieveEtudiantsData(String selectedMatiere) {
        DBConnect dbConnect = new DBConnect(this);
        SQLiteDatabase db = dbConnect.getReadableDatabase();

        // Query the inscription table to get the etudiants inscrits in the selected matiere
        String[] columns = {"nom_etudiant"};
        String selection = "nom_matiere = ?";
        String[] selectionArgs = {selectedMatiere};
        Cursor cursor = db.query("inscription", columns, selection, selectionArgs, null, null, null);

        etudiantsList.clear();

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("nom_etudiant");
            do {
                String nomEtudiant = cursor.getString(columnIndex);
                etudiantsList.add(nomEtudiant);
            } while (cursor.moveToNext());
        }

        cursor.close();
        etudiantsAdapter.notifyDataSetChanged();
    }


}

