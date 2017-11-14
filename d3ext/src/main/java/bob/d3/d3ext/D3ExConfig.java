package bob.d3.d3ext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import bob.d3.d3ext.D3ExException.ConfigException;

/**
 * Die lokale Benutzerkonfiguration zum Programm.
 * 
 * @author m.boettcher@btmx.net
 *
 */
public class D3ExConfig {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(D3ExConfig.class.getName());

	/** die Konfigurationsdatei */
	private static final File CONFIG_FILE;

	public static final String DATABASE_USERNAME = "D3ExConfig.database_username";

	public static final String DATABASE_PASSWORD = "D3ExConfig.database_password";

	public static final String DEVELOPMENT_PROPERTY = "d3ext.development";

	static {
		final String path = System.getProperty("user.home");
		CONFIG_FILE = new File(path, "d3ext.properties");
	}

	/** die Singleton-Instanz */
	private static D3ExConfig _self = new D3ExConfig();

	/** die Konfiguration aus Schlüssel/Wert-Paaren */
	private Properties applicationProps;

	/**
	 * Geschützter Konstrukter ({@link #getDefault()} verwenden).
	 */
	private D3ExConfig() {
	}

	/**
	 * Liefert die Singleton-Instanz von der lokalen Benutzerkonfiguration.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public static D3ExConfig getDefault() {
		return _self;
	}

	/**
	 * Lädt die lokale Benutzerkonfiguration.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 * @throws MapitException
	 *             wenn Probleme beim Laden
	 */
	public static synchronized Properties load() throws ConfigException {
		final Properties x = new Properties();
		InputStream in = null;
		if (CONFIG_FILE.exists()) {
			try {
				in = new FileInputStream(CONFIG_FILE);
				x.load(in);
				LOG.info("config loaded, file = " + CONFIG_FILE.getAbsolutePath());
			} catch (final IOException ex) {
				throw new D3ExException.ConfigException("cannot read " + CONFIG_FILE.getAbsolutePath(), ex);
			} finally {
				if (null != in) {
					try {
						in.close();
					} catch (IOException ex) {
						// ignored
					}
				}
			}
		}
		return x;
	}

	/**
	 * Prüft die Benutzerkonfiguration und lädt diese, wenn noch nicht
	 * geschehen. Kann die Konfiguration nicht geladen werden, wird ein nicht
	 * auflösbarer schwerer Ausnahmezustand erzeugt.
	 */
	private void checkProps() {
		if (null == applicationProps) {
			try {
				applicationProps = load();
			} catch (D3ExException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * Liefert den zum Schlüssel passenden Wert oder <code>null</code>, wenn der
	 * Schlüssel nicht bekannt ist.
	 * 
	 * @param key
	 *            ein Schlüssel
	 * @return eine Zeichenkette oder <code>null</code>
	 */
	public String getProperty(final String key) {
		checkProps();
		final String x = applicationProps.getProperty(key);
		LOG.fine(String.format("value pulled, key = %s, value = %s", key, x));
		return x;
	}

	/**
	 * Liefert die gespeicherte Ganzzahl oder <code>null</code>, wenn der
	 * Schlüssel unbekannt ist. Wurde ein Wert gespeichert, der nicht als Zahl
	 * interpretiert werden kann, wird ebenfalls <code>null</code> geliefert.
	 * 
	 * @param key
	 *            der Schlüssel
	 * @return ein Objekt oder <code>null</code>
	 */
	public Long getNumber(final String key) {
		Long x = null;
		final String value = getProperty(key);
		if (null != value && !value.isEmpty()) {
			try {
				x = Long.parseLong(value);
			} catch (final NumberFormatException ex) {
				ex.printStackTrace();
			}
		}
		return x;
	}

	/**
	 * Speichert den Wert zum Schlüssel. Ist der Wert gleich <code>null</code>
	 * wird der Schlüssel gelöscht.
	 * 
	 * @param key
	 *            der Schlüssel
	 * @param value
	 *            der Wert
	 */
	public void setProperty(final String key, final String value) {
		checkProps();
		if (null == value) {
			applicationProps.remove(key);
			LOG.fine(String.format("value removed, key = %s", key));
		} else {
			applicationProps.setProperty(key, value);
			LOG.fine(String.format("value pushed, key = %s, value = %s", key, value));
		}
	}

	/**
	 * Speichert die lokale Benutzerkonfiguration.
	 * 
	 * @throws MapitException
	 *             wenn Probleme beim Schreiben
	 */
	public synchronized void save() throws ConfigException {
		if (null != applicationProps) {
			try {
				final FileOutputStream out = new FileOutputStream(CONFIG_FILE);
				applicationProps.store(out, "--- D3-Export-Tool ---");
				out.close();
				LOG.info("config saved, file = " + CONFIG_FILE.getAbsolutePath());
			} catch (final IOException ex) {
				throw new D3ExException.ConfigException("cannot write " + CONFIG_FILE.getAbsolutePath(), ex);
			}
		}
	}

	public boolean isDevelomentActiv() {
		String prop = System.getProperty(DEVELOPMENT_PROPERTY, "false");
		return Boolean.valueOf(prop).booleanValue();
	}

}
