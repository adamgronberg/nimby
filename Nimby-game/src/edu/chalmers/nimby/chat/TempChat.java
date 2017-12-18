package edu.chalmers.nimby.chat;

import java.util.Scanner;

import com.esotericsoftware.minlog.Log;

import edu.chalmers.nimby.network.lobby.LobbyServerHandle;
import edu.chalmers.nimby.util.Pipe;


/**
 * Temp chat service.
 * @author Adam Gr�nberg, Gustav Dahl
 *
 */
public class TempChat implements Runnable {

	private Scanner scanner;
	private LobbyServerHandle lobbyServerHandle;
	private Pipe<String> chatPipe;

	/**
	 * Temp chat.
	 * @param chatPipe pipe used to send messages.
	 */
	public TempChat(final Pipe<String> chatPipe) { 
		this.lobbyServerHandle = LobbyServerHandle.getInstance();
		scanner = new Scanner(System.in);
		this.chatPipe = chatPipe;
	}

	@Override
	public final void run() {
		String userName = "";
		
		Log.info("Enter your user name: ");	 //login should not be done here
		if (scanner.hasNext()) {
			userName = scanner.nextLine();
			lobbyServerHandle.sendLoginRequest(userName);
		}
		
		while (true) {		//Should not be a while loop that does nothing
			Log.info("Enter a message to send:");		
			if (scanner.hasNext()) {
				InputDecider.inputDecision(scanner.nextLine(), chatPipe);
			}
		}
	}
}
