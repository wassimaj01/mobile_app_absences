package com.example.mobile_app_absences;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView descriptionTextView = findViewById(R.id.description_textview);
        descriptionTextView.setText(getString(R.string.app_description));
    }
}
