package xerxes.soaproject.tests.modules;

import java.io.File;
import java.util.Iterator;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.FrameBodyTIT2;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.object.AbstractMP3Object;

public class TestMP3Parser {
	public static void main(String args[]) throws Exception {
		MP3File mp3 = new MP3File(new File("tests/test2.mp3"));
		if (mp3.hasID3v2Tag()) {
			getID3v2Parse(mp3);
		} else if (mp3.hasID3v1Tag()) {
			getID3v1Parse(mp3);
		}
	}

	public static void getID3v2Parse(MP3File mp3) {
		AbstractID3v2 tag = mp3.getID3v2Tag();
		System.out.println("album = " + tag.getAlbumTitle());		
		System.out.println("artist = " + tag.getLeadArtist());
		System.out.println("comment = " + tag.getSongComment());
		System.out.println("genre = " + "(" + tag.getSongGenre() + ")");
		System.out.println("title = " + tag.getSongTitle());
		System.out.println("album2 = "+ ((FrameBodyTIT2)tag.getFrame("TIT2").getBody()).getText());
		System.out.println("year = " + tag.getYearReleased());
		Iterator it = tag.iterator();
		while (it.hasNext()) {
			AbstractID3v2Frame frame = (AbstractID3v2Frame) it.next();
			String name = frame.getIdentifier().trim();
			if (!name.equals("APIC")) {
				Iterator itBody = frame.getBody().iterator();
				while (itBody.hasNext()) {
					AbstractMP3Object mp3Obj = (AbstractMP3Object) itBody
							.next();
					String bodyName = mp3Obj.getIdentifier();
					if (!bodyName.equals("Picture data")) {
						String bodyValue = mp3Obj.getValue().toString();
						System.out.println(name + "-" + bodyName + "-"
								+ bodyValue);
					}
				}
			}
		}
		// ParseData parseData = new ParseData(ParseStatus.STATUS_SUCCESS,
		// metadataCollector.getTitle(),
		// metadataCollector.getOutlinks(),
		// contentMeta,
		// metadataCollector.getData());
		// return new ParseImpl(metadataCollector.getText(), parseData);

	}

	public static void getID3v1Parse(MP3File mp3) {
		ID3v1 tag = mp3.getID3v1Tag();
		System.out.println("album = " + tag.getAlbum());
		System.out.println("artist = " + tag.getArtist());
		System.out.println("comment = " + tag.getComment());
		System.out.println("genre = " + "(" + tag.getGenre() + ")");
		System.out.println("title = " + tag.getTitle());
		System.out.println("year = " + tag.getYear());

	}
}
