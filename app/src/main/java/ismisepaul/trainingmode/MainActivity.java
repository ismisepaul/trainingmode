package ismisepaul.trainingmode;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.ContentResolver;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.util.Log;

import java.lang.reflect.Method;

import static android.provider.Settings.Global.*;


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

        Switch switch_lockScreen = (Switch) findViewById(R.id.switch_lockScreen);
        if(lockScreenCheck()){
            switch_lockScreen.setChecked(lockScreenCheck());
        }

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
        Switch switch_autoSync = (Switch) findViewById(R.id.switch_autoSync);
        if(getAutoSyncStatus()){
            switch_autoSync.setChecked(getAutoSyncStatus());
        }

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
        Switch switch_mobileData = (Switch) findViewById(R.id.switch_mobileData);
        if(getMobileDataStatus()){
            switch_mobileData.setChecked(getMobileDataStatus());
        }

        switch_mobileData.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setMobileDataStatus(Boolean.TRUE);
                }
                else
                    setMobileDataStatus(Boolean.FALSE);
            }
        });


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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Return whether the keyguard requires a password to unlock. */
    public boolean lockScreenCheck(){
        KeyguardManager km = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        return km.isKeyguardSecure();
    }

    /* Turn Lock Screen Off. */
    public void setLockScreen(KeyguardManager.KeyguardLock lock, Boolean lock_screen){

        if(lock_screen) {
            //Log.d("Setting Lock Screen", "On");
            lock.disableKeyguard();
            lock.reenableKeyguard();
        }
        else if (!lock_screen){
            //Log.d("Setting Lock Screen", "Off");
             lock.disableKeyguard();
        }
    }

    public boolean getAutoSyncStatus(){
        return ContentResolver.getMasterSyncAutomatically();

    }

    public void setAutoSyncStatus(Boolean setSync){
        Log.d("Setting Auto Sync", setSync.toString());
        ContentResolver.setMasterSyncAutomatically(setSync);
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

    public void setMobileDataStatus(Boolean setMobileData){

        TelephonyManager telephonyManager = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);

        try {
            Class telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
            Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            Object ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
            Class ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());
            Method dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("");
            dataConnSwitchmethod.setAccessible(true);
            dataConnSwitchmethod.invoke(ITelephonyStub);

            if (setMobileData) {
                dataConnSwitchmethod = ITelephonyClass
                        .getDeclaredMethod("disableDataConnectivity");
            }
            else if (!setMobileData) {
                dataConnSwitchmethod = ITelephonyClass
                        .getDeclaredMethod("enableDataConnectivity");
            }

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }

    }


}
