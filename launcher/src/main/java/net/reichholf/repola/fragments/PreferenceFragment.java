package net.reichholf.repola.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import net.reichholf.repola.R;
import net.reichholf.repola.Setup;

import java.util.Locale;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class PreferenceFragment extends PreferenceFragmentCompat {
	public static final String PREFERENCE_TRANSPARENCY = "preference_transparency_new";
	public static final String PREFERENCE_SCREEN_ON = "preference_screen_always_on";
	public static final String PREFERENCE_SHOW_DATE = "preference_show_date";
	public static final String PREFERENCE_GRID_X = "preference_grid_x";
	public static final String PREFERENCE_GRID_Y = "preference_grid_y";
	public static final String PREFERENCE_SHOW_NAME = "preference_show_name";
	public static final String PREFERENCE_MARGIN_X = "preference_margin_x";
	public static final String PREFERENCE_MARGIN_Y = "preference_margin_y";
	public static final String PREFERENCE_LOCKED = "preference_locked";
	public static final String PREFERENCE_GITHUB = "preference_github";
	public static final String PREFERENCE_ABOUT = "preference_about";
	public static final String PREFERENCE_COLORFUL_ICONS = "preference_colorful_icons";

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.preferences, rootKey);

		Setup setup = new Setup(getContext());

		bindSummary(PREFERENCE_GRID_X, R.string.summary_grid_x);
		bindSummary(PREFERENCE_GRID_Y, R.string.summary_grid_y);
		bindSummary(PREFERENCE_MARGIN_X, R.string.summary_margin_x);
		bindSummary(PREFERENCE_MARGIN_Y, R.string.summary_margin_y);

		findPreference(PREFERENCE_GITHUB).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sreichholf/repola")));
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getActivity(),
							String.format(getString(R.string.error_opening_link), "Github", e.getMessage()),
							Toast.LENGTH_LONG).show();
				}
				return (true);
			}
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
		findPreference(PREFERENCE_ABOUT).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.cosinus.launchertv")));
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getActivity(),
							String.format(getString(R.string.error_opening_link), "Play Store", e.getMessage()),
							Toast.LENGTH_LONG).show();
				}
				return (true);
			}
		});
	}

	private void bindSummary(String key, final int resId) {
		final ListPreference p = findPreference(key);
		setPreferenceSummaryValue(p, resId, p.getValue());
		p.setOnPreferenceChangeListener((preference, newValue) -> {
			setPreferenceSummaryValue(p, resId, (String) newValue);
			return true;
		});
	}

	private void setPreferenceSummaryValue(ListPreference prefs, int resId, String value) {
		prefs.setSummary(
				String.format(Locale.getDefault(), getString(resId), value)
		);
	}
}
