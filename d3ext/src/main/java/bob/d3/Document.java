package bob.d3;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Document {

	// --- Datenbankspalten --------------

	private final String doku_id;
	private final String dokuart;
	private final long size_in_byte;
	private final String logi_verzeichnis;
	private final String datei_erw;
	private final int doku_nr;

	// --- ENDE --------------------------

	private final String folder;

	private String artLong = null;

	/** die Eigenschaften */
	private List<Property> props = new LinkedList<>();

	/** die Datei im Filesystem */
	private File file = null;

	public Document(String id, String artShort, long size, String dir, String erw, int nr) {
		this.doku_id = id;
		this.dokuart = artShort;
		this.size_in_byte = size;
		this.logi_verzeichnis = dir;
		this.datei_erw = erw;
		this.doku_nr = nr;
		this.folder = computeFolder(id);
	}

	private String computeFolder(String id) {
		String x;
		if (8 == id.length()) {
			x = id.substring(0, 4);
		} else if (10 == id.length()) {
			x = id.substring(0, 6);
		} else {
			throw new IllegalArgumentException("format from [id] is unknown: " + id, null);
		}
		return x;
	}

	public String getId() {
		return doku_id;
	}

	public String getFolder() {
		return folder;
	}

	public String getErw() {
		return datei_erw;
	}

	/**
	 * Liefert das Kürzel von der Dokumentenart.
	 * 
	 * @return eine Zeichenkette, niemals <code>null</code>
	 */
	public String getArt() {
		return dokuart;
	}

	/**
	 * Liefert die Dokumentenart ausgeschrieben.
	 * 
	 * @return eine Zeichenkette oder <code>null</code>
	 */
	public String getArtLong() {
		return artLong;
	}

	public long getSize() {
		return size_in_byte;
	}

	public int getDokuNr() {
		return doku_nr;
	}

	public void add(final Property prop) {
		props.add(prop);
	}

	/**
	 * Liefert die Eigenschaften vom Dokument. Sind keine Eigenschaften bekannt,
	 * wird eine leere Liste geliefert.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public List<Property> getProps() {
		return props;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean hasAttachment() {
		return (null != file);
	}

	public File getFile() {
		return file;
	}

	@Override
	public String toString() {
		int propsSize = (null == props ? 0 : props.size());
		// @formatter:off
		return String.format("D3ExDoc [doku_id=%s"
				+ ", artShort=%s, artLong=%s"
				+ ", props=%d, nr=%d, bytes=%d, "
				+ "dir=%s, erw=%s, path=%s]"
				, doku_id, dokuart, artLong
				, propsSize
				, doku_nr, size_in_byte
				, logi_verzeichnis, datei_erw
				, (null == file ? "null" : file.getAbsolutePath()));
		// @formatter:on
	}

	public void setArtLong(String value) {
		this.artLong = value;
	}

}
