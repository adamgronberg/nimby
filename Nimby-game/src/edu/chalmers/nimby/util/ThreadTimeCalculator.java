package edu.chalmers.nimby.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Runs a {@link Thread} and shuts it down if the thread timesout.
 * @author Gustav Dahl
 *
 */
public class ThreadTimeCalculator implements Runnable {

	private static final int TIME_TO_SLEEP = 100;
	
	
	@Override
	public void run() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(CreateLoginAccountHandle.getInstance());
		//System.out.println("Entered the thread killer class");
		threadTimeCheck(executor);
		
	}
	
	/**
	 * Starts  a thread and kills if after some time.
	 * @param executor 
	 */
	private void threadTimeCheck(final ExecutorService executor) {
		try {
			Thread.sleep(TIME_TO_SLEEP);
			executor.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
