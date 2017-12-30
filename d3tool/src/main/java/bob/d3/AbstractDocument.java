package bob.d3;

import java.util.Date;

public abstract class AbstractDocument {

	/** doku_id */
	protected final String id;
	protected final Date einbring;
	/** dokuart */
	protected final String art;
	/** datei_erw */
	protected final String erw;

	protected final String folder;

	public AbstractDocument(String id, Date einbring, String art, String erw) {
		this.id = id;
		this.einbring = einbring;
		this.art = (null == art ? "???" : art);
		this.erw = erw;
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
		return id;
	}

	public String getFolder() {
		return folder;
	}

	/**
	 * Liefert das Kürzel von der Dokumentenart.
	 * 
	 * @return eine Zeichenkette, niemals <code>null</code>
	 */
	public String getArt() {
		return art;
	}

	public String getErw() {
		return erw;
	}

}
