package org.floodping.chargingkeepalive;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class BluetoothKeepaliveActivity extends Activity {

	private Handler mHandler = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean bAutorun = prefs.getBoolean("autorun", true);
		boolean bByNotification = false;

		if (this.getIntent() != null && this.getIntent().getExtras() != null)
		{
		  bByNotification = this.getIntent().getExtras().get("bynotification") != null;
		}
		
		if (bAutorun && !bByNotification) {
			mHandler.postDelayed(new Runnable() {
				public void run() {
					BluetoothKeepaliveActivity.this.finish();
				}
			}, 2000);

			StaticHelper.StartServiceAuto(this);

			setContentView(R.layout.splashscreen);

			boolean bShowToasts = prefs.getBoolean("show_toasts", false);
			if (bShowToasts) {
				Toast.makeText(this, R.string.StartingService,
						Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			setContentView(R.layout.splashscreennoauto);
		  this.UpdateProgress(true);
		}
	}

  @Override
  protected void onNewIntent(Intent intent)
  {
    // TODO Auto-generated method stub
    super.onNewIntent(intent);
  }

  public void RecheckProgressLater() {
    if(mHandler != null)
    {
      mHandler.postDelayed(new Runnable() {
        public void run() {
          BluetoothKeepaliveActivity.this.UpdateProgress(true/*bRecheck*/);
        }
      }, 2000);
    }
  }
  
  public void UpdateProgress(boolean bRecheck) {
    ImageView iv = (ImageView)findViewById(R.id.progressBar1);
		if (StaticHelper.isMyServiceRunning(this, "org.floodping.chargingkeepalive.BluetoothKeepaliveService"))
		{
		  iv.setImageDrawable(getResources().getDrawable(R.drawable.btka_running));
		}
		else
		{
      iv.setImageDrawable(getResources().getDrawable(R.drawable.btka_notrunning));
		}
		if (bRecheck)
		{
		  this.RecheckProgressLater();
		}
	}

	public void StartService(View view) {
		Intent serviceIntent = new Intent();
		serviceIntent
				.setAction("org.floodping.chargingkeepalive.BluetoothKeepaliveService");
		this.startService(serviceIntent);
	}
	
	public void StopService(View view) {
		Intent serviceIntent = new Intent();
		serviceIntent
				.setAction("org.floodping.chargingkeepalive.BluetoothKeepaliveService");
		this.stopService(serviceIntent);
	}

	public void ShowConfig(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ConfigurationActivity.class);
		startActivity(intent);
	}
}
