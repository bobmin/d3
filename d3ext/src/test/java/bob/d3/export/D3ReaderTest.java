package bob.d3.export;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.D3ExException.DatabaseException;
import bob.d3.D3ExException.ResourceException;
import bob.d3.export.D3Reader;
import bob.d3.Document;
import bob.d3.Property;

public class D3ReaderTest {

	@Test
	public void testOpenGetDocNext() throws ResourceException, DatabaseException {
		D3Reader fac = null;
		try {
			fac = new D3Reader("/query_docs_demo.sql");
			Assert.assertTrue(fac.open());
			do {
				Document doc = fac.getDoc();
				System.out.println(doc);
				List<Property> props = doc.getProps();
				for (Property p : props) {
					System.out.println("\t" + p);
				}
			} while (fac.next());
		} finally {
			if (null != fac) {
				fac.close();
			}
		}
	}

}
