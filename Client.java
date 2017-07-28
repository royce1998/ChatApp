import java.net.*;
import java.io.*;
import java.util.*;

public class Client  {
	public static ArrayList<Integer> rooms = new ArrayList<Integer>();
	private ObjectInputStream sInput;		// read
	private ObjectOutputStream sOutput;		// write
	private Socket socket;

	private String server, username;
	private int port;

	Client(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}
	
	public boolean start() {
		try {
			socket = new Socket(server, port);
		} 
		catch(Exception e) {return false;}
		
		display("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
	
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (Exception e) {return false;}

		new ListenFromServer().start();
		try
		{
			sOutput.writeObject(username);
		}
		catch (Exception e) {return false;}
		return true;
	}


	private void display(String msg) {
		System.out.println(msg);
	}
	
	void sendMessage(Message msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(Exception e) {
			display("Error");
		}
	}


	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		} catch(Exception e) {}
		try {
			if(sOutput != null) sOutput.close();
		} catch(Exception e) {}
        try{
			if(socket != null) socket.close();
		} catch(Exception e) {}
	}

	public static void main(String[] args) {
		int portNumber = 1400;
		String serverAddress = "192.168.0.137"; // this is the local ip address of my computer
		String userName = "Anonymous";

		switch(args.length) {
			case 3:
				serverAddress = args[2];
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {return;}
			case 1: 
				userName = args[0];
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		Scanner askforroom = new Scanner(System.in);
		while(true){
			System.out.print("What room would you like to join? (1 - 100) : ");
			try{
				int room = Integer.parseInt(askforroom.nextLine());
				if (rooms.indexOf(room) == -1){
					Server newserver = new Server(room + portNumber - 1, "Server " + room);
					newserver.start();
					rooms.add(room);
				}
				portNumber += (room - 1);
				break;
			}
			catch(Exception e){System.out.println("Invalid room number. Please enter an integer between 1 and 100.");}
		}
		Client client = new Client(serverAddress, portNumber, userName);
		if(!client.start())
			return;
		
		Scanner scan = new Scanner(System.in);
		while(true) {
			System.out.print("> ");
			String msg = scan.nextLine();
			// check type
			if(msg.equalsIgnoreCase("RM")) {
				client.sendMessage(new Message(Message.RM, ""));
				break;
			}
			else if(msg.equalsIgnoreCase("LS")) {
				client.sendMessage(new Message(Message.LS, ""));				
			}
			else {
				client.sendMessage(new Message(Message.MESSAGE, msg));
			}
		}
		client.disconnect();
	}

	class ListenFromServer extends Thread {
		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					System.out.println(msg);
					System.out.print("> ");
					JavaAudioPlaySoundExample sound = new JavaAudioPlaySoundExample();
					sound.start();
				}
				catch(IOException e) {
					display("Server has close the connection: " + e);
					break;
				}
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}
