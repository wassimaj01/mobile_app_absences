package com.example.mobile_app_absences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Dashboard2Activity extends AppCompatActivity {

    private Button profile2;
    private  Button logout2;
    private Button absences;
    private Button matieres;
    private Button about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        profile2=findViewById(R.id.btnProfile2);
        profile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard2Activity.this, Profile2Activity.class);
                startActivity(intent);
            }
        });
        logout2=findViewById(R.id.btnLogout2);
        logout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform logout operation here
                // For example, you can clear user session, reset preferences, or navigate to the login screen

                // Example: Navigating to the LoginActivity
                Intent intent = new Intent(Dashboard2Activity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finish the DashboardActivity to prevent returning to it using the back button
            }
        });
        absences=findViewById(R.id.mesabsences);
        absences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard2Activity.this, MesAbsencesActivity.class);
                startActivity(intent);
            }
        });
        matieres=findViewById(R.id.btnMatiere2);
        matieres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard2Activity.this, MesMatieresActivity.class);
                startActivity(intent);
            }
        });

        about=findViewById(R.id.btnAbout2);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard2Activity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }
}