package net.reichholf.repola.adapter;

import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import net.reichholf.repola.AppInfo;
import net.reichholf.repola.R;
import net.reichholf.repola.Setup;
import net.reichholf.repola.Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.ViewHolder> {
	private final List<AppInfo> mApps;
	private final int mLayoutId;

	public AppInfoAdapter(List<AppInfo> apps, int layoutId) {
		mApps = apps;
		mLayoutId = layoutId;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(mLayoutId, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(mApps.get(position));
	}

	@Override
	public int getItemCount() {
		return mApps.size();
	}

	public void updateApps(List<AppInfo> apps) {
		mApps.clear();
		mApps.addAll(apps);
		notifyDataSetChanged();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		ImageView mPackageImage;
		TextView mPackageName;

		ViewHolder(View itemView) {
			super(itemView);
			mPackageImage = itemView.findViewById(R.id.application_icon);
			mPackageName = itemView.findViewById(R.id.application_name);
			Setup setup = new Setup(itemView.getContext());
			itemView.setOutlineProvider(new ViewOutlineProvider() {
				@Override
				public void getOutline(View view, Outline outline) {
					outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), Utils.pixelFromDp(itemView.getContext(), setup.getCornerRadius()));
				}
			});
			itemView.setClipToOutline(true);
		}

		public void bind(AppInfo appInfo) {
			Setup setup = new Setup(itemView.getContext());

			Drawable backgroundDrawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.application_selector);
			int alpha = 255 - (int) (255 * setup.getTransparency());
			backgroundDrawable.setAlpha(alpha);

			if (appInfo != null) {
				if (setup.colorfulIcons())
					Utils.tintAppIcon(backgroundDrawable, appInfo.getPalette(), alpha);
				itemView.setTag(appInfo);
				mPackageName.setText(appInfo.getName());
				if (appInfo.getIcon() != null)
					mPackageImage.setImageDrawable(appInfo.getIcon());
			}
			itemView.setBackground(backgroundDrawable);
		}
	}

}
