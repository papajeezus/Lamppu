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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class LamppuBroadcastReceiver extends BroadcastReceiver {

    private static final String LIGHT_ON = "com.vmt.lamppu.LIGHT_ON";
    private static final String LIGHT_OFF = "com.vmt.lamppu.LIGHT_OFF";
    private static final String HIGHMODE_ON = "com.vmt.lamppu.HIGHMODE_ON";
    private static final String HIGHMODE_OFF = "com.vmt.lamppu.HIGHMODE_OFF";
    private static final String SET_DEV = "com.vmt.lamppu.SET_DEV";

    private static final String PREFS_NAME = "LamppuPreferences";
    static boolean mLight;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Log.d("Lamppu", "BroadcastReceiver");
        // Toast.makeText(context, "BroadcastReceiver", Toast.LENGTH_SHORT).show();
        final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();

        String action = intent.getAction();
        if (action.equals(LIGHT_ON)) {
            context.startService(new Intent("com.vmt.lamppu.ILamppuService.LIGHT_ON"));
            mLight = true;
            editor.putBoolean("light", mLight);
            editor.commit();
           
        } else if (action.equals(LIGHT_OFF)) {
            context.startService(new Intent("com.vmt.lamppu.ILamppuService.LIGHT_OFF"));
            mLight = false;
            editor.putBoolean("light", mLight);
            editor.commit();
            
        } else if (action.equals(HIGHMODE_ON)) {
            context.startService(new Intent("com.vmt.lamppu.ILamppuService.HIGHMODE_ON"));

        } else if (action.equals(HIGHMODE_OFF)) {
            context.startService(new Intent("com.vmt.lamppu.ILamppuService.HIGHMODE_OFF"));

        } else if (action.equals(SET_DEV)) {
            context.startService(new Intent("com.vmt.lamppu.ILamppuService.SET_DEV"));

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // Log.d("Lamppu", "BCR screen off");
            context.sendBroadcast(new Intent("com.vmt.lamppu.LIGHT_OFF"));
        }

    }
    
}
