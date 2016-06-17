/*
 *   Copyright 2015 Okinawa Open Laboratory, General Incorporated Association
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.okinawaopenlabs.orientdb.client;

import java.util.concurrent.TimeoutException;

public class DbAccessFlag {
	private volatile static boolean flag = true;

	static public boolean isFlag() {
		return flag;
	}

	static private void setFlag(boolean flag) {
		DbAccessFlag.flag = flag;
	}

	synchronized static public void lock() throws TimeoutException {
		int timeout_cnt = 10000;
		while((!DbAccessFlag.isFlag()) && (timeout_cnt > 0)) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.getMessage();
			}
			timeout_cnt--;
		}
		if (DbAccessFlag.isFlag()) {
			DbAccessFlag.setFlag(false);
		} else {
			throw new TimeoutException();
		}
	}

	synchronized static public void unlock() {
		DbAccessFlag.setFlag(true);
	}
}
