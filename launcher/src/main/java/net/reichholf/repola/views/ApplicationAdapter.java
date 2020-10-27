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

package net.reichholf.repola.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.reichholf.repola.AppInfo;
import net.reichholf.repola.R;
import net.reichholf.repola.Setup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class ApplicationAdapter extends ArrayAdapter<AppInfo> {
	private final int mResource;

	public ApplicationAdapter(Context context, int resId, AppInfo[] items) {
		super(context, resId, items);
		mResource = resId;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = View.inflate(getContext(), mResource, null);
		}

		Setup setup = new Setup(getContext());
		Drawable backgroundDrawable = ContextCompat.getDrawable(getContext(), R.drawable.application_normal);
		int alpha = 255 - (int) (255 * setup.getTransparency());
		backgroundDrawable.setAlpha(alpha);
		view.setBackground(backgroundDrawable);

		ImageView packageImage = view.findViewById(R.id.application_icon);
		TextView packageName = view.findViewById(R.id.application_name);
		AppInfo appInfo = getItem(position);

		if (appInfo != null) {
			view.setTag(appInfo);
			packageName.setText(appInfo.getName());
			if (appInfo.getIcon() != null)
				packageImage.setImageDrawable(appInfo.getIconHighRes());
		}
		return (view);
	}
}
