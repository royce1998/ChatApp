import java.io.*;

/**
* @author Royce Yang
* @since 7/25/17
*/

public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	static final int LS = 0;
	static final int MESSAGE = 1;
	static final int RM = 2;
	private int type;
	private String msg;
	
	Message (int type, String message){
		this.type = type;
		this.msg = message;
	}
	
	//getter methods
	int getType(){
		return type;
	}
	
	String getMsg(){
		return msg;
	}
}