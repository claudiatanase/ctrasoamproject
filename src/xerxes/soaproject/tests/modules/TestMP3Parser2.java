package xerxes.soaproject.tests.modules;

import java.io.File;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Frame;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.id3.framebody.AbstractFrameBodyTextInfo;

public class TestMP3Parser2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		MP3File f      = (MP3File)AudioFileIO.read(new File("tests/test2.mp3"));
		Tag tag        = f.getTag();
		//ID3v1Tag         v1Tag  = (ID3v1Tag)tag;
		AbstractID3v2Tag v2tag  = f.getID3v2Tag();
		ID3v24Tag        v24tag = f.getID3v2TagAsv24();
		System.out.println(v2tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST));
		System.out.println(v2tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM));
		System.out.println(v2tag.getFirst(ID3v24Frames.FRAME_ID_YEAR));


	}

}
