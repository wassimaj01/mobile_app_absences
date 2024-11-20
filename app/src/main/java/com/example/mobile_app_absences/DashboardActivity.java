package com.example.mobile_app_absences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends AppCompatActivity {
    private Button profile;
    private Button matiere;

    private Button etudiants;
    private Button abscents;
    private Button about;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        profile = findViewById(R.id.btnProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, CompleteProfileActivity.class);
                startActivity(intent);
            }
        });
        matiere=findViewById(R.id.btnMatiere);
        matiere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, MatiereActivity.class);
                startActivity(intent);
            }
        });

        etudiants=findViewById(R.id.btnEtudiants);
        etudiants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, EtudiantsDispoActivity.class);
                startActivity(intent);
            }
        });
        abscents=findViewById(R.id.btnAbscents);
        abscents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, AbsenceActivity.class);
                startActivity(intent);
            }
        });

        logout=findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform logout operation here
                // For example, you can clear user session, reset preferences, or navigate to the login screen

                // Example: Navigating to the LoginActivity
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finish the DashboardActivity to prevent returning to it using the back button
            }
        });
        about=findViewById(R.id.btnAbout);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }
}