package org.floodping.chargingkeepalive;

import org.floodping.chargingkeepalive.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BluetoothKeepaliveService extends Service {

	private PowerManager.WakeLock wl = null;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothKeepaliveService.this.onReceiveBtEvent(context, intent);
		}
	};

	protected void onReceiveBtEvent(Context context, Intent intent) {
		String action = intent.getAction();

		if (wl == null) {
			PowerManager pm = null;
			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			if (pm == null) {
				return;
			}
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					"org.floodping.chargingkeepalive.BluetoothKeepaliveService");
			if (wl == null) {
				return;
			}
		}

		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			boolean bShowToasts = prefs.getBoolean("show_toasts", false);
			if (bShowToasts) {
				Toast.makeText(context, R.string.RecogConnect,
						Toast.LENGTH_SHORT).show();
			}
			if (!wl.isHeld()) {
				SharedPreferences prefs1 = PreferenceManager
						.getDefaultSharedPreferences(this);
				boolean bShowIcon = prefs1.getBoolean("notifier_running", true);
				if (bShowIcon) {
					Notification notification = null;
					int icon = R.drawable.ic_launcher;
					CharSequence tickerText = getResources().getText(R.string.TickerText);
					long when = System.currentTimeMillis();
					notification = new Notification(icon, tickerText, when);
					notification.flags |= Notification.FLAG_NO_CLEAR;
					CharSequence contentTitle = getResources().getText(R.string.app_name);
					CharSequence contentText = getResources().getText(R.string.GotWakeLock);
		      Intent newIntent = new Intent(this, BluetoothKeepaliveActivity.class);
		      newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		      newIntent.putExtra("bynotification", true);
					PendingIntent contentIntent = PendingIntent.getActivity (getApplicationContext(), 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

					notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
					this.startForeground(1, notification);

					NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					notificationManager.cancel(2);
				}
			}
			wl.acquire();

		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			boolean bShowToasts = prefs.getBoolean("show_toasts", false);
			if (bShowToasts) {
				Toast.makeText(context, R.string.BtDisconn, Toast.LENGTH_SHORT)
						.show();
			}
			if (wl.isHeld()) {
				wl.release();
			}
			if (!wl.isHeld()) {
				// mNotificationManager.cancel(1);
				this.stopForeground(true);
				
				SharedPreferences prefs1 = PreferenceManager
						.getDefaultSharedPreferences(this);
				boolean bShowIcon = prefs1.getBoolean("notifier_idle", false);
				if (bShowIcon) {
					Notification notification = null;
					int icon = R.drawable.btka_idle;
					CharSequence tickerText = getResources().getText(R.string.TickerTextIdle);
					long when = System.currentTimeMillis();
					notification = new Notification(icon, tickerText, when);
					notification.flags |= Notification.FLAG_NO_CLEAR;
					CharSequence contentTitle = getResources().getText(R.string.app_name);
					CharSequence contentText = getResources().getText(R.string.Idle);
		      Intent newIntent = new Intent(this, BluetoothKeepaliveActivity.class);
		      newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		      newIntent.putExtra("bynotification", true);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

					notification.setLatestEventInfo(context, contentTitle,contentText, contentIntent);
					NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					notificationManager.notify(2, notification);
					//this.startForeground(2, notification);
				}

			}
		} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
					BluetoothAdapter.ERROR);
			switch (state) {
			case BluetoothAdapter.STATE_ON:
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				if (wl.isHeld()) {
					wl.release();
				}
				if (!wl.isHeld()) {
					this.stopForeground(true);
				}
				break;
			}
		}
	}

	@Override
	public void onCreate() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// Toast.makeText(this, "BluetoothKeepalive Service started",
		// Toast.LENGTH_SHORT).show();
		IntentFilter filter1 = new IntentFilter( BluetoothDevice.ACTION_ACL_CONNECTED);
    filter1.addAction("android.intent.action.ACTION_POWER_CONNECTED");
    filter1.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
		this.registerReceiver(mReceiver, filter1);
		
		SharedPreferences prefs1 = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean bShowIcon = prefs1.getBoolean("notifier_idle", false);
		if (bShowIcon) {
			Notification notification = null;
			int icon = R.drawable.btka_idle;
			CharSequence tickerText = getResources().getText(R.string.TickerTextIdle);
			long when = System.currentTimeMillis();
			notification = new Notification(icon, tickerText, when);
			notification.flags |= Notification.FLAG_NO_CLEAR;
			CharSequence contentTitle = getResources().getText(R.string.app_name);
			CharSequence contentText = getResources().getText(R.string.Idle);
			Intent newIntent = new Intent(this, BluetoothKeepaliveActivity.class);
			newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			newIntent.putExtra("bynotification", true);
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(this.getApplicationContext(), contentTitle, contentText, contentIntent);
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notificationManager.notify(2, notification);
		}
		else
		{
			this.stopForeground(true);
		}
	}

	@Override
	public void onDestroy() {
		NotificationManager notificationManager =
			    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(2);
		
		this.unregisterReceiver(mReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
