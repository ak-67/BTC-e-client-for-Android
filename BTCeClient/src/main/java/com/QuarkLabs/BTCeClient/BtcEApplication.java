package com.QuarkLabs.BTCeClient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.QuarkLabs.BTCeClient.api.Api;
import com.QuarkLabs.BTCeClient.fragments.SettingsFragment;

public class BtcEApplication extends Application implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences defaultPreferences;

    private Api api;

    @Override
    public void onCreate() {
        super.onCreate();
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        /* // in Russia btc-e.com is blocked, so need to use mirror
        if ("RU".equalsIgnoreCase(getResources().getConfiguration().locale.getCountry())
                && !defaultPreferences.contains(SettingsFragment.KEY_USE_MIRROR)) {
            // commit instead of apply, because need to make sure changes are effective before
            // continuing
            defaultPreferences.edit()
                    .putBoolean(SettingsFragment.KEY_USE_MIRROR, true)
                    .apply();
        }*/

        defaultPreferences.registerOnSharedPreferenceChangeListener(this);

        SecurityManager securityManager = SecurityManager.getInstance(this);

        String apiKey = securityManager
                .decryptString(defaultPreferences.getString(SettingsFragment.KEY_API_KEY, ""));
        String apiSecret = securityManager
                .decryptString(defaultPreferences.getString(SettingsFragment.KEY_API_SECRET, ""));
        api = new Api(this, getHostUrl(), apiKey, apiSecret);
    }

    private void refreshApiCredentials() {
        SecurityManager securityManager = SecurityManager.getInstance(this);

        String apiKey = securityManager
                .decryptString(defaultPreferences.getString(SettingsFragment.KEY_API_KEY, ""));
        String apiSecret = securityManager
                .decryptString(defaultPreferences.getString(SettingsFragment.KEY_API_SECRET, ""));
        api.setCredentials(apiKey, apiSecret);
    }

    public String getHostUrl() {
        return "https://wex.nz";
    }

    public Api getApi() {
        return api;
    }

    public static BtcEApplication get(@NonNull Context context) {
        return (BtcEApplication) context.getApplicationContext();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (SettingsFragment.KEY_API_KEY.equals(key)
                || SettingsFragment.KEY_API_SECRET.equals(key)) {
            refreshApiCredentials();
        } else if (SettingsFragment.KEY_USE_MIRROR.equals(key)) {
            api.setHostUrl(getHostUrl());
        }
    }
}
