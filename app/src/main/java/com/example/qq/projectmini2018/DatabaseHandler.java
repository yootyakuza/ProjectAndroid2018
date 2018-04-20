package com.example.qq.projectmini2018;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contactsManager";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_CONTACTS = "contacts";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_E_MAIL = "email";
    private static final String KEY_IMG_PATH = "image_path";

    private static final String TABLE_USERS = "users";
    //Users Table columns names
    private static final String KEY_USERS_ID = "users_id";
    private static final String KEY_USER_NAME = "names";
    private static final String KEY_PASSWORD = "password";
    SQLiteDatabase db;

    Context _context;

    private DatabaseReference databaseUser, databaseContact;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //private final String TAG = "PROJECT2018";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
        _context = context;
    }

    //If db exist, it not create.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT NOT NULL UNIQUE,"
                + KEY_PH_NO + " TEXT NOT NULL UNIQUE,"
                + KEY_E_MAIL + " TEXT NOT NULL UNIQUE,"
                + KEY_IMG_PATH + " TEXT NOT NULL UNIQUE" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + KEY_USERS_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER_NAME + " TEXT NOT NULL UNIQUE,"
                + KEY_PASSWORD + " TEXT NOT NULL UNIQUE" + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    private static final String DATABASE_ALTER_CONTACT_1 = "ALTER TABLE "
            + TABLE_CONTACTS + " ADD COLUMN " + KEY_IMG_PATH + " TEXT;";
    private static final String DATABASE_ALTER_USERS_1 = "ALTER TABLE "
            + TABLE_USERS;

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL(DATABASE_ALTER_CONTACT_1);
            db.execSQL(DATABASE_ALTER_USERS_1);
        }
    }

    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact._name);
        values.put(KEY_PH_NO, contact._phone_number);
        values.put(KEY_E_MAIL, contact._email);
        values.put(KEY_IMG_PATH, contact._img_path);
        long insert = db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public void updateOrInsert(Contact contact) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact._name);
        values.put(KEY_PH_NO, contact._phone_number);
        values.put(KEY_E_MAIL, contact._email);
        values.put(KEY_IMG_PATH, contact._img_path);

        String queryStr = KEY_NAME + " = '" + contact._name + "' AND " +
                KEY_E_MAIL + " = '" + contact._email + "' AND " +
                KEY_PH_NO + " = '" + contact._phone_number + "' AND " +
                KEY_IMG_PATH + " = '" + contact._img_path + "'";

        int rows = db.update(TABLE_CONTACTS, values, queryStr, null);

        String toastStr = "";
        if (rows == 0) {
            long insert = db.insert(TABLE_CONTACTS, null, values);
            if (insert != -1) {
                toastStr = "Inserted data already";
            } else {
                toastStr = "Error to insert data";
            }
        } else {
            toastStr = "Data already exist";
        }
        Toast.makeText(_context, toastStr, Toast.LENGTH_LONG).show();
        db.close();
    }

    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact._id = Integer.parseInt(cursor.getString(0));
                contact._name = cursor.getString(1);
                contact._email = cursor.getString(2);
                contact._phone_number = cursor.getString(3);
                contact._img_path = cursor.getString(4);
                contactList.add(contact);
                //firebase update ข้อมูลเมื่อเปิดหน้า view
                databaseContact = database.getReference("Contact");
//                String id = databaseContact.push().getKey();//set random key
                databaseContact.child(String.valueOf(contact._id)).setValue(contact);
                //test read from firebase
//                databaseContact.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String value = dataSnapshot.getValue(String.class);
//                        Log.d(TAG, "Value is: " + value);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "Failed to read Contact value.", databaseError.toException());
//                    }
//                });
            } while (cursor.moveToNext());
        }
        return contactList;
    }

    public Contact getTheContactFromName(String name) {
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " +
                KEY_NAME + " = '" + name + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Contact _contact = new Contact();
        if (cursor.moveToFirst()) {
            do {
                _contact._id = Integer.parseInt(cursor.getString(0));
                _contact._name = cursor.getString(1);
                _contact._email = cursor.getString(2);
                _contact._phone_number = cursor.getString(3);
                _contact._img_path = cursor.getString(4);
            } while (cursor.moveToNext());
        }

        //Don't close it yet
        //db.close();

        return _contact;
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact._name);
        values.put(KEY_E_MAIL, contact._email);
        values.put(KEY_PH_NO, contact._phone_number);

        //updating row
        return db.update(
                TABLE_CONTACTS,
                values,
                KEY_ID + "=  ?",
                new String[]{String.valueOf(contact._id)}
        );
    }

    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                TABLE_CONTACTS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(contact._id)}
        );
        //firebase if want to delete
        databaseContact = database.getReference("Contact");
        databaseContact.child(String.valueOf(contact._id)).removeValue();
        db.close();
    }

    //Table Users
    public void addUsers(Users users) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, users._users_name);
        values.put(KEY_PASSWORD, users._password);
        long insert = db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public List<Users> getAllUsers() {
        List<Users> userList = new ArrayList<Users>();//รอแก้กำหนดขนาดของ array เพื่อรองรับการเก็บข้อมูลได้ 20 รายการ
        String selectQuery = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Users users = new Users();
                users._user_id = Integer.parseInt(cursor.getString(0));
                users._users_name = cursor.getString(1);
                users._password = cursor.getString(2);
                userList.add(users);
                //firebase
                databaseUser = database.getReference("User");
                databaseUser.child(String.valueOf(users._user_id)).setValue(users);
            } while (cursor.moveToNext());
        }
        return userList;
    }

    public void deleteUsers(Users users) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                TABLE_USERS,
                KEY_USERS_ID + " = ?",
                new String[]{String.valueOf(users._user_id)}
        );
        //firebase if want to delete
//        databaseUser = database.getReference("User");
//        databaseUser.child(String.valueOf(users._user_id)).removeValue();
        db.close();
    }
}
