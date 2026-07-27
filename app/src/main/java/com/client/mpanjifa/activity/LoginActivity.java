package com.client.mpanjifa.activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.client.mpanjifa.R;
import com.client.mpanjifa.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoToRegister = findViewById(R.id.btnGoToRegister);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
            else if(!dbHelper.checkUserCredentials(username,password)){
                Toast.makeText(this, "Utilisateur inéxistant", Toast.LENGTH_SHORT).show();
            }
            else {
                // Redirection vers la liste des clients
                Intent intent = new Intent(LoginActivity.this, ClientListActivity.class);
                startActivity(intent);
                finish(); // Ferme l'écran de Login pour ne pas y revenir avec le bouton retour
            }
        });

        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }


}