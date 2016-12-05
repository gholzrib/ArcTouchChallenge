package gholzrib.arctouchchallenge.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import gholzrib.arctouchchallenge.core.models.TMDBConfiguration;

/**
 * Created by Gunther Ribak on 01/12/2016.
 * For more information contact me
 * through guntherhr@gmail.com
 */
public class PreferencesManager {

    private static final String TMDB_CONFIGURATION_KEY = "tmdb_configuration_key";
    private static final String TMDB_CONFIGURATION_LAST_UPDATE_KEY = "tmdb_configuration_last_update_key";

    private static SharedPreferences getDefaultPreferencesManager(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static void setInt(Context context, String key, int value) {
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        return preferences.getInt(key, defaultValue);
    }

    private static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        return preferences.getBoolean(key, defaultValue);
    }

    private static void setString(Context context, String key, String value) {
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getString(Context context, String key, String defaultValue) {
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        return preferences.getString(key, defaultValue);
    }

    private static Boolean containsValue(Context context, String key){
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        return preferences.contains(key);
    }

    public static void clearPreferences(Context context){
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void setTmdbConfiguration(Context context, TMDBConfiguration configuration){
        String configJson = null;
        if (configuration != null) {
            Gson gson = new Gson();
            configJson = gson.toJson(configuration);
        }
        setString(context, TMDB_CONFIGURATION_KEY, configJson);
    }

    public static TMDBConfiguration getTmdbConfiguration(Context context){
        Gson gson = new Gson();
        String json = getString(context, TMDB_CONFIGURATION_KEY, null);
        if (json != null) {
            try {
                return gson.fromJson(json, TMDBConfiguration.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Boolean containsTmdbConfiguration(Context context){
        return containsValue(context, TMDB_CONFIGURATION_KEY);
    }

    public static void setTmdbConfigurationLastUpdateKey(Context context, String date) {
        setString(context, TMDB_CONFIGURATION_LAST_UPDATE_KEY, date);
    }

    public static String getTmdbConfigurationLastUpdateKey(Context context) {
        return getString(context, TMDB_CONFIGURATION_LAST_UPDATE_KEY, null);
    }

    public static boolean containsTmdbConfigurationLastUpdateKey(Context context) {
        return containsValue(context, TMDB_CONFIGURATION_LAST_UPDATE_KEY);
    }
}
