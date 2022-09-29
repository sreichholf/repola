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

package net.reichholf.repola.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import net.reichholf.repola.R;
import net.reichholf.repola.fragments.ApplicationFragment;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;

public class Launcher extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		super.onCreate(savedInstanceState);

		setFullScreen();
		setContentView(R.layout.activity_launcher);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, ApplicationFragment.newInstance(), ApplicationFragment.TAG)
				.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setFullScreen();
	}

	private void setFullScreen() {
		try {
			View decorView = getWindow().getDecorView();

			int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
					| View.SYSTEM_UI_FLAG_IMMERSIVE;

			decorView.setSystemUiVisibility(uiOptions);

			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER,
					WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
