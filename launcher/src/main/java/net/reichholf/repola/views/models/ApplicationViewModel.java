package net.reichholf.repola.views.models;

import android.app.Application;

import net.reichholf.repola.AppInfo;
import net.reichholf.repola.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ApplicationViewModel extends AndroidViewModel implements Utils.ApplicationListReadyHandler {
	private MutableLiveData<List<AppInfo>> applications;

	public ApplicationViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<List<AppInfo>> getApplications() {
		if (applications == null) {
			applications = new MutableLiveData<>();
			loadApplications();
		}
		return applications;
	}

	private void loadApplications() {
		Utils.loadApplications(getApplication(), this);
	}

	@Override
	public void onApplicationListReady(ArrayList<AppInfo> apps) {
		applications.setValue(apps);
	}
}
