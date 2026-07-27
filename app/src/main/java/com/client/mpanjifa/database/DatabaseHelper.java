package com.client.mpanjifa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.client.mpanjifa.models.Client;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nom et version de la base de données (Incrémentée à 2 pour forcer la mise à jour de la table)
    private static final String DATABASE_NAME = "mpanjifa.db";
    private static final int DATABASE_VERSION = 2;

    // --- TABLE CLIENT ---
    public static final String TABLE_CLIENT = "client";
    public static final String COL_CLIENT_ID = "id_cli";
    public static final String COL_CLIENT_NOM = "nom";
    public static final String COL_CLIENT_DATENAISSANCE = "datenais";
    public static final String COL_CLIENT_PHOTO = "photo";

    // --- TABLE USERS (Utilisateurs / Inscription) ---
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_PSEUDO = "pseudo";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";

    // Requête SQL de création de la table CLIENT (id_cli TEXT PRIMARY KEY)
    private static final String CREATE_TABLE_CLIENT = "CREATE TABLE " + TABLE_CLIENT + " (" +
            COL_CLIENT_ID + " TEXT PRIMARY KEY, " +
            COL_CLIENT_NOM + " TEXT NOT NULL, " +
            COL_CLIENT_DATENAISSANCE + " TEXT, " +
            COL_CLIENT_PHOTO + " TEXT);";

    // Requête SQL de création de la table USERS (avec PSEUDO UNIQUE)
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USER_PSEUDO + " TEXT UNIQUE NOT NULL, " +
            COL_USER_EMAIL + " TEXT NOT NULL, " +
            COL_USER_PASSWORD + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLIENT);
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // =========================================================================
    //                    MÉTHODES DE GESTION DES CLIENTS (CRUD)
    // =========================================================================

    public boolean insertClient(Client client) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Insertion manuelle du String ID saisi par l'utilisateur
        cv.put(COL_CLIENT_ID, client.getIdcli());
        cv.put(COL_CLIENT_NOM, client.getNom());
        cv.put(COL_CLIENT_DATENAISSANCE, client.getDateNaissance().toString());
        cv.put(COL_CLIENT_PHOTO, client.getPhoto());

        long result = db.insert(TABLE_CLIENT, null, cv);
        db.close();
        return result != -1;
    }

    public List<Client> getAllClients() {
        List<Client> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLIENT, null);

        if (cursor.moveToFirst()) {
            do {
                Date date = Date.valueOf(cursor.getString(2));
                Client c = new Client(
                        cursor.getString(0), // id_cli (String)
                        cursor.getString(1), // nom
                        date,                // datenais
                        cursor.getString(3)  // photo
                );
                list.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean updateClient(Client client) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_CLIENT_NOM, client.getNom());
        cv.put(COL_CLIENT_DATENAISSANCE, client.getDateNaissance().toString());
        cv.put(COL_CLIENT_PHOTO, client.getPhoto());

        int result = db.update(TABLE_CLIENT, cv, COL_CLIENT_ID + "=?", new String[]{client.getIdcli()});
        db.close();
        return result > 0;
    }

    public boolean deleteClient(String idcli) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CLIENT, COL_CLIENT_ID + "=?", new String[]{idcli});
        db.close();
        return result > 0;
    }

    public List<Client> searchClientsByName(String keyword) {
        List<Client> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLIENT + " WHERE " + COL_CLIENT_NOM + " LIKE ?", new String[]{"%" + keyword + "%"});

        if (cursor.moveToFirst()) {
            do {
                Date date = Date.valueOf(cursor.getString(2));
                Client c = new Client(
                        cursor.getString(0),
                        cursor.getString(1),
                        date,
                        cursor.getString(3)
                );
                list.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Client> searchClientsById(String keyword) {
        List<Client> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLIENT + " WHERE " + COL_CLIENT_ID + " LIKE ?", new String[]{"%" + keyword + "%"});

        if (cursor.moveToFirst()) {
            do {
                Date date = Date.valueOf(cursor.getString(2));
                Client c = new Client(
                        cursor.getString(0),
                        cursor.getString(1),
                        date,
                        cursor.getString(3)
                );
                list.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // =========================================================================
    //                    MÉTHODES DE GESTION DES UTILISATEURS
    // =========================================================================

    public long registerUser(String pseudo, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PSEUDO, pseudo.trim());
        values.put(COL_USER_EMAIL, email.trim());
        values.put(COL_USER_PASSWORD, password);

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public boolean checkPseudoExists(String pseudo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USER_PSEUDO + "=?",
                new String[]{pseudo.trim()},
                null, null, null
        );

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public boolean checkUserCredentials(String pseudo, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USER_PSEUDO + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{pseudo.trim(), password},
                null, null, null
        );

        boolean isValid = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return isValid;
    }
}