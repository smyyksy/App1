package com.example.app1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!username.isEmpty() && !password.isEmpty()) {
            Cursor cursor = database.query("users",
                    new String[]{"id"},
                    "username = ? AND password = ?",
                    new String[]{username, password},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                cursor.close();

                // Giriş başarılı, ExperienceActivity'ye yönlendir
                Intent intent = new Intent(LoginActivity.this, ExperienceActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Giriş başarısız. Kullanıcı adı veya şifre yanlış.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
        }
    }
}
