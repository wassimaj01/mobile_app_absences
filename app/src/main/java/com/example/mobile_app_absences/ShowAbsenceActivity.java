package com.example.mobile_app_absences;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class ShowAbsenceActivity extends AppCompatActivity {

    private Button pdf_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_absence);
        DBConnect dbConnect = new DBConnect(this);

        // Retrieve the selected absence data from the intent
        String seance = getIntent().getStringExtra("seance");
        String date = getIntent().getStringExtra("date");
        String penalite = getIntent().getStringExtra("penalite");
        String nomMatiere = getIntent().getStringExtra("nomMatiere");
        String nomEtudiant = getIntent().getStringExtra("nomEtudiant");

        String etat=getEtatForSelectedAbsence( seance,  date,  penalite,  nomMatiere,  nomEtudiant);

        // Find the TextViews in the layout
        TextView seanceTextView = findViewById(R.id.seanceTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView penaliteTextView = findViewById(R.id.penaliteTextView);
        TextView matiereTextView = findViewById(R.id.matiereTextView);
        TextView etudiantTextView = findViewById(R.id.etudiantTextView);
        TextView name=findViewById(R.id.nameEtu);
        TextView  etattextview= findViewById(R.id.etatTextView);
        seanceTextView.setText(seance);
        dateTextView.setText(date);
        penaliteTextView.setText(penalite);
        matiereTextView.setText(nomMatiere);
        etudiantTextView.setText(nomEtudiant);
        name.setText(nomEtudiant);
        etattextview.setText(etat);
        pdf_button = findViewById(R.id.pdf_button);
        pdf_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    int id_absence = getIdForSelectedAbsence(seance, date, penalite, nomMatiere, nomEtudiant);
                    SQLiteDatabase db = dbConnect.getReadableDatabase();
                    String[] columns = {"justification"};
                    String selection = "id=?";
                    String[] selectionArgs = {String.valueOf(id_absence)};
                    Cursor cursor = db.query("absence", columns, selection, selectionArgs, null, null, null);

                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex("justification");
                        if (columnIndex != -1) {
                            String justification = cursor.getString(columnIndex);
                            if (justification != null && !justification.isEmpty()) {
                                // PDF file exists and justification is not empty
                                File file = new File(justification);
                                if (file.exists()) {
                                    Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.mobile_app_absences.fileprovider", file);

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(uri, "application/pdf");
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                    try {
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        // Handle case where no PDF viewer application is available
                                        Toast.makeText(getApplicationContext(), "No PDF viewer installed", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle case where the PDF file doesn't exist
                                    Toast.makeText(getApplicationContext(), "PDF file not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle case where the absence is not justified or justification is null
                                Toast.makeText(getApplicationContext(), "Absence non justifiée", Toast.LENGTH_SHORT).show();
                            }
                        }


                    cursor.close();
                }
            }
        });

    }
    private String getEtatForSelectedAbsence(String seance, String dte, String pen, String nomMatiere, String nomEtudiant) {
        DBConnect dbConnect = new DBConnect(this);
        SQLiteDatabase db = dbConnect.getReadableDatabase();

        // Perform a database query to retrieve the etat for the selected absence
        String[] columns = {"etat"};
        String selection = "seance=? AND date=? AND penalite=? AND nom_matiere=? AND nom_etudiant=?";
        String[] selectionArgs = {seance, dte, pen, nomMatiere, nomEtudiant};
        Cursor cursor = db.query("absence", columns, selection, selectionArgs, null, null, null);

        String absenceEtat = null;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("etat");
            if (columnIndex != -1) {
                absenceEtat = cursor.getString(columnIndex);
            }
        }

        cursor.close();
        return absenceEtat;
    }

    private int getIdForSelectedAbsence(String seance, String dte, String pen, String nomMatiere, String nomEtudiant) {
        DBConnect dbConnect = new DBConnect(this);
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

}
