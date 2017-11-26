package bob.d3.d3ext;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import bob.d3.D3ExException;
import bob.d3.Document;

/**
 * Das Programm liest gespeicherte Dokumente und deren Eigenschaften aus der
 * D3-Datenbank. Zusätzlich versucht es, die Dokumente im Dateisystem vom
 * D3-Server zu finden. Zu jedem Dokument wird eine beschreibende Textdatei
 * angelegt. Der Name der Textdaten wird von der ID des Dokuments abgeleitet.
 * Wurde eine Datei zum Dokument gefunden, wird diese zur Textdatei kopiert.
 * 
 * @author maik@btmx.net
 *
 */
public class ExportTool {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(ExportTool.class.getName());

	/** der Zeitpunkt vom Programmstart */
	private static final long startTimeMillis = System.currentTimeMillis();

	/**
	 * Das Programm erwartet den Zielpfad als Argument.
	 * 
	 * @param args
	 *            die Argumente
	 * @throws D3ExException
	 *             wenn Probleme beim Programmablauf
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws D3ExException, ClassNotFoundException, SQLException {
		if (0 < args.length) {
			final String exportPath = args[0];
			final boolean onlyDatabase = (1 < args.length ? "--onlyDatabase".equals(args[1]) : false);

			log("Program starts...\n\texport path = " + exportPath + "\n\tonly database = " + onlyDatabase);

			File path = new File(exportPath);
			final MemoryWriter memory = new MemoryWriter(path);

			TextWriter writer = (onlyDatabase ? null : new TextWriter(exportPath));
			DocumentFolder src = (onlyDatabase ? null : DocumentFolder.create());

			D3Reader fac = null;
			try {
				fac = new D3Reader("/query_docs_all.sql");
				if (fac.open()) {
					int count = 0;
					do {
						Document doc = fac.getDoc();
						if (null != doc) {
							if (null != writer) {
								// Datei suchen
								final File f = src.lookFor(doc.getId(), doc.getFolder(), doc.getErw());
								doc.setFile(f);
								// Textdatei schreiben
								writer.pull(doc);
							}
							memory.pull(doc);
						}
						log(String.format("[%d] document %s pulled", ++count, doc.getId()));
					} while (fac.next());
				}
			} finally {
				if (null != fac) {
					fac.close();
				}
				memory.close();
			}
			log("Program finished. Bye!");
		} else {
			System.out.println("usage: java -cp ... bob.d3.d3ext.D3ExTool <path>");
		}

	}

	private static void log(final String msg) {
		long interval = System.currentTimeMillis() - startTimeMillis;
		final long hr = TimeUnit.MILLISECONDS.toHours(interval);
		final long min = TimeUnit.MILLISECONDS.toMinutes(interval) % 60;
		final long sec = TimeUnit.MILLISECONDS.toSeconds(interval) % 60;
		LOG.info(String.format("%02d:%02d:%02d %s", hr, min, sec, msg));
	}

}
