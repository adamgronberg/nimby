package edu.chalmers.nimby.chat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import edu.chalmers.nimby.avatar.Avatar;
import edu.chalmers.nimby.network.lobby.LobbyNetwork;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.AbstractPacket;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.ActionType;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.MessageType;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet3Message;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet4Action;
import edu.chalmers.nimby.network.lobby.LobbyServerHandle;
import edu.chalmers.nimby.network.match.MatchServerHandle;
import edu.chalmers.nimby.util.JSONParser;
import edu.chalmers.nimby.util.PipeIn;

/**
 * Handles Input from the chat.
 * @author gustav
 *
 */
public final class InputDecider {

	public static final String WHISPER = "/w";
	public static final String FEDERATION = "/fed";
	public static final String ADD_FRIEND = "/addfriend";
	public static final String REMOVE_FRIEND = "/remfriend";
	public static final String GET_FRIEND_LIST = "/frlist";
	public static final String JOIN_GENERAL = "/joing";
	public static final String LEAVE_GENERAL = "/leaveg";
	public static final String SHIP = "/s";
	public static final String MATCH = "/m";
	public static final String TEAM = "/t";
	public static final String HELP = "/h";
	public static final String BROADCAST = "/bc";
	private static final String EASTER_EGG = "i do a like gaming";
	private static final String TEXT2VOICE = "text2voice";

	/**
	 * This is a utility class, cannot be created.
	 */
	private InputDecider() { }

	/**
	 * Takes a String and decides what to do with the information it contains.
	 * @param chatInput The string to parse information from
	 * @param chatPipe pipe connected to chat output
	 */
	public static void inputDecision(final String chatInput, final PipeIn<String> chatPipe) {

		
		String[] inputAnalysis = chatInput.split(" ");
		String whichServer;
		int inputLength = inputAnalysis.length;
		int counter = 0;
		AbstractPacket packet = null;

		easterEggCheck(chatInput);
		
		switch (inputAnalysis[counter]) {

		case WHISPER:	
			packet = createWhisper(counter, inputAnalysis, inputLength);
			whichServer = "lobby";
			sendInput(packet, whichServer);
			break;
		case BROADCAST:
			counter++;
			packet = createPacket(counter, inputAnalysis, inputLength,
					LobbyNetwork.MessageType.BROADCAST, null);
			whichServer = "lobby";
			sendInput(packet, whichServer);
			break;
		case HELP:
			String help = "This is a help message, should be alot longer";
			chatPipe.put(help);
			System.out.println(help);
			break;
		case FEDERATION:
			HashSet<String> federationMembers = getFedSet();
			packet = createPacket(counter, inputAnalysis, inputLength,
					LobbyNetwork.MessageType.FEDERATION, federationMembers);
			whichServer = "lobby";
			sendInput(packet, whichServer);
			break;
		case ADD_FRIEND:
			counter++;
			String newFriend = inputAnalysis[counter];
			addRemoveFriend(newFriend, true);
			break;
		case REMOVE_FRIEND:
			counter++;
			String oldFriend = inputAnalysis[counter];
			addRemoveFriend(oldFriend, false);
			break;
		case GET_FRIEND_LIST:
//			HashSet<String> friendSet = getFriendList();
//			for (String s: friendSet) {
//				chatPipe.put(s);
//				System.out.println(s);
//			}
			break;
		case JOIN_GENERAL:
			packet = createAction(LobbyNetwork.ActionType.JOINGENERAL, null);
			System.out.println("joining the general chat");
			whichServer = "lobby";
			sendInput(packet, whichServer);
			break;
		case LEAVE_GENERAL:
			packet = createAction(LobbyNetwork.ActionType.LEAVEGENERAL, null);
			System.out.println("leaving the general chat");
			whichServer = "lobby";
			sendInput(packet, whichServer);
			break;
		case SHIP:
			//Shipchat msg
			packet = createPacket(counter, inputAnalysis, inputLength,

					LobbyNetwork.MessageType.SHIP, null);
			whichServer = "match";
			sendInput(packet, whichServer);
			break;
		case TEAM:
			//teamchat msg
			packet = createPacket(counter, inputAnalysis, inputLength,
					LobbyNetwork.MessageType.TEAM, null);
			whichServer = "match";
			sendInput(packet, whichServer);
			break;
		case MATCH:
			//game chat
			packet = createPacket(counter, inputAnalysis, inputLength,
					LobbyNetwork.MessageType.MATCH, null);
			whichServer = "match";
			sendInput(packet, whichServer);
			break;
		default:  //GENERAL CHAT
			packet = createPacket(counter, inputAnalysis, inputLength, 
					LobbyNetwork.MessageType.GENERAL, null);
			whichServer = "lobby";
			sendInput(packet, whichServer);
			break;
		}
	}

