package xerxes.soaproject.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;

import xerxes.soaproject.interfaces.Modules;

public class LuceneMP3Module implements Modules {

	static final File INDEX_DIR = new File("index");

	/**
	 * Log4j logger
	 */
	static Logger log4j = Logger.getLogger("org.soarproject.xerxes");

	public LuceneMP3Module() {

	}

	public boolean initializeModule() {
		if (INDEX_DIR.exists()) {
			log4j.debug("Cannot save index to '" + INDEX_DIR
					+ "' directory, please delete it first");
			return false;
		}
		return true;
	}

	public String getExtension() {
		return "mp3";
	}

	public boolean parseFile(String pathName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseFile(String pathName, String[] tags) {
		Date start = new Date();
		boolean createNewIndex = true;
		try {

			if (INDEX_DIR.list().length > 1)
				createNewIndex = false;

			IndexWriter writer = new IndexWriter(INDEX_DIR,
					new StandardAnalyzer(), createNewIndex,
					IndexWriter.MaxFieldLength.LIMITED);
			log4j.debug("Indexing to directory '" + INDEX_DIR + "'...");
			indexDocs(writer, new File(pathName), tags);
			log4j.debug("Optimizing...");
			writer.optimize();
			writer.close();

			Date end = new Date();
			log4j
					.debug(end.getTime() - start.getTime()
							+ " total milliseconds");

		} catch (IOException e) {
			log4j.debug(" caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
			return false;
		} catch (InvalidAudioFrameException e) {
			log4j.debug(" caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
			return false;
		} catch (ReadOnlyFileException e) {
			log4j.debug(" caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
			return false;
		} catch (TagException e) {
			log4j.debug(" caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
			return false;
		} catch (CannotReadException e) {
			log4j.debug(" caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
			return false;
		}
		return true;
	}

	static void indexDocs(IndexWriter writer, File file, String[] tags)
			throws IOException, InvalidAudioFrameException,
			ReadOnlyFileException, TagException, CannotReadException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]), tags);
					}
				}
			} else {
				log4j.debug("adding " + file);
				try {
					Document doc = new Document();
					String title = file.getName();
					int startExt = title.lastIndexOf('.');
					for (int i = 0; i < tags.length; i++)
						doc.add(new Field("tag", tags[i], Field.Store.YES,
								Field.Index.ANALYZED));
					doc.add(new Field("filename", title, Field.Store.YES,
							Field.Index.ANALYZED));
					if (startExt > 0)
						doc.add(new Field("ext", title.substring(startExt),
								Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("path", file.getAbsolutePath(),
							Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("modified", DateTools.timeToString(file
							.lastModified(), DateTools.Resolution.MINUTE),
							Field.Store.YES, Field.Index.NOT_ANALYZED));
					MP3File f = (MP3File) AudioFileIO.read(file);
					if (f.hasID3v2Tag()) {
						getID3v1Parse(f, doc);
					} else {
						getID3v1Parse(f, doc);
					}
					writer.addDocument(doc);
				}
				// at least on windows, some temporary files raise this
				// exception with an "access denied" message
				// checking if the file can be read doesn't help
				catch (FileNotFoundException fnfe) {
					;
				}
			}
		}
	}

	public static void getID3v2Parse(MP3File f, Document doc) {

		AbstractID3v2Tag v2tag = f.getID3v2Tag();

		doc.add(new Field("artist", v2tag
				.getFirst(ID3v24Frames.FRAME_ID_ARTIST), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("album", v2tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM),
				Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("title", v2tag.getFirst(ID3v24Frames.FRAME_ID_TITLE),
				Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("comment", v2tag
				.getFirst(ID3v24Frames.FRAME_ID_COMMENT), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("genre", v2tag.getFirst(ID3v24Frames.FRAME_ID_GENRE),
				Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("year", v2tag.getFirst(ID3v24Frames.FRAME_ID_YEAR),
				Field.Store.YES, Field.Index.ANALYZED));

	}

	public static void getID3v1Parse(MP3File mp3, Document doc) {
		ID3v1Tag tag = mp3.getID3v1Tag();
		doc.add(new Field("artist", tag.getArtist().toString(),
				Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("album", tag.getAlbum().toString(), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("title", tag.getTitle().toString(), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("comment", tag.getComment().toString(),
				Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("genre", tag.getGenre().toString(), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("year", tag.getYear().toString(), Field.Store.YES,
				Field.Index.ANALYZED));

	}
}
