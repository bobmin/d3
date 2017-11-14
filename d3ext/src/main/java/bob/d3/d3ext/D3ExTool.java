package bob.d3.d3ext;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.Assert;

import bob.d3.d3ext.D3ExDocFactory.D3ExDoc;

public class D3ExTool {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(D3ExTool.class.getName());

	private static final long startTimeMillis = System.currentTimeMillis();

	public static void main(String[] args) throws D3ExException {
		if (0 < args.length) {
			log("Program starts...");

			D3ExWriter writer = new D3ExWriter(args[0]);

			D3ExSourceFolder src = D3ExSourceFolder.create();
			D3ExDocFactory fac = null;
			try {
				fac = D3ExDocFactory.create();
				Assert.assertTrue(fac.open());
				do {
					D3ExDoc doc = fac.getDoc();
					if (null != doc) {
						// Anhang
						File f = src.lookFor(doc.getId(), doc.getFolder(), doc.getErw());
						doc.setFile(f);
						// Export
						writer.export(doc);
					}
				} while (fac.next());
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
