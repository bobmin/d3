package bob.d3.d3ext;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import bob.d3.d3ext.D3ExDocFactory.D3ExDoc;

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
public class D3ExTool {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(D3ExTool.class.getName());

	/** der Zeitpunkt vom Programmstart */
	private static final long startTimeMillis = System.currentTimeMillis();

	/**
	 * Das Programm erwartet den Zielpfad als Argument.
	 * 
	 * @param args
	 *            die Argumente
	 * @throws D3ExException
	 *             wenn Probleme beim Programmablauf
	 */
	public static void main(String[] args) throws D3ExException {
		if (0 < args.length) {
			log("Program starts...");

			String exportPath = args[0];

			D3ExWriter writer = new D3ExWriter(exportPath);
			D3ExMemory memory = D3ExMemory.getPath(exportPath);

			D3ExSourceFolder src = D3ExSourceFolder.create();

			D3ExDocFactory fac = null;
			try {
				fac = D3ExDocFactory.create();
				if (fac.open()) {
					int count = 0;
					do {
						D3ExDoc doc = fac.getDoc();
						if (null != doc) {
							// Datei suchen
							final File f = src.lookFor(doc.getId(), doc.getFolder(), doc.getErw());
							doc.setFile(f);
							// Textdatei schreiben
							writer.pull(doc);
							memory.pull(doc);
						}
						log(String.format("[%d] document %s pulled", ++count, doc.getId()));
					} while (fac.next());
				}
			} finally {
				if (null != fac) {
					fac.close();
				}
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
