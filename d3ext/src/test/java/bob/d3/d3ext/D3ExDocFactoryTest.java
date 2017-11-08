package bob.d3.d3ext;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.d3ext.D3ExDocFactory.D3ExDoc;
import bob.d3.d3ext.D3ExException.DatabaseException;
import bob.d3.d3ext.D3ExException.ResourceException;

public class D3ExDocFactoryTest {

	@Test
	public void testOpenGetDocNext() throws ResourceException, DatabaseException {
		D3ExDocFactory fac = null;
		try {
			fac = D3ExDocFactory.create();
			Assert.assertTrue(fac.open());
			do {
				D3ExDoc doc = fac.getDoc();
				System.out.println(doc);
			} while (fac.next());
		} finally {
			if (null != fac) {
				fac.close();
			}
		}
	}

}
