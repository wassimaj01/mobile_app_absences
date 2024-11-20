package com.example.mobile_app_absences;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText inputUsername, inputEmail, inputPassword;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialize views
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        radioGroup = findViewById(R.id.radioGroup);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String role = "";

                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    role = selectedRadioButton.getText().toString();
                }

                Log.d("RegisterActivity", "username: " + username);
                Log.d("RegisterActivity", "email: " + email);
                Log.d("RegisterActivity", "password: " + password);
                Log.d("RegisterActivity", "role: " + role);

                // insert the user data into the database
                DBConnect dbHelper = new DBConnect(RegisterActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("username", username);
                values.put("email", email);
                values.put("password", password);
                values.put("role", role);
                db.insert("users", null, values);

                // display a toast to indicate that the data has been saved
                Toast.makeText(RegisterActivity.this, "User data saved", Toast.LENGTH_SHORT).show();
            }
        });
        TextView btn=findViewById(R.id.alreadyHaveAccount);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, com.example.mobile_app_absences.LoginActivity.class));
            }
        });
    }
}

