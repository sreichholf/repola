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

package net.reichholf.repola.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.reichholf.repola.AppInfo;
import net.reichholf.repola.R;
import net.reichholf.repola.Setup;
import net.reichholf.repola.Utils;
import net.reichholf.repola.activities.ApplicationList;
import net.reichholf.repola.activities.Preferences;
import net.reichholf.repola.views.ApplicationView;
import net.reichholf.repola.views.models.ApplicationViewModel;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ApplicationFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
	public static final String TAG = "ApplicationFragment";
	private static final String PREFERENCES_NAME = "applications";
	private static final int REQUEST_CODE_APPLICATION_LIST = 0x1E;
	private static final int REQUEST_CODE_WALLPAPER = 0x1F;
	private static final int REQUEST_CODE_APPLICATION_START = 0x20;
	private static final int REQUEST_CODE_PREFERENCES = 0x21;
	private static final int REQUEST_CODE_WIFI = 0x22;

	private int mGridX = 3;
	private int mGridY = 2;
	private LinearLayout mContainer;
	private ApplicationView[][] mApplications = null;
	private View mSettings;
	private View mWifiSettings;
	private View mGridView;
	private View mBluetoothSettings;
	private Setup mSetup;
	private ProgressBar mProgress;


	public ApplicationFragment() {
		// Required empty public constructor
	}

	public static ApplicationFragment newInstance() {
		return new ApplicationFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_application, container, false);

		mSetup = new Setup(getContext());
		mContainer = view.findViewById(R.id.container);
		mSettings = view.findViewById(R.id.settings);
		mWifiSettings = view.findViewById(R.id.wifi);
		mBluetoothSettings = view.findViewById(R.id.bluetooth);
		mGridView = view.findViewById(R.id.applications);
		mProgress = view.findViewById(R.id.progress);
		mProgress.setVisibility(View.VISIBLE);
		mProgress.setActivated(true);

		if (mSetup.keepScreenOn())
			mContainer.setKeepScreenOn(true);

		mSettings.setOnClickListener(this);
		mWifiSettings.setOnClickListener(this);
		mBluetoothSettings.setOnClickListener(this);
		mGridView.setOnClickListener(this);
		createApplications();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mProgress.setVisibility(View.VISIBLE);
		ApplicationViewModel model = new ViewModelProvider(this).get(ApplicationViewModel.class);
		model.getApplications().observe(getActivity(), applications -> {
			updateApplications();
			setApplicationOrder();
			mProgress.setVisibility(View.GONE);
			mContainer.setVisibility(View.VISIBLE);
		});
	}

	private void createApplications() {
		mContainer.removeAllViews();

		mGridX = mSetup.getGridX();
		mGridY = mSetup.getGridY();

		if (mGridX < 2)
			mGridX = 2;
		if (mGridY < 1)
			mGridY = 1;

		int marginX = Utils.pixelFromDp(getContext(), mSetup.getMarginX());
		int marginY = Utils.pixelFromDp(getContext(), mSetup.getMarginY());

		boolean showNames = mSetup.showNames();

		mApplications = new ApplicationView[mGridY][mGridX];

		int position = 0;
		for (int y = 0; y < mGridY; y++) {
			LinearLayout ll = new LinearLayout(getContext());
			ll.setOrientation(LinearLayout.HORIZONTAL);
			ll.setGravity(Gravity.CENTER_VERTICAL);
			ll.setFocusable(false);
			ll.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1
			));

			for (int x = 0; x < mGridX; x++) {
				ApplicationView av = new ApplicationView(getContext());
				av.setOnClickListener(this);
				av.setOnLongClickListener(this);
				av.setOnMenuOnClickListener(v -> onLongClick(v));
				av.setPosition(position++);
				av.showName(showNames);
				av.setId(View.generateViewId());
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
				lp.setMargins(marginX, marginY, marginX, marginY);
				av.setLayoutParams(lp);
				ll.addView(av);
				mApplications[y][x] = av;
			}
			mContainer.addView(ll);
		}
		mContainer.setVisibility(View.INVISIBLE);
	}

	private void setApplicationOrder() {
		for (int y = 0; y < mGridY; y++) {
			for (int x = 0; x < mGridX; x++) {
				int upId = R.id.applications;
				int downId = R.id.applications;
				int leftId = R.id.bluetooth;
				int rightId = R.id.applications;

				if (y > 0)
					upId = mApplications[y - 1][x].getId();

				if (y + 1 < mGridY)
					downId = mApplications[y + 1][x].getId();

				if (x > 0)
					leftId = mApplications[y][x - 1].getId();
				else if (y > 0)
					leftId = mApplications[y - 1][mGridX - 1].getId();

				if (x + 1 < mGridX)
					rightId = mApplications[y][x + 1].getId();
				else if (y + 1 < mGridY)
					rightId = mApplications[y + 1][0].getId();
				if (x == 0 && y == 0)
					mApplications[y][x].requestFocus();
				mApplications[y][x].setNextFocusLeftId(leftId);
				mApplications[y][x].setNextFocusRightId(rightId);
				mApplications[y][x].setNextFocusUpId(upId);
				mApplications[y][x].setNextFocusDownId(downId);
			}
		}


		mGridView.setNextFocusLeftId(mApplications[mGridY - 1][mGridX - 1].getId());
		mGridView.setNextFocusRightId(R.id.settings);
		mGridView.setNextFocusUpId(mApplications[mGridY - 1][mGridX - 1].getId());
		mGridView.setNextFocusDownId(mApplications[0][0].getId());

		mSettings.setNextFocusLeftId(R.id.applications);
		mSettings.setNextFocusRightId(R.id.wifi);
		mSettings.setNextFocusUpId(mApplications[mGridY - 1][mGridX - 1].getId());
		mSettings.setNextFocusDownId(mApplications[0][0].getId());

		mWifiSettings.setNextFocusLeftId(R.id.settings);
		mWifiSettings.setNextFocusRightId(R.id.bluetooth);
		mWifiSettings.setNextFocusUpId(mApplications[mGridY - 1][mGridX - 1].getId());
		mWifiSettings.setNextFocusDownId(mApplications[0][0].getId());

		mBluetoothSettings.setNextFocusLeftId(R.id.wifi);
		mBluetoothSettings.setNextFocusRightId(mApplications[0][0].getId());
		mBluetoothSettings.setNextFocusUpId(mApplications[mGridY - 1][mGridX - 1].getId());
		mBluetoothSettings.setNextFocusDownId(mApplications[0][0].getId());
	}


	private void updateApplications() {
		if (getActivity() == null)
			return;
		PackageManager pm = getActivity().getPackageManager();
		SharedPreferences prefs = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

		for (int y = 0; y < mGridY; y++) {
			for (int x = 0; x < mGridX; x++) {
				ApplicationView app = mApplications[y][x];
				setApplication(pm, app, prefs.getString(app.getPreferenceKey(), null));
			}
		}
	}

	private void writePreferences(int appNum, String packageName) {
		SharedPreferences prefs = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		String key = ApplicationView.getPreferenceKey(appNum);

		if (TextUtils.isEmpty(packageName))
			editor.remove(key);
		else
			editor.putString(key, packageName);

		editor.apply();
	}

	private void setApplication(PackageManager pm, ApplicationView app, String packageName) {
		try {

			if (!TextUtils.isEmpty(packageName)) {
				PackageInfo pi = pm.getPackageInfo(packageName, 0);
				if (pi != null) {
					AppInfo appInfo = new AppInfo(pm, pi.applicationInfo);
					app.setImageDrawable(appInfo.getIcon())
							.setText(appInfo.getName())
							.setPackageName(appInfo.getPackageName());
					if (mSetup.colorfulIcons()) {
						int alpha = (int) (255 - (mSetup.getTransparency() * 255)) << 24;
						Utils.tintAppIcon(app.getBackground(), appInfo.getPalette(), alpha);
					}
				}
			} else {
				app.getBackground().setColorFilter(null);
				if (mSetup.iconsLocked())
					app.setImageDrawable(null);
				else
					app.setImageResource(R.drawable.ic_add);
				app.setText("").setPackageName(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onLongClick(View v) {
		if (v instanceof ApplicationView) {
			ApplicationView appView = (ApplicationView) v;
			if (appView.hasPackage() && mSetup.iconsLocked()) {
				Toast.makeText(getActivity(), R.string.home_locked, Toast.LENGTH_SHORT).show();
			} else {
				openApplicationList(ApplicationList.VIEW_LIST, appView.getPosition(), appView.hasPackage(), REQUEST_CODE_APPLICATION_LIST);
			}
			return (true);
		}
		return (false);
	}

	@Override
	public void onClick(View v) {
		if (v instanceof ApplicationView) {
			openApplication((ApplicationView) v);
			return;
		}

		int id = v.getId();
		if (id == R.id.applications) {
			openApplicationList(ApplicationList.VIEW_GRID, 0, false, REQUEST_CODE_APPLICATION_START);
		} else if (id == R.id.settings) {
			startActivityForResult(new Intent(getContext(), Preferences.class), REQUEST_CODE_PREFERENCES);
		} else if (id == R.id.wifi) {
			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		} else if (id == R.id.bluetooth) {
			startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
		}
	}

	private void openApplication(ApplicationView v) {
		if (!v.hasPackage()) {
			if (mSetup.iconsLocked())
				return;
			openApplicationList(ApplicationList.VIEW_LIST, v.getPosition(), false, REQUEST_CODE_APPLICATION_LIST);
			return;
		}

		try {
			startActivity(getLaunchIntentForPackage(v.getPackageName()));
		} catch (Exception e) {
			Toast.makeText(getActivity(), v.getName() + " : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void openApplication(String packageName) {
		try {
			Intent startApp = getLaunchIntentForPackage(packageName);
			startActivity(startApp);
		} catch (Exception e) {
			Toast.makeText(getActivity(), packageName + " : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void openApplicationList(int viewType, int appNum, boolean showDelete, int requestCode) {
		Intent intent = new Intent(getActivity(), ApplicationList.class);
		intent.putExtra(ApplicationList.APPLICATION_NUMBER, appNum);
		intent.putExtra(ApplicationList.VIEW_TYPE, viewType);
		intent.putExtra(ApplicationList.SHOW_DELETE, showDelete);
		startActivityForResult(intent, requestCode);
	}

	private Intent getLaunchIntentForPackage(String packageName) {
		PackageManager pm = getActivity().getPackageManager();
		Intent launchIntent = pm.getLaunchIntentForPackage(packageName);

		if (launchIntent == null) {
			launchIntent = pm.getLeanbackLaunchIntentForPackage(packageName);
		}

		return launchIntent;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
			case REQUEST_CODE_WALLPAPER:
				break;
			case REQUEST_CODE_PREFERENCES:
				getActivity().recreate();
				break;
			case REQUEST_CODE_APPLICATION_START:
				if (intent != null)
					openApplication(intent.getExtras().getString(ApplicationList.PACKAGE_NAME));
				break;
			case REQUEST_CODE_APPLICATION_LIST:
				if (resultCode == Activity.RESULT_OK) {
					Bundle extra = intent.getExtras();
					int appNum = intent.getExtras().getInt(ApplicationList.APPLICATION_NUMBER);

					if (extra.containsKey(ApplicationList.DELETE) && extra.getBoolean(ApplicationList.DELETE)) {
						writePreferences(appNum, null);
					} else {
						writePreferences(appNum,
								intent.getExtras().getString(ApplicationList.PACKAGE_NAME)
						);
					}
					updateApplications();
				}
				break;
		}
	}


}
