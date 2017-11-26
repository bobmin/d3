package bob.d3.finder;

import java.io.File;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.Document;

public class MemoryReaderTest {

	private static final String MEMORY_PATH = System.getProperty("d3ext.mempath");

	private static final String SQL = "SELECT * FROM document INNER JOIN property ON doc_id = prop_doc_id LIMIT 0, 100";

	@Test
	public void testOpenGetDocNextClose() throws ClassNotFoundException, SQLException {
		System.out.println("path: " + MEMORY_PATH);
		File folder = new File(MEMORY_PATH);
		Assert.assertTrue(folder.exists());
		MemoryReader reader = null;
		try {
			reader = new MemoryReader(folder);
			if (reader.open(SQL)) {
				do {
					Document doc = reader.getDoc();
					System.out.println(doc);
				} while (reader.next());
			}
		} finally {
			if (null != reader) {
				reader.close();
			}
		}
	}

}
