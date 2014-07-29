package ismisepaul.trainingmode;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        //final ListPreference listPref_trainingApps =
         //       (ListPreference) findPreference("pref_selectTrainingApp");
        final ListPreference listPref_otherApps =
                (ListPreference) findPreference("pref_selectOtherApp");

        //required if no entries or entryValues in preferences.xml
        //setListPrefTrainingApps(listPref_trainingApps, getApplicationContext());
        setListPrefOtherApps(listPref_otherApps, getApplicationContext());

        /*
        listPref_trainingApps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setListPrefTrainingApps(listPref_trainingApps, getApplicationContext());
                return false;
            }
        });
        */
        listPref_otherApps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setListPrefOtherApps(listPref_otherApps, getApplicationContext());

                String packageName = "com.wahoo.fitness";
                String appName = "Wahoo Fitness";

                TrainingModeDb db = new TrainingModeDb(SettingsActivity.this);
                db.open();
                db.create(packageName, appName);
                db.close();

                return false;
            }
        });
    }

    /*

    protected static void setListPrefTrainingApps(ListPreference listPreference, Context context) {
        final PackageManager pm = context.getPackageManager();
        final String TAG = "Application";
        final int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES;

        List<ApplicationInfo> packages = pm.getInstalledApplications(flags);
        List<String> usr_installed_packages = new ArrayList<String>();

        for (ApplicationInfo appInfo : packages) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // System application
            } else {
                // User installed applications
                String packageName = appInfo.packageName;
                if(packageName == "com.wahoofitness.fitness") {
                    String label = appInfo.loadLabel(context.getPackageManager()).toString();
                    usr_installed_packages.add(label);
                }
            }

        }

        Collections.sort(usr_installed_packages);
        CharSequence[] entries =
                usr_installed_packages.toArray(new CharSequence[usr_installed_packages.size()]);
        CharSequence[] entryValues =
                usr_installed_packages.toArray(new CharSequence[usr_installed_packages.size()]);
        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);
    }
    */

    protected static void setListPrefOtherApps(ListPreference listPreference, Context context) {
        final PackageManager pm = context.getPackageManager();
        final String TAG = "Application";
        final int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES;

        List<ApplicationInfo> packages = pm.getInstalledApplications(flags);
        List<String> usr_installed_packages = new ArrayList<String>();

        for (ApplicationInfo appInfo : packages) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // System application
            } else {
                // User installed applications
                String label = appInfo.loadLabel(context.getPackageManager()).toString();
                if(label != null) {
                    usr_installed_packages.add(label);
                }


                //Log.d(TAG, "Installed package :" + appInfo.packageName);
                //Log.d(TAG, "Source dir : " + appInfo.sourceDir);
                //Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(appInfo.packageName));
            }

        }

        Collections.sort(usr_installed_packages);
        CharSequence[] entries =
                usr_installed_packages.toArray(new CharSequence[usr_installed_packages.size()]);
        CharSequence[] entryValues =
                usr_installed_packages.toArray(new CharSequence[usr_installed_packages.size()]);
        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);
    }


}
