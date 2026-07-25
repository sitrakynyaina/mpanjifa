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

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "mpanjifa.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CLIENT = "CLIENT";
    public static final String COL_ID = "id_cli";
    public static final String COL_NOM = "nom";
    public static final String COL_DATENAISSANCE = "datenais";
    public static final String COL_PHOTO = "photo";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_CLIENT + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOM + " TEXT, " +
                COL_DATENAISSANCE + " TEXT, " +
                COL_PHOTO + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int olversion,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_CLIENT);
        onCreate(db);

    }

    public boolean insertClient(Client client){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ID,client.getIdcli());
        cv.put(COL_NOM, client.getNom());
        cv.put(COL_DATENAISSANCE, client.getDateNaissance().toString());
        cv.put(COL_PHOTO, client.getPhoto());

        long result = db.insert(TABLE_CLIENT,null,cv);
        return result != -1;
    }

    public List<Client> getAllClients(){
        List<Client> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLIENT,null);
        if(cursor.moveToFirst()){
            do {
                Date date = Date.valueOf(cursor.getString(2));
                Client c = new Client(cursor.getString(0),
                        cursor.getString(1),
                        date,
                        cursor.getString(3)
                );
                list.add(c);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean updateClient(Client client){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOM,client.getNom());
        cv.put(COL_DATENAISSANCE,client.getDateNaissance().toString());
        cv.put(COL_PHOTO,client.getPhoto());
        int result = db.update(TABLE_CLIENT,cv,COL_ID + "=?",new String[]{String.valueOf(client.getIdcli())});
        return result > 0;
    }

    public boolean deleteClient(int idcli){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CLIENT,COL_ID + "=?",new String[]{String.valueOf(idcli)});
        return result > 0;
    }

    public List<Client> serahcClientsByName(String keyword){
        List<Client>  list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLIENT + " WHERE " + COL_NOM + " LIKE ?",new String[]{"%" + keyword + "%"});
        if(cursor.moveToFirst()){
            do {
                Date date = Date.valueOf(cursor.getString(2));
                Client c = new Client(cursor.getString(0),
                        cursor.getString(1),
                        date,
                        cursor.getString(3)
                );
                list.add(c);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public List<Client> serahcClientsById(String keyword){
        List<Client>  list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLIENT + " WHERE " + COL_ID + " LIKE ?",new String[]{"%" + keyword + "%"});
        if(cursor.moveToFirst()){
            do {
                Date date = Date.valueOf(cursor.getString(2));
                Client c = new Client(cursor.getString(0),
                        cursor.getString(1),
                        date,
                        cursor.getString(3)
                );
                list.add(c);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
