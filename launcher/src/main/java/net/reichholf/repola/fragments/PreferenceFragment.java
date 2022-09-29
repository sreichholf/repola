package net.reichholf.repola.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import net.reichholf.repola.R;

import java.util.Locale;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

public class PreferenceFragment extends PreferenceFragmentCompat {
	public static final String PREFERENCE_TRANSPARENCY = "preference_transparency_new";
	public static final String PREFERENCE_SHOW_DATE = "preference_show_date";
	public static final String PREFERENCE_GRID_X = "preference_grid_x";
	public static final String PREFERENCE_GRID_Y = "preference_grid_y";
	public static final String PREFERENCE_SHOW_NAME = "preference_show_name";
	public static final String PREFERENCE_LOCKED = "preference_locked";
	public static final String PREFERENCE_GITHUB = "preference_github";
	public static final String PREFERENCE_ABOUT = "preference_about";
	public static final String PREFERENCE_COLORFUL_ICONS = "preference_colorful_icons";
	public static final String PREFERENCE_ALL_APPS_COLS = "preference_apps_cols";
	public static final String PREFERENCE_CORNER_RADIUS = "preference_corner_radius";
	public static final String PREFERENCE_BACKGROUND = "preference_background";

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.preferences, rootKey);

		bindSummary(PREFERENCE_GRID_X, R.string.summary_grid_x);
		bindSummary(PREFERENCE_GRID_Y, R.string.summary_grid_y);
		bindSeekbarSummary(PREFERENCE_CORNER_RADIUS, R.string.corner_radius_summary);

		findPreference(PREFERENCE_GITHUB).setOnPreferenceClickListener(preference -> {
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sreichholf/repola")));
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getActivity(),
						String.format(getString(R.string.error_opening_link), "Github", e.getMessage()),
						Toast.LENGTH_LONG).show();
			}
			return (true);
		});

		findPreference(PREFERENCE_BACKGROUND).setOnPreferenceClickListener(preference -> {
			Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
			startActivity(Intent.createChooser(intent, getString(R.string.action_wallpaper)));
			return (false);
		});

		PackageInfo pInfo;
		String version = "#Err";
		try {
			pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			version = pInfo.versionName;
		} catch (
				PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		findPreference(PREFERENCE_ABOUT).setTitle(getString(R.string.app_name) + " version " + version);
	}

	private void bindSummary(String key, final int resId) {
		final ListPreference p = findPreference(key);
		setPreferenceSummaryValue(p, resId, p.getValue());
		p.setOnPreferenceChangeListener((preference, newValue) -> {
			setPreferenceSummaryValue(p, resId, (String) newValue);
			return true;
		});
	}

	private void bindSeekbarSummary(String key, final int resId) {
		final SeekBarPreference p = findPreference(key);
		setPreferenceSummaryValue(p, resId, String.valueOf(p.getValue()));
		p.setOnPreferenceChangeListener((preference, newValue) -> {
			setPreferenceSummaryValue(p, resId, String.valueOf(newValue));
			return true;
		});
	}

	private void setPreferenceSummaryValue(Preference prefs, int resId, String value) {
		prefs.setSummary(
				String.format(Locale.getDefault(), getString(resId), value)
		);
	}
}
