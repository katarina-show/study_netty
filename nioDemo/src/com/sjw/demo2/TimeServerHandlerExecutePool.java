package com.sjw.demo2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池类
 */
public class TimeServerHandlerExecutePool {

	private ExecutorService executor;
	
	//使用原生的ThreadPoolExecutor
	public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
		executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize, 120l, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
	}

	public void execute(Runnable task) {
		executor.execute(task);
	}

}
