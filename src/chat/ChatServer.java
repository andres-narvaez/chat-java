package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

public class ChatServer {
	static HashMap<String, UserHandler> users = new HashMap<String, UserHandler>();
	private static boolean running = true;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws UnknownHostException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter the ip server by default is localhost address: ");
		String ipCustom = scanner.nextLine();
		String ipName = ipCustom != "" ? ipCustom : "localhost";
		InetAddress ipLocal = InetAddress.getByName(ipName);
		System.out.println("Please enter the port server by default is 9000: ");
		String portCustom = scanner.nextLine();
		int port = portCustom != "" ? Integer.parseInt(portCustom) : 9000;
		System.out.println("Server is starting...");
		
		try {
			startServer(port, ipLocal);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void removeUser(String userId) {
		Boolean userExist = users.containsKey(userId);
		
		if (userExist) {
			users.remove(userId);
			System.out.println("--------------------------------------------");
			System.out.println("User " + userId + " is disconnected.");
			System.out.println("--------------------------------------------");
			notifyNewUserconnection(userId, false);
		}
	}
	
	private static void startServer(int port, InetAddress ip) throws IOException {
		ServerSocket server = new ServerSocket(port, 50, ip);
		Socket socket;
		System.out.println(server);
		
		while(running) {
		 socket = server.accept();
		 DataInputStream input = new DataInputStream(socket.getInputStream());
         DataOutputStream output = new DataOutputStream(socket.getOutputStream());
         String userName = input.readUTF();
         UserHandler user = new UserHandler(socket, userName, input, output);
         Thread thread = new Thread(user);
         users.put(userName, user);
         System.out.println("----------------------------------------------");
         System.out.println("User " + userName + " is connected.");
         System.out.println("----------------------------------------------");
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
