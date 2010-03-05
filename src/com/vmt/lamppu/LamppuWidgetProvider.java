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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class LamppuWidgetProvider extends AppWidgetProvider {

	private static final String LIGHT_ON = "com.vmt.lamppu.LIGHT_ON";
	private static final String LIGHT_OFF = "com.vmt.lamppu.LIGHT_OFF";

	public static final String PREFS_NAME = "LamppuPreferences";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, appWidgetId);
			// Log.d("Lamppu", "widget, onUpdate" + mLight);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Log.d("Lamppu", "widget, onReceive");
		Intent lIntent = new Intent();
		final String action = intent.getAction();
		if (action.equals(LIGHT_ON) || action.equals("android.intent.action.SCREEN_OFF")) {
			// Log.d("Lamppu", "widget, light on");
			final int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
					new ComponentName(context, LamppuWidgetProvider.class));

			for (int appWidgetId : appWidgetIds) {
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lamppuwidget);

				views.setImageViewResource(R.id.ImageView01, R.drawable.selector_light_on);
				lIntent.setAction(LIGHT_OFF);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, lIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.ImageView01, pendingIntent);

				AppWidgetManager mgr = AppWidgetManager.getInstance(context);
				mgr.updateAppWidget(appWidgetId, views);
			}
		} else if (action.equals(LIGHT_OFF)) {
			final int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
					new ComponentName(context, LamppuWidgetProvider.class));
			for (int appWidgetId : appWidgetIds) {
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lamppuwidget);

				// Log.d("Lamppu", "widget, light off");
				views.setImageViewResource(R.id.ImageView01, R.drawable.selector_light_off);
				lIntent.setAction(LIGHT_ON);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, lIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.ImageView01, pendingIntent);

				AppWidgetManager mgr = AppWidgetManager.getInstance(context);
				mgr.updateAppWidget(appWidgetId, views);
			}
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		context.sendBroadcast(new Intent("com.vmt.lamppu.SET_DEV"));
		// to verify device is writable or set device permissions up
	}

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		final boolean light = settings.getBoolean("light", false);

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lamppuwidget);

		Intent intent = new Intent();

		if (!light) {
			views.setImageViewResource(R.id.ImageView01, R.drawable.selector_light_off);
			intent.setAction(LIGHT_ON);
		} else {
			views.setImageViewResource(R.id.ImageView01, R.drawable.selector_light_on);
			intent.setAction(LIGHT_OFF);
		}

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.ImageView01, pendingIntent);
		// Log.d("Lamppu", "updateAppWidget: " + mLight);

		appWidgetManager.updateAppWidget(appWidgetId, views);
	};

}
