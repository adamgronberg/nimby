package edu.chalmers.nimby.network.lobby;

import java.util.HashSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import edu.chalmers.nimby.network.match.PlayerInfo;

/**
 * Wraps the different data sent over the network in packages.
 * @author Adam Gr�nberg, Gustav Dahl
 *
 */
public final class LobbyNetwork {
	
	/**
	 * Utility class. Should not be instanced.
	 */
	private LobbyNetwork() { }
	
	/**
	 * The different message types that can be used when sending a Packet3_Message.
	 * @author Adam Gr�nberg, Gustav Dahl
	 *
	 */
	public enum MessageType { WHISPER, FEDERATION, CURRENT_CHANNEL, BROADCAST, GENERAL, SHIP, TEAM, MATCH };
	
	/**
	 * The different chat commands the server needs to handle.
	 * @author Gustav Dahl
	 */
	public enum ActionType { FRIENDADD, FRIENDREMOVE, FRIENDLIST, IGNOREUSER, UNIGNOREUSER, LISTIGNORE, JOINGENERAL, LEAVEGENERAL };
			
	/**
	 * The type of connection used by the LobbyServer.
	 * @author Adam Gr�nberg
	 */
	public enum ConnectionType { MATCH_SERVER, CLIENT };

	/**
	 * Used to simplify code.
	 * @author Adam Gr�nberg
	 *
	 */
	public interface AbstractPacket { };
	
	/**
	 * Packet used to for request to start the connection from a client to the
	 * server.
	 * @author gustav Dahl and Adam grönberg
	 */
	public static class Packet0LoginRequest implements AbstractPacket  {
		public boolean login;
		public String userName; 
	}
	
	/**
	 * Packet sent from the server to the client to confirm the succesful conncetion
	 * attempt.
	 * @author gustav Dahl and Adam grönberg
	 */
	public static class Packet1LoginAccepted implements AbstractPacket {
		public boolean accepted = false; 
	}
	
	/**
	 * Packet used to notify the client if an error has occured.
	 * @author gustav dahl and Adam grönberg
	 */
	public static class Packet2ErrorMessage implements AbstractPacket { 
		public String message; 
	}
	
	/**
	 * Contains all different chat messages sent on the network.
	 * @author Adam Gr�nberg, Gustav Dahl
	 */
	public static class Packet3Message implements AbstractPacket {
		public MessageType messageType = MessageType.CURRENT_CHANNEL;
		public String sender = "";
		public String receiver = "";
		public String message = "";
		public HashSet<String> set = null;
	}
	
	/**
	 * Packet used to initate a new chat room.
	 * @author gustav dahl and Adam grönberg
	 */
	public static class Packet4Action implements AbstractPacket { 
		public ActionType actionType;
		public String sender = "";
		public String objectName = "";
	}
	
	/**
	 * Used to distinct the connection type (server or client).
	 * @author Adam Gr�nberg
	 *
	 */
	public static class Packet5RegisterConnectionType implements AbstractPacket {
		public ConnectionType connectionType;
	}
	
	/**
	 * Used to tell user where their match is.
	 * @author Adam Gr�nberg
	 */
	public static class Packet6InitializeGameClient implements AbstractPacket {
		public String ipAddress = "";
		public int matchID;
	}
	
	/**
	 * Used by game client to tell lobby server that he whants to join matchmaking.
	 * @author Adam Gr�nberg
	 *
	 */
	public static class Packet7JoinMatchQueueRequest implements AbstractPacket { 
		public boolean join;
	}
	
	/**
	 * Used to tell the match server who is in a match and the ID of the match to start.
	 * @author Adam Gr�nberg
	 *
	 */
	public static class Packet8InitializeMatchServer implements AbstractPacket {
		public HashSet<PlayerInfo> players = new HashSet<PlayerInfo>();
		public int matchID;
	}
	
	/**
	 * Used by the match server to tell the lobby server that it's ready to receive the game clients.
	 * @author Adam Gr�nberg
	 */
	public static class Packet9MatchServerReady implements AbstractPacket {
		public int matchID;
	}
	
	/**
	 * Registers the different data types sent over the network.
	 * @param endPoint	Either a server or client
	 */
	public static void register(final EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(Packet0LoginRequest.class);
		kryo.register(Packet1LoginAccepted.class);
		kryo.register(Packet2ErrorMessage.class);
		kryo.register(Packet3Message.class);
		kryo.register(Packet4Action.class);
		kryo.register(Packet5RegisterConnectionType.class);
		kryo.register(Packet6InitializeGameClient.class);
		kryo.register(Packet8InitializeMatchServer.class);
		kryo.register(ConnectionType.class);
		kryo.register(MessageType.class);
		kryo.register(HashSet.class);
		kryo.register(UserData.class);
		kryo.register(ActionType.class);
		kryo.register(PlayerInfo.class);
		kryo.register(Packet7JoinMatchQueueRequest.class);
		kryo.register(Packet9MatchServerReady.class);
	}	
}
