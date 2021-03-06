package dk.stigc.javatunes.audioplayer.player;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.AudioTrack;
import net.sourceforge.jaad.mp4.api.Frame;
import net.sourceforge.jaad.mp4.api.Movie;
import net.sourceforge.jaad.mp4.api.Track;


public class AacMp4Player extends BasePlayer
{
	
	private final File file;
	private volatile Track currentTrack;

	public AacMp4Player(File file) {
		this.file = file;
	}
	
    public void decode() throws Exception
    {
		boolean init = false;
		byte[] b;
		
		final MP4Container cont = file != null ? new MP4Container(new RandomAccessFile(file, "r")) : new MP4Container(bin);
//		final MP4Container cont = new MP4Container(bin);
		final Movie movie = cont.getMovie();
		final List<Track> tracks = movie.getTracks(AudioTrack.AudioCodec.AAC);
		if(tracks.isEmpty()) throw new Exception("movie does not contain any AAC track");
		final AudioTrack track = (AudioTrack) tracks.get(0);		this.currentTrack = track;
		final SampleBuffer buf = new SampleBuffer();
		buf.setBigEndian(false);
		
		audioInfo.setLengthInSeconds((int)movie.getDuration());
		
		final Decoder dec = new Decoder(track.getDecoderSpecificInfo());

		while(track.hasMoreFrames()) 
		{
			Frame frame = track.readNextFrame();
			dec.decodeFrame(frame.getData(), buf);

			audioInfo.addVariableBitrate(buf.getEncodedBitrate());

			if(!init)
			{
				initAudioLine(buf.getChannels(), buf.getSampleRate(), buf.getBitsPerSample(), true, false);	
				init = true;
			}

			if (!running) 
				return;
				
			b = buf.getData();
			write(b, b.length);
		}
    }

	public void seek(double time) {
		Track track = currentTrack;
		if (track != null) {
			track.seek(time);
		}
	}
}