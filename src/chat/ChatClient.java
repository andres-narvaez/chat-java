package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {
	private static String clientName;
	

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter your name: ");
		clientName = scanner.nextLine();
		System.out.println("Please enter the ip server by default is localhost address: ");
		String ipCustom = scanner.nextLine();
		String ipName = ipCustom != "" ? ipCustom : "localhost";
		System.out.println("Please enter the port server by default is 9000: ");
		String portCustom = scanner.nextLine();
		int port = portCustom != "" ? Integer.parseInt(portCustom) : 9000;
		InetAddress ipLocal = InetAddress.getByName(ipName);
		startClient(ipLocal, port);
	}
	
	@SuppressWarnings("resource")
	private static void startClient(InetAddress ip, int port) throws UnknownHostException, IOException {
		try {
			Socket socket = new Socket(ip, port);
			Scanner reader = new Scanner(System.in);
			DataInputStream input = new DataInputStream(socket.getInputStream());
	        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
	        
	        output.writeUTF(clientName);
	        
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
	                    try{
	                    	String message = reader.nextLine();
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
