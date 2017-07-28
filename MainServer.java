import java.net.*;
import java.io.*;
import java.util.*;

public class MainServer{
	
	public static void main (String[] args){
		int portnum = 1400;
		ArrayList<Server> servers = new ArrayList<Server>();
		Server tempserver;
		for (int i = portnum; i < portnum + 3; i++){
			servers.add(new Server(i, "Server " + (i - portnum + 1)));
		}
		
		for (Server s : servers){
			s.start();
		}
	}
}