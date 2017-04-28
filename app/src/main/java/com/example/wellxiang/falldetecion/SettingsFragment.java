package com.example.wellxiang.falldetecion;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by LiuWeixiang on 2017/3/1.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    public static final String KEY_NAME = "pre_key_name";
    public static final String KEY_SEX = "pre_key_sex";
    public static final String KEY_AGE = "pre_key_age";
    public static final String KEY_ALERT = "pre_key_alert";
    public static final String KEY_VIBRATE = "pre_key_vibrate";
    public static final String KEY_PHONE = "pre_key_phone";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("liuweixiang", "SettingsFragment.onsharedPreferenceChanged");
//        System.out.print("key是： " + key);
        Log.d("liuweixiang", key);
//        if(key.equals(KEY_AGE)){
//            Preference agePre = findPreference(key);
//            agePre.setSummary(sharedPreferences.getString(key, "请输入年龄"));
//        }
        switch (key){
            case KEY_NAME:
                Preference namePre = findPreference(key);
                namePre.setSummary(sharedPreferences.getString(key, ""));
                break;
            case KEY_SEX:
                Preference sexPre = findPreference(key);
                sexPre.setSummary(sharedPreferences.getString(key, ""));
                break;
            case KEY_AGE:
                Preference agePre = findPreference(key);
                agePre.setSummary(sharedPreferences.getString(key, ""));
                break;
            case KEY_ALERT:
                Preference alertPre = findPreference(key);
                alertPre.setSummary(sharedPreferences.getString(key, ""));
                break;
            case KEY_VIBRATE:
                break;
            case KEY_PHONE:
                Preference phonePre = findPreference(key);
                phonePre.setSummary(sharedPreferences.getString(key, ""));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
