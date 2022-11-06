package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ChatServer {
	static HashMap<String, UserHandler> users = new HashMap<String, UserHandler>();
	private static boolean running = true;

	public static void main(String[] args) {
		System.out.println("Server is starting...");
		
		try {
			startServer(9000);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void removeUser(String userId) {
		Boolean userExist = users.containsKey(userId);
		
		if (userExist) {
			users.remove(userId);
			System.out.println("------------------");
			System.out.println("User " + userId + " is disconnected.");
			System.out.println("------------------");
			notifyNewUserconnection(userId, false);
		}
	}
	
	private static void startServer(int port) throws IOException {
		ServerSocket server = new ServerSocket(port);
		Socket socket;
		System.out.println(server);
		
		while(running) {
		 socket = server.accept();
		 DataInputStream input = new DataInputStream(socket.getInputStream());
         DataOutputStream output = new DataOutputStream(socket.getOutputStream());
         int numberOfUsers = users.size();
         String userName = "Poli" + numberOfUsers;
         UserHandler user = new UserHandler(socket, userName, input, output);
         Thread thread = new Thread(user);
         users.put(userName, user);
         System.out.println("------------------");
         System.out.println("User " + userName + " is connected.");
         System.out.println("------------------");
         thread.start();
         user.printUsersConnected();
         notifyNewUserconnection(userName, true);
		}
	}
	
	private static void notifyNewUserconnection(String newUserName, Boolean isConnection) {
		String message = isConnection ? " is online" : " is offline";

		users.forEach((id, user) -> {
			if(user.receptorUser == null && id != newUserName) {
				user.printInUserConsole("------------------------");
				user.printInUserConsole(newUserName + message);
				user.printUsersConnected();
			}
		});
	}
}
