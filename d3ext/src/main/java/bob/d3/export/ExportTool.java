package bob.d3.export;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import bob.d3.ConsoleUtil;
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
	public static void main(String[] args) throws D3ExException, ClassNotFoundException, SQLException, IOException {
		if (!(1 == args.length || 2 == args.length)) {
			System.out.println("usage: java -cp ... bob.d3.export.ExportTool <exportPath> [<filesPath>]");
			System.exit(-1);
		}

		final String exportPath = args[0];
		final boolean onlyDatabase = (1 == args.length);

		ConsoleUtil.log("Program starts...\n\texport path = " + exportPath + "\n\tonly database = " + onlyDatabase);

		File path = new File(exportPath, "memdb");
		final MemoryWriter memory = new MemoryWriter(path);

		TextWriter writer = (onlyDatabase ? null : new TextWriter(exportPath));
		DocumentFolder src = (onlyDatabase ? null : DocumentFolder.create(new File(args[1])));

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
					ConsoleUtil.log("[%d] document %s pulled", ++count, doc.getId());
				} while (fac.next());
			}
		} finally {
			if (null != fac) {
				fac.close();
			}
			memory.close();
		}
		ConsoleUtil.log("Program finished. Bye!");

	}

}