	/**
	 *  Creates a packet to be sent to a specific chat.
	 * @param counter on which step to begin
	 * @param inputAnalysis Array used as input to be transformed into the msg
	 * @param length the length of the array
	 * @param type the Chat type
	 * @param fedMembers The members to send to
	 * @return the packet for the message
	 */
	private static AbstractPacket createPacket(final int counter, final String[] inputAnalysis, final int length,
			final MessageType type, final HashSet<String> fedMembers) {
		Packet3Message msg = new Packet3Message();
		msg.messageType = type;
		msg.set = fedMembers;
		msg.message = createMessage(inputAnalysis, length, counter);
		if (!msg.message.isEmpty()) {
			return msg;
		}
		return null;
	}

	/**	  
	 * Creates an packet for actions.
	 * @param type The type of the action
	 * @param name corresponding to the action.
	 * @return the action packet
	 */
	private static AbstractPacket createAction(final ActionType type, final String name) {
		Packet4Action action = new Packet4Action();
		action.actionType = type;
		action.objectName = name;
		return action;
	}

	/**
	 * Creates an whisper to be sent to a user.
	 * @param counter on which step to begin
	 * @param inputAnalysis Array used as input to be transformed into the msg
	 * @param length the length of the array
	 * @return a message packet or null depending on if the message acctually conatined something
	 */
	private static AbstractPacket createWhisper(final int counter, final String[] inputAnalysis, 
			final int length) {
		int c = counter;
		Packet3Message whisperToSend = new Packet3Message();
		whisperToSend.messageType = LobbyNetwork.MessageType.WHISPER;
		c++;
		String whisperReceiver = inputAnalysis[c];
		c++;
		whisperToSend.receiver = whisperReceiver;
		whisperToSend.message = createMessage(inputAnalysis, length, c);
		if (!whisperToSend.message.isEmpty()) {
			return whisperToSend;
		}
		return null;

	}
	/**
	 * Sends the packet to the ConnectionHandler.
	 * @param packet The abstractPacket to be sent
	 * @param whichServer 
	 */
	private static void sendInput(final AbstractPacket packet, final String whichServer) {
		if (packet != null) {
			if (whichServer.equals("lobby")) {
				LobbyServerHandle.getInstance().sendTCPPacket(packet);
			} else if (whichServer.equals("server")) {
				MatchServerHandle.getInstance().sendTCPPacket(packet);
			}

		} else {
			System.out.println("Could direct that message to the server");
		}		
	}
	
	/**
	 * Creates a String from an array of Strings with suitable blankspaces. 
	 * @param message	The array of strings to combine
	 * @param length	The number of Strings to combine
	 * @param start		The start 
	 * @return The combined message
	 */
	private static String createMessage(final String[] message, final int length, final int start) {
		String newMessage = "";
		for (int i = start; i < length; i++) {
			newMessage += " " + message[i];
		}
		newMessage = newMessage.trim();
		return newMessage;
	}

