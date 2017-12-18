package edu.chalmers.matchServer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import edu.chalmers.nimby.model.gameLogic.Entity;
import edu.chalmers.nimby.model.gameLogic.IdentifiableEntity;
import edu.chalmers.nimby.model.gameLogic.Part;
import edu.chalmers.nimby.model.projectile.Projectile;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.AbstractPacket;
import edu.chalmers.nimby.network.match.EntitySnapshot;
import edu.chalmers.nimby.network.match.MatchNetwork;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet7MatchServerProjectilesToRemove;
import edu.chalmers.nimby.network.match.UniqueIdentifier;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet4MatchServerSnapShot;
import edu.chalmers.nimby.network.match.PlayerConnection;

/**
 * Packs information and sends it to the receivers.
 * @author Adam Gr�nberg
 *
 */
public final class MatchPacketFactory extends Server {
	
	private static final int MAX_NUMBER_OF_SNAPSHOTS = 100;
	private static final int MAX_NUMBER_OF_PROJECTILES = 50;
	
	public MatchPacketFactory() {
		super(MatchNetwork.WRITE_BUFFER_SIZE, MatchNetwork.OBJECT_BUFFER_SIZE);
	}

	@Override
	protected Connection newConnection() {			//The server will only use UserConnections
		return new PlayerConnection();
	}
	
	/**
	 * Sends the match current status to all game clients.
	 * @param identifiableEntities entities to send to client.
	 * @param playersInGame the players to send the list to.
	 */
	public void sendSnapshot(final List<IdentifiableEntity> identifiableEntities, final HashSet<PlayerConnection> playersInGame) { 
		List<EntitySnapshot> snapshotList = new LinkedList<EntitySnapshot>();
		Iterator<IdentifiableEntity> itr = identifiableEntities.iterator();
		while (itr.hasNext()) {
			Entity e = itr.next();
			if (e instanceof Part) {
				Part part = (Part) e;
				EntitySnapshot snapshot = new EntitySnapshot();
				snapshot.angle = part.body.getAngle();
				snapshot.playerMatchUniqueID = part.getMatchPartUniqueID().userID;
				snapshot.partMatchUniqueID = part.getMatchPartUniqueID().entityID;
				snapshot.pos = part.body.getPosition();
				snapshot.velocity = part.body.getLinearVelocity();
				snapshot.serialize();
				snapshotList.add(snapshot);
			} else if (e instanceof Projectile) {
				Projectile projectile = (Projectile) e;
				EntitySnapshot snapshot = new EntitySnapshot();
				snapshot.angle = projectile.body.getAngle();
				snapshot.playerMatchUniqueID = projectile.getMatchProjectileUniqueID().userID;
				snapshot.projectileMatchUniqueID = projectile.getMatchProjectileUniqueID().entityID;
				snapshot.pos = projectile.body.getPosition();
				snapshot.velocity = projectile.body.getLinearVelocity();
				snapshot.serialize();
				snapshotList.add(snapshot);
			}
		}
		Iterator<EntitySnapshot> itr2 = snapshotList.iterator();
		while (true) {
			Packet4MatchServerSnapShot snapshot = new Packet4MatchServerSnapShot();
			List<EntitySnapshot> snapshotCluster = new LinkedList<EntitySnapshot>();
			for (int i = 0; i < MAX_NUMBER_OF_SNAPSHOTS; i++) {
				if (itr2.hasNext()) {
					snapshotCluster.add(itr2.next());
				} else if (!snapshotCluster.isEmpty()) {
					snapshot.partSnapshotList = snapshotCluster;
					sendToListUDP(snapshot, playersInGame);
					return;
				} else {
					return;
				}
			}
			snapshot.partSnapshotList = snapshotCluster;
			sendToListUDP(snapshot, playersInGame);
		}
	}
	
	/**
	 * Sends a list of projectiles that the server should remove. OMG TEMP
	 * @param idsToRemove
	 * @param playersInGame
	 */
	public void sendProjectilesToRemove(final List<UniqueIdentifier> idsToRemove, final HashSet<PlayerConnection> playersInGame) {
		Iterator<UniqueIdentifier> itr2 = idsToRemove.iterator();
		while (true) {
			Packet7MatchServerProjectilesToRemove ids = new Packet7MatchServerProjectilesToRemove();
			List<UniqueIdentifier> idCluster = new LinkedList<UniqueIdentifier>();
			for (int i = 0; i < MAX_NUMBER_OF_PROJECTILES; i++) {
				if (itr2.hasNext()) {
					idCluster.add(itr2.next());
				} else if (!idCluster.isEmpty()) {
					ids.idsToRemove = idCluster;
					sendToListUDP(ids, playersInGame);
					return;
				} else {
					return;
				}
			}
			ids.idsToRemove = idCluster;
			sendToListUDP(ids, playersInGame);
		}
	}
	
	/**
	 * Sends a message to everyone on the list.
	 * @param message the message to send
	 * @param recivers the receivers
	 */
	public void sendToListUDP(final AbstractPacket message, final HashSet<PlayerConnection> recivers) {		
		for (PlayerConnection playerConn: recivers) {
			sendPrivateMessageUDP(message, playerConn);
		}
	}
	
	/**
	 * Sends a message to everyone on the list.
	 * @param message the message to send
	 * @param recivers the receivers
	 */
	public void sendToListTCP(final AbstractPacket message, final HashSet<PlayerConnection> recivers) {		
		for (PlayerConnection playerConn: recivers) {
			sendPrivateMessageTCP(message, playerConn);
		}
	}
	
	/**
	 * Sends a message to a specific connection.
	 * @param message message to send
	 * @param conn The connection of the receiver
	 */
	public void sendPrivateMessageTCP(final AbstractPacket message, final Connection conn) {
		sendToTCP(conn.getID(), message);
	}
	
	/**
	 * Sends a message to a specific connection.
	 * @param message message to send
	 * @param conn The connection of the receiver
	 */
	public void sendPrivateMessageUDP(final AbstractPacket message, final Connection conn) {
		sendToUDP(conn.getID(), message);
	}
}
