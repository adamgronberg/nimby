package edu.chalmers.matchServer.matchLogic;

import java.util.HashSet;

import edu.chalmers.nimby.network.match.PlayerConnection;

/**
 * The Players in a game.
 * @author Adam Grönberg
 *
 */
public final class PlayersInGame {
	
	private final HashSet<PlayerConnection> playersInGame;
	
	public PlayersInGame() {
		playersInGame = new HashSet<PlayerConnection>();
	}
	
	public synchronized HashSet<PlayerConnection> getSnapshotOfPlayersIngame() throws InterruptedException {
		HashSet<PlayerConnection> players = new HashSet<PlayerConnection>();
		for (PlayerConnection player: playersInGame) {
			players.add(new PlayerConnection(player));
		}
		return playersInGame;
	}

	public synchronized void removePlayer(final PlayerConnection player) {
		playersInGame.remove(player);
	}
	
	public synchronized void addPlayer(final PlayerConnection player) {
		playersInGame.add(player);
	}
}
