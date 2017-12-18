package edu.chalmers.nimby.network.match;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import edu.chalmers.nimby.model.PlayerShip;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.AbstractPacket;

/**
 * Contains the different packets sent between game client and match server.
 * @author Adam Grönberg
 *
 */
public final class MatchNetwork {
	
	public static final int WRITE_BUFFER_SIZE  =  16384;
	public static final int OBJECT_BUFFER_SIZE = 64 * 1024; 
	
	/**
	 * What type a keypress from the user was.
	 * @author Adam Grönberg
	 *
	 */
	public static enum KeyPress { UP, DOWN };
	
	/**
	 * Should not be instanced.
	 */
	private MatchNetwork() { }
	
	/**
	 * Game client sends a Packet0UniqueIDRequest to match server to request a Unique ID. 
	 * @author Adam grönberg
	 */
	public static class Packet0ConnectToMatchRequest {
		public int matchID;
		public String userName;
		public PlayerShip ship;
		public int token = -1;
	}
	
	/**
	 * Match server sends a Packet0UniqueIDAnwser when a client requests a unique ID.
	 * If the uniqueID is -1 the request was rejected
	 * @author Adam grönberg
	 */
	public static class Packet1ConnectToMatchAnswer implements AbstractPacket {
		public int uniqueID = -1;
	}
	
	/**
	 * @author Adam grönberg
	 */
	public static class Packet2InitialisedShips implements AbstractPacket {
		public int totalPackages;
		public byte[] ship;
	}
	
	/**
	 * Packet used to for request to start the connection from a client to the
	 * server.
	 * @author Adam grönberg
	 */
	public static class Packet3GameClientUpdate implements AbstractPacket  {
		public int matchUniquePartID;
		public KeyPress keyPress;
		public UniqueIdentifier spawnedEntityID;
	}
	
	/**
	 * Packet used to for request to start the connection from a client to the
	 * server.
	 * @author Adam grönberg
	 */
	public static class Packet4MatchServerSnapShot implements AbstractPacket  {
		public List<EntitySnapshot> partSnapshotList; 
	}
	
	/**
	 * Packet used to tell the match server that the client is ready building its ships.
	 * @author Adam grönberg
	 */
	public static class Packet5ClientShipBuildingReady implements AbstractPacket  { }
	
	/**
	 * Used to send requests to spawn projectiles from game clients to match servers.
	 * @author Adam grönberg
	 */
	public static class Packet6GameClientProjectileSpawnRequest implements AbstractPacket {
		public Vector2 position;
		public Vector2 velocity;
		public UniqueIdentifier uniqueProjectileID;
	}
	
	/**
	 * TEMP =))))))))))). Contains IDs of projectiles that client shout remove
	 * @author Adam Grönberg
	 *
	 */
	public static class Packet7MatchServerProjectilesToRemove implements AbstractPacket {
		public List<UniqueIdentifier> idsToRemove;
	}
	
	/**
	 * Registers the different data types sent over the network.
	 * @param endPoint	Either a server or client
	 */
	public static void register(final EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(Packet0ConnectToMatchRequest.class);
		kryo.register(Packet1ConnectToMatchAnswer.class);
		kryo.register(Packet2InitialisedShips.class);
		kryo.register(Packet3GameClientUpdate.class);
		kryo.register(Packet4MatchServerSnapShot.class);
		kryo.register(Packet5ClientShipBuildingReady.class);
		kryo.register(Packet6GameClientProjectileSpawnRequest.class);
		kryo.register(Packet7MatchServerProjectilesToRemove.class);
		kryo.register(UniqueIdentifier.class);
		kryo.register(EntitySnapshot.class);
		kryo.register(HashSet.class);
		kryo.register(PlayerShip.class);
		kryo.register(LinkedList.class);
		kryo.register(byte[].class);
		kryo.register(KeyPress.class);
		kryo.register(Vector2.class);
	}	
}