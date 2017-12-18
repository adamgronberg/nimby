package edu.chalmers.matchServer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import edu.chalmers.matchServer.matchLogic.Match;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet3PlayerCommand;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet5ShipVote;
import edu.chalmers.nimby.network.match.PlayerConnection;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet0ConnectToMatchRequest;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet1ConnectToMatchAnswer;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet2ShipData;

/**
 * Used to listen on game clients.
 * @author Adam Grönberg
 *
 */
public class GameClientListener extends Listener {

	private int uniqueIDCounter = 1; // TODO: TEMP!!!!
	private MatchServer matchServer;

	/**
	 * Constructor.
	 * @param matchServer the server
	 */
	public GameClientListener(final MatchServer matchServer) {
		this.matchServer = matchServer;
	}

	/**
	 * Called when a server connected with user.
	 * @param conn the server connected
	 */
	@Override
	public void connected(final Connection conn) {
	}

	/**
	 * Called when user has disconnected.
	 * @param conn the server that disconnected
	 */
	@Override
	public void disconnected(final Connection conn) {
	}

	/**
	 * Called when receiving a package from server.
	 * @param conn The server received from
	 * @param data	The data received
	 */
	@Override
	public final void received(final Connection conn, final Object data) {
		PlayerConnection playerConn = (PlayerConnection) conn;
		if (data instanceof Packet3PlayerCommand) {
			Packet3PlayerCommand command = (Packet3PlayerCommand) data;
			handleCommands(playerConn, command);
		} else if (data instanceof Packet0ConnectToMatchRequest) {
			Packet0ConnectToMatchRequest request = (Packet0ConnectToMatchRequest) data;
			handleConnectToMatchReqest(playerConn, request);
		} else if (data instanceof Packet2ShipData) {
			Packet2ShipData ship = (Packet2ShipData) data;
			matchServer.getMatchServer().sendToAllExceptTCP(conn.getID(), ship);
		} else if (data instanceof Packet5ShipVote) {
			Packet5ShipVote shipsVotedOn = (Packet5ShipVote) data;
			handleShipVotes(playerConn, shipsVotedOn);
		}
	}

	/**
	 * Handle incoming votes.
	 * @param playerConn The player who voted
	 * @param shipsVotedOn The ships he voted on
	 */
	private void handleShipVotes(final PlayerConnection playerConn, final Packet5ShipVote shipsVotedOn) {
		Match match = matchServer.getMatch(playerConn.getMatchID());
		if (match != null) {
			match.addPlayerVotes(playerConn, shipsVotedOn.ships);
		} 
	}

	/**
	 * Handles player commands.
	 * @param playerConn The player that tries to connect	
	 * @param command The command given
	 */
	private void handleCommands(final PlayerConnection playerConn, final Packet3PlayerCommand command) {
		Match match = matchServer.getMatch(playerConn.getMatchID());
		match.giveCommand(command);
	}

	/**
	 * Handles a player that tries to connect to a match.
	 * @param playerConn The player that tries to connect
	 * @param request The request
	 */
	private void handleConnectToMatchReqest(final PlayerConnection playerConn, final Packet0ConnectToMatchRequest request) {
		Match match = matchServer.getMatch(request.matchID);
		Packet1ConnectToMatchAnswer anwser = new Packet1ConnectToMatchAnswer();	
		if (match != null) {
			if (match.playerValidForMatch(request.userName)) {
				anwser.uniqueID = uniqueIDCounter++;
				//player.setToken(token);			TODO: Might have to sync different threads ect
				playerConn.setMatchID(request.matchID);
				playerConn.setUserName(request.userName);
				playerConn.setShips(request.ships);
				match.addPlayerToGame(playerConn);
			}
		} 
		matchServer.getMatchServer().sendToTCP(playerConn.getID(), anwser);
	}
}
