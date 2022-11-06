package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		String regex = "\\bPoli.*\\b";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(message);
		match.find();
		return match.group();
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
						receptorUser.messageOutput.writeUTF(userName + " finished the chat");
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
		printInUserConsole("------------------------");
		printInUserConsole("Total users online: " + users.size());
		if(users.size() > 0) {
			printInUserConsole("List of users");
			users.forEach(
					(key, value)
					-> printInUserConsole(key + " is connected"));			
		}
		printInUserConsole("------------------------");
	}
	
	public void printInUserConsole(String message) {
		try {
			messageOutput.writeUTF(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
