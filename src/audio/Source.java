package audio;

import org.lwjgl.openal.AL10;

public class Source 
{
	private int sourceID;
	
	public Source()
	{
		this.sourceID = AL10.alGenSources();
	}
	
	public void Play(int buffer)
	{
		Stop();
		AL10.alSourcei(sourceID, AL10.AL_BUFFER, buffer);
		ContinuePlaying();
	}
	
	public void Pause()
	{
		AL10.alSourcePause(sourceID);
	}
	
	public void ContinuePlaying()
	{
		AL10.alSourcePlay(sourceID);
	}
	
	public void Stop()
	{
		AL10.alSourceStop(sourceID);
	}
	
	public void Delete()
	{
		Stop();
		AL10.alDeleteSources(sourceID);
	}
	
	public boolean IsPlaying()
	{
		return AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}
	
	public void SetLooping(boolean loop)
	{
		AL10.alSourcei(sourceID, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
	}
	
	public void SetVelocity(float x, float y, float z)
	{
		AL10.alSource3f(sourceID, AL10.AL_VELOCITY, x, y, z);
	}
	
	public void SetVolume(float volume)
	{
		AL10.alSourcef(sourceID, AL10.AL_GAIN, volume);
	}
	
	public void SetPitch(float pitch)
	{
		AL10.alSourcef(sourceID, AL10.AL_PITCH, pitch);
	}
	
	public void SetPosition(float x, float y, float z)
	{
		AL10.alSource3f(sourceID, AL10.AL_POSITION, x, y, z);
	}
}
