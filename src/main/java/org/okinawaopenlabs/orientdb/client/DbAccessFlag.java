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
