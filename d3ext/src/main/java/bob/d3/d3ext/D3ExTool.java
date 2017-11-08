package bob.d3.d3ext;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;

import bob.d3.d3ext.D3ExDocFactory.D3ExDoc;

public class D3ExTool {

	private static final long startTimeMillis = System.currentTimeMillis();

	public static void main(String[] args) throws D3ExException {
		log("Program starts...");
		D3ExSourceFolder src = D3ExSourceFolder.create();
		D3ExDocFactory fac = null;
		try {
			fac = D3ExDocFactory.create();
			Assert.assertTrue(fac.open());
			do {
				D3ExDoc doc = fac.getDoc();
				if (null != doc) {
					System.out.println(doc);
					src.lookFor(doc.getId());
				}
			} while (fac.next());
		} finally {
			if (null != fac) {
				fac.close();
			}
		}
		log("Program ended. Bye!");
	}

	private static void log(final String msg) {
		long interval = System.currentTimeMillis() - startTimeMillis;
		final long hr = TimeUnit.MILLISECONDS.toHours(interval);
		final long min = TimeUnit.MILLISECONDS.toMinutes(interval) % 60;
		final long sec = TimeUnit.MILLISECONDS.toSeconds(interval) % 60;
		System.out.println(String.format("%02d:%02d:%02d %s", hr, min, sec, msg));
	}

}
