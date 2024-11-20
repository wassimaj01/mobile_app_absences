package com.example.mobile_app_absences;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBConnect extends SQLiteOpenHelper {
    public DBConnect(@Nullable Context context){
        super(context, "GestionAbsences", null, 10);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT, password TEXT, role TEXT)");
        db.execSQL("CREATE TABLE profile(id INTEGER PRIMARY KEY AUTOINCREMENT, nom_complet TEXT, cin TEXT, numero_tele TEXT, specialite TEXT, user_id INTEGER, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE profile2(id INTEGER PRIMARY KEY AUTOINCREMENT, nom_complet TEXT, cne TEXT, numero_tele TEXT,niveau_scolaire TEXT, branche TEXT, user_id INTEGER, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE matiere(id INTEGER PRIMARY KEY AUTOINCREMENT, nom_matiere TEXT, categorie TEXT, description TEXT, prof_id INTEGER, nom_prof TEXT, FOREIGN KEY(prof_id) REFERENCES profile(user_id) ON DELETE CASCADE, FOREIGN KEY(nom_prof) REFERENCES profile(nom_complet) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE inscription(id INTEGER PRIMARY KEY AUTOINCREMENT,prof_id INTEGER,nom_etudiant TEXT,nom_matiere TEXT,FOREIGN KEY(prof_id) REFERENCES profile(user_id) ON DELETE CASCADE,FOREIGN KEY(nom_etudiant) REFERENCES profile2(nom_complet) ON DELETE CASCADE,FOREIGN KEY(nom_matiere) REFERENCES matiere(nom_matiere) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE absence(id INTEGER PRIMARY KEY AUTOINCREMENT,prof_id INTEGER,nom_etudiant TEXT,nom_matiere TEXT,cne TEXT,seance TEXT,date TEXT,justification TEXT,penalite TEXT,etat TEXT DEFAULT 'non justifié',FOREIGN KEY(prof_id) REFERENCES profile(user_id) ON DELETE CASCADE,FOREIGN KEY(nom_etudiant) REFERENCES profile2(nom_complet) ON DELETE CASCADE,FOREIGN KEY(nom_matiere) REFERENCES matiere(nom_matiere) ON DELETE CASCADE,FOREIGN KEY(cne) REFERENCES profile2(cne) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if needed
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS profile");
        db.execSQL("DROP TABLE IF EXISTS matiere");
        db.execSQL("DROP TABLE IF EXISTS profile2");
        db.execSQL("DROP TABLE IF EXISTS inscription");
        db.execSQL("DROP TABLE IF EXISTS absence");

        // Create new tables
        onCreate(db);
    }

    public String getEmailForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"email"};
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);

        String email = null;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("email");
            if (columnIndex != -1) {
                email = cursor.getString(columnIndex);
            }
        }

        cursor.close();
        return email;
    }
}


