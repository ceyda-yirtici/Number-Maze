// Java program to play an Audio
// file using Clip Object
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SimpleAudioPlayer
{

	// to store current position
	Long currentFrame;
	Clip clip;
	
	// current status of clip
	String status;
	
	AudioInputStream audioInputStream;

	// constructor to initialize streams and clip
	public SimpleAudioPlayer(String filePath)		throws UnsupportedAudioFileException,IOException,LineUnavailableException	{
		// create AudioInputStream object
		audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
		
		// create clip reference
		clip = AudioSystem.getClip();
		
		// open audioInputStream to the clip
		clip.open(audioInputStream);
		
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		play();
	}
	
	// Method to play the audio
	public void play()
	{
		//start the clip
		clip.start();
		
		status = "play";
	}
	
	// Method to pause the audio
	public void pause()
	{
		if (status.equals("paused"))
		{
			return;
		}
		this.currentFrame =
		this.clip.getMicrosecondPosition();
		clip.stop();
		status = "paused";
	}
	
	// Method to resume the audio
	public void resumeAudio(String filePath) throws UnsupportedAudioFileException,
								IOException, LineUnavailableException
	{
		if (status.equals("play"))
		{
			
			return;
		}
		clip.close();
		resetAudioStream(filePath);
		clip.setMicrosecondPosition(currentFrame);
		this.play();
	}
	
	// Method to restart the audio
	public void restart(String filePath) throws IOException, LineUnavailableException,
											UnsupportedAudioFileException
	{
		clip.stop();
		clip.close();
		resetAudioStream(filePath);
		currentFrame = 0L;
		clip.setMicrosecondPosition(0);
		this.play();
	}
	
	// Method to stop the audio
	public void stop() throws UnsupportedAudioFileException,
	IOException, LineUnavailableException
	{
		currentFrame = 0L;
		clip.stop();
		clip.close();
	}

	// Method to reset audio stream
	public void resetAudioStream(String filePath) throws UnsupportedAudioFileException, IOException,
											LineUnavailableException
	{
		audioInputStream = AudioSystem.getAudioInputStream(
		new File(filePath).getAbsoluteFile());
		clip.open(audioInputStream);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

}
