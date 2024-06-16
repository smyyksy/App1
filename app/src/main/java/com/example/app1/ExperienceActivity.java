package com.example.app1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ExperienceActivity extends AppCompatActivity {

    private EditText experienceEditText;
    private Button saveExperienceButton;
    private Button goToAddMedicationButton;
    private LinearLayout experienceListLayout;
    private DBHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience);

        experienceEditText = findViewById(R.id.experienceEditText);
        saveExperienceButton = findViewById(R.id.saveExperienceButton);
        goToAddMedicationButton = findViewById(R.id.goToAddMedicationButton);
        experienceListLayout = findViewById(R.id.experienceListLayout);

        dbHelper = new DBHelper(this);

        userId = getIntent().getIntExtra("userId", -1);

        saveExperienceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExperience();
            }
        });

        goToAddMedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExperienceActivity.this, AddMedicationActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        loadExperiences();
    }

    private void saveExperience() {
        String experienceText = experienceEditText.getText().toString().trim();

        if (!experienceText.isEmpty()) {
            long newRowId = dbHelper.addExperience(userId, experienceText);

            if (newRowId != -1) {
                Toast.makeText(ExperienceActivity.this, "Deneyim kaydedildi.", Toast.LENGTH_SHORT).show();
                experienceEditText.setText("");
                loadExperiences();
            } else {
                Toast.makeText(ExperienceActivity.this, "Deneyim kaydedilirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ExperienceActivity.this, "Lütfen bir deneyim girin.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadExperiences() {
        Cursor cursor = dbHelper.getAllExperiences();
        experienceListLayout.removeAllViews();

        while (cursor.moveToNext()) {
            final int experienceId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            String experience = cursor.getString(cursor.getColumnIndexOrThrow("experience"));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));

            View experienceView = getLayoutInflater().inflate(R.layout.experience_item, null);
            TextView experienceTextView = experienceView.findViewById(R.id.experienceTextView);
            TextView usernameTextView = experienceView.findViewById(R.id.usernameTextView);
            TextView timestampTextView = experienceView.findViewById(R.id.timestampTextView);


            usernameTextView.setText(dbHelper.getUsername(userId));
            experienceTextView.setText(experience);
            timestampTextView.setText(timestamp);



            experienceListLayout.addView(experienceView);
        }
        cursor.close();
    }
}
