package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {

	public static void main(String[] args) throws IOException {
		InetAddress ip = InetAddress.getByName("localhost");
		startClient(ip, 9000);
	}
	
	private static void startClient(InetAddress ip, int port) throws UnknownHostException, IOException {
		try {
			Socket socket = new Socket(ip, port);
			Scanner reader = new Scanner(System.in);
			DataInputStream input = new DataInputStream(socket.getInputStream());
	        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
	        Thread readMessageProcess = new Thread( new Runnable() {
	        	@Override
	            public void run(){
	                while(!socket.isClosed()){
	                    try{
	                    	if(input.available() > 0) {
	                    		String message = input.readUTF();
	                    		System.out.println(message);	                    		
	                    	}
	                    }catch(IOException e){
	                        e.printStackTrace();
	                    }
	                }
	            }
	        });
	        Thread sendMessageProcess = new Thread( new Runnable() {
	        	@Override
	            public void run(){
	                while(true){
	                    String message = reader.nextLine();
	                    try{
	                        output.writeUTF(message);
	                    }catch (IOException e){
	                        e.printStackTrace();
	                    }
	                }
	            }
	        });
	        readMessageProcess.start();
	        sendMessageProcess.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
