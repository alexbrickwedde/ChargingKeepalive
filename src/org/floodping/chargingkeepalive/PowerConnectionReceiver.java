package org.floodping.chargingkeepalive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PowerConnectionReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		StaticHelper.StartServiceBoot(context);
	}
}
