package bob.d3.gadget;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Logger;

import bob.d3.ConsoleUtil;
import bob.d3.Document;
import bob.d3.export.MemoryWriter;
import bob.d3.finder.MemoryReader;

public class DemoMemoryCreatorGadget {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(DemoMemoryCreatorGadget.class.getName());

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		if (!(1 == args.length)) {
			System.out.println("usage: java -cp ... bob.d3.export.DemoMemoryCreatorGadget <path>");
			System.exit(-1);
		}

		final String exportPath = args[0];
		ConsoleUtil.log("%s starts: %s", DemoMemoryCreatorGadget.class.getSimpleName(), exportPath);

		File srcPath = new File(exportPath, "export");
		final MemoryReader reader = new MemoryReader(new File(srcPath, "d3exdb"));

		File dstPath = new File(exportPath, "demo");
		final MemoryWriter writer = new MemoryWriter(new File(dstPath, "memdb"));

		try {
			final String sql = "SELECT TOP 100 * FROM document WHERE doc_einbring_datum IS NOT NULL";
			if (reader.open(sql)) {
				do {
					Document doc = reader.getDoc();
					writer.pull(doc);
					ConsoleUtil.log(doc.toString());
				} while (reader.next());
			}
		} finally {
			reader.close();
			writer.close();
		}

		ConsoleUtil.log("%s finished. Bye!", DemoMemoryCreatorGadget.class.getSimpleName());

	}

}
