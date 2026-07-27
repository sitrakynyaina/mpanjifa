package com.client.mpanjifa.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.client.mpanjifa.R;
import com.client.mpanjifa.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister, btnBackToLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 2. Initialisation de la base de données
        dbHelper = new DatabaseHelper(this);

        // 3. Liaison avec les vues de activity_register.xml
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // 4. Écouteur sur le bouton Inscription
        btnRegister.setOnClickListener(v -> handleRegistration());

        // 5. Écouteur pour revenir à l'écran de Login
        if (btnBackToLogin != null) {
            btnBackToLogin.setOnClickListener(v -> finish());
        }
    }

    private void handleRegistration() {
        // La lecture du texte se fait AU MOMENT du clic
        String pseudo = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        // Validations
        if (pseudo.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
        } else if (dbHelper.checkPseudoExists(pseudo)) {
            Toast.makeText(this, "Ce pseudo est déjà pris", Toast.LENGTH_SHORT).show();
        } else {
            long result = dbHelper.registerUser(pseudo, email, password);
            if (result != -1) {
                Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                finish(); // Retourne à l'écran de Login
            } else {
                Toast.makeText(this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
            }
        }
    }
}