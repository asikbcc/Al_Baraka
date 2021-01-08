package com.nascenia.albarakahhajj;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref extends Application {


    public static final String USER_PREFERENCE="user_data";
    SharedPreferences sharedPreferences = null;

    public SharedPref(Context context)
    {
        if(context!=null)
            sharedPreferences  = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);

    }


    public void set_data(String key,String value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String get_data(String key)
    {
        return sharedPreferences.getString(key,"");

    }
}
