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

import java.io.IOException;
import java.io.OutputStreamWriter;

import android.os.Handler;

public class InitialiseDeviceThread extends Thread {
	final String mShell = "sh";
	final String mPermCommand = "su -c \"chmod 666 /dev/msm_camera/config0\"";
	Handler mHandler;

	InitialiseDeviceThread(Handler h) {
		mHandler = h;
	}

	@Override
	public void run() {
		runSuCommand(mPermCommand);
	}

	private boolean runSuCommand(String command) {
		try {
			Process process = Runtime.getRuntime().exec(mShell);
			OutputStreamWriter output = new OutputStreamWriter(process
					.getOutputStream());
			output.write(mPermCommand + "\n");
			output.write("exit\n");
			output.flush();
			output.close();
			process.waitFor();
			
			mHandler.sendMessage(mHandler.obtainMessage());

			return true;
		} catch (IOException e) {
			e.printStackTrace();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
}
