package com.example.mobile_app_absences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private DBConnect dbc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailEditText = findViewById(R.id.inputEmail);
        mPasswordEditText = findViewById(R.id.inputPassword);
        mLoginButton = findViewById(R.id.btnlogin);

        dbc = new DBConnect(LoginActivity.this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email or password field is empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the email and password match with the data in the database
                    DBConnect dbHelper = new DBConnect(LoginActivity.this);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String[] projection = {
                            "id",
                            "username",
                            "email",
                            "password",
                            "role"
                    };
                    String selection = "email = ? AND password = ?";
                    String[] selectionArgs = { email, password };
                    Cursor cursor = db.query(
                            "users",
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
                        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                        String emaill = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                        String passwordd = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                        User currentUser = new User(userId, username, emaill, passwordd, role);
                        MyApplication.currentUser = currentUser;

                        if (role.equals("ENSEIGNANT")) {
                            // Check if the teacher has a profile
                            boolean hasProfile = checkIfTeacherHasProfile(userId);
                            if (hasProfile) {
                                // Redirect to the dashboard activity for teachers
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            } else {
                                // Redirect to the profile completion activity for teachers
                                Intent intent = new Intent(LoginActivity.this, CompleteProfileActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            // Check if the student has a profile
                            boolean hasProfile = checkIfStudentHasProfile(userId);
                            if (hasProfile) {
                                // Redirect to the dashboard activity for students
                                Intent intent = new Intent(LoginActivity.this, Dashboard2Activity.class);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            } else {
                                // Redirect to the profile completion activity for students
                                Intent intent = new Intent(LoginActivity.this, Profile2Activity.class);
                                startActivity(intent);
                            }
                        }
                    } else {
                        // Login failed
                        Toast.makeText(LoginActivity.this, "Invalid Email or password", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                    db.close();
                }
            }
        });



        TextView btn=findViewById(R.id.textViewSignUp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

    private boolean checkIfTeacherHasProfile(int userId) {
        SQLiteDatabase db = dbc.getReadableDatabase();
        String[] projection = { "user_id" };
        String selection = "user_id = ?";
        String[] selectionArgs = { String.valueOf(userId) };
        Cursor cursor = db.query(
                "profile",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean hasProfile = cursor.getCount() > 0;
        cursor.close();
        return hasProfile;
    }

    private boolean checkIfStudentHasProfile(int userId) {
        SQLiteDatabase db = dbc.getReadableDatabase();
        String[] projection = { "user_id" };
        String selection = "user_id = ?";
        String[] selectionArgs = { String.valueOf(userId) };
        Cursor cursor = db.query(
                "profile2",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean hasProfile = cursor.getCount() > 0;
        cursor.close();
        return hasProfile;
    }

}
