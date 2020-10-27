package net.reichholf.repola.activities;

import android.os.Bundle;

import net.reichholf.repola.R;

import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings("deprecation")
public class Preferences extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
	}
}
