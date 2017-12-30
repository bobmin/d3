package bob.d3;

public class Property {

	/** der Spaltenname */
	private final String columnName;

	/** der Wert */
	private final String value;

	/** die optionale Bezeichnung (abhängig von der Dokumentenart) */
	private String longtext = null;

	public Property(String columnName, String value) {
		this.columnName = columnName;
		this.value = value;
	}

	/**
	 * Setzt einen Bezeichner.
	 * 
	 * @param longtext
	 *            der neue Langtext
	 */
	public void setLongtext(final String longtext) {
		this.longtext = longtext;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getValue() {
		return value;
	}

	public String getLongtext() {
		return longtext;
	}

	public boolean hasLongtext() {
		return (null != longtext);
	}

	@Override
	public String toString() {
		// @formatter:off
		return String.format("D3ExProp [columnName=\"%s\", value=\"%s\""
				+ ", longtext=\"%s\"]"
				, columnName, value, longtext);
		// @formatter:on
	}

}
