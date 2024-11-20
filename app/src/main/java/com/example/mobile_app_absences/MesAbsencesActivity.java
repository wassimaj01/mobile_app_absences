package com.example.mobile_app_absences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.Manifest;


public class MesAbsencesActivity extends AppCompatActivity {

    private ListView absencesListView;
    private Button justifyButton;

    private ArrayAdapter<String> absencesAdapter;
    private List<String> absencesData;

    private DBConnect dbConnect;
    private String etudiant;
    private int selectedAbsenceId;

    private static final int FILE_PICKER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_absences);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        absencesListView = findViewById(R.id.absences_listview);
        justifyButton = findViewById(R.id.justify_button);

        dbConnect = new DBConnect(this);
        absencesData = new ArrayList<>();
        absencesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, absencesData);
        absencesListView.setAdapter(absencesAdapter);

        MyApplication myApplication = (MyApplication) getApplication();
        User currentUser = myApplication.currentUser;
        int idd = currentUser.getId();

        etudiant = getNomFromProfile2(idd);

        fetchAbsencesFromDatabase();

        justifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedPosition = absencesListView.getCheckedItemPosition();
                if (selectedPosition != ListView.INVALID_POSITION) {
                    // Adjust based on your ID indexing
                    justifyAbsence();
                } else {
                    Toast.makeText(MesAbsencesActivity.this, "Please select an absence", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchAbsencesFromDatabase() {
        SQLiteDatabase db = dbConnect.getReadableDatabase();

        String[] columns = {"id", "nom_etudiant", "nom_matiere", "date"};
        String selection = "nom_etudiant = ?";
        String[] selectionArgs = {etudiant};
        Cursor cursor = db.query("absence", columns, selection, selectionArgs, null, null, null);

        int idIndex = cursor.getColumnIndex("id");
        int nomEtudiantIndex = cursor.getColumnIndex("nom_etudiant");
        int nomMatiereIndex = cursor.getColumnIndex("nom_matiere");
        int dateIndex = cursor.getColumnIndex("date");

        absencesData.clear(); // Clear previous data

        if (cursor.moveToFirst()) {
            do {
                int absenceId = (idIndex >= 0) ? cursor.getInt(idIndex) : 0;
                String nomEtudiant = (nomEtudiantIndex >= 0) ? cursor.getString(nomEtudiantIndex) : "";
                String nomMatiere = (nomMatiereIndex >= 0) ? cursor.getString(nomMatiereIndex) : "";
                String date = (dateIndex >= 0) ? cursor.getString(dateIndex) : "";

                String absence = nomEtudiant + " - " + nomMatiere + " - " + date;
                absencesData.add(absence);

                // Save the absence ID for later use
                if (absenceId != 0 && nomEtudiant.equals(etudiant)) {
                    selectedAbsenceId = absenceId;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        absencesAdapter.notifyDataSetChanged();
    }


    private void justifyAbsence() {
        SQLiteDatabase db = dbConnect.getReadableDatabase();

        String[] columns = {"justification"};
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(selectedAbsenceId)};
        Cursor cursor = db.query("absence", columns, selection, selectionArgs, null, null, null);

        String justification = null;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("justification");
            if (columnIndex != -1) {
                justification = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        db.close();

        if (justification != null) {
            Toast.makeText(this, "Already justified", Toast.LENGTH_SHORT).show();
        } else {
            // Open the Downloads folder using a file manager app
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri downloadsUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
            intent.setDataAndType(downloadsUri, "application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected file URI
            Uri fileUri = data.getData();

            // Store the file URI in the database
            storeFileUriInDatabase(fileUri);
        }
    }

    private void storeFileUriInDatabase(Uri fileUri) {
        String fileName = generateUniqueFileName();
        String fileExtension = getFileExtension(fileUri);
        String destinationPath = getFilesDir() + File.separator + fileName + "." + fileExtension;

        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            OutputStream outputStream = new FileOutputStream(destinationPath);

            copyStream(inputStream, outputStream);
            String etatjust= "justifiee";
            // Save the file URI in the "absence" table in the database
            SQLiteDatabase db = dbConnect.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("justification", destinationPath);
            values.put("etat",etatjust);
            db.update("absence", values, "id = ?", new String[]{String.valueOf(selectedAbsenceId)});

            db.close();

            // Show a success message or perform any other necessary actions
            Toast.makeText(this, "File uploaded successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
            Toast.makeText(this, "Failed to upload file!", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private String generateUniqueFileName() {
        // Generate a unique file name using a timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "justification_" + timeStamp ;
        return fileName;
    }

}
