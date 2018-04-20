package com.example.qq.projectmini2018;

/**
 * Created by Sarayut on 13/4/2561.
 */

public class Users {

    public int _user_id;
    public String _users_name;
    public String _password;

    public Users(){//for getAllUsers
    }

    public Users(int _user_id) {
        this._user_id = _user_id;
    }
}
