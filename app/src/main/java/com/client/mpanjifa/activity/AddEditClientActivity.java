package com.client.mpanjifa.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.client.mpanjifa.R;
import com.client.mpanjifa.database.DatabaseHelper;
import com.client.mpanjifa.models.Client;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AddEditClientActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 102;
    private static final int TAKE_PHOTO_REQUEST = 103;

    private ImageView ivPhoto;
    private TextInputEditText etClientId, etNom, etDateNaissance;
    private String selectedPhotoUri = "";
    private Client currentClient = null;

    private Uri cameraImageUri = null;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_client);

        dbHelper = new DatabaseHelper(this);

        ivPhoto = findViewById(R.id.ivFormPhoto);
        etClientId = findViewById(R.id.etClientId);
        etNom = findViewById(R.id.etName);
        etDateNaissance = findViewById(R.id.etBirthDate);

        // Mode Édition : Récupération des données du client sélectionné
        if (getIntent().hasExtra("CLIENT_DATA")) {
            currentClient = (Client) getIntent().getSerializableExtra("CLIENT_DATA");
            if (currentClient != null) {
                etClientId.setText(currentClient.getIdcli());
                etClientId.setEnabled(false); // ID non modifiable en édition
                etNom.setText(currentClient.getNom());
                etDateNaissance.setText(currentClient.getDateNaissance() != null ? currentClient.getDateNaissance().toString() : "");
                selectedPhotoUri = currentClient.getPhoto() != null ? currentClient.getPhoto() : "";

                if (!selectedPhotoUri.isEmpty()) {
                    Glide.with(this)
                            .load(selectedPhotoUri)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(ivPhoto);
                }
            }
        }

        // Choix de la source photo (Appareil photo ou Galerie)
        findViewById(R.id.btnSelectPhoto).setOnClickListener(v -> showImagePickerDialog());

        // Choix de la date de naissance via MaterialDatePicker
        etDateNaissance.setOnClickListener(v -> showDatePicker());

        // Sauvegarde (Création ou Modification dans SQLite)
        findViewById(R.id.btnSave).setOnClickListener(v -> saveClient());

        // Annuler et fermer
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }

    private void showImagePickerDialog() {
        String[] options = {"Prendre une photo", "Choisir dans la galerie"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Photo du client");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraImageUri = createImageUri();

        if (cameraImageUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            startActivityForResult(intent, TAKE_PHOTO_REQUEST);
        } else {
            Toast.makeText(this, "Impossible d'accéder au stockage pour la photo", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createImageUri() {
        try {
            String fileName = "camera_photo_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getCacheDir(), fileName);

            return FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    file
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * DatePicker restreint aux dates passées (<= aujourd'hui)
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1);

        // Bloque toutes les dates situées dans le futur (après aujourd'hui)
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Sélectionner la date de naissance")
                .setCalendarConstraints(constraints)
                .setSelection(calendar.getTimeInMillis())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            TimeZone timeZone = TimeZone.getDefault();
            long offset = timeZone.getOffset(selection);
            Date date = new Date(selection - offset);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            etDateNaissance.setText(format.format(date));
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri sourceUri = null;

            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                sourceUri = data.getData();
            } else if (requestCode == TAKE_PHOTO_REQUEST) {
                sourceUri = cameraImageUri;
            }

            if (sourceUri != null) {
                String localPath = saveImageToInternalStorage(sourceUri);
                if (localPath != null) {
                    selectedPhotoUri = localPath;
                    Glide.with(this).load(selectedPhotoUri).into(ivPhoto);
                }
            }
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            String fileName = "client_photo_" + System.currentTimeMillis() + ".jpg";
            File destinationFile = new File(getFilesDir(), fileName);

            InputStream inputStream = getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return destinationFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Valide et sauvegarde les données avec l'ID personnalisé
     */
    private void saveClient() {
        String idCli = etClientId.getText() != null ? etClientId.getText().toString().trim() : "";
        String nom = etNom.getText() != null ? etNom.getText().toString().trim() : "";
        String dateStr = etDateNaissance.getText() != null ? etDateNaissance.getText().toString().trim() : "";

        if (idCli.isEmpty()) {
            etClientId.setError("L'identifiant est obligatoire");
            etClientId.requestFocus();
            return;
        }

        if (nom.isEmpty()) {
            etNom.setError("Le nom est obligatoire");
            etNom.requestFocus();
            return;
        }

        if (dateStr.isEmpty()) {
            etDateNaissance.setError("La date de naissance est obligatoire");
            return;
        }

        try {
            Date dateNaissance = Date.valueOf(dateStr);

            if (currentClient == null) {
                // Mode Ajout : Utilise l'ID saisi manuellement
                Client newClient = new Client(idCli, nom, dateNaissance, selectedPhotoUri);

                boolean isInserted = dbHelper.insertClient(newClient);
                if (isInserted) {
                    Toast.makeText(this, "Client ajouté avec succès", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Erreur : Cet ID existe déjà !", Toast.LENGTH_LONG).show();
                }
            } else {
                // Mode Modification : On conserve l'ID d'origine
                Client updatedClient = new Client(currentClient.getIdcli(), nom, dateNaissance, selectedPhotoUri);

                boolean isUpdated = dbHelper.updateClient(updatedClient);
                if (isUpdated) {
                    Toast.makeText(this, "Client mis à jour", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (IllegalArgumentException e) {
            etDateNaissance.setError("Format de date invalide (AAAA-MM-JJ)");
        }
    }
}