	/**
	 * Adds or removes a friend depending.
	 * @param friend name of the friend to add
	 * @param add boolean true if user wants to add a friend or false if he wishes to remove
	 */
	private static void addRemoveFriend(final String friend, final boolean add) {
		ClientRequest request = null;
		ClientResponse<String> response = null;
		String sessionToken = LobbyServerHandle.getInstance().getSessionToken();

		
		System.out.println("Friend to Remove/add:  " + friend + "From my token   :" + sessionToken);
		
		try {
			if (add) {
				System.out.println("Add");
				String stringAdd = "http://192.168.1.101:8080/nimbyAccServer/account/friend/add/" + sessionToken + "/" + friend;
				System.out.println(stringAdd);
				request = new ClientRequest(stringAdd);
				request.accept("text/plain");
				response = request.post(String.class);
			} else {
				System.out.println("Remove");
				String stringRem = "http://192.168.1.101:8080/nimbyAccServer/account/friend/remove/" + sessionToken + "/" + friend;
				System.out.println(stringRem);
				request = new ClientRequest(stringRem);
				request.accept("text/plain");
				response = request.put(String.class);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * If the inputed string is the easter egg text, avatar will say it.
	 * @param input the inserted string
	 */
	private static void easterEggCheck(final String input) {
		if (input.equals(EASTER_EGG)) {
			Avatar.getInstance().setCmd(TEXT2VOICE);
			Avatar.getInstance().setText(EASTER_EGG);
			new Thread(Avatar.getInstance()).start();
		}
	}

//	/**
//	 * Gets the friend list.
//	 * @return a HashSet with all his friends
//	 */
//	@SuppressWarnings("null")
//	private static HashSet<String> getFriendList() {
//		String sessionToken = LobbyServerHandle.getInstance().getSessionToken();
//
//		HashSet<String> friendSet = new HashSet<String>();
//		String[] friends = null;
//		String rec = null;
//		int size = 0;
//		//ArrayIndex out of bounds when in JSONParser. Because we receive account 
//		// objects instead of usernames. CHANGE
//		try {
//			ClientRequest request = new ClientRequest(
//					"http://192.168.1.101:8080/nimbyAccServer/account/find/friends/" + sessionToken);
//			request.accept("application/json");
//			ClientResponse<String> response = request.get(String.class);
//
//			rec = response.getEntity();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println(rec);
//		try {
//			//Makes the klient crash, becasue we have to many fields which is unknown to the parser
//			friends = (String[]) JSONParser.toObject(rec, String[].class);
//			size = friends.length;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		//Add the friend names of a set of Strings. 
//		//TODO Ask Stolpe to change so that we reviece the names, instead of the object
//		for (int counter = 0; counter < size; counter++) {
//			friendSet.add(friends[counter]);
//			System.out.println(friends[counter]);
//		}			
//		return friendSet;
//	}

	/**
	 * Returns a list of all the federation members.
	 * @return A List of all members of the federation
	 */
	private static HashSet<String> getFedSet() {
		HashSet<String> fedHash = null;
		ClientResponse<String> response = null;  
		String[] fedMembers = null;
		int size = 0;
		String rec = null;
		String sessionToken = LobbyServerHandle.getInstance().getSessionToken();
		
		try {	
			ClientRequest request = new ClientRequest(
					"http://192.168.1.101:8080/nimbyAccServer/federation/find/coMembers/" + sessionToken); //Insert federationname here
			request.accept("application/json");

			response = request.get(String.class);  		
			rec = response.getEntity();
			System.out.println("JSON RESPONSE:  " + rec);

		} catch (Exception e) {
			System.out.println("Could not get the federation members from the server.");
			e.printStackTrace();
		}

		try {    
			fedMembers = (String[]) JSONParser.toObject(rec, String[].class); 
			System.out.println("FEDERATION MEMEBERS:    " + fedMembers);
			size = fedMembers.length;
		} catch (Exception e) {
			System.out.println("Could not parse the JSON to a String[] ");
			e.printStackTrace();
		}

		//Create a hashSet of the usernames
		fedHash = new HashSet<String>();
		System.out.println("Fedderation Hash");
		for (int counter = 0; counter < size; counter++) {
			fedHash.add(fedMembers[counter]);
			System.out.println(fedMembers[counter]);
		}
		return fedHash;
	}
}
