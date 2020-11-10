/*
 * Simple TV Launcher
 * Copyright 2017 Alexandre Del Bigio
 * Copyright 2020 Stephan Reichholf
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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.palette.graphics.Palette;


public class AppInfo {
	private Drawable mIcon;
	private Palette mPalette;
	private String mName;
	private String mPackageName;


	public AppInfo(PackageManager packageManager, ResolveInfo resolveInfo) {
		init(packageManager, resolveInfo.activityInfo.applicationInfo);
	}

	public AppInfo(PackageManager packageManager, ApplicationInfo applicationInfo) {
		init(packageManager, applicationInfo);
	}

	private void init(PackageManager packageManager, ApplicationInfo applicationInfo) {
		mPackageName = applicationInfo.packageName;
		loadIcon(packageManager, applicationInfo.icon, applicationInfo.loadIcon(packageManager));

		try {
			mName = applicationInfo.loadLabel(packageManager).toString();
		} catch (Exception e) {
			mName = mPackageName;
		}
	}

	private void loadIcon(PackageManager packageManager, int icon, @NonNull Drawable defaultIcon) {
		try {
			Resources res = packageManager.getResourcesForApplication(mPackageName);
			Configuration config = res.getConfiguration();
			int densityOld = config.densityDpi;
			config.densityDpi = DisplayMetrics.DENSITY_XXHIGH;
			DisplayMetrics dm = res.getDisplayMetrics();
			res.updateConfiguration(config, dm);
			mIcon = ResourcesCompat.getDrawable(res, icon, null);
			config.densityDpi = densityOld;
			res.updateConfiguration(config, dm);
		} catch (Exception e) {
			mIcon = defaultIcon;
		}
		mPalette = Palette.from(getBitmapFromDrawable(mIcon)).generate();

	}

	@NonNull
	private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
		final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(bmp);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bmp;
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

	public String getPackageName() {
		return mPackageName;
	}

	public Palette getPalette() {
		return mPalette;
	}
}
