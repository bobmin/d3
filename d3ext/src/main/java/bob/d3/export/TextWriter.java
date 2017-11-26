package bob.d3.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.logging.Logger;

import bob.d3.D3ExException;
import bob.d3.D3ExException.ConfigException;
import bob.d3.D3ExException.WriterException;
import bob.d3.Document;
import bob.d3.Property;

public class TextWriter {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(TextWriter.class.getName());

	/** das Wurzelverzeichnis */
	private final File root;

	public TextWriter(final String path) throws ConfigException {
		Objects.requireNonNull(path);
		this.root = new File(path);
		if (!root.exists()) {
			throw new D3ExException.ConfigException("[path] is not writable: " + path, null);
		}
		LOG.info("path assigned: " + path);
	}

	public void pull(final Document doc) throws WriterException {
		File f = new File(root, doc.getFolder());
		f.mkdirs();
		writeTextfile(f, doc);
		if (doc.hasAttachment()) {
			copyAttachment(f, doc);
		}
		LOG.info("document exported: " + doc.getId());
	}

	private void writeTextfile(final File folder, final Document doc) throws WriterException {
		final String id = doc.getId();
		File file = new File(folder, id + ".txt");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writeLine(writer, "ID", doc.getId());
			String dokuart = doc.getArt();
			writeLine(writer, "DOKUMENTENART", formatDokuart(dokuart, doc.getArtLong()));
			writeLine(writer, "DOKUMENTNUMMER", String.valueOf(doc.getDokuNr()));

			for (Property p : doc.getProps()) {
				String key;
				if (p.hasLongtext()) {
					key = String.format("%s [%s]", p.getLongtext(), p.getColumnName());
				} else {
					key = p.getColumnName();
				}
				writeLine(writer, key, p.getValue());
			}

			writeLine(writer, "ERWEITERUNG", doc.getErw());
			long size = doc.getSize();
			if (0 < size) {
				writeLine(writer, "BYTES", String.valueOf(size));
			} else {
				writeLine(writer, "BYTES", "unbekannt");
			}
			if (doc.hasAttachment()) {
				File f = doc.getFile();
				writeLine(writer, "DATEIPFAD", f.getAbsolutePath());
			} else {
				writeLine(writer, "DATEIPFAD", "(ohne Datei)");
			}
			
		} catch (IOException ex) {
			throw new D3ExException.WriterException("cannot write textfile for " + id, ex);

		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				LOG.warning("[writer] not closable: " + ex.getMessage());
			}

		}
	}

	private String formatDokuart(String artShort, String artLong) {
		Objects.requireNonNull(artShort);
		return (null == artLong ? artShort : String.format("%s [%s]", artLong, artShort));
	}

	private void writeLine(final BufferedWriter writer, final String key, final String value) throws IOException {
		writer.write(key);
		writer.write(": ");
		if (null != value && 0 < value.trim().length()) {
			writer.write(value.trim());
		} else {
			writer.write("(ohne Wert)");
		}
		writer.newLine();
	}

	private void copyAttachment(final File folder, final Document doc) throws WriterException {
		String id = doc.getId();
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(doc.getFile());
			File f2 = new File(folder, doc.getId() + "." + doc.getErw());
			out = new FileOutputStream(f2);
			final byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (IOException ex) {
			throw new D3ExException.WriterException("cannot copy attachment for " + id, ex);
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException ex) {
					LOG.warning("[out] not closable: " + ex.getMessage());
				}
			}
			if (null != in) {
				try {
					in.close();
				} catch (IOException ex) {
					LOG.warning("[in] not closable: " + ex.getMessage());
				}
			}
		}

	}

}
