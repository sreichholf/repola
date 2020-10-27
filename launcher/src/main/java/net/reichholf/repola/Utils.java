/*
 * Simple TV Launcher
 * Copyright 2017 Alexandre Del Bigio
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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Utils {
	public interface ApplicationListReadyHandler {
		void onApplicationListReady(ArrayList<AppInfo> applications);
	}

	private static class AsyncGetApps extends AsyncTask {
		ApplicationListReadyHandler mReadyHandler;
		Context mContext;
		ArrayList<AppInfo> mApplications;

		public AsyncGetApps(Context context, ApplicationListReadyHandler readyHandler) {
			mReadyHandler = readyHandler;
			mContext = context;
			mApplications = new ArrayList<AppInfo>();
		}

		@Override
		protected Object doInBackground(Object[] objects) {
			mApplications.clear();
			PackageManager packageManager = mContext.getPackageManager();

			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> intentActivities = packageManager.queryIntentActivities(mainIntent, 0);
			for (ResolveInfo resolveInfo : intentActivities) {
				mApplications.add(new AppInfo(packageManager, resolveInfo));
			}

			Collections.sort(mApplications, new Comparator<AppInfo>() {
				@Override
				public int compare(AppInfo lhs, AppInfo rhs) {
					return lhs.getName().compareToIgnoreCase(rhs.getName());
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Object o) {
			mReadyHandler.onApplicationListReady(mApplications);
		}
	}

	public static AsyncTask loadApplications(Context context, ApplicationListReadyHandler handler) {
		return new AsyncGetApps(context, handler).execute();
	}

	public static int pixelFromDp(Context context, int dp) {
		Resources r = context.getResources();
		return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
	}
}
