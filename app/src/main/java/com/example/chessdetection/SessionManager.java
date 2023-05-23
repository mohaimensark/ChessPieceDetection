package com.example.chessdetection;

import android.content.SharedPreferences;
import android.content.Context;

import java.util.HashMap;


// It is called shared preferance in java.
public class SessionManager {


    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_FULL_NAME = "fullName";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";

    public static final String KEY_PHONENUMBER = "phoneNumber";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DATE = "date";

    public static final String KEY_GENDER = "gender";


    public SessionManager(Context _context){
        context = _context;
        userSession = _context.getSharedPreferences("userLoginSessioon",Context.MODE_PRIVATE);
         editor = userSession.edit();

    }

    public void createLoginSession(String email, String name, String phone,String password)
    {
        editor.putBoolean(IS_LOGIN,true);


        editor.putString(KEY_FULL_NAME,name);

        editor.putString(KEY_PASSWORD,password);

        editor.putString(KEY_EMAIL,email);

        editor.putString(KEY_PHONENUMBER,phone);

        editor.commit();
    }

    public HashMap<String, String> getUsersDetailFromSession(){
        HashMap<String,String> userData = new HashMap<String,String>();

        userData.put(KEY_FULL_NAME,userSession.getString(KEY_FULL_NAME,null));
        userData.put(KEY_EMAIL,userSession.getString(KEY_EMAIL,null));
        userData.put(KEY_PASSWORD,userSession.getString(KEY_PASSWORD,null));
        userData.put(KEY_PHONENUMBER,userSession.getString(KEY_PHONENUMBER,null));

        return userData;
    }


    public boolean checkLogin(){
        if(userSession.getBoolean(IS_LOGIN,true)){
            return true;
        }
        else return false;
    }

    public void logoutUserFromSession(){
        editor.clear();
        editor.commit();
    }



}
