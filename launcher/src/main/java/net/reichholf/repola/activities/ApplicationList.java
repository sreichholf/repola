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

package net.reichholf.repola.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import net.reichholf.repola.AppInfo;
import net.reichholf.repola.R;
import net.reichholf.repola.Setup;
import net.reichholf.repola.adapter.AppInfoAdapter;
import net.reichholf.repola.adapter.ItemClickSupport;
import net.reichholf.repola.databinding.ApplicationsBinding;
import net.reichholf.repola.views.models.ApplicationViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ApplicationList extends AppCompatActivity implements View.OnClickListener, ItemClickSupport.OnItemClickListener, ItemClickSupport.OnItemLongClickListener {
	public static final String PACKAGE_NAME = "package_name";
	public static final String APPLICATION_NUMBER = "application";
	public static final String VIEW_TYPE = "view_type";
	public static final String DELETE = "delete";
	public static final String SHOW_DELETE = "show_delete";
	//
	public static final int VIEW_GRID = 0;
	public static final int VIEW_LIST = 1;
	//
	private int mApplication = -1;
	private int mViewType = 0;
	private ApplicationsBinding mBinding;
	private AppInfoAdapter mAdapter;
	private ItemClickSupport mItemClickSupport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle args = intent.getExtras();
		mBinding = ApplicationsBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());
		if (args != null) {
			if (args.containsKey(APPLICATION_NUMBER))
				mApplication = args.getInt(APPLICATION_NUMBER);
			if (args.containsKey(VIEW_TYPE))
				mViewType = args.getInt(VIEW_TYPE);
			if (args.getBoolean(SHOW_DELETE, true)) {
				mBinding.cancel.setOnClickListener(this);
				mBinding.delete.setOnClickListener(this);
			} else {
				mBinding.bottomPanel.setVisibility(View.GONE);
			}
		}

		int spans = new Setup(this).getAllAppColumns();
		RecyclerView.LayoutManager lm = mViewType == VIEW_LIST ? new LinearLayoutManager(this) : new GridLayoutManager(this, spans);
		mBinding.list.setLayoutManager(lm);
		mAdapter = new AppInfoAdapter(new ArrayList<>(), mViewType == VIEW_LIST ? R.layout.list_item : R.layout.grid_item);
		mBinding.list.setAdapter(mAdapter);

		mItemClickSupport = ItemClickSupport.addTo(mBinding.list)
				.setOnItemClickListener(this)
				.setOnItemLongClickListener(this);

		ApplicationViewModel model = new ViewModelProvider(this).get(ApplicationViewModel.class);
		model.getApplications().observe(this, applications -> {
			onApplicationListReady(applications);
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.delete) {
			Intent data = new Intent();

			data.putExtra(DELETE, true);
			data.putExtra(APPLICATION_NUMBER, mApplication);

			if (getParent() == null)
				setResult(Activity.RESULT_OK, data);
			else
				getParent().setResult(Activity.RESULT_OK, data);
			finish();
		} else if (id == R.id.cancel) {
			if (getParent() == null)
				setResult(Activity.RESULT_CANCELED);
			else
				getParent().setResult(Activity.RESULT_CANCELED);
			finish();
		}
	}

	public void onApplicationListReady(List<AppInfo> applications) {
		mAdapter.updateApps(applications);
	}

	@Override
	public void onItemClicked(RecyclerView recyclerView, int position, View v) {
		AppInfo appInfo = (AppInfo) v.getTag();
		Intent data = new Intent();

		data.putExtra(PACKAGE_NAME, appInfo.getPackageName());
		data.putExtra(APPLICATION_NUMBER, mApplication);

		if (getParent() == null) {
			setResult(Activity.RESULT_OK, data);
		} else {
			getParent().setResult(Activity.RESULT_OK, data);
		}
		finish();
	}

	@Override
	public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
		AppInfo appInfo = (AppInfo) v.getTag();
		// Create intent to start new activity
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
		startActivity(intent);
		return true;
	}
}
