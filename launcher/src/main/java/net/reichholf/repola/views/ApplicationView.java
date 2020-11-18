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

package net.reichholf.repola.views;

import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.reichholf.repola.R;
import net.reichholf.repola.Setup;

import java.util.Locale;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import static android.content.ContentValues.TAG;

public class ApplicationView extends LinearLayout {
	private OnClickListener mMenuClickListener;
	private ImageView mIcon;
	private TextView mText;
	private String mPackageName;
	private int mPosition;

	public ApplicationView(Context context) {
		super(context);
		initialize(context, null, null);
	}

	public ApplicationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs, null);
	}

	public ApplicationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs, defStyle);
	}

	public static String getPreferenceKey(int appNum) {
		return (String.format(Locale.getDefault(), "application_%02d", appNum));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "keyCode => " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mMenuClickListener != null) {
				mMenuClickListener.onClick(this);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void setOnMenuOnClickListener(OnClickListener clickListener) {
		mMenuClickListener = clickListener;
	}

	private void setBackgroundStateDrawable(float transparency) {
		RippleDrawable backgroundDrawable = (RippleDrawable) ContextCompat.getDrawable(getContext(), R.drawable.application_selector);
		int alpha = 255 - (int) (255 * transparency);
		backgroundDrawable.setAlpha(alpha);
		setBackground(backgroundDrawable);
	}

	@SuppressWarnings("UnusedParameters")
	private void initialize(Context context, AttributeSet attrs, Integer defStyle) {
		inflate(context, R.layout.application, this);

		setClickable(true);
		setFocusable(true);
		setClipToOutline(false);
		setClipChildren(false);

		Setup setup = new Setup(context);
		setBackgroundStateDrawable(setup.getTransparency());
		setStateListAnimator(AnimatorInflater.loadStateListAnimator(context, R.animator.selection));

		mIcon = findViewById(R.id.application_icon);
		mText = findViewById(R.id.application_name);
	}

	@SuppressWarnings("SameParameterValue")
	public ApplicationView setImageResource(@DrawableRes int res) {
		mIcon.setImageResource(res);
		return (this);
	}

	public ApplicationView setImageDrawable(Drawable drawable) {
		mIcon.setImageDrawable(drawable);
		return (this);
	}

	public ApplicationView setText(CharSequence text) {
		mText.setText(text);
		showName(true);
		return (this);
	}

	public void showName(boolean show) {
		if (mText.getText().length() == 0 || !show) {
			mText.setVisibility(GONE);
		} else {
			mText.setVisibility(VISIBLE);
		}
	}

	public String getPackageName() {
		return mPackageName;
	}

	@SuppressWarnings("UnusedReturnValue")
	public ApplicationView setPackageName(String packageName) {
		mPackageName = packageName;
		return (this);
	}

	public String getName() {
		return mText.getText().toString();
	}

	public boolean hasPackage() {
		return !TextUtils.isEmpty(mPackageName);
	}

	public int getPosition() {
		return mPosition;
	}

	public void setPosition(int position) {
		mPosition = position;
	}

	public String getPreferenceKey() {
		return (getPreferenceKey(getPosition()));
	}

}
