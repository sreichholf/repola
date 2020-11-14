package net.reichholf.repola;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AppManager implements Utils.ApplicationListReadyHandler {
	private static AppManager appManager = null;
	private static MutableLiveData<List<AppInfo>> applications = null;

	public static AppManager getInstance(Context context) {
		return appManager == null ? (appManager = new AppManager(context)) : appManager;
	}

	private Context mContext;
	private PackageManager mPackageManager;

	public AppManager(Context context) {
		mContext = context;
		mPackageManager = mContext.getPackageManager();
	}

	private AsyncTask mTask;

	private class AsyncGetApps extends AsyncTask<Void, Void, Void> {
		Utils.ApplicationListReadyHandler mReadyHandler;
		ArrayList<AppInfo> mApplications;

		public AsyncGetApps() {
			mApplications = new ArrayList<>();
			mTask = null;
		}

		@Override
		protected Void doInBackground(Void... voids) {
			mApplications.clear();
			PackageManager packageManager = mContext.getPackageManager();

			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> intentActivities = packageManager.queryIntentActivities(mainIntent, 0);
			for (ResolveInfo resolveInfo : intentActivities) {
				mApplications.add(new AppInfo(packageManager, resolveInfo));
			}

			Collections.sort(mApplications, (lhs, rhs) -> lhs.getName().compareToIgnoreCase(rhs.getName()));
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			onApplicationListReady(mApplications);
		}
	}

	public LiveData<List<AppInfo>> getApplications() {
		return getApplications(false);
	}

	public LiveData<List<AppInfo>> getApplications(boolean reload) {
		if (applications == null) {
			applications = new MutableLiveData<>();
			loadApplications();
		} else if (reload) {
			applications.getValue().clear();
			loadApplications();
		}
		return applications;
	}

	private void loadApplications() {
		if (mTask != null) {
			mTask.cancel(true);
			mTask = null;
		}
		mTask = new AsyncGetApps().execute();
	}

	public void onApplicationListReady(ArrayList<AppInfo> apps) {
		applications.setValue(apps);
	}
}
