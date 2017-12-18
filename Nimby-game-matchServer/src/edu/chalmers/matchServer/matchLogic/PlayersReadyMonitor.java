package edu.chalmers.matchServer.matchLogic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.chalmers.nimby.network.match.Player;

/**
 * Used to wait until a list is empty.
 * @author Adam grönberg
 */
public class PlayersReadyMonitor {
	
	private boolean allPlayersReady = false;
	
	private final Lock lock = new ReentrantLock();
	private final Condition playersReady = lock.newCondition();

	private final HashSet<? extends Player> playersNotReady;
	
	/**
	 * Constructor. Sets the players to connect.
	 * @param playersNotReady players to connect
	 */
	public PlayersReadyMonitor(final HashSet<? extends Player> playersNotReady) {
		this.playersNotReady = playersNotReady;
	}

	/**
	 * Waits for all players to connect.
	 * @throws InterruptedException InterruptedException
	 */
	public final void waitForPlayersReady() throws InterruptedException {
		lock.lock();
		while (!allPlayersReady) {
			playersReady.await();
		}
		lock.unlock();
	}
	
	/**
	 * Checks if the game is ready.
	 */
	public final void checkPlayersReady() {
		lock.lock();
		if (playersNotReady.isEmpty()) {
			allPlayersReady = true;
		}
		playersReady.signal();
		lock.unlock();		
	}
	
	/**
	 * Set player to ready.
	 * @param playerName name of player to set ready
	 */
	public final void setPlayerReady(final String playerName) {
		lock.lock();
		Iterator<? extends Player> itr = playersNotReady.iterator();
		while (itr.hasNext()) {
			Player p = itr.next();
			if (p.getUserName().equals(playerName)) {
				playersNotReady.remove(p);
				break;
			}
		}
		lock.unlock();
	}
	
	/**
	 * @return returns the number of players who ain't ready.
	 */
	public final int getNumberOfPlayersLeft() {
		lock.lock();
		int playersLeft = playersNotReady.toArray().length;
		lock.unlock();
		return playersLeft;
	}
	
	/**
	 * @param playerName the player to find
	 * @return true if the player contains in list. 
	 */
	public final boolean playerContains(final String playerName) {
		boolean contains = false;
		lock.lock();
		Iterator<? extends Player> itr = playersNotReady.iterator();
		while (itr.hasNext()) {
			Player p = itr.next();
			if (p.getUserName().equals(playerName)) {
				contains = true;
				break;
			}
		}
		lock.unlock();
		return contains;
	}
}
