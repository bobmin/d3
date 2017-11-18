package bob.d3.d3ext;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.d3ext.D3ExDocFactory.D3ExDoc;
import bob.d3.d3ext.D3ExDocFactory.D3ExProp;
import bob.d3.d3ext.D3ExException.DatabaseException;
import bob.d3.d3ext.D3ExException.ResourceException;

public class D3ExDocFactoryTest {

	@Test
	public void testOpenGetDocNext() throws ResourceException, DatabaseException {
		D3ExDocFactory fac = null;
		try {
			fac = D3ExDocFactory.create("/query_docs_demo.sql");
			Assert.assertTrue(fac.open());
			do {
				D3ExDoc doc = fac.getDoc();
				System.out.println(doc);
				List<D3ExProp> props = doc.getProps();
				for (D3ExProp p : props) {
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
