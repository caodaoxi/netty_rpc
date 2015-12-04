package com.gbdex.rpc.protocol.utils;

import java.util.concurrent.locks.ReentrantLock;

public class KeyUtils {
	static long current = System.currentTimeMillis();
	static ReentrantLock lock = new ReentrantLock(true);

	public static long uuLongKey() {
		try {
			lock.lock();
			if (current < System.currentTimeMillis()) {
				current = System.currentTimeMillis();
			} else {
				current++;
			}
			return current;
		} finally {
			lock.unlock();
		}
	}
}
