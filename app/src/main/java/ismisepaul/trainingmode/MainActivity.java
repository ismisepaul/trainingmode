package ismisepaul.trainingmode;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.ContentResolver;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.util.Log;
import android.widget.ToggleButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Must create one instance of KeyguardManager and pass to method or it's not
        * possible to renable the lock screen
        * http://stackoverflow.com/questions/5773504/reenablekeyguard-not-working*/
        final KeyguardManager km = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock lock = km.newKeyguardLock(KEYGUARD_SERVICE);
        final Switch switch_lockScreen = (Switch) findViewById(R.id.switch_lockScreen);
        final Switch switch_autoSync = (Switch) findViewById(R.id.switch_autoSync);
        final Switch switch_mobileData = (Switch) findViewById(R.id.switch_mobileData);
        final ToggleButton toggleBtn_mobileData =
                (ToggleButton) findViewById(R.id.toggleBtn_everything);


        switch_lockScreen.setChecked(getLockScreenStatus(km));
        switch_autoSync.setChecked(getAutoSyncStatus());
        switch_mobileData.setChecked(getMobileDataStatus());

        /*lock screen listener*/
        switch_lockScreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setLockScreen(lock, Boolean.TRUE);
                }
                else
                    setLockScreen(lock, Boolean.FALSE);
            }
        });

        /*Auto Sync */
        switch_autoSync.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setAutoSyncStatus(Boolean.TRUE);
                }
                else
                    setAutoSyncStatus(Boolean.FALSE);
            }
        });

        /*Mobile Data */
        switch_mobileData.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setMobileDataStatus(Boolean.TRUE);
                }
                else
                    setMobileDataStatus(Boolean.FALSE);
            }
        });

        /*All Settings */
        toggleBtn_mobileData.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setLockScreen(lock, Boolean.FALSE);
                    setAutoSyncStatus(Boolean.FALSE);
                    setMobileDataStatus(Boolean.FALSE);

                    switch_lockScreen.setChecked(Boolean.FALSE);
                    switch_autoSync.setChecked(Boolean.FALSE);
                    switch_mobileData.setChecked(Boolean.FALSE);

                } else {
                    setLockScreen(lock, Boolean.TRUE);
                    setAutoSyncStatus(Boolean.TRUE);
                    setMobileDataStatus(Boolean.TRUE);

                    switch_lockScreen.setChecked(Boolean.TRUE);
                    switch_autoSync.setChecked(Boolean.TRUE);
                    switch_mobileData.setChecked(Boolean.TRUE);
                }
            }

           });

    }

    @Override
    public void onResume(){
        /*IF the application is resumed check the status of the settings
         * lockscreen, autosync and mobiledata */
        super.onResume();

        final Switch switch_lockScreen = (Switch) findViewById(R.id.switch_lockScreen);
        final Switch switch_autoSync = (Switch) findViewById(R.id.switch_autoSync);
        final Switch switch_mobileData = (Switch) findViewById(R.id.switch_mobileData);
        final ToggleButton toggleBtn_mobileData =
                (ToggleButton) findViewById(R.id.toggleBtn_everything);

        //Check the status of autosync and mobile data and set the buttons appropriately
        switch_autoSync.setChecked(getAutoSyncStatus());
        switch_mobileData.setChecked(getMobileDataStatus());

        /*On Resume check if all buttons are on/off and set the Full Training mode Toggle
         * to on/off as appropriate */
        if(!switch_lockScreen.isChecked() && !switch_autoSync.isChecked()
                && !switch_mobileData.isChecked()){
            toggleBtn_mobileData.setChecked(Boolean.TRUE);
            Log.d("Full Training","set");
        }

    }
/*
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
    /* Return whether the keyguard requires a password to unlock. */
    public boolean getLockScreenStatus(KeyguardManager km){
        return km.isKeyguardSecure();
    }
    public boolean getAutoSyncStatus(){
        return ContentResolver.getMasterSyncAutomatically();

    }
    public boolean getMobileDataStatus(){

        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            Log.d("Error", "Cannot Access Hidden API");
        }

        return mobileDataEnabled;
    }


    /* Turn Lock Screen Off. */
    public void setLockScreen(KeyguardManager.KeyguardLock lock, Boolean lock_screen){

        if(lock_screen) {
            //Log.d("Setting Lock Screen", "On");
            lock.reenableKeyguard();
        }
        else if (!lock_screen){
            //Log.d("Setting Lock Screen", "Off");
             lock.disableKeyguard();
        }
    }

    public void setAutoSyncStatus(Boolean setSync){
        Log.d("Setting Auto Sync", setSync.toString());
        ContentResolver.setMasterSyncAutomatically(setSync);
    }

    public void setMobileDataStatus(Boolean setMobileData){

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
                    iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, setMobileData);

        }
        catch (NoSuchMethodException e) {
            /*The method 'setMobileDataEnabled' been changed
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
            }
            catch (Exception ee){Log.e("Error", e.toString());}

        }
        catch (Exception e) {Log.e("Error", e.toString());}

    }


}
