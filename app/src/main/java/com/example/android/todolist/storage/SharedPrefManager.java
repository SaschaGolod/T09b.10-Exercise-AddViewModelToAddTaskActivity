package com.example.android.todolist.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.todolist.R;

//Todo muss noch implementiert werden. Wäre gut das zu haben.
public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "my_shared_preff";

    private static SharedPrefManager mInstance;
    private Context mCtx;

    private SharedPrefManager(Context mCtx) {
        this.mCtx = mCtx;
    }


    public static synchronized SharedPrefManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(mCtx);
        }
        return mInstance;
    }


    public void saveLvlBool(String key, Boolean value) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(key, value);

        editor.apply();
    }

    public String getAllLvlAktiv() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String lvlfilter = "";

        if (sharedPreferences.getBoolean(mCtx.getString(R.string.key_lvl_1), true)) {
            lvlfilter += "1";
        }
        if (sharedPreferences.getBoolean(mCtx.getString(R.string.key_lvl_2), true)) {
            lvlfilter += "2";
        }
        if (sharedPreferences.getBoolean(mCtx.getString(R.string.key_lvl_3), true)) {
            lvlfilter += "3";
        }
        if (sharedPreferences.getBoolean(mCtx.getString(R.string.key_lvl_4), true)) {
            lvlfilter += "4";
        }
        if (sharedPreferences.getBoolean(mCtx.getString(R.string.key_lvl_5), true)) {
            lvlfilter += "5";
        }
        if (sharedPreferences.getBoolean(mCtx.getString(R.string.key_lvl_6), true)) {
            lvlfilter += "6";
        }
        return lvlfilter;

    }

    public String getStatusAktiv() {
        return mCtx.getString(R.string.boulderitem_status_default_value);
    }

    public void clear() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
