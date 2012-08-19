package org.floodping.chargingkeepalive;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ConfigurationActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {    
			    super.onCreate(savedInstanceState);       
			    this.addPreferencesFromResource(R.xml.preferences);
			}

	@Override
	protected void onStop() {
		super.onStop();

		if (StaticHelper.isMyServiceRunning(this, "org.floodping.chargingkeepalive.BluetoothKeepaliveService"))
		{
			Intent serviceIntent = new Intent();
			serviceIntent.setAction("org.floodping.chargingkeepalive.BluetoothKeepaliveService");
//			this.stopService(serviceIntent);
			this.startService(serviceIntent);
		}
	}
	
}
