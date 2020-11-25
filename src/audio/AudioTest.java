package audio;

import java.io.IOException;

public class AudioTest 
{
	public static void main(String[] args) throws IOException
	{
		AudioMaster.Init();
		AudioMaster.SetListenerData(0, 0, 0);
		
		int buffer = AudioMaster.LoadSound("audio/bounce.wav");
		Source source = new Source();
		source.SetLooping(true);
		source.Play(buffer);
		
		Source source2 = new Source();
		source2.SetPitch(2);
		
		char c = ' ';
		while (c != 'q')
		{
			c = (char) System.in.read();
			
			if (c == 'p')
			{
				if (source.IsPlaying())
					source.Pause();
				else
					source.ContinuePlaying();
			}
			
			if (c == 'o')
				source2.Play(buffer);
		}
		
		source2.Delete();
		source.Delete();
		AudioMaster.CleanUp();
	}
}
