package bob.d3.d3ext;

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

import bob.d3.d3ext.D3ExDocFactory.D3ExDoc;
import bob.d3.d3ext.D3ExException.ConfigException;
import bob.d3.d3ext.D3ExException.DatabaseException;
import bob.d3.d3ext.D3ExException.ExportException;

public class D3ExWriter {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(D3ExTool.class.getName());

	private D3ExDocArts arts = null;

	private D3ExDocFields fields = null;

	private final File root;

	public D3ExWriter(final String path) throws ConfigException, DatabaseException {
		Objects.requireNonNull(path);
		this.root = new File(path);
		if (!root.exists()) {
			throw new D3ExException.ConfigException("[path] is not writable: " + path, null);
		}
		LOG.info("path assigned: " + path);
		arts = D3ExDocArts.getDefault();
		fields = D3ExDocFields.getDefault();
	}

	public void export(final D3ExDoc doc) throws ExportException {
		File f = new File(root, doc.getFolder());
		f.mkdirs();
		writeTextfile(f, doc);
		if (doc.hasAttachment()) {
			copyAttachment(f, doc);
		}
		LOG.info("document exported: " + doc.getId());
	}

	private void writeTextfile(final File folder, final D3ExDoc doc) throws ExportException {
		final String id = doc.getId();
		File file = new File(folder, id + ".txt");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writeLine(writer, "ID", doc.getId());
			String dokuart = doc.getArt();
			writeLine(writer, "DOKUMENTENART", formatDokuart(dokuart));
			for (String key : doc.getPropKeys()) {
				String keyFormatted = formatKey(dokuart, key);
				writeLine(writer, keyFormatted, doc.getPropValue(key));
			}
			writeLine(writer, "ERWEITERUNG", doc.getErw());
			if (doc.hasAttachment()) {
				File f = doc.getFile();
				writeLine(writer, "DATEIPFAD", f.getAbsolutePath());
			} else {
				writeLine(writer, "DATEIPFAD", "(ohne Datei)");
			}
			
		} catch (IOException ex) {
			throw new D3ExException.ExportException("cannot write textfile for " + id, ex);

		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				LOG.warning("[writer] not closable: " + ex.getMessage());
			}

		}
	}

	private String formatKey(String dokuart, String name) {
		Objects.requireNonNull(name);
		String x;
		if (name.matches("dok_dat_feld_[\\d]+")) {
			int startIndex = name.lastIndexOf("_");
			String nr = name.substring(startIndex + 1);
			String label = fields.lookFor(dokuart, Integer.parseInt(nr));
			if (null != label) {
				x = String.format("%s [%s]", label, name);
			} else {
				x = name;
			}
		} else {
			x = name;
		}
		return x;
	}

	private String formatDokuart(String dokuart) {
		Objects.requireNonNull(dokuart);
		String x = arts.lookFor(dokuart);
		return (null == x ? dokuart : String.format("%s [%s]", x, dokuart));
	}

	private void writeLine(final BufferedWriter writer, final String key, final String value) throws IOException {
		writer.write(key);
		writer.write(": ");
		if (null == value || 0 == value.trim().length()) {
			writer.write("(ohne Wert)");
		} else {
			writer.write(value.trim());
		}
		writer.newLine();
	}

	private void copyAttachment(final File folder, final D3ExDoc doc) throws ExportException {
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
			throw new D3ExException.ExportException("cannot copy attachment for " + id, ex);
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
