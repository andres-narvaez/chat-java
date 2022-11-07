package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;

public class UserHandler implements Runnable {
	Boolean isConnected;
	String userName;
	DataInputStream messageInput;
	DataOutputStream messageOutput;
	UserHandler receptorUser = null;
	Socket socket;
	
	

	public UserHandler(Socket userSocket, String name, DataInputStream input, DataOutputStream output) throws IOException {
		isConnected = true;
		userName = name;
		socket = userSocket;
		messageInput = input;
		messageOutput = output;
		
		printInUserConsole("Welcome " + name + " you are online :)");
	}
	
	private static String findReceptorName(String message) {
		StringTokenizer nameToken = new StringTokenizer(message,"-");
		nameToken.nextToken();
        String userName = nameToken.nextToken();

		return userName;
	}

	@Override
	public void run() {
		while(isConnected) {
			try {
				if (messageInput.available() > 0) {
					String message = messageInput.readUTF();
					
					if (receptorUser == null) {
						String receptorName = findReceptorName(message);
						receptorUser = ChatServer.users.get(receptorName);
					} 
					
					if (receptorUser != null) {
						receptorUser.messageOutput.writeUTF(userName + " --> " + message);
					}
					
					
					if (message.toLowerCase().compareTo("chao") == 0 && receptorUser != null) {		
						receptorUser.messageOutput.writeUTF("----------------------------------------------");
						receptorUser.messageOutput.writeUTF(userName + " finished the chat");
						receptorUser.messageOutput.writeUTF("----------------------------------------------");
						receptorUser = null;
						isConnected = false;
						socket.close();
						ChatServer.removeUser(userName);
						break;
					}					
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		try{
            messageInput.close();
            messageOutput.close();
        }catch(IOException e){
            e.printStackTrace();
        }
	}
	
	public void printUsersConnected() {
		HashMap<String, UserHandler> users = ChatServer.users;
		int numberOfUsers = users.size();
		
		if (numberOfUsers > 1) {
			printInUserConsole("-----------------------------------------------");
			printInUserConsole("Total users online: " + numberOfUsers);
			if(users.size() > 0) {
				printInUserConsole("List of users");
				users.forEach(
						(key, value)
						-> printInUserConsole(key + " is connected"));			
			}
			printInUserConsole("-----------------------------------------------");
			printInUserConsole("Please enter a username preceded by hola -");
			printInUserConsole("-----------------------------------------------");
		} else {
			printInUserConsole("-----------------------------------------------");
			printInUserConsole("You'r the only one user online, please wait until someone else is online");
			printInUserConsole("-----------------------------------------------");
		}
	}
	
	public void printInUserConsole(String message) {
		try {
			messageOutput.writeUTF(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
