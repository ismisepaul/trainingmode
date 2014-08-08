package ismisepaul.trainingmode;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.System;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.util.Log;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Must create one instance of KeyguardManager and pass to method or it's not
        * possible to re-enable the lock screen*/
        final Button button_start = (Button) findViewById(R.id.button_start);
        final KeyguardManager km = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock lock = km.newKeyguardLock(KEYGUARD_SERVICE);
        final Switch switch_lockScreen = (Switch) findViewById(R.id.switch_lockScreen);
        final Switch switch_autoSync = (Switch) findViewById(R.id.switch_autoSync);
        final Switch switch_mobileData = (Switch) findViewById(R.id.switch_mobileData);
        final Switch switch_bright = (Switch) findViewById(R.id.switch_bright);
        final TextView brightness_level = (TextView) findViewById(R.id.textView_bright_level);
        final ToggleButton toggleBtn_mobileData =
                (ToggleButton) findViewById(R.id.toggleBtn_everything);

        /*Get the status of the settings and set the switches on/off accordingly */
        switch_lockScreen.setChecked(getLockScreenStatus(km));
        switch_autoSync.setChecked(getAutoSyncStatus());
        switch_mobileData.setChecked(getMobileDataStatus());
        switch_bright.setChecked(getScreenBrightnessStatus());

        //Set the value of the screen brightness and update the text view
        brightness_level.setText("(" + getScreenBrightLevel() + ")");


        /*Lock Screen Switch listener to turn lock screen on/off*/
        switch_lockScreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    setLockScreen(lock, Boolean.TRUE);
                else
                    setLockScreen(lock, Boolean.FALSE);
            }
        });

        /*Auto-Sync Switch listener to turn auto-sync on/off*/
        switch_autoSync.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    setAutoSyncStatus(Boolean.TRUE);
                else
                    setAutoSyncStatus(Boolean.FALSE);
            }
        });

        /*Mobile Data Switch listener to turn mobile data on/off*/
        switch_mobileData.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    setMobileDataStatus(Boolean.TRUE);
                else
                    setMobileDataStatus(Boolean.FALSE);
            }
        });

        /*Screen brightness Switch listener to turn auto screen brightness off
        * the level of the screen to 10 */
        switch_bright.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    setScreenBrightnessStatus(1, 99);
                else
                    setScreenBrightnessStatus(0, 10);

                brightness_level.setText("(" + getScreenBrightLevel() + ")");
            }
        });

        /*All Settings Toggle Button Listener*/
        toggleBtn_mobileData.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setLockScreen(lock, Boolean.FALSE);
                    setAutoSyncStatus(Boolean.FALSE);
                    setMobileDataStatus(Boolean.FALSE);
                    setScreenBrightnessStatus(0, 10);

                    switch_lockScreen.setChecked(Boolean.FALSE);
                    switch_autoSync.setChecked(Boolean.FALSE);
                    switch_mobileData.setChecked(Boolean.FALSE);
                    switch_bright.setChecked(Boolean.FALSE);
                } else {
                    setLockScreen(lock, Boolean.TRUE);
                    setAutoSyncStatus(Boolean.TRUE);
                    setMobileDataStatus(Boolean.TRUE);
                    setScreenBrightnessStatus(1, 100);

                    switch_lockScreen.setChecked(Boolean.TRUE);
                    switch_autoSync.setChecked(Boolean.TRUE);
                    switch_mobileData.setChecked(Boolean.TRUE);
                    switch_bright.setChecked(Boolean.TRUE);
                }
            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get the shared pref for the app on what app to launch
                String appToLaunch = getShareSetting("pref_selectAppToLaunch");


                //if start is pressed and on app is set launch the settings
                if(appToLaunch != null && appToLaunch != "NULL"){
                    //inform the user the app is going to be launched
                    Toast.makeText(getApplicationContext(), "Launching: "+ appToLaunch,
                            Toast.LENGTH_LONG).show();

                    //launch the chosen application
                    Intent LaunchIntent =
                            getPackageManager().getLaunchIntentForPackage(appToLaunch);
                    startActivity(LaunchIntent);

                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "No Application Set. \nPlease Configure an Application",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    public void onResume() {
        /*IF the application is resumed check the status of the settings
         * 'Auto-Sync' and 'Mobile-Data' */
        super.onResume();

        final Switch switch_lockScreen = (Switch) findViewById(R.id.switch_lockScreen);
        final Switch switch_autoSync = (Switch) findViewById(R.id.switch_autoSync);
        final Switch switch_mobileData = (Switch) findViewById(R.id.switch_mobileData);
        final Switch switch_bright = (Switch) findViewById(R.id.switch_bright);
        final TextView brightness_level = (TextView) findViewById(R.id.textView_bright_level);
        final ToggleButton toggleBtn_mobileData =
                (ToggleButton) findViewById(R.id.toggleBtn_everything);

        //Set the value of the screen brightness and update the text view
        brightness_level.setText("(" + getScreenBrightLevel() + ")");

        //Check the status of 'Auto-Sync' and 'Mobile-Data' and set the buttons appropriately
        switch_autoSync.setChecked(getAutoSyncStatus());
        switch_mobileData.setChecked(getMobileDataStatus());
        switch_bright.setChecked(getScreenBrightnessStatus());

        /*On Resume check if all buttons are on/off and set the Full Training mode Toggle
         * to on/off as appropriate */
        if (!switch_lockScreen.isChecked() && !switch_autoSync.isChecked()
                && !switch_mobileData.isChecked()) {
            toggleBtn_mobileData.setChecked(Boolean.TRUE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent = null;
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Return the status of the lock screen on/off */
    public boolean getLockScreenStatus(KeyguardManager km) {
        return km.isKeyguardSecure();
    }

    /*Return the status of Auto-Sync setting on/off */
    public boolean getAutoSyncStatus() {
        return ContentResolver.getMasterSyncAutomatically();
    }

    /*Return the status of Mobile Data setting on/off */
    public boolean getMobileDataStatus() {

        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "ERROR: Could not retrieve the status of mobile data" + e.toString(),
                    Toast.LENGTH_LONG).show();
            Log.e("ERROR", e.toString());
        }
        return mobileDataEnabled;
    }
    /*Return the True if auto brightness is on and the brightness of the screen
    * is greater than 10*/
    public boolean getScreenBrightnessStatus() {
        int auto_screen_bright;
        int screen_bright_level;

        try {
            auto_screen_bright = System.getInt(getContentResolver(),
                    System.SCREEN_BRIGHTNESS_MODE);
            screen_bright_level = System.getInt(getContentResolver(),
                    System.SCREEN_BRIGHTNESS);
            if (auto_screen_bright == 1 || screen_bright_level > 10)
                return Boolean.TRUE;
            else
                return Boolean.FALSE;
        } catch(Settings.SettingNotFoundException e){
            Log.e("Can't get Setting: ", e.toString());
        }

        return Boolean.FALSE;
    }
    /*Get the value of the screen brightness i.e. 0-255 */
    public String getScreenBrightLevel(){
        int screen_bright = 2;

        try{
            screen_bright = System.getInt(getContentResolver(), System.SCREEN_BRIGHTNESS);
        } catch(Settings.SettingNotFoundException e){
            Log.e("Can't get Setting: ", e.toString());
        }

        return Integer.toString(screen_bright);
    }


    /* Turn lock screen setting (require passcode/pattern) on/off. */
    public void setLockScreen(KeyguardManager.KeyguardLock lock, Boolean lock_screen) {
        if (lock_screen)
            lock.reenableKeyguard();
        else if (!lock_screen)
            lock.disableKeyguard();
    }

    /*Turn Auto-Sync Setting on/off */
    public void setAutoSyncStatus(Boolean setSync) {
        ContentResolver.setMasterSyncAutomatically(setSync);
    }

    /*Uses Java reflection to access "hidden" API to turn mobile data on/off*/
    public void setMobileDataStatus(Boolean setMobileData) {

        final ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Field iConnectivityManagerField = cmClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            Object iConnectivityManager = iConnectivityManagerField.get(cm);
            Class iConnectivityManagerClass =
                    Class.forName(iConnectivityManager.getClass().getName());
            Method setMobileDataEnabledMethod =
                    iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled",
                            Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, setMobileData);

        } catch (NoSuchMethodException e) {
            /*The method 'setMobileDataEnabled' been changed in recent versions of Android
             * This catch tries to access the method again in Android 4.4.x */
            try {
                Class[] cArg = new Class[2];
                cArg[0] = String.class;
                cArg[1] = Boolean.TYPE;

                Class cmClass = Class.forName(cm.getClass().getName());
                Field iConnectivityManagerField = cmClass.getDeclaredField("mService");
                iConnectivityManagerField.setAccessible(true);
                Object iConnectivityManager = iConnectivityManagerField.get(cm);
                Class iConnectivityManagerClass =
                        Class.forName(iConnectivityManager.getClass().getName());
                Method setMobileDataEnabledMethod =
                        iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", cArg);

                Object[] pArg = new Object[2];
                pArg[0] = getPackageName();
                pArg[1] = setMobileData;
                setMobileDataEnabledMethod.setAccessible(true);
                setMobileDataEnabledMethod.invoke(iConnectivityManager, pArg);
            } catch (Exception ee) {
                Toast.makeText(getApplicationContext(), "ERROR: Could not set mobile data"
                        + ee.toString(), Toast.LENGTH_LONG).show();
                Log.e("ERROR", ee.toString());
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "ERROR: Could not set mobile data"
                            + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("ERROR: ", e.toString());
        }
    }

    /*Set the brightness level on the phone
     * auto_bright 0=auto brightness is off, 1=auto brightness is on
      * bright_level 0-255 the level of light from the back light*/
    public void setScreenBrightnessStatus(int auto_bright, int bright_level) {
        System.putInt(getContentResolver(), System.SCREEN_BRIGHTNESS_MODE, auto_bright);
        System.putInt(getContentResolver(),  System.SCREEN_BRIGHTNESS, bright_level);

    }

    public String getShareSetting(String androidKey){
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String val = sharedPrefs.getString(androidKey, "NULL");
        return val;

    }

    public void removeShareSetting(String androidKey){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.remove(androidKey);
        editor.apply();

    }

}
