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

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;


public class AppInfo {
	private final Drawable mIcon;
	private Drawable mIconHighRes;
	private String mName;
	private final String mPackageName;

	public AppInfo(PackageManager packageManager, ResolveInfo resolveInfo) {
		mPackageName = resolveInfo.activityInfo.packageName;
		mIcon = resolveInfo.loadIcon(packageManager);
		mIconHighRes = mIcon;
		try {
			mName = resolveInfo.loadLabel(packageManager).toString();
		} catch (Exception e) {
			mName = mPackageName;
		}
	}

	public AppInfo(PackageManager packageManager, ApplicationInfo applicationInfo) {
		mPackageName = applicationInfo.packageName;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Resources res = null;
			try {
				res = packageManager.getResourcesForApplication(applicationInfo);
				// Get a copy of the configuration, and set it to the desired resolution
				Configuration config = res.getConfiguration();
				Configuration originalConfig = new Configuration(config);
				config.densityDpi = DisplayMetrics.DENSITY_XHIGH;
				DisplayMetrics dm = res.getDisplayMetrics();
				res.updateConfiguration(config, dm);
				mIconHighRes = res.getDrawable(applicationInfo.icon);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (mIconHighRes.equals(null)) {
			mIconHighRes = applicationInfo.loadIcon(packageManager);
		}
		mIcon = mIconHighRes;
		try {
			mName = applicationInfo.loadLabel(packageManager).toString();
		} catch (Exception e) {
			mName = mPackageName;
		}
	}


	@NonNull
	public String getName() {
		if (mName != null)
			return mName;
		return ("");
	}

	public Drawable getIcon() {
		return mIcon;
	}

	public Drawable getIconHighRes() {
		return mIconHighRes;
	}

	public String getPackageName() {
		return mPackageName;
	}
}
