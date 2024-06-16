package com.example.app1;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!username.isEmpty() && !password.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);

            long newRowId = database.insert("users", null, values);

            if (newRowId != -1) {
                Toast.makeText(RegisterActivity.this, "Kayıt başarılı.", Toast.LENGTH_SHORT).show();
                finish(); // Kayıt başarılıysa, kayıt ekranını kapat
            } else {
                Toast.makeText(RegisterActivity.this, "Kayıt başarısız. Kullanıcı adı zaten mevcut.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegisterActivity.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
        }
    }
}
