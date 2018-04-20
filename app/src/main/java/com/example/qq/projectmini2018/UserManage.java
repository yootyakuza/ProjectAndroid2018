package com.example.qq.projectmini2018;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


/**
 * Created by Sarayut on 13/4/2561.
 */

public class UserManage {

    private DatabaseHandler db;
    private List<Users> users;

    public UserManage(Context context) {
        db = new DatabaseHandler(context);
    }

    public boolean checkLoginValidate(String username, String password) {

        users = db.getAllUsers();

        String[] user = new String[users.size()];
        String[] pass = new String[users.size()];

        for (int i = 0; i < user.length; i++) {
            user[i] = users.get(i)._users_name;
            pass[i] = users.get(i)._password;

            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && username.equalsIgnoreCase(user[i]) && password.equalsIgnoreCase(pass[i])) {
                return true;
            }
        }

        return false;
    }

    public boolean registerUser(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        }
        Users user = new Users();
        user._users_name = username;
        user._password = password;
        db.addUsers(user);
        return true;
    }
}
