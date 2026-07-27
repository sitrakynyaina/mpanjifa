package com.client.mpanjifa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mpanjifa.R;
import com.client.mpanjifa.adapter.ClientAdapter;
import com.client.mpanjifa.database.DatabaseHelper;
import com.client.mpanjifa.models.Client;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ClientListActivity extends AppCompatActivity implements ClientAdapter.OnClientClickListener {
    private static final int REQUEST_CODE_ADD_EDIT = 101;

    private RecyclerView rvClients;
    private ClientAdapter adapter;
    private EditText etSearch;

    // Source de vérité (tous les clients de SQLite)
    private List<Client> fullClientList = new ArrayList<>();
    // Liste filtrée envoyée au RecyclerView
    private List<Client> displayedList = new ArrayList<>();

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        dbHelper = new DatabaseHelper(this);

        rvClients = findViewById(R.id.rvClients);
        etSearch = findViewById(R.id.etSearch);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddClient);

        // Configuration du LayoutManager et de l'Adapter
        rvClients.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClientAdapter(displayedList, this);

        // CORRECTION 1 : Ne pas oublier de lier l'adapter au RecyclerView !
        rvClients.setAdapter(adapter);

        // Écouteur pour le champ de recherche
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filterClients(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Clic sur le FAB Ajouter
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ClientListActivity.this, AddEditClientActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_EDIT);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharge les données à chaque retour sur l'activité
        refreshClientList();
    }

    /**
     * Charge les données de SQLite dans fullClientList puis applique le filtre
     */
    private void refreshClientList() {
        fullClientList.clear();
        fullClientList.addAll(dbHelper.getAllClients());

        String currentQuery = etSearch.getText() != null ? etSearch.getText().toString() : "";
        filterClients(currentQuery);
    }

    /**
     * Filtre la liste selon le nom ou l'identifiant
     */
    private void filterClients(String query) {
        displayedList.clear();

        if (query.trim().isEmpty()) {
            displayedList.addAll(fullClientList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (Client client : fullClientList) {
                boolean matchNom = client.getNom() != null && client.getNom().toLowerCase().contains(filterPattern);
                boolean matchId = client.getIdcli() != null && client.getIdcli().toLowerCase().contains(filterPattern);

                if (matchNom || matchId) {
                    displayedList.add(client);
                }
            }
        }

        // Notification à l'adapter
        adapter.updateData(displayedList);
    }

    @Override
    public void OnClientClick(Client client) {
        openEditScreen(client);
    }

    @Override
    public void OnClientLongClick(Client client, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenu().add("Modifier");
        popup.getMenu().add("Supprimer");

        popup.setOnMenuItemClickListener(item -> {
            if ("Modifier".equals(item.getTitle())) {
                openEditScreen(client);
                return true;
            } else if ("Supprimer".equals(item.getTitle())) {
                // CORRECTION 2 : Supprimer en base SQLite
                boolean isDeleted = dbHelper.deleteClient(client.getIdcli());
                if (isDeleted) {
                    Toast.makeText(this, "Client supprimé", Toast.LENGTH_SHORT).show();
                    refreshClientList();
                } else {
                    Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void openEditScreen(Client client) {
        Intent intent = new Intent(this, AddEditClientActivity.class);
        intent.putExtra("CLIENT_DATA", client);
        startActivityForResult(intent, REQUEST_CODE_ADD_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_EDIT && resultCode == RESULT_OK) {
            refreshClientList();
        }
    }
}