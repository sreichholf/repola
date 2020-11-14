/*
 * Simple TV Launcher
 * Copyright 2020 Alexandre Del Bigio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.reichholf.repola;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class App extends Application {
	private static String LOG_TAG = App.class.getCanonicalName();
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.w(LOG_TAG, "Package changed!");
			AppManager.getInstance(getApplicationContext()).getApplications(true);
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		AppManager.getInstance(getApplicationContext()).getApplications();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onTerminate() {
		unregisterReceiver(mReceiver);
		super.onTerminate();
	}
}
