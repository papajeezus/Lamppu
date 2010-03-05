/* 
 * Copyright 2010 Veli-Matti Toratti
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 * http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

package com.vmt.lamppu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ToggleButton;

public class LamppuActivity extends Activity {

	private static final String PREFS_NAME = "LamppuPreferences";
	private static final int PROGRESS_DIALOG = 0;
	private BroadcastReceiver mReceiver;

	private boolean mHighMode;
	private boolean mLightOn;

	private static final int MENU_ABOUT = Menu.FIRST;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Log.d("Lamppu", "Activity onCreate");
		final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		mHighMode = settings.getBoolean("highMode", false);
		final SharedPreferences.Editor editor = settings.edit();

		sendBroadcast(new Intent("com.vmt.lamppu.SET_DEV"));
		// to make sure device permissions are ok.

		final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.ToggleButton01);
		togglebutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks
				if (togglebutton.isChecked()) {
					sendBroadcast(new Intent("com.vmt.lamppu.LIGHT_ON"));
					mLightOn = true;
				} else {
					sendBroadcast(new Intent("com.vmt.lamppu.LIGHT_OFF"));
					mLightOn = false;
				}
			}
		});

		final CheckBox checkbox = (CheckBox) findViewById(R.id.CheckBox01);
		checkbox.setChecked(mHighMode);
		checkbox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					sendBroadcast(new Intent("com.vmt.lamppu.HIGHMODE_ON"));
					mHighMode = true;
					editor.putBoolean("highMode", mHighMode);
					editor.commit();
					/* Set highmode trough settings */

				} else {
					sendBroadcast(new Intent("com.vmt.lamppu.HIGHMODE_OFF"));
					mHighMode = false;
					editor.putBoolean("highMode", mHighMode);
					editor.commit();
				}
			}
		});
	}
	
/* XXX Implement about menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ABOUT, 0, R.string.menu_quit);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ABOUT:
			// XXX Show menu
			return true;
		}
		return false;
	}
*/
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onRestart() {
		super.onRestart();
	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.d("Lamppu", "Activity onResume");
		final CheckBox checkbox = (CheckBox) findViewById(R.id.CheckBox01);
		checkbox.setChecked(mHighMode); // Should be ok, since only place to
										// change is here.

		final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		mLightOn = settings.getBoolean("light", false);
		final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.ToggleButton01);
		togglebutton.setChecked(mLightOn);

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void updateLighOn() {
		mLightOn = true;
	}

}