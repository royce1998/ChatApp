import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import sun.audio.*;

public class Server implements Runnable{
	public static int ID;
	private ArrayList<ClientThread> clients;
	private SimpleDateFormat fdate;
	private int port;
	private boolean serverOn;
	private Thread t;
	private String servername;
	
	public Server(int port, String name){
		this.port = port;
		fdate = new SimpleDateFormat("HH:mm:ss");
		clients = new ArrayList<ClientThread>();
		servername = name;
	}
	
	private int getPort(){
		return port;
	}
	
	public void start(){
		System.out.println("Starting " + servername + "...");
		if (t == null){
			t = new Thread(this, servername);
			t.start();
		}
	}
	
	public void run(){
		System.out.println("Running " + servername +"...");
		serverOn = true;
		try{
			ServerSocket socket = new ServerSocket(port);
			// loop to wait for connections, ongoing server
			while(serverOn){
				System.out.println("The server is waiting for clients on port " + port + "...");
				Socket csocket = socket.accept(); // open client socket
				if (!serverOn)
					break;
				ClientThread thread = new ClientThread(csocket);
				clients.add(thread);
				thread.start();
			}
			try{
				socket.close();
				for (int i = 0; i < clients.size(); i++){
					ClientThread client = clients.get(i);
					try{
						client.sInput.close();
						client.sOutput.close();
						client.socket.close(); // close client socket
					} catch(Exception e){}
				}
			} catch(Exception e){}
		} catch(Exception e){}
	}
	
	// display event
	private void display(String msg){
		//System.out.println(fdate.format(new Date()) + " " + msg);
	}
	
	private synchronized void pushMessage(String msg){
		String time = fdate.format(new Date());
		msg = time + " " + msg;
		//System.out.println(msg);
		for (int i = clients.size() - 1; i >= 0; i--){ // going backwards to avoid removal null error
			ClientThread client = clients.get(i);
			if (client.writeMessage(msg) == false){
				clients.remove(i);
				display(client.username + " has been removed from the clients list.");
			}
		}
	}
	
	public synchronized void remove(int removalID){
		for (int i = 0; i < clients.size(); i++){
			if (clients.get(i).id == removalID){
				clients.remove(i);
				return;
			}
		}
	}
	
	public static void main (String[] args){
		int portnum = 1400;
		switch(args.length) {
			case 1:
				try {
					portnum = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number. Enter digits only.");
					return;
				}
			default:
				break;				
		}
		System.out.println("Using port " + portnum + ".");
		Server server = new Server(portnum, "Server 1");
		server.start();
	}
	
	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;
		String username;
		Message cm;
		String date;

		ClientThread(Socket socket) {
			ID++;
			id = ID;
			this.socket = socket;
			System.out.println("Connecting thread...");
			try{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				username = (String) sInput.readObject();
				System.out.println("OutputStream and InputStream successfully established!");
			}
			catch(Exception e){}
			display(username + " has connected.");
            date = new Date().toString() + "\n";
		}

		public void run() {
			boolean keepGoing = true;
			while(keepGoing) {
				try {
					cm = (Message) sInput.readObject();
				}
				catch (Exception e) {
					break;				
				}
				String message = cm.getMsg();

				switch(cm.getType()) {
					case Message.MESSAGE:
						pushMessage(username + ": " + message);
						break;
					case Message.RM:
						display(username + " disconnected with a RM message.");
						keepGoing = false;
						break;
					case Message.LS:
						writeMessage("List of the users connected at " + fdate.format(new Date()) + "\n");
						for(int i = 0; i < clients.size(); ++i) {
							ClientThread client = clients.get(i);
							writeMessage((i+1) + ". " + client.username + " since " + client.date);
						}
						break;
				}
			}
			remove(id);
			close();
		}
		
		private void close() {
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {}
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		private boolean writeMessage(String msg) {
			if(!socket.isConnected()) {
				close();
				return false;
			}
			try {
				sOutput.writeObject(msg);
			}
			catch(IOException e) {
				display("Error sending message... :'(");
				display(e.toString());
			}
			return true;
		}
	}
}