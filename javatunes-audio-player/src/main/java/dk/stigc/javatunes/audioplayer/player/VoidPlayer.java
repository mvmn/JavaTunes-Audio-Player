package dk.stigc.javatunes.audioplayer.player;

public class VoidPlayer extends BasePlayer
{
	public VoidPlayer()
	{
		ended = true;
	}
	
	public void decode()
    {
    }

	@Override
	public void seek(double time) {
	}
}
