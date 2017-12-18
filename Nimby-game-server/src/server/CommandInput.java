package server;

import java.util.Scanner;

import com.esotericsoftware.minlog.Log;

/**
 * Temp command input for server.
 * @author Adam Grönberg
 *
 */
public class CommandInput implements Runnable {

	private Scanner scanner;
	private LobbyPacketFactory lobbyServer;
	
	/**
	 * Constructor. Sets scanner
	 * @param lobbyServer the server used
	 */
	public CommandInput(final LobbyPacketFactory lobbyServer) {
		scanner = new Scanner(System.in);
		this.lobbyServer = lobbyServer;
	}
	
	/**
	 * Bad implementation!!!!!!!! Temp input to start game.
	 */
	@Override
	public final void run() {
		while (true) {
			Log.info("Type 'start' to start a game with all current online users");	
			while (true) {		
				if (scanner.hasNext()) {
					if (scanner.nextLine().equals("s")) {
						lobbyServer.startMatch();
					}
				}
			}
		}	
	}

}
