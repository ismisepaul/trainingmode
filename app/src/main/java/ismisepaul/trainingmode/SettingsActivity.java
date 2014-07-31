package ismisepaul.trainingmode;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        final ListPreference listPref_launchApp =
                (ListPreference) findPreference("pref_selectAppToLaunch");

        setListPrefLaunchApp(listPref_launchApp, getApplicationContext());

        listPref_launchApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setListPrefLaunchApp(listPref_launchApp, getApplicationContext());
                return true;

            }
        });
    }


    protected static void setListPrefLaunchApp(ListPreference listPreference, Context context) {
        final PackageManager pm = context.getPackageManager();
        final int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES;

        List<ApplicationInfo> packages = pm.getInstalledApplications(flags);
        List<String> array_app_names = new ArrayList<String>();
        List<String> array_package_names = new ArrayList<String>();

        for (ApplicationInfo appInfo : packages) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // System application
            } else {
                // User installed applications
                String label = appInfo.loadLabel(context.getPackageManager()).toString();
                String packageName = appInfo.packageName;
                if(label != null) {
                    array_app_names.add(label);
                    array_package_names.add(packageName);
                }


                //Log.d(TAG, "Installed package :" + appInfo.packageName);
                //Log.d(TAG, "Source dir : " + appInfo.sourceDir);
                //Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(appInfo.packageName));
            }

        }

        String[] yo = array_app_names.toArray(new String[array_app_names.size()]);
        String[] yoyo = array_package_names.toArray(new String[array_package_names.size()]);

        for(int i=0; i < 1; i++){
            for(int j=0; j < 10; j++){
                Log.v(yo[j], yoyo[j]);
            }

        }

        Arrays.sort(yo);

        for(int i=0; i < 1; i++){
            for(int j=0; j < 10; j++){
                Log.d("Sorted: "+yo[j], yoyo[j]);
            }

        }

        CharSequence[] entries =
                array_app_names.toArray(new CharSequence[array_app_names.size()]);
        CharSequence[] entryValues =
                array_package_names.toArray(new CharSequence[array_package_names.size()]);


        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);
    }


}
