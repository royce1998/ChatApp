import java.io.*;
import sun.audio.*;

/**
 * A simple Java sound file example (i.e., Java code to play a sound file).
 * AudioStream and AudioPlayer code comes from a javaworld.com example.
 * @author alvin alexander, devdaily.com.
 */
public class JavaAudioPlaySoundExample implements Runnable
{
	private Thread t;
	public static void main(String[] args) 
	throws Exception
	{
		// open the sound file as a Java input stream
		String gongFile = "smw_coin.wav";
		InputStream in = new FileInputStream(gongFile);

		// create an audiostream from the inputstream
		AudioStream audioStream = new AudioStream(in);

		// play the audio clip with the audioplayer class
		AudioPlayer.player.start(audioStream);
	}
	  
	public void start(){
		if (t == null){
			t = new Thread(this, "Coin Sound");
			t.start();
		}
	}
	
	public void run(){
		String gongFile = "smw_coin.wav";
		try{
			InputStream in = new FileInputStream(gongFile);

			// create an audiostream from the inputstream
			AudioStream audioStream = new AudioStream(in);

			// play the audio clip with the audioplayer class
			AudioPlayer.player.start(audioStream);
		}
		catch(Exception e){}
	}
}