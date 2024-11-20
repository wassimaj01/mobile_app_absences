package com.example.mobile_app_absences;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MesMatieresActivity extends AppCompatActivity {

    private ListView mes_matieres;
    private ArrayAdapter<String> matieresAdapter;
    private List<String> matieresData;
    private DBConnect dbConnect;
    private String etudiant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_matieres);
        mes_matieres = findViewById(R.id.mes_matieres_listview);

        dbConnect = new DBConnect(this);
        matieresData = new ArrayList<>();
        matieresAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matieresData);
        mes_matieres.setAdapter(matieresAdapter);

        MyApplication myApplication = (MyApplication) getApplication();
        User currentUser = myApplication.currentUser;
        int idd = currentUser.getId();

        etudiant=getNomFromProfile2(idd);

        fetchMatieresFromDatabase();
    }
    private void fetchMatieresFromDatabase() {
        SQLiteDatabase db = dbConnect.getReadableDatabase();

        String[] columns = {"nom_matiere"};
        String selection = "nom_etudiant = ?";
        String[] selectionArgs = {etudiant};
        Cursor cursor = db.query("inscription", columns, selection, selectionArgs, null, null, null);


        int nomMatiereIndex = cursor.getColumnIndex("nom_matiere");


        matieresData.clear(); // Clear previous data

        if (cursor.moveToFirst()) {
            do {

                String nomMatiere = (nomMatiereIndex >= 0) ? cursor.getString(nomMatiereIndex) : "";


                String mat =  " Matiere : " + nomMatiere ;
                matieresData.add(mat);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        matieresAdapter.notifyDataSetChanged();
    }

    private String getNomFromProfile2(int userId) {
        SQLiteDatabase db = dbConnect.getReadableDatabase();
        String nomComplet = null;

        String[] columns = {"nom_complet"};
        String selection = "user_id = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query("profile2", columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("nom_complet");
            if (columnIndex != -1) {
                nomComplet = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        db.close();

        return nomComplet;
    }
}