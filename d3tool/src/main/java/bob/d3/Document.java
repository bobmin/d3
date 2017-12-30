package bob.d3;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Document extends AbstractDocument {

	private final long size_in_byte;
	private final String logi_verzeichnis;
	private final int doku_nr;
	private final Date sterbe;

	private String artLong = null;

	/** die Eigenschaften */
	private List<Property> props = new LinkedList<>();

	/** die Datei im Filesystem */
	private File file = null;

	public Document(String id, Date einbring, String artShort, long size, String dir, String erw, int nr,
			Date sterbe) {
		super(id, einbring, artShort, erw);
		this.size_in_byte = size;
		this.logi_verzeichnis = dir;
		this.doku_nr = nr;
		this.sterbe = sterbe;
	}

	public Date getEinbring() {
		return einbring;
	}

	@Override
	public String getFolder() {
		return folder;
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

	public Date getSterbe() {
		return sterbe;
	}

	@Override
	public String toString() {
		int propsSize = (null == props ? 0 : props.size());
		// @formatter:off
		return String.format("D3ExDoc [doku_id=%s"
				+ ", einbring=%s"
				+ ", artShort=%s, artLong=%s"
				+ ", props=%d, nr=%d, bytes=%d"
				+ ", sterbe=%s"
				+ ", dir=%s, erw=%s, path=%s]"
				, id, einbring, art, artLong
				, propsSize
				, doku_nr, size_in_byte, sterbe
				, logi_verzeichnis, erw
				, (null == file ? "null" : file.getAbsolutePath()));
		// @formatter:on
	}

	public void setArtLong(String value) {
		this.artLong = value;
	}

}
