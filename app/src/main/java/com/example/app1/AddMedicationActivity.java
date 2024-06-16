// AddMedicationActivity.java
package com.example.app1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddMedicationActivity extends AppCompatActivity {

    private EditText medicationNameEditText;
    private TextView medicationTimeTextView;
    private Button addMedicationButton;
    private LinearLayout medicationListLayout;
    private ScrollView medicationListScrollView;
    private DBHelper dbHelper;
    private int userId;
    private View addMedicationForm;
    private SimpleDateFormat dateFormat;
    private ImageButton deleteMedicationIconButton;
    private ImageButton addMedicationIconButton;
    private ImageButton audioBookMusicButton;
    private boolean isDeleteMode = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        medicationNameEditText = findViewById(R.id.medicationNameEditText);
        medicationTimeTextView = findViewById(R.id.medicationTimeTextView);
        addMedicationButton = findViewById(R.id.addMedicationButton);
        medicationListLayout = findViewById(R.id.medicationListLayout);
        medicationListScrollView = findViewById(R.id.medicationListScrollView);
        addMedicationIconButton = findViewById(R.id.addMedicationIconButton);
        deleteMedicationIconButton = findViewById(R.id.deleteMedicationIconButton);
        addMedicationForm = findViewById(R.id.addMedicationForm);
        audioBookMusicButton = findViewById(R.id.audioBookMusicButton);

        dbHelper = new DBHelper(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Geçerli kullanıcı kimliğini al
        userId = getIntent().getIntExtra("userId", -1);

        addMedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedication();
            }
        });

        addMedicationIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAddMedicationForm();
            }
        });

        deleteMedicationIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDeleteMode = !isDeleteMode; // Silme modunu değiştir
                if (isDeleteMode) {
                    deleteMedicationIconButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                } else {
                    deleteMedicationIconButton.setImageResource(android.R.drawable.ic_delete);
                }
                loadMedications(); // İlaç listesini güncelle
            }
        });

        medicationTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTime();
            }
        });

        audioBookMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMedicationActivity.this, AudioBookMusicActivity.class);
                startActivity(intent);
            }
        });

        loadMedications();
    }

    private void toggleAddMedicationForm() {
        if (addMedicationForm.getVisibility() == View.GONE) {
            addMedicationForm.setVisibility(View.VISIBLE);
            medicationListScrollView.setVisibility(View.GONE);
            deleteMedicationIconButton.setVisibility(View.VISIBLE); // Tüm ilaçların yanındaki çarpıyı görünür yap
        } else {
            addMedicationForm.setVisibility(View.GONE);
            medicationListScrollView.setVisibility(View.VISIBLE);
            deleteMedicationIconButton.setVisibility(View.GONE); // Tüm ilaçların yanındaki çarpıyı gizle
        }
    }

    private void addMedication() {
        String name = medicationNameEditText.getText().toString().trim();
        String time = medicationTimeTextView.getText().toString().trim();

        if (!name.isEmpty() && !time.isEmpty()) {
            long newRowId = dbHelper.addMedication(userId, name, time);

            if (newRowId != -1) {
                medicationNameEditText.setText("");
                medicationTimeTextView.setText("Tarih ve Saat Seç");
                loadMedications();
                toggleAddMedicationForm();
            }
        }
    }

    private void loadMedications() {
        Cursor cursor = dbHelper.getMedications(userId);
        medicationListLayout.removeAllViews();

        while (cursor.moveToNext()) {
            @SuppressLint("InflateParams")
            View medicationView = getLayoutInflater().inflate(R.layout.medication_item, null);

            TextView medicationNameTextView = medicationView.findViewById(R.id.medicationNameTextView);
            TextView medicationTimeTextView = medicationView.findViewById(R.id.medicationTimeTextView);
            CheckBox medicationTakenCheckBox = medicationView.findViewById(R.id.medicationTakenCheckBox);
            ImageButton deleteMedicationButton = medicationView.findViewById(R.id.deleteMedicationButton);

            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            boolean taken = cursor.getInt(cursor.getColumnIndexOrThrow("taken")) > 0;

            medicationNameTextView.setText(name);
            medicationTimeTextView.setText(time);
            medicationTakenCheckBox.setChecked(taken);

            medicationTakenCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    dbHelper.updateMedicationStatus(id, isChecked));
            if (isDeleteMode) {
                deleteMedicationButton.setVisibility(View.VISIBLE);
            } else {
                deleteMedicationButton.setVisibility(View.GONE);
            }

            deleteMedicationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(AddMedicationActivity.this)
                            .setTitle("İlaç Sil")
                            .setMessage("Bu ilacı silmek istediğinizden emin misiniz?")
                            .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbHelper.deleteMedication(id);
                                    loadMedications();
                                }
                            })
                            .setNegativeButton("Hayır", null)
                            .show();
                }
            });

            medicationListLayout.addView(medicationView);
        }

        cursor.close();
    }

    private void pickDateTime() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(AddMedicationActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);
                                        medicationTimeTextView.setText(dateFormat.format(calendar.getTime()));
                                    }
                                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                        timePickerDialog.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
