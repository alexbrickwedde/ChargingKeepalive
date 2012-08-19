package org.floodping.chargingkeepalive;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StaticHelper {

	static public void StartServiceBoot(Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean bAutoexec = prefs.getBoolean("autoexec", true);
		if (bAutoexec) {
			Intent serviceIntent = new Intent();
			serviceIntent.setAction("org.floodping.chargingkeepalive.ChargingKeepaliveService");
			context.startService(serviceIntent);
		}
	}

	static public void StartServiceAuto(Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean bAutoexec = prefs.getBoolean("autorun", true);
		if (bAutoexec) {
			Intent serviceIntent = new Intent();
			serviceIntent.setAction("org.floodping.chargingkeepalive.ChargingKeepaliveService");
			context.startService(serviceIntent);
		}
	}
	static public boolean isMyServiceRunning(Context context, String sName) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (sName.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
