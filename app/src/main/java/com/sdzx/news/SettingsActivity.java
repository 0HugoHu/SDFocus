package com.sdzx.news;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.sdzx.tools.ApplicationHelper;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        Preference pf=(Preference)findPreference("clear_cache");
        pf.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ApplicationHelper.deleteFolderFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "SDFocus/cache", true);
                ApplicationHelper.deleteFolderFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bichooser", true);
                Toast.makeText(SettingsActivity.this, "已删除所有缓存", Toast.LENGTH_SHORT).show();
                ApplicationHelper.makeRootDirectory("/SDFocus/cache/");
                return true;
            }
        });

        Preference pf1=(Preference)findPreference("clear_help_flag");
        pf1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().clear().commit();
                Toast.makeText(SettingsActivity.this, "已删除所有标记", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference pf2=(Preference)findPreference("show_user");
        pf2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setClass(SettingsActivity.this, UserActivity.class);
                intent.putExtra("type",0);
                startActivity(intent);
                return true;
            }
        });

    }
}
