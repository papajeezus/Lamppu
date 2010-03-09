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

import java.io.File;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class LamppuService extends Service {

    InitialiseDeviceThread initialiseDeviceThread;
    HighModeThread highModeThread;
    BroadcastReceiver mReceiver;

    private static final String INTENT_LIGHT_ON = "com.vmt.lamppu.ILamppuService.LIGHT_ON";
    private static final String INTENT_LIGHT_OFF = "com.vmt.lamppu.ILamppuService.LIGHT_OFF";
    private static final String INTENT_HIGHMODE_ON = "com.vmt.lamppu.ILamppuService.HIGHMODE_ON";
    private static final String INTENT_HIGHMODE_OFF = "com.vmt.lamppu.ILamppuService.HIGHMODE_OFF";
    private static final String INTENT_SET_DEV = "com.vmt.lamppu.ILamppuService.SET_DEV";

    private static final String PREFS_NAME = "LamppuPreferences";

    boolean mDevOpen = false;
    boolean mHighMode = false;
    boolean mLightOn = false;

    @Override
    public IBinder onBind(Intent intent) {
        // Do not bind me.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialiseDevice();

        // To receive and register screen off, for closing device if light on
        // otherwise battery drainage will ensue
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        mReceiver = new LamppuBroadcastReceiver();
        registerReceiver(mReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Log.d("Lamppu", "Service onStartCommand: " + intent);
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mHighMode = settings.getBoolean("highMode", false);

        final String action = intent.getAction();
        if (action.equals(INTENT_LIGHT_ON)) {
            lightOn();

        } else if (action.equals(INTENT_LIGHT_OFF)) {
            lightOff();

        } else if (action.equals(INTENT_HIGHMODE_ON)) {
            highModeOn();

        } else if (action.equals(INTENT_HIGHMODE_OFF)) {
            highModeOff();

        } else if (action.equals(INTENT_SET_DEV)) {
            initialiseDevice();

        } // else if (action.equals("android.intent.action.SCREEN_OFF"))
        // this.stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Log.d("Lamppu", "Service destroyed");
        unregisterReceiver(mReceiver);
    }

    private void lightOn() {
        // Log.d("Lamppu", "Service: Light ON");
        if (!mDevOpen)
            openLedDev();
        if (mHighMode)
            highModeOn();
        else
            ledLow();
        mLightOn = true;
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("light", true);
        editor.commit();

    }

    private void lightOff() {
        // Log.d("Lamppu", "Service: Light off");
        mLightOn = false;
        if (mHighMode)
            highModeOff();
        else
            ledOff();
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("light", false);
        editor.commit();
        closeLedDev();
        this.stopSelf();
    }

    public void highModeOn() {
        mHighMode = true;
        highModeThread = new HighModeThread();
        highModeThread.start();
    }

    public void highModeOff() {
        mHighMode = false;
        if (highModeThread != null)
            highModeThread.mState = HighModeThread.STATE_DONE;
        if (mLightOn)
            ledLow();
    }

    private class HighModeThread extends Thread {
        final static int STATE_DONE = 0;
        final static int STATE_RUNNING = 1;
        int mState;

        @Override
        public void run() {
            mState = STATE_RUNNING;
            while (mState == STATE_RUNNING) {
                try {
                    if (mLightOn)
                        ledHigh();
                    Thread.sleep(300);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initialiseDevice() {
        final File dev = new File("/dev/msm_camera/config0");
        if (dev.canWrite()) {
            openLedDev();
        } else {
            initialiseDeviceThread = new InitialiseDeviceThread(deviceHandler);
            initialiseDeviceThread.start();
        }

    }

    final Handler deviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // openLedDev();
            lightOff();
        }
    };

    private void openLedDev() {
        if (!mDevOpen) {
            mDevOpen = openDev();
        }
    }

    private void closeLedDev() {
        if (mDevOpen) {
            closeDev();
            mDevOpen = false;
        }
    }

    private native boolean openDev();

    private native boolean closeDev();

    private native boolean ledHigh();

    private native boolean ledLow();

    private native boolean ledOff();

    static {
        System.loadLibrary("lamppu");
    }

}